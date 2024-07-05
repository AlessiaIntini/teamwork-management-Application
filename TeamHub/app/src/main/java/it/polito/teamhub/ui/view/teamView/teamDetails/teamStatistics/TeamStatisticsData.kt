package it.polito.teamhub.ui.view.teamView.teamDetails.teamStatistics

import android.os.Build
import androidx.annotation.RequiresApi
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.utils.getDateWithoutTime
import it.polito.teamhub.utils.getDaysOfCurrentWeek
import it.polito.teamhub.utils.isBeforeCurrentDate
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class TeamStatisticsData(
    val totStatesValues: FloatArray,
    val currentMembersCompletedOnTimeTasksWithZeros: Map<Long, Int>,
    val currentMembersCompletedBehindScheduleTasksWithZeros: Map<Long, Int>,
    val currentMembersOverdueTasksWithZeros: Map<Long, Int>,
    val currentMembersToDoTasksWithZeros: Map<Long, Int>,
    val currentMembers: List<Member>,
    val pastMembersCompletedOnTimeTasksWithZeros: Map<Long, Int>,
    val pastMembersCompletedBehindScheduleTasksWithZeros: Map<Long, Int>,
    val pastMembersOverdueTasksWithZeros: Map<Long, Int>,
    val pastMembersToDoTasksWithZeros: Map<Long, Int>,
    val pastMembers: List<Member>,
    val thisWeekDays: List<LocalDate>,
    val thisWeekTasks: Map<LocalDate, Int>,
    val thisWeekTasksWithZeros: Map<LocalDate, Int>,
    val thisWeekCompletedTasksWithZeros: Map<LocalDate, Int>,
    val mostProductiveDay: Map.Entry<Date, Int>?,
    val teamTaskList: List<Task>,
    val avgTasksRate: Double,
    val totParticipationTimeValues: FloatArray,
    val mostProductiveMembers: Map<Long, Int>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamStatisticsData

        if (!totStatesValues.contentEquals(other.totStatesValues)) return false
        if (currentMembersCompletedOnTimeTasksWithZeros != other.currentMembersCompletedOnTimeTasksWithZeros) return false
        if (currentMembersCompletedBehindScheduleTasksWithZeros != other.currentMembersCompletedBehindScheduleTasksWithZeros) return false
        if (currentMembersOverdueTasksWithZeros != other.currentMembersOverdueTasksWithZeros) return false
        if (currentMembersToDoTasksWithZeros != other.currentMembersToDoTasksWithZeros) return false
        if (currentMembers != other.currentMembers) return false
        if (thisWeekDays != other.thisWeekDays) return false
        if (thisWeekTasksWithZeros != other.thisWeekTasksWithZeros) return false
        if (thisWeekCompletedTasksWithZeros != other.thisWeekCompletedTasksWithZeros) return false
        if (mostProductiveDay != other.mostProductiveDay) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totStatesValues.contentHashCode()
        result = 31 * result + currentMembersCompletedOnTimeTasksWithZeros.hashCode()
        result = 31 * result + currentMembersCompletedBehindScheduleTasksWithZeros.hashCode()
        result = 31 * result + currentMembersOverdueTasksWithZeros.hashCode()
        result = 31 * result + currentMembersToDoTasksWithZeros.hashCode()
        result = 31 * result + currentMembers.hashCode()
        result = 31 * result + thisWeekDays.hashCode()
        result = 31 * result + thisWeekTasksWithZeros.hashCode()
        result = 31 * result + thisWeekCompletedTasksWithZeros.hashCode()
        result = 31 * result + (mostProductiveDay?.hashCode() ?: 0)
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTeamStatisticsData(
    teamList: List<Team>,
    taskList: List<Task>,
    memberList: List<Member>,
    teamId: Long
): TeamStatisticsData {
    val teamTaskList = taskList.filter { it.idTeam == teamId }

    val toDoTasks = teamTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && !isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val completedOnTimeTasks = teamTaskList.count { it ->
        it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date <= it.dueDate
    }.toFloat()
    val completedBehindScheduleTasks = teamTaskList.count { it ->
        it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date > it.dueDate
    }.toFloat()
    val overdueTasks = teamTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val totStatesValues = floatArrayOf(
        toDoTasks, completedOnTimeTasks, completedBehindScheduleTasks, overdueTasks
    )

    val currentMemberIds =
        teamList.first { it.id == teamId }.members.filter { it.isMember }.map { it.idMember }
            .sorted()
    val currentMembers =
        memberList.filter { it.id in currentMemberIds }.distinct().sortedBy { it.id }
    val currentMemberTeamTaskList = teamTaskList.filter { task ->
        task.members.any { member -> member in currentMemberIds }
    }
    val currentMemberMap =
        teamList.first { it.id == teamId }.members.filter { it.isMember }.sortedBy {
            it.idMember
        }
            .associate { it.idMember to 0 }

    val currentMembersCompletedOnTimeTasks = currentMemberTeamTaskList
        .filter { it -> it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date <= it.dueDate }
        .flatMap { task ->
            task.members.filter { it in currentMemberIds }.map { member -> member to task }
        }
        .groupingBy { it.first }
        .eachCount()
    val currentMembersCompletedOnTimeTasksWithZeros = currentMemberMap.toMutableMap().apply {
        currentMembersCompletedOnTimeTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val currentMembersCompletedBehindScheduleTasks = currentMemberTeamTaskList
        .filter { it -> it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date > it.dueDate }
        .flatMap { task ->
            task.members.filter { it in currentMemberIds }.map { member -> member to task }
        }
        .groupingBy { it.first }
        .eachCount()
    val currentMembersCompletedBehindScheduleTasksWithZeros =
        currentMemberMap.toMutableMap().apply {
            currentMembersCompletedBehindScheduleTasks.forEach { (key, value) ->
                this[key] = value
            }
        }
    val currentMembersOverdueTasks = currentMemberTeamTaskList
        .filter {
            (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && isBeforeCurrentDate(
                it.dueDate
            )
        }
        .flatMap { task -> task.members.map { member -> member to task } }
        .groupingBy { it.first }
        .eachCount()
    val currentMembersOverdueTasksWithZeros = currentMemberMap.toMutableMap().apply {
        currentMembersOverdueTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val currentMembersToDoTasks = currentMemberTeamTaskList
        .filter {
            (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && !isBeforeCurrentDate(
                it.dueDate
            )
        }
        .flatMap { task -> task.members.map { member -> member to task } }
        .groupingBy { it.first }
        .eachCount()
    val currentMembersToDoTasksWithZeros = currentMemberMap.toMutableMap().apply {
        currentMembersToDoTasks.forEach { (key, value) ->
            this[key] = value
        }
    }

    val pastMemberIds =
        teamList.first { it.id == teamId }.members.filter { !it.isMember }.map { it.idMember }
    val pastMembers = memberList.filter { it.id in pastMemberIds }.distinct()
    val pastMemberTeamTaskList = teamTaskList.filter { task ->
        task.members.any { member -> member in pastMemberIds }
    }
    val pastMemberMap = teamList.first { it.id == teamId }.members.filter { !it.isMember }
        .associate { it.idMember to 0 }

    val pastMembersCompletedOnTimeTasks = pastMemberTeamTaskList
        .filter { it -> it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date <= it.dueDate }
        .flatMap { task ->
            task.members.filter { it in pastMemberIds }.map { member -> member to task }
        }
        .groupingBy { it.first }
        .eachCount()
    val pastMembersCompletedOnTimeTasksWithZeros = pastMemberMap.toMutableMap().apply {
        pastMembersCompletedOnTimeTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val pastMembersCompletedBehindScheduleTasks = pastMemberTeamTaskList
        .filter { it -> it.state == State.COMPLETED && it.histories.first { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }.date > it.dueDate }
        .flatMap { task ->
            task.members.filter { it in pastMemberIds }.map { member -> member to task }
        }
        .groupingBy { it.first }
        .eachCount()
    val pastMembersCompletedBehindScheduleTasksWithZeros = pastMemberMap.toMutableMap().apply {
        pastMembersCompletedBehindScheduleTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val pastMembersOverdueTasks = pastMemberTeamTaskList
        .filter {
            (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && isBeforeCurrentDate(
                it.dueDate
            )
        }
        .flatMap { task -> task.members.map { member -> member to task } }
        .groupingBy { it.first }
        .eachCount()
    val pastMembersOverdueTasksWithZeros = pastMemberMap.toMutableMap().apply {
        pastMembersOverdueTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val pastMembersToDoTasks = pastMemberTeamTaskList
        .filter {
            (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && !isBeforeCurrentDate(
                it.dueDate
            )
        }
        .flatMap { task -> task.members.map { member -> member to task } }
        .groupingBy { it.first }
        .eachCount()
    val pastMembersToDoTasksWithZeros = pastMemberMap.toMutableMap().apply {
        pastMembersToDoTasks.forEach { (key, value) ->
            this[key] = value
        }
    }

    val thisWeekDays = getDaysOfCurrentWeek()
    val initialMap = thisWeekDays.associateWith { 0 }

    val thisWeekTasks = teamTaskList.filter {
        thisWeekDays.contains(
            (getDateWithoutTime(it.dueDate)).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )
    }.groupingBy {
        (getDateWithoutTime(it.dueDate)).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.eachCount()
    val thisWeekTasksWithZeros = initialMap.toMutableMap().apply {
        thisWeekTasks.forEach { (key, value) ->
            this[key] = value
        }
    }
    val thisWeekCompletedTasks = teamTaskList.filter {
        thisWeekDays.contains(
            (getDateWithoutTime(it.dueDate)).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        ) && it.state == State.COMPLETED
    }.groupingBy {
        (getDateWithoutTime(it.dueDate)).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.eachCount()
    val thisWeekCompletedTasksWithZeros = initialMap.toMutableMap().apply {
        thisWeekCompletedTasks.forEach { (key, value) ->
            this[key] = value
        }
    }

    // Most productive day
    val mostProductiveDay = teamTaskList.filter { it.state == State.COMPLETED }
        .map { task ->
            val completionDate =
                task.histories.filter { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }
                    .maxByOrNull { it.date }?.date
            val date = getDateWithoutTime(completionDate!!)
            Triple(task.id, task.title, date)
        }
        .groupBy { it.third }
        .mapValues { (_, value) -> value.size }
        .maxByOrNull { it.value }

    // Average tasks rate
    val avgTasksRate = teamTaskList.filter { it.state == State.COMPLETED }
        .map { task ->
            task.review
        }.average()

    val fullTimeTeamMembers = teamList.first { it.id == teamId }.members.count {
        it.timeParticipation == TimeParticipation.FULL_TIME
    }
    val partTimeMembers = teamList.first { it.id == teamId }.members.count {
        it.timeParticipation == TimeParticipation.PART_TIME
    }
    val totParticipationTimeValues = floatArrayOf(
        fullTimeTeamMembers.toFloat(), partTimeMembers.toFloat()
    )

    // Most productive members -> members of the team with the highest number of completed tasks for that team
    val tasksByMember = taskList.filter { it.state == State.COMPLETED && it.idTeam == teamId }
        .flatMap { task -> task.members.map { member -> member to task } }
        .groupingBy { it.first }
        .eachCount()

    val maxTasks = tasksByMember.values.maxOrNull()

    val mostProductiveMembers = tasksByMember.filter { it.value == maxTasks }

    return TeamStatisticsData(
        totStatesValues,
        currentMembersCompletedOnTimeTasksWithZeros,
        currentMembersCompletedBehindScheduleTasksWithZeros,
        currentMembersOverdueTasksWithZeros,
        currentMembersToDoTasksWithZeros,
        currentMembers,
        pastMembersCompletedOnTimeTasksWithZeros,
        pastMembersCompletedBehindScheduleTasksWithZeros,
        pastMembersOverdueTasksWithZeros,
        pastMembersToDoTasksWithZeros,
        pastMembers,
        thisWeekDays,
        thisWeekTasks,
        thisWeekTasksWithZeros,
        thisWeekCompletedTasksWithZeros,
        mostProductiveDay,
        teamTaskList,
        avgTasksRate,
        totParticipationTimeValues,
        mostProductiveMembers
    )
}