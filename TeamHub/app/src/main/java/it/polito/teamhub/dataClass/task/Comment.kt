package it.polito.teamhub.dataClass.task

import com.google.firebase.Timestamp
import java.util.Date

data class Comment(
    var text: String,
    var author: Long,
    var date: Date
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Comment {
            return Comment(
                text = map["text"] as String,
                author = map["author"] as Long,
                date = (map["date"] as Timestamp).toDate()
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "text" to text,
            "author" to author,
            "date" to date
        )
    }
}