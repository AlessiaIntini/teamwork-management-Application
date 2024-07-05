package it.polito.teamhub.dataClass.task

import com.google.firebase.Timestamp
import java.util.Date

data class History(
    var author: Long,
    var action: Action,
    var date: Date,
    var description: String,
    var addedMembers: List<Long> = emptyList(),
    var removedMembers: List<Long> = emptyList()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): History {
            return History(
                map["author"] as? Long ?: -1,
                Action.valueOf(map["action"].toString()),
                (map["date"] as Timestamp).toDate(),
                map["description"].toString(),
                (map["addedMembers"] as? List<*>)?.filterIsInstance<Long>() ?: emptyList(),
                (map["removedMembers"] as? List<*>)?.filterIsInstance<Long>() ?: emptyList()
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "author" to author,
            "action" to action.name,
            "date" to Timestamp(date),
            "description" to description,
            "addedMembers" to addedMembers,
            "removedMembers" to removedMembers
        )
    }
}