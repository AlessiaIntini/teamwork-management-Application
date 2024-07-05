package it.polito.teamhub.dataClass.task

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import it.polito.teamhub.dataClass.condition.Condition
import it.polito.teamhub.ui.theme.LightGreen
import it.polito.teamhub.ui.theme.Orange
import it.polito.teamhub.ui.theme.Yellow


enum class State {
    COMPLETED,
    ON_HOLD,
    IN_PROGRESS,
    PENDING;

    fun getState(): Color {
        return when (this) {
            COMPLETED -> LightGreen
            ON_HOLD -> Orange
            IN_PROGRESS -> Yellow
            PENDING -> White
        }
    }

    fun getStateString(): String {
        return when (this) {
            COMPLETED -> "Completed"
            ON_HOLD -> "On hold"
            IN_PROGRESS -> "In progress"
            PENDING -> "Pending"
        }
    }

}

fun listOfState(): List<String> {
    return listOf(
        "COMPLETED",
        "ON_HOLD",
        "IN_PROGRESS",
        "PENDING"
    )
}

fun listOfStatePrint(): List<String> {
    return listOf(
        "Completed",
        "On hold",
        "In progress",
        "Pending"
    )
}

fun listStates(): List<State> {
    return listOf(
        State.COMPLETED,
        State.ON_HOLD,
        State.IN_PROGRESS,
        State.PENDING
    )
}

fun convertStateCheckedInCondition(
    stateChecked: List<MutableState<Boolean>>
): List<Condition> {
    val conditions = mutableListOf<Condition>()
    for (i in stateChecked.indices) {
        if (stateChecked[i].value) {
            val condition = Condition(
                field = "state",
                condition = { task ->
                    task.state == State.entries.toTypedArray()[i]
                }
            )
            conditions.add(condition)
        }
    }
    return conditions
}

fun stateInOr(
    conditions: MutableState<List<Condition>>
): Condition? {
    val stateCondition = conditions.value.filter { it.field == "state" }
    conditions.value = conditions.value.filter { it.field != "state" }
    if (stateCondition.isNotEmpty()) {
        return Condition(
            field = "state",
            condition = { task ->
                stateCondition.any { it.condition(task) }
            }
        )
    }
    return null
}


