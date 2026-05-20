package com.citizenconnect.app.ui.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citizenconnect.app.data.api.RetrofitClient
import com.citizenconnect.app.data.model.ChatMessage
import com.citizenconnect.app.data.model.Content
import com.citizenconnect.app.data.model.GeminiRequest
import com.citizenconnect.app.data.model.Part
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ChatViewModel : ViewModel() {

    // Using your updated API Key
    private val apiKey = "AIzaSyCxUSTJCdk0SHuZzyqIGTBhosKyULNUdxw"
    
    // Fallback list
    private val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-pro",
        "gemini-pro"
    )
    private var modelIndex = 0

    private val _messages = MutableLiveData<ChatMessage>()
    val messages: LiveData<ChatMessage> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun sendMessage(userText: String) {
        if (modelIndex >= models.size) modelIndex = 0
        
        _messages.value = ChatMessage(userText, true)

        val prompt = """
            You are a helpful civic assistant for the CitizenConnect app. 
            Keep your responses short (under 3 sentences) and professional.
            Focus on civic issues like roads, garbage, and water.
            User: $userText
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        callApi(request)
    }

    private fun callApi(request: GeminiRequest) {
        if (modelIndex >= models.size) {
            _error.value = "Chatbot service failed. Please ensure Gemini API is enabled in Google AI Studio."
            return
        }

        val currentModel = models[modelIndex]

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.geminiApi.generateContent(currentModel, apiKey, request)
                
                if (response.isSuccessful) {
                    val botText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "I'm sorry, I couldn't process that request."
                    
                    val cleanText = botText.replace(Regex("[*#_]"), "").trim()
                    _messages.value = ChatMessage(cleanText, false)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    when (response.code()) {
                        404 -> {
                            modelIndex++
                            callApi(request)
                        }
                        403 -> {
                            _error.value = "Access Denied (403). Ensure Gemini API is enabled for this key."
                        }
                        else -> {
                            _error.value = "Chatbot Error (${response.code()})"
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                _error.value = "Network Error: Check your internet connection."
            } catch (e: Exception) {
                _error.value = "Connection Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
