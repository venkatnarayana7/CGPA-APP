package com.citizenconnect.app.ui.chatbot

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.citizenconnect.app.data.model.ChatMessage
import com.citizenconnect.app.databinding.ActivityChatbotBinding
import com.citizenconnect.app.utils.showSnackbar

class ChatBotActivity : AppCompatActivity() {

    private lateinit var b: ActivityChatbotBinding
    private val vm: ChatViewModel by viewModels()
    private val adapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupUI()
        observeViewModel()

        // Welcome message
        if (adapter.itemCount == 0) {
            adapter.addMessage(ChatMessage("Hello! I am your Citizen Support Assistant. How can I help you today?", false))
        }
    }

    private fun setupUI() {
        b.rvChat.adapter = adapter
        b.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        b.ivBack.setOnClickListener { finish() }

        b.btnSend.setOnClickListener {
            val text = b.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                b.etMessage.setText("")
                vm.sendMessage(text)
            }
        }
    }

    private fun observeViewModel() {
        vm.messages.observe(this) { message ->
            adapter.addMessage(message)
            b.rvChat.smoothScrollToPosition(adapter.itemCount - 1)
        }

        vm.isLoading.observe(this) { isLoading ->
            b.tvStatus.text = if (isLoading) "Typing..." else "Online"
            b.btnSend.isEnabled = !isLoading
        }

        vm.error.observe(this) { errorMsg ->
            b.root.showSnackbar(errorMsg)
        }
    }
}
