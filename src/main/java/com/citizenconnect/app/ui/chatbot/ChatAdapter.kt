package com.citizenconnect.app.ui.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citizenconnect.app.data.model.ChatMessage
import com.citizenconnect.app.databinding.ItemChatBotBinding
import com.citizenconnect.app.databinding.ItemChatUserBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_BOT = 2
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            UserViewHolder(ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BotViewHolder(ItemChatBotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val time = timeFormat.format(Date(message.timestamp))
        if (holder is UserViewHolder) {
            holder.binding.tvMessage.text = message.text
            holder.binding.tvTime.text = time
        } else if (holder is BotViewHolder) {
            holder.binding.tvMessage.text = message.text
            holder.binding.tvTime.text = time
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class UserViewHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    inner class BotViewHolder(val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root)
}
