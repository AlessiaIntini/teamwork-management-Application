package it.polito.teamhub.dataClass.task

import com.google.firebase.Timestamp
import java.security.SecureRandom
import java.util.Date
import kotlin.math.abs

data class Task(
    var title: String,
    var description: String,
    var tag: MutableList<Long>,
    var category: Long? = -1,
    var state: State = State.PENDING,
    var priority: Priority,
    var members: MutableList<Long>,
    var creationDate: Date,
    var dueDate: Date,
    var comments: MutableList<Comment>,
    var histories: MutableList<History>,
    var idTeam: Long,
    var mapReview: MutableMap<Long, Float>,
    var attachment: MutableList<Attachment> = mutableListOf(),
    var url: MutableList<String> = mutableListOf()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Task {
            return Task(
                title = map["title"] as String,
                description = map["description"] as String,
                tag = (map["tag"] as List<*>).map { it as Long }.toMutableList(),
                category = map["category"] as? Long ?: -1L,
                state = State.valueOf(map["state"] as String),
                priority = Priority.valueOf(map["priority"] as String),
                members = (map["members"] as List<*>).map { it as Long }.toMutableList(),
                creationDate = (map["creationDate"] as Timestamp).toDate(),
                dueDate = (map["dueDate"] as Timestamp).toDate(),
                comments = (map["comments"] as List<Map<String, Any>>).map { Comment.fromMap(it) }
                    .toMutableList(),
                histories = (map["histories"] as List<Map<String, Any>>).map { History.fromMap(it) }
                    .toMutableList(),
                idTeam = map["idTeam"] as Long,
                mapReview = (map["mapReview"] as Map<String, Any>).mapKeys {
                    it.key.toLong()
                }.mapValues { (it.value as Double).toFloat() }.toMutableMap(),
                attachment = (map["attachment"] as List<Map<String, Any>>).map {
                    Attachment.fromMap(it)
                }.toMutableList(),
                url = (map["url"] as List<*>).map { it as String }.toMutableList(),
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

    val review: Float
        get() {
            var sum = 0f
            if (this.mapReview.isEmpty()) return 0f
            for (review in this.mapReview) {
                sum += review.value
            }
            return sum / this.mapReview.size
        }

    constructor() : this(
        title = "",
        description = "",
        tag = mutableListOf(),
        category = null,
        state = State.PENDING,
        priority = Priority.LOW,
        members = mutableListOf(),
        creationDate = Date(),
        dueDate = Date(),
        comments = mutableListOf(),
        histories = mutableListOf(),
        idTeam = 0,
        mapReview = mutableMapOf(),
        attachment = mutableListOf(),
        url = mutableListOf()
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "tag" to tag,
            "category" to category,
            "state" to state.name,
            "priority" to priority.name,
            "members" to members,
            "creationDate" to creationDate,
            "dueDate" to dueDate,
            "comments" to comments.map { it.toMap() },
            "histories" to histories.map { it.toMap() },
            "idTeam" to idTeam,
            "mapReview" to mapReview.mapKeys { it.key.toString() },
            "attachment" to attachment.map { it.toMap() },
            "url" to url
        )
    }
}