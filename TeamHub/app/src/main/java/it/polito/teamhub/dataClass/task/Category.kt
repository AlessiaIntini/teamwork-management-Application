package it.polito.teamhub.dataClass.task

import androidx.compose.runtime.MutableState
import it.polito.teamhub.dataClass.condition.Condition
import java.security.SecureRandom
import kotlin.math.abs


data class Category(
    var name: String,
    var teamId: Long
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Category {
            val name = map["name"] as? String ?: ""
            val teamId = map["teamId"] as? Long ?: -1L
            val id = map["id"] as? Long ?: -1L
            return Category(name, teamId).apply { this.id = id }
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

fun convertCategoryCheckedInCondition(
    categoryChecked: List<MutableState<Boolean>>,
    categoryList: MutableList<Category>
): List<Condition> {
    val conditions = mutableListOf<Condition>()
    for (i in categoryChecked.indices) {
        if (categoryChecked[i].value) {
            val condition = Condition(
                field = "category",
                condition = { task ->
                    val category = categoryList.find { it.id == task.category }
                    category?.name == categoryList[i].name
                }
            )
            conditions.add(condition)
        }
    }
    return conditions
}

fun categoryInOr(
    conditions: MutableState<List<Condition>>
): Condition? {
    val categoryCondition = conditions.value.filter { it.field == "category" }
    conditions.value = conditions.value.filter { it.field != "category" }
    if (categoryCondition.isNotEmpty()) {
        return Condition(
            field = "category",
            condition = { task ->
                categoryCondition.any { it.condition(task) }
            }
        )
    }
    return null
}