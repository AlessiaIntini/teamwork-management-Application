package it.polito.teamhub.viewmodel

import androidx.lifecycle.ViewModel
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.chat.Message
import it.polito.teamhub.model.ChatModel

class ChatViewModel(private val chatModel: ChatModel) : ViewModel() {
    fun getChats() = chatModel.getChats()

    fun addChat(chat: Chat) = chatModel.addChat(chat)


    fun getNotification(memberId: Long) = chatModel.getNotification(memberId)

    fun addMessage(chatId: Long, message: Message) = chatModel.addMessage(chatId, message)


    fun deleteMessage(chatId: Long, messageId: Long) = chatModel.deleteMessage(chatId, messageId)

    fun readMessage(chatId: Long, memberId: Long) = chatModel.readMessage(chatId, memberId)
}