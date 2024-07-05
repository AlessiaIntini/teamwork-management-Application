package it.polito.teamhub.dataClass.team

enum class TimeParticipation {
    FULL_TIME,
    PART_TIME;

    fun getTimeParticipationString(): String {
        return when (this) {
            FULL_TIME -> "Full Time"
            PART_TIME -> "Part Time"
        }
    }
}

fun listOfTimeParticipationPrintable(): List<String> {
    return TimeParticipation.values().map { it.getTimeParticipationString() }
}