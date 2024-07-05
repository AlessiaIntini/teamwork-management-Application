package it.polito.teamhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.model.CategoryModel
import it.polito.teamhub.model.MemberModel
import it.polito.teamhub.model.TagModel
import it.polito.teamhub.model.TaskModel
import it.polito.teamhub.model.TeamModel
import it.polito.teamhub.ui.theme.PurpleBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date


class TeamViewModel(
    private val teamModel: TeamModel,
    private val taskModel: TaskModel,
    private val tagModel: TagModel,
    private val categoryModel: CategoryModel,
    private val memberModel: MemberModel
) : ViewModel() {
    val memberLogged = teamModel.memberLogged

    private val _teamList = MutableStateFlow(listOf<Team>())
    val teamList: StateFlow<List<Team>> = _teamList

    init {
        viewModelScope.launch {
            teamModel.getTeamsByMemberId(memberLogged.value.id).collect { newTeams ->
                _teamList.value = newTeams
            }
        }
    }

    fun updateTeamList(id: Long) {
        viewModelScope.launch {
            teamModel.getTeamsByMemberId(id).collect { newTeams ->
                _teamList.value = newTeams
            }
        }
    }


    fun getTeamById(id: Long): Flow<Team> = teamModel.getTeamById(id)

    private fun updateTeam(team: Team, id: Long) {
        val oldMembers = listMember
        val newMembers = team.members
        val removedMembers = oldMembers.filter { !newMembers.contains(it) }
        removedMembers.forEach {
            removeMemberFromTasks(it.idMember, id, it.fullname, true)
            team.members.add(
                TeamMember(
                    it.idMember,
                    it.fullname,
                    Role.MEMBER,
                    TimeParticipation.FULL_TIME,
                    false
                )
            )

        }
        teamModel.updateTeam(team, id)
    }

    fun getAllTeams() = teamModel.getAllTeams()

    private fun addTeam(team: Team) = teamModel.addTeam(team)

    private fun deleteTeamById(id: Long) = teamModel.deleteTeam(id)

    private fun deleteTasksByTeamId(id: Long) = taskModel.deleteTasksByTeamId(id)

    private fun deleteTagsOfTeam(teamId: Long) = tagModel.deleteTagsOfTeam(teamId)

    private fun deleteCategoriesOfTeam(teamId: Long) = categoryModel.deleteCategoriesOfTeam(teamId)

    private fun deleteChatByTeamId(teamId: Long) = teamModel.deleteChatByTeamId(teamId)

    private fun deleteChat(memberId: Long, teamId: Long) = memberModel.deleteChat(memberId, teamId)

    private fun getChatsByTeamId(teamId: Long) = teamModel.getChatIdByTeamId(teamId)

    fun deleteTeam(id: Long) {
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            var team: Team?
            getTeamById(id).collect { value ->
                getChatsByTeamId(id).collect { chatId ->
                    team = value
                    team?.members?.forEach { member ->
                        deleteChat(member.idMember, chatId)
                    }
                    deleteChatByTeamId(id)
                    deleteTeamById(id)
                    deleteTasksByTeamId(id)
                    deleteTagsOfTeam(id)
                    deleteCategoriesOfTeam(id)
                }

            }
        }
    }


    var nameValue by mutableStateOf("")
        private set

    var descriptionValue by mutableStateOf("")
        private set

    var teamImage by mutableStateOf("")

    val defaultImage: Int = R.drawable.group_2

    var defaultColor = PurpleBlue.toArgb().toLong()

    fun updateImageTeam(uri: String) {
        previousImageProfile = teamImage
        teamImage = uri
        imageChanged = true
    }

    fun deleteImageTeam() {
        previousImageProfile = teamImage
        teamImage = ""
        imageChanged = true
    }

    var photo by mutableStateOf(false)
        private set

    fun changePhoto(p: Boolean) {
        photo = p
    }


    var listMember by mutableStateOf(mutableListOf<TeamMember>())
        private set

    fun addNewMember(newList: List<TeamMember>) {
        listMember = newList.toMutableList()
    }

    fun addMember(member: TeamMember, teamId: Long) =
        teamModel.addMember(member, teamId)

    private fun removeMemberById(idMember: Long, teamId: Long) =
        teamModel.removeMemberById(idMember, teamId)

    private fun removeMemberFromTasks(
        idMember: Long,
        teamId: Long,
        fullname: String,
        removed: Boolean
    ) =
        taskModel.removeMemberFromTasks(idMember, teamId, fullname, removed)

    fun removeMember(
        id: Long,
        teamId: Long,
        fullname: String,
        isLastMember: Boolean,
        removed: Boolean
    ) {
        removeMemberFromTasks(id, teamId, fullname, removed)
        removeMemberById(id, teamId)
        if (isLastMember) {
            teamModel.deleteTeam(teamId)
        }
    }

    fun updateRoleMember(role: Role, id: Long) {
        listMember.find { it.idMember == id }?.role = role
    }

    fun changeRole(role: Role?, id: Long, teamId: Long) = teamModel.changeRole(role, id, teamId)

    fun updateTimeParticipation(timeParticipation: TimeParticipation, id: Long) {
        listMember.find { it.idMember == id }?.timeParticipation = timeParticipation
    }

    fun changeTimeParticipation(timeParticipation: TimeParticipation, id: Long, teamId: Long) =
        teamModel.changeTimeParticipation(timeParticipation, id, teamId)

    fun updateNameTeam(name: String) {
        nameValue = name
    }

    fun updateDescriptionTeam(description: String) {
        descriptionValue = description
    }


    var nameError by mutableStateOf("")
        private set

    var descriptionError by mutableStateOf("")
        private set

    var memberError by mutableStateOf("")
        private set

    private var previousImageProfile by mutableStateOf("")
    private var imageChanged by mutableStateOf(false)

    private fun checkNameTeam() {
        if (nameValue.isEmpty()) {
            nameError = "Name is required"
        } else {
            nameError = ""
            nameValue = nameValue.trim()
        }
    }

    private fun checkDescription() {
        if (descriptionValue.isBlank()) {
            descriptionError = "Description cannot be blank"
        } else {
            descriptionValue = descriptionValue.trim()
            descriptionError = ""
        }
    }

    private fun checkMember() {
        memberError = if (listMember.any { it.role == null }) {
            "All members must have a role"
        } else if (!listMember.any { it.role == Role.ADMIN }) {
            "At least one admin is required"
        } else {
            ""
        }
    }

    fun setCurrentValue(team: Team?) {
        if (team == null) return
        nameValue = team.name
        descriptionValue = team.description
        defaultColor = team.longColor!!
        teamImage = team.imageTeam
        listMember = team.members.filter { it.isMember }.toMutableList()
    }

    fun validate(teamId: Long? = null): Boolean {
        checkNameTeam()
        checkDescription()
        checkMember()
        if (nameError.isBlank() && descriptionError.isBlank() && memberError.isBlank()) {
            val newTeam = Team(
                name = nameValue,
                description = descriptionValue,
                imageTeam = teamImage,
                longColor = defaultColor,
                members = if (listMember.isEmpty()) mutableListOf(
                    TeamMember(
                        0,
                        memberLogged.value.fullname,
                        Role.ADMIN,
                        TimeParticipation.FULL_TIME
                    )
                ) else listMember,
                creationDate = Date()
            )
            if (teamId == null) {
                addTeam(newTeam)
                cleanVariables()
                return true
            } else {
                updateTeam(newTeam, teamId)
                cleanVariables()
                return true
            }

        }
        return false
    }


    fun cleanVariables() {
        nameValue = ""
        descriptionValue = ""
        teamImage = ""
        listMember = mutableListOf()
        nameError = ""
        descriptionError = ""
        memberError = ""
    }

    fun getIdsMemberTeam(listTeam: MutableList<TeamMember>): List<Long> {
        return listTeam.map { it.idMember }.filter { it != memberLogged.value.id }
    }

    fun getRoleOfMemberLoggedByTeamId(idTeam: Long) =
        teamModel.getRoleOfMemberLoggedByTeamId(idTeam)

}