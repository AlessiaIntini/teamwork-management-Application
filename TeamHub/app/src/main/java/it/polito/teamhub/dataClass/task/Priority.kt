package it.polito.teamhub.dataClass.task

import androidx.compose.runtime.MutableState
import it.polito.teamhub.dataClass.condition.Condition

enum class Priority {
    HIGH,
    MEDIUM,
    LOW;

    fun getPriority(): String {
        return when (this) {
            HIGH -> "!!! High"
            MEDIUM -> "!! Medium"
            LOW -> "! Low"

        }
    }
}

fun listOfPriority(): List<String> {
    return listOf(
        "HIGH",
        "MEDIUM",
        "LOW"
    )
}

fun listOfPriorityPrint(): List<String> {
    return listOf(
        "High",
        "Medium",
        "Low"
    )
}


fun convertPriorityCheckedInCondition(
    priorityChecked: List<MutableState<Boolean>>
): List<Condition> {
    val conditions = mutableListOf<Condition>()
    for (i in priorityChecked.indices) {
        if (priorityChecked[i].value) {
            val condition = Condition(
                field = "priority",
                condition = { task ->
                    task.priority == Priority.entries.toTypedArray()[i]
                }
            )
            conditions.add(condition)
        }
    }
    return conditions
}

fun priorityInOr(
    conditions: MutableState<List<Condition>>
): Condition? {
    val priorityCondition = conditions.value.filter { it.field == "priority" }
    conditions.value = conditions.value.filter { it.field != "priority" }
    if (priorityCondition.isNotEmpty()) {
        return Condition(
            field = "priority",
            condition = { task ->
                priorityCondition.any { it.condition(task) }
            }
        )
    }
    return null
}