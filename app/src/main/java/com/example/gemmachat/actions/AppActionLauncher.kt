package com.example.gemmachat.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

object AppActionLauncher {

    fun canLaunch(context: Context, action: AssistantAction): Boolean =
        resolveIntent(context, action) != null

    fun launch(context: Context, action: AssistantAction): Boolean {
        val intent = resolveIntent(context, action) ?: return false
        context.startActivity(intent)
        return true
    }

    private fun resolveIntent(context: Context, action: AssistantAction): Intent? {
        return when (action.type.lowercase()) {
            "open_app" -> resolveOpenAppIntent(context, action)
            "open_url" -> action.uri?.let {
                Intent(Intent.ACTION_VIEW, Uri.parse(it)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            else -> null
        }
    }

    private fun resolveOpenAppIntent(context: Context, action: AssistantAction): Intent? {
        val appKey = action.app?.trim()?.lowercase().orEmpty()
        val packageName = when (appKey) {
            "zomato" -> "com.application.zomato"
            "youtube" -> "com.google.android.youtube"
            "whatsapp" -> "com.whatsapp"
            "maps", "google maps" -> "com.google.android.apps.maps"
            "chrome", "browser" -> "com.android.chrome"
            else -> null
        }

        if (packageName != null) {
            context.packageManager.getLaunchIntentForPackage(packageName)?.let { launchIntent ->
                return launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        return when (appKey) {
            "zomato" -> browse("https://www.zomato.com/")
            "youtube" -> browse(
                if (action.query.isNullOrBlank()) {
                    "https://www.youtube.com/"
                } else {
                    "https://www.youtube.com/results?search_query=${encode(action.query)}"
                },
            )
            "whatsapp" -> browse("https://wa.me/")
            "maps", "google maps" -> browse(
                if (action.query.isNullOrBlank()) {
                    "https://maps.google.com/"
                } else {
                    "https://www.google.com/maps/search/?api=1&query=${encode(action.query)}"
                },
            )
            "chrome", "browser" -> browse(action.uri ?: "https://www.google.com/")
            else -> null
        }
    }

    private fun browse(url: String): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}
