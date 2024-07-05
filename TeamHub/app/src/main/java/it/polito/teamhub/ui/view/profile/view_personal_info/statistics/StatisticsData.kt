package it.polito.teamhub.ui.view.profile.view_personal_info.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.utils.getDateWithoutTime
import it.polito.teamhub.utils.isBeforeCurrentDate
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class StatisticsData(
    val personalTaskList: List<Task>,
    val totStatesValues: FloatArray,
    val categories: List<String>,
    val totCategoriesValues: FloatArray,
    val tags: List<String>,
    val totTagsValues: FloatArray,
    val currentTeams: List<Team>,
    val totTasksPerCurrentTeam: FloatArray,
    val totCompletedTasksPerCurrentTeam: FloatArray,
    val pastTeams: List<Team>,
    val totTasksPerPastTeam: FloatArray,
    val totCompletedTasksPerPastTeam: FloatArray,
    val mostProductiveDay: Map.Entry<Date?, Int>?,
    val thisMonthTaskList: List<Task>,
    val thisMonthStatesValues: FloatArray,
    val thisMonthCategories: List<String>,
    val thisMonthCategoriesValues: FloatArray,
    val thisMonthTags: List<String>,
    val thisMonthTagsValues: FloatArray,
    val thisMonthTasksPerTeam: FloatArray,
    val thisMonthCompletedTasksPerTeam: FloatArray,
    val thisMonthMostProductiveDay: Map.Entry<Date?, Int>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatisticsData

        if (!totStatesValues.contentEquals(other.totStatesValues)) return false
        if (categories != other.categories) return false
        if (!totCategoriesValues.contentEquals(other.totCategoriesValues)) return false
        if (tags != other.tags) return false
        if (!totTagsValues.contentEquals(other.totTagsValues)) return false
        if (currentTeams != other.currentTeams) return false
        if (!totTasksPerCurrentTeam.contentEquals(other.totTasksPerCurrentTeam)) return false
        if (!totCompletedTasksPerCurrentTeam.contentEquals(other.totCompletedTasksPerCurrentTeam)) return false
        if (mostProductiveDay != other.mostProductiveDay) return false
        if (!thisMonthStatesValues.contentEquals(other.thisMonthStatesValues)) return false
        if (thisMonthCategories != other.thisMonthCategories) return false
        if (!thisMonthCategoriesValues.contentEquals(other.thisMonthCategoriesValues)) return false
        if (thisMonthTags != other.thisMonthTags) return false
        if (!thisMonthTagsValues.contentEquals(other.thisMonthTagsValues)) return false
        if (!thisMonthTasksPerTeam.contentEquals(other.thisMonthTasksPerTeam)) return false
        if (!thisMonthCompletedTasksPerTeam.contentEquals(other.thisMonthCompletedTasksPerTeam)) return false
        if (thisMonthMostProductiveDay != other.thisMonthMostProductiveDay) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totStatesValues.contentHashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + totCategoriesValues.contentHashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + totTagsValues.contentHashCode()
        result = 31 * result + currentTeams.hashCode()
        result = 31 * result + totTasksPerCurrentTeam.contentHashCode()
        result = 31 * result + totCompletedTasksPerCurrentTeam.contentHashCode()
        result = 31 * result + (mostProductiveDay?.hashCode() ?: 0)
        result = 31 * result + thisMonthStatesValues.contentHashCode()
        result = 31 * result + thisMonthCategories.hashCode()
        result = 31 * result + thisMonthCategoriesValues.contentHashCode()
        result = 31 * result + thisMonthTags.hashCode()
        result = 31 * result + thisMonthTagsValues.contentHashCode()
        result = 31 * result + thisMonthTasksPerTeam.contentHashCode()
        result = 31 * result + thisMonthCompletedTasksPerTeam.contentHashCode()
        result = 31 * result + (thisMonthMostProductiveDay?.hashCode() ?: 0)
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getStatisticsData(
    teamList: List<Team>,
    taskList: List<Task>,
    tagList: List<Tag>,
    categoryList: List<Category>,
    memberId: Long
): StatisticsData {

    val personalTaskList = taskList.filter { it.members.contains(memberId) }

    val toDoTasks = personalTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && !isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val completedOnTimeTasks = personalTaskList.count { task ->
        if (task.state == State.COMPLETED) {
            val completionDate =
                task.histories.firstOrNull { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }?.date
            completionDate != null && completionDate <= task.dueDate
        } else {
            false
        }
    }.toFloat()
    val completedBehindScheduleTasks = personalTaskList.count { task ->
        if (task.state == State.COMPLETED) {
            val completionDate =
                task.histories.firstOrNull { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }?.date
            completionDate != null && completionDate > task.dueDate
        } else {
            false
        }
    }.toFloat()
    val overdueTasks = personalTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val totStatesValues = floatArrayOf(
        toDoTasks, completedOnTimeTasks, completedBehindScheduleTasks, overdueTasks
    )

    // Total tasks per category
    val categories = personalTaskList.mapNotNull { task ->
        categoryList.find { it.id == task.category }?.name
    }.distinct()
    val totCategoriesValues = categories.map { category ->
        personalTaskList.count { task ->
            categoryList.find { it.id == task.category }?.name == category
        }.toFloat()
    }.toFloatArray()

    // Total tasks per tags
    val tags = personalTaskList.flatMap { task ->
        task.tag.mapNotNull { tagId ->
            tagList.find { it.id == tagId }?.name
        }
    }.distinct()

    val totTagsValues = tags.map { tagName ->
        personalTaskList.count { task ->
            task.tag.any { tagId ->
                tagList.find { it.id == tagId }?.name == tagName
            }
        }.toFloat()
    }.toFloatArray()

    // Current teams
    val currentTeams =
        teamList.filter { it -> it.members.any { it.idMember == memberId && it.isMember } }
            .distinct()
    val totTasksPerTeam = currentTeams.map { team ->
        personalTaskList.count { it.idTeam == team.id }.toFloat()
    }.toFloatArray()
    val totCompletedTasksPerTeam = currentTeams.map { team ->
        personalTaskList.count { it.idTeam == team.id && it.state == State.COMPLETED }.toFloat()
    }.toFloatArray()

    // Past teams
    val pastTeams =
        teamList.filter { it -> it.members.any { it.idMember == memberId && !it.isMember } }
            .distinct()
    val totTasksPerPastTeam = pastTeams.map { team ->
        personalTaskList.count { it.idTeam == team.id }.toFloat()
    }.toFloatArray()
    val totCompletedTasksPerPastTeam = pastTeams.map { team ->
        personalTaskList.count { it.idTeam == team.id && it.state == State.COMPLETED }.toFloat()
    }.toFloatArray()

    // Most productive day
    val completedTasks = personalTaskList.filter { it.state == State.COMPLETED }
    val mostProductiveDay = if (completedTasks.isEmpty()) null else
        completedTasks
            .map { task ->
                val completionDate =
                    task.histories.filter { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }
                        .maxByOrNull { it.date }?.date
                val date = if (completionDate != null) getDateWithoutTime(completionDate) else null
                Triple(task.id, task.title, date)
            }
            .groupBy { it.third }
            .mapValues { (_, value) -> value.size }
            .maxByOrNull { it.value }

    // This month
    val currentMonth = LocalDate.now().monthValue
    val thisMonthTaskList = personalTaskList.filter {
        it.creationDate.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDate().monthValue == currentMonth
    }
    val thisMonthToDoTasks = thisMonthTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && !isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val thisMonthCompletedOnTimeTasks = thisMonthTaskList.count { task ->
        if (task.state == State.COMPLETED) {
            val completionDate =
                task.histories.firstOrNull { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }?.date
            completionDate != null && completionDate <= task.dueDate
        } else {
            false
        }
    }.toFloat()
    val thisMonthCompletedBehindScheduleTasks = thisMonthTaskList.count { task ->
        if (task.state == State.COMPLETED) {
            val completionDate =
                task.histories.firstOrNull { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }?.date
            completionDate != null && completionDate > task.dueDate
        } else {
            false
        }
    }.toFloat()
    val thisMonthOverdueTasks = thisMonthTaskList.count {
        (it.state == State.PENDING || it.state == State.ON_HOLD || it.state == State.IN_PROGRESS) && isBeforeCurrentDate(
            it.dueDate
        )
    }.toFloat()
    val thisMonthStatesValues = floatArrayOf(
        thisMonthToDoTasks,
        thisMonthCompletedOnTimeTasks,
        thisMonthCompletedBehindScheduleTasks,
        thisMonthOverdueTasks
    )

    val thisMonthCategories = thisMonthTaskList.mapNotNull { task ->
        categoryList.find { it.id == task.category }?.name
    }.distinct()
    val thisMonthCategoriesValues = thisMonthCategories.map { category ->
        thisMonthTaskList.count { task ->
            categoryList.find { it.id == task.category }?.name == category
        }.toFloat()
    }.toFloatArray()

    val thisMonthTags = thisMonthTaskList.flatMap { task ->
        task.tag.mapNotNull { tagId ->
            tagList.find { it.id == tagId }?.name
        }
    }.distinct()
    val thisMonthTagsValues = thisMonthTags.map { tagName ->
        thisMonthTaskList.count { task ->
            task.tag.any { tagId ->
                tagList.find { it.id == tagId }?.name == tagName
            }
        }.toFloat()
    }.toFloatArray()

    val thisMonthTasksPerTeam = currentTeams.map { team ->
        thisMonthTaskList.count { it.idTeam == team.id }.toFloat()
    }.toFloatArray()
    val thisMonthCompletedTasksPerTeam = currentTeams.map { team ->
        thisMonthTaskList.count { it.idTeam == team.id && it.state == State.COMPLETED }.toFloat()
    }.toFloatArray()

    // Most productive day
    val thisMonthCompletedTasks = thisMonthTaskList.filter { it.state == State.COMPLETED }
    val thisMonthMostProductiveDay = if (thisMonthCompletedTasks.isEmpty()) null else
        thisMonthCompletedTasks
            .map { task ->
                val completionDate =
                    task.histories.filter { it.description == "Updated Status to: \"Completed\"" || it.description == "State updated to Completed\n" }
                        .maxByOrNull { it.date }?.date
                val date = if (completionDate != null) getDateWithoutTime(completionDate) else null
                Triple(task.id, task.title, date)
            }
            .groupBy { it.third }
            .mapValues { (_, value) -> value.size }
            .maxByOrNull { it.value }

    return StatisticsData(
        personalTaskList,
        totStatesValues,
        categories,
        totCategoriesValues,
        tags,
        totTagsValues,
        currentTeams,
        totTasksPerTeam,
        totCompletedTasksPerTeam,
        pastTeams,
        totTasksPerPastTeam,
        totCompletedTasksPerPastTeam,
        mostProductiveDay,
        thisMonthTaskList,
        thisMonthStatesValues,
        thisMonthCategories,
        thisMonthCategoriesValues,
        thisMonthTags,
        thisMonthTagsValues,
        thisMonthTasksPerTeam,
        thisMonthCompletedTasksPerTeam,
        thisMonthMostProductiveDay
    )
}