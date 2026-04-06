package com.example.gemmachat

import android.app.Application
import com.example.gemmachat.data.ChatRepository
import com.example.gemmachat.data.download.HfDownloadRepository
import com.example.gemmachat.inference.EngineHolder

class GemmaChatApplication : Application() {

    lateinit var chatRepository: ChatRepository
        private set
    lateinit var downloadRepository: HfDownloadRepository
        private set
    lateinit var engineHolder: EngineHolder
        private set

    override fun onCreate() {
        super.onCreate()
        chatRepository = ChatRepository(this)
        downloadRepository = HfDownloadRepository()
        engineHolder = EngineHolder(this)
    }
}
