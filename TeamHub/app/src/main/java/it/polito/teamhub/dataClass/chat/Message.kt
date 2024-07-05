package it.polito.teamhub.dataClass.chat


import com.google.firebase.Timestamp
import java.security.SecureRandom
import java.util.Date
import kotlin.math.abs

data class Message(
    var author: Long,
    var text: String,
    var date: Date,
    //map of receiver and status of message (0 = not read, 1 = read)
    val receiver: MutableMap<Long, Int>,
    //map of receiver and status of message (0 =exist, -1 = deleted)
    val deleted: MutableMap<Long, Int>? = null
) {

    companion object {
        private val secureRandom = SecureRandom()
        // var id: Long

        /*init {
            do {
                id = abs(secureRandom.nextLong())
            } while (id < 0)
        }*/

        fun fromMap(map: Map<String, Any>): Message {
            return Message(
                author = map["author"] as Long,
                text = map["text"] as String,
                date = (map["date"] as Timestamp).toDate(),
                receiver = (map["receiver"] as Map<*, *>).mapKeys {
                    it.key.toString().toLong()
                }.mapValues { (it.value as Long).toInt() }.toMutableMap(),
                deleted = (map["deleted"] as Map<*, *>).mapKeys {
                    it.key.toString().toLong()
                }.mapValues { (it.value as Long).toInt() }.toMutableMap()
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "author" to author,
            "text" to text,
            "date" to date,
            "receiver" to receiver.mapKeys { it.key.toString() },
            "deleted" to deleted?.mapKeys { it.key.toString() }
        )
    }

    var id: Long = abs(secureRandom.nextLong())
}
