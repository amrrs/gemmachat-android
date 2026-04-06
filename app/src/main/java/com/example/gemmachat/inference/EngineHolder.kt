package com.example.gemmachat.inference

import android.content.Context
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.SamplerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class EngineHolder(private val context: Context) {

    private val mutex = Mutex()
    private var engine: Engine? = null
    private var conversation: Conversation? = null

    suspend fun loadModel(modelFile: File): Result<Unit> = mutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                closeConversationLocked()
                engine?.close()
                engine = null

                val path = modelFile.absolutePath
                val cacheDir = context.cacheDir.absolutePath

                fun tryEngine(
                    main: Backend,
                    vision: Backend?,
                    audio: Backend?,
                ): Engine {
                    val cfg = EngineConfig(
                        modelPath = path,
                        backend = main,
                        visionBackend = vision,
                        audioBackend = audio,
                        cacheDir = cacheDir,
                    )
                    val e = Engine(cfg)
                    e.initialize()
                    return e
                }

                val eng = try {
                    tryEngine(
                        main = Backend.GPU(),
                        vision = Backend.GPU(),
                        audio = Backend.CPU(),
                    )
                } catch (_: Throwable) {
                    tryEngine(
                        main = Backend.CPU(),
                        vision = Backend.CPU(),
                        audio = Backend.CPU(),
                    )
                }

                engine = eng
                newConversationLocked()
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    suspend fun resetConversation() {
        mutex.withLock {
            closeConversationLocked()
            newConversationLocked()
        }
    }

    private fun newConversationLocked() {
        val eng = engine ?: return
        val cfg = ConversationConfig(
            systemInstruction = Contents.of(
                """
                You are Gemma 4 E2B, a helpful assistant running entirely on the user's device.

                If the user clearly asks to open, launch, or hand off to another app or URL,
                you may suggest exactly one structured action by appending this tag at the very end
                of your response:
                <app_action>{"type":"open_app","app":"zomato","query":"biryani","label":"Open Zomato"}</app_action>

                Rules:
                - Keep normal human-readable text before the tag.
                - The tag must contain valid JSON.
                - Supported action types: open_app, open_url.
                - Supported app names: zomato, youtube, whatsapp, maps, chrome, browser.
                - Only emit an action tag when the user's request genuinely implies an external app handoff.
                - Never emit more than one app_action tag.
                """.trimIndent(),
            ),
            samplerConfig = SamplerConfig(
                topK = 64,
                topP = 0.95,
                temperature = 1.0,
            ),
        )
        conversation = eng.createConversation(cfg)
    }

    private fun closeConversationLocked() {
        try {
            conversation?.close()
        } catch (_: Throwable) {
        }
        conversation = null
    }

    fun close() {
        synchronized(this) {
            closeConversationLocked()
            try {
                engine?.close()
            } catch (_: Throwable) {
            }
            engine = null
        }
    }

    fun streamReply(
        userText: String,
        imagePath: String?,
        audioPath: String?,
        thinking: Boolean,
        concise: Boolean,
    ): Flow<String> {
        val conv = synchronized(this) { conversation }
            ?: throw IllegalStateException("Conversation not ready")
        val extraContext: Map<String, Any> = if (thinking) {
            mapOf("enable_thinking" to true)
        } else {
            emptyMap()
        }

        val text = styledPrompt(userText.trim(), concise)
        val hasMedia =
            (imagePath != null && File(imagePath).exists()) ||
                (audioPath != null && File(audioPath).exists())

        val upstream: Flow<Message> = if (!hasMedia) {
            conv.sendMessageAsync(text, extraContext)
        } else {
            val parts = mutableListOf<Content>()
            if (text.isNotEmpty()) parts.add(Content.Text(text))
            imagePath?.let { p ->
                if (File(p).exists()) parts.add(Content.ImageFile(p))
            }
            audioPath?.let { p ->
                if (File(p).exists()) parts.add(Content.AudioFile(p))
            }
            require(parts.isNotEmpty()) { "Nothing to send" }
            conv.sendMessageAsync(Contents.of(parts), extraContext)
        }

        return flow {
            upstream.collect { msg ->
                emit(textFromMessage(msg))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun styledPrompt(text: String, concise: Boolean): String {
        val styleInstruction = if (concise) {
            "Answer briefly and directly. Prefer 1-4 short paragraphs or short bullets unless the user asks for more detail."
        } else {
            "Answer with fuller detail and clear structure, but stay relevant to what the user asked."
        }
        return if (text.isBlank()) {
            styleInstruction
        } else {
            "$styleInstruction\n\nUser request:\n$text"
        }
    }

    private fun textFromMessage(message: Message): String {
        val parts = message.contents.contents.map { content ->
            when (content) {
                is Content.Text -> content.text
                else -> ""
            }
        }
        val joined = parts.joinToString("")
        return joined.ifEmpty { message.toString() }
    }

    /**
     * One-shot title using a temporary [Conversation] so the main chat state is unchanged.
     */
    suspend fun generateConversationTitle(userPrompt: String, assistantReply: String): String =
        mutex.withLock {
            withContext(Dispatchers.IO) {
                val eng = engine ?: return@withContext ""
                val cfg = ConversationConfig(
                    systemInstruction = Contents.of(
                        "You write ultra-short chat titles. Reply with exactly one title only: " +
                            "6 words or fewer, no quotes, no newlines, no preamble or explanation.",
                    ),
                    samplerConfig = SamplerConfig(
                        topK = 64,
                        topP = 0.9,
                        temperature = 0.35,
                    ),
                )
                val conv = eng.createConversation(cfg)
                try {
                    val prompt = buildString {
                        append("Summarize this conversation into one short title.\n")
                        append("User: ")
                        append(userPrompt.take(500))
                        append("\nAssistant: ")
                        append(assistantReply.take(2000))
                    }
                    val sb = StringBuilder()
                    conv.sendMessageAsync(prompt, emptyMap()).collect { msg ->
                        sb.append(textFromMessage(msg))
                    }
                    cleanTitle(sb.toString())
                } finally {
                    try {
                        conv.close()
                    } catch (_: Throwable) {
                    }
                }
            }
        }

    private fun cleanTitle(raw: String): String {
        var s = raw.replace("\r\n", "\n").trim()
        val firstLine = s.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
        s = firstLine.trim()
        if (s.length >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length - 1).trim()
        }
        if (s.length >= 2 && s.startsWith("'") && s.endsWith("'")) {
            s = s.substring(1, s.length - 1).trim()
        }
        if (s.startsWith("Title:", ignoreCase = true)) {
            s = s.removePrefix("Title:").removePrefix("title:").trim()
        }
        return s.take(120)
    }
}
