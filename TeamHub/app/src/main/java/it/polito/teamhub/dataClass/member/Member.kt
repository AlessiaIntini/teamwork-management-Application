package it.polito.teamhub.dataClass.member

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import it.polito.teamhub.dataClass.condition.Condition
import java.security.SecureRandom
import java.util.Date
import kotlin.math.abs

data class Member(
    var fullname: String,
    var jobTitle: String,
    var description: String,
    var nickname: String,
    var email: String,
    var location: String,
    var phoneNumber: String? = null,
    var birthDate: Date? = null,
    var gender: Gender = Gender.PREFER_NOT_TO_SAY,
    var userImage: String = "",
    var color: MutableList<Long>,
    var chats: MutableList<Long>? = mutableListOf(),
    var isDeleted: Boolean = false
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Member {
            return Member(
                fullname = map["fullname"] as? String ?: "",
                jobTitle = map["jobTitle"] as? String ?: "",
                description = map["description"] as? String ?: "",
                nickname = map["nickname"] as? String ?: "",
                email = map["email"] as? String ?: "",
                location = map["location"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String,
                birthDate = (map["birthDate"] as? Timestamp?)?.toDate(),
                gender = Gender.valueOf(map["gender"] as? String ?: Gender.PREFER_NOT_TO_SAY.name),
                userImage = map["userImage"] as? String ?: "",
                color = (map["color"] as? List<*>)?.map { it as? Long ?: 0L }?.toMutableList()
                    ?: mutableListOf(),
                chats = (map["chats"] as? List<*>)?.map { it as? Long ?: 0L }?.toMutableList(),
                isDeleted = map["isDeleted"] as? Boolean ?: false
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

    val initialsName: String
        get() {
            val names = fullname.split(" ")
            return if (names.size >= 2) {
                names[0].first().toString() + names[1].first().toString()
            } else {
                names[0].first().toString()
            }
        }

    val colorBrush: Brush
        get() {
            // Check if the color list has at least two elements
            return if (color.size >= 2) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(color[0]),
                        Color(color[1])
                    )
                )
            } else {
                // Provide a default gradient if the list has fewer than two elements
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF000000), // Black color
                        Color(0xFFFFFFFF)  // White color
                    )
                )
            }
        }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fullname" to fullname,
            "jobTitle" to jobTitle,
            "description" to description,
            "nickname" to nickname,
            "email" to email,
            "location" to location,
            "phoneNumber" to phoneNumber,
            "birthDate" to birthDate?.let { Timestamp(it) },
            "gender" to gender.name,
            "userImage" to userImage,
            "color" to color,
            "chats" to chats,
            "isDeleted" to isDeleted
        )
    }
}

fun convertMemberFilteredInCondition(
    memberChoosen: MutableList<Long>
): List<Condition> {
    val conditions = mutableListOf<Condition>()
    for (i in memberChoosen.indices) {
        conditions.add(
            Condition(
                "member"
            ) { task ->
                task.members.any { it == memberChoosen[i] }
            }
        )
    }
    return conditions
}

fun memberInOr(
    conditions: MutableState<List<Condition>>
): Condition? {
    val memberCondition = conditions.value.filter { it.field == "member" }
    conditions.value = conditions.value.filter { it.field != "member" }
    if (memberCondition.isNotEmpty()) {
        return Condition(
            field = "member",
            condition = { task ->
                memberCondition.any { it.condition(task) }
            }
        )
    }
    return null
}
