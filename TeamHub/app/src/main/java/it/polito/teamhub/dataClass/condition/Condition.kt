package it.polito.teamhub.dataClass.condition

import it.polito.teamhub.dataClass.task.Task

data class Condition(
    val field: String,
    @Transient val condition: (Task) -> Boolean
) : java.io.Serializable
