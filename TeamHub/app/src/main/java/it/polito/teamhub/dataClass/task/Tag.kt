package it.polito.teamhub.dataClass.task

import androidx.compose.runtime.MutableState
import it.polito.teamhub.dataClass.condition.Condition
import java.security.SecureRandom
import kotlin.math.abs


data class Tag(
    var name: String,
    var teamId: Long,
) {
    companion object {

        fun fromMap(map: Map<String, Any>): Tag {
            val name = map["name"] as? String ?: ""
            val teamId = map["teamId"] as? Long ?: -1L
            val id = map["id"] as? Long ?: -1L
            return Tag(name, teamId).apply { this.id = id }
        }
    }

    private val secureRandom = SecureRandom()
    var id: Long

    init {
        do {
            id = abs(secureRandom.nextLong())
        } while (id < 0)
    }


    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "teamId" to teamId,
            "id" to id
        )
    }
}

fun convertTagCheckedInCondition(
    tagChecked: List<MutableState<Boolean>>,
    tagList: List<Tag>
): List<Condition> {
    val conditions = mutableListOf<Condition>()
    for (i in tagChecked.indices) {
        if (tagChecked[i].value) {
            val condition = Condition(
                field = "tag",
                condition = { task: Task ->
                    var containsTag = false
                    for (tag in task.tag) {
                        val t = tagList.find { it.id == tag }
                        if (t?.name == tagList[i].name) {
                            containsTag = true
                            break
                        }
                    }
                    containsTag
                }
            )
            conditions.add(condition)
        }
    }
    return conditions
}

fun tagInOr(
    conditions: MutableState<List<Condition>>
): Condition? {
    val tagCondition = conditions.value.filter { it.field == "tag" }
    conditions.value = conditions.value.filter { it.field != "tag" }
    if (tagCondition.isNotEmpty()) {
        return Condition(
            field = "state",
            condition = { task ->
                tagCondition.any { it.condition(task) }
            }
        )
    }
    return null
}