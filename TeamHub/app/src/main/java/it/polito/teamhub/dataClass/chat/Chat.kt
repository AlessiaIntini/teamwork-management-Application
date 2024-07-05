package it.polito.teamhub.dataClass.chat

import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import java.security.SecureRandom
import kotlin.math.abs

data class Chat(
    var type: TypeProfileIcon,
    var members: MutableList<Long>,
    var messages: MutableList<Message> = mutableListOf(),
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Chat {
            return Chat(
                type = TypeProfileIcon.valueOf(map["type"] as String),
                members = (map["members"] as List<*>).map { it as Long }.toMutableList(),
                messages = (map["messages"] as List<Map<String, Any>>).map { Message.fromMap(it) }
                    .toMutableList()
            )
        }
    }

    private val secureRandom = SecureRandom()
    var id: Long

    init {
        do {
            id = abs(secureRandom.nextLong())
        } while (id < 0)
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "type" to type,
            "members" to members.map { it },
            "messages" to messages.map { it.toMap() }
        )
    }
}

