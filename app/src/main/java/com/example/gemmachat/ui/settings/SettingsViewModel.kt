package com.example.gemmachat.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemmachat.GemmaChatApplication
import com.example.gemmachat.data.download.HfDownloadRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GemmaChatApplication

    fun clearChatHistory() {
        viewModelScope.launch {
            app.chatRepository.clearAllChats()
        }
    }

    fun deleteModel() {
        val f = HfDownloadRepository.modelFile(getApplication())
        if (f.exists()) f.delete()
    }
}
