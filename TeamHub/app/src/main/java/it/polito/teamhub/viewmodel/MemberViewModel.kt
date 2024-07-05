package it.polito.teamhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.teamhub.dataClass.member.Gender
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.model.MemberModel
import it.polito.teamhub.ui.theme.gradientPairList
import java.util.Date

class MemberViewModel(
    private val memberModel: MemberModel,
) : ViewModel() {

    val memberLogged = memberModel.memberLogged

    var fullNameValue by mutableStateOf("")
        private set
    var initialsName by mutableStateOf("")
        private set
    var jobTitleValue by mutableStateOf("")
        private set
    var descriptionValue by mutableStateOf("")
        private set
    var nicknameValue by mutableStateOf("")
        private set
    var emailValue by mutableStateOf("")
        private set
    var locationValue by mutableStateOf("")
        private set
    var phoneNumberValue by mutableStateOf<String?>("")
        private set
    var birthDateValue by mutableStateOf<Date?>(
        null
    )
        private set
    var genderValue by mutableStateOf(Gender.PREFER_NOT_TO_SAY)
        private set

    var userImage by mutableStateOf("")
        private set

    var color by mutableStateOf(gradientPairList[0].toList())
        private set

    var invitationTeam by mutableStateOf<Long?>(null)
        private set

    var invitationRole by mutableStateOf<String?>(null)
        private set

    // Errors
    var fullNameError by mutableStateOf("")
        private set
    var jobTitleError by mutableStateOf("")
        private set
    var descriptionError by mutableStateOf("")
        private set
    var nicknameError by mutableStateOf("")
        private set
    var emailError by mutableStateOf("")
        private set
    var locationError by mutableStateOf("")
        private set
    var phoneNumberError by mutableStateOf("")
        private set
    var birthDateError by mutableStateOf("")
        private set
    var genderError by mutableStateOf("")
        private set


    // Setter functions
    fun setFullName(fn: String) {
        fullNameValue = fn
    }

    fun setNameInitials(i: String) {
        initialsName = i
    }

    private fun setInitialsName() {
        initialsName = fullNameValue.split(" ").let {
            it[0].first().toString() + it[1].first().toString()
        }
    }

    fun setJobTitle(j: String) {
        jobTitleValue = j
    }

    fun setDescription(d: String) {
        descriptionValue = d
    }

    fun setNickname(n: String) {
        nicknameValue = n
    }

    fun setEmail(e: String) {
        emailValue = e
    }

    fun setLocation(l: String) {
        locationValue = l
    }

    fun setPhoneNumber(p: String?) {
        phoneNumberValue = p
    }

    fun setBirthDate(b: Date?) {
        birthDateValue = b
    }

    fun setGender(g: Gender) {
        genderValue = g
    }

    fun setMemberColor(c: List<Long>) {
        color = c
    }

    fun updateInvitationTeam(teamId: Long?) {
        invitationTeam = teamId
    }

    fun updateInvitationRole(role: String?) {
        invitationRole = role
    }

    private var previousImageProfile by mutableStateOf("")
    private var imageChanged by mutableStateOf(false)

    fun updateImageProfile(uri: String) {
        previousImageProfile = userImage
        userImage = uri
        imageChanged = true
    }

    fun deleteImageProfile() {
        previousImageProfile = userImage
        userImage = ""
        imageChanged = true
    }

    var photo by mutableStateOf(false)
        private set

    fun changePhoto(p: Boolean) {
        photo = p
    }

    // Validation functions
    private fun checkFullName() {
        if (fullNameValue.isBlank()) {
            fullNameError = "Full Name cannot be blank"
        } else if (fullNameValue.trim().split(" ").filter { it.isNotEmpty() }.size < 2) {
            fullNameError = "Full Name must contain at least two words"
        } else {
            fullNameValue = fullNameValue.trim().split(" ").filter { it.isNotEmpty() }
                .joinToString(" ") { it.trim() }
            fullNameError = ""
            setInitialsName()
        }
    }

    private fun checkJobTitle() {
        if (jobTitleValue.isBlank()) {
            jobTitleError = "Job title cannot be blank"
        } else {
            jobTitleValue = jobTitleValue.trim().split(" ").filter { it.isNotEmpty() }
                .joinToString(" ") { it.trim() }
            jobTitleError = ""
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

    private fun checkNickname() {
        if (nicknameValue.isBlank()) {
            nicknameError = "Nickname cannot be blank"
        } else if (!nicknameValue.trim().matches("^[a-zA-Z0-9._-]{3,20}\$".toRegex())) {
            nicknameError = "Invalid nickname"
        } else {
            nicknameValue = nicknameValue.trim()
            nicknameError = ""
        }
    }

    private fun checkEmail() {
        if (emailValue.isBlank()) {
            emailError = "Email cannot be blank"
        } else if (!emailValue.trim()
                .matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}".toRegex())
        ) { // Check email with regex
            emailError = "Invalid email address"
        } else {
            emailValue = emailValue.trim()
            emailError = ""
        }
    }

    private fun checkLocation() {
        if (locationValue.isBlank()) {
            locationError = "Location cannot be blank"
        } else {
            locationValue = locationValue.trim()
            locationError = ""
        }
    }

    private fun checkPhoneNumber() {
        if (phoneNumberValue.isNullOrBlank()) {
            phoneNumberError = ""
        } else if (!phoneNumberValue?.trim()?.matches("(\\+\\d{2})?\\d{10}".toRegex())!!) {
            phoneNumberError = "Invalid phone number"
        } else {
            phoneNumberValue = phoneNumberValue?.trim()
            phoneNumberError = ""
        }
    }

    private fun checkBirthDate() {
        birthDateError = if (birthDateValue.toString().isBlank()) {
            ""
        } else {
            ""
        }
    }


    var showPopup by mutableStateOf(false)

    fun validate(memberId: Long? = null, documentId: String? = null, listTeams: List<Long>): Boolean {
        checkFullName()
        checkJobTitle()
        checkDescription()
        checkNickname()
        checkEmail()
        checkLocation()
        checkPhoneNumber()
        checkBirthDate()
        if (fullNameError.isBlank() && jobTitleError.isBlank() && descriptionError.isBlank() && nicknameError.isBlank() && emailError.isBlank() && locationError.isBlank()
            && phoneNumberError.isBlank() && birthDateError.isBlank() && genderError.isBlank()
        ) {
            val updatedMember = Member(
                fullname = fullNameValue,
                jobTitle = jobTitleValue,
                description = descriptionValue,
                nickname = nicknameValue,
                email = emailValue,
                location = locationValue,
                phoneNumber = phoneNumberValue,
                birthDate = birthDateValue,
                gender = genderValue,
                userImage = if (imageChanged) userImage else previousImageProfile,
                color = color.toMutableList()
            )

            if (memberId == null) {
                if (documentId != null) {
                    registerNewMember(updatedMember, documentId)
                    cleanVariables()
                }
            } else {
                updatedMember.id = memberLogged.value.id
                updateMember(updatedMember, listTeams)
            }

            return true
        }
        return false
    }

    private fun cleanVariables() {
        fullNameValue = ""
        jobTitleValue = ""
        descriptionValue = ""
        nicknameValue = ""
        emailValue = ""
        locationValue = ""
        phoneNumberValue = ""
        birthDateValue = null
        genderValue
        userImage = ""
        fullNameError = ""
        jobTitleError = ""
        descriptionError = ""
        nicknameError = ""
        emailError = ""
        locationError = ""
        phoneNumberError = ""
        birthDateError = ""
    }

    fun getMembers() = memberModel.getMembers()

    fun getAllMemberById(idList: MutableList<Long>) = memberModel.getAllMemberById(idList)

    fun getMemberById(id: Long) = memberModel.getMemberById(id)

    private fun registerNewMember(member: Member, documentId: String) =
        memberModel.registerNewMember(member, documentId)

    private fun updateMember(updatedMember: Member, listTeams: List<Long>) = memberModel.updateMember(updatedMember, listTeams)

    fun updateMemberLogged(member: Member) = memberModel.updateMemberLogged(member)

    fun deleteMember(documentId: String, memberId: Long, logout: () -> Unit) =
        memberModel.deleteMember(documentId, memberId, logout)

    fun deleteChat(memberId: Long, chatId: Long) = memberModel.deleteChat(memberId, chatId)

    fun addChat(memberId: Long, chatId: Long, receiverId: Long = -1) =
        memberModel.addChat(memberId, chatId, receiverId)

    fun isMemberAlreadyRegistered(documentId: String) =
        memberModel.isMemberAlreadyRegistered(documentId)
}