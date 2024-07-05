package it.polito.teamhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.teamhub.dataClass.member.Gender
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.Attachment
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.Comment
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.model.CategoryModel
import it.polito.teamhub.model.MemberModel
import it.polito.teamhub.model.TagModel
import it.polito.teamhub.model.TaskModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskViewModel(
    private val taskModel: TaskModel,
    private val tagModel: TagModel,
    private val categoryModel: CategoryModel,
    memberModel: MemberModel,
) : ViewModel() {
    val memberLogged = memberModel.memberLogged
    var isEditing by mutableStateOf(false)
    var idEdit by mutableLongStateOf((-1).toLong())
    private val formatter = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var idTask by mutableLongStateOf(-1L)

    //function db

    fun getTasks() = taskModel.getTasks()
    fun getTasksByTeamId(teamId: Long) = taskModel.getTasksByTeamId(teamId)
    fun getTasksCompletedByTeamId(teamId: Long) = taskModel.getTasksCompletedByTeamId(teamId)
    fun getMemberLoggedNotCompletedTasks() = taskModel.getMemberLoggedNotCompletedTasks()
    fun getTaskById(id: Long) = taskModel.getTaskById(id)

    private fun addTask(task: Task) = taskModel.addTask(task)

    fun updateTask(id: Long) = taskModel.updateTask(id, currentTask = this.currentTask.value)

    fun addComment(id: Long, comment: Comment, history: History) =
        taskModel.addComment(id, comment, history)


    private fun deleteTagById(id: Long, name: String, memberId: Long) =
        taskModel.deleteTagById(id, name, memberId)

    fun deleteTag(id: Long, name: String, memberId: Long) {
        deleteTagById(id, name, memberId)
        tagModel.deleteTagList(id)
    }

    private fun deleteCategoryById(id: Long, name: String, memberId: Long) {
        taskModel.deleteCategoryById(id, name, memberId)
    }

    fun deleteCategory(id: Long, name: String, memberId: Long) {
        deleteCategoryById(id, name, memberId)
        categoryModel.deleteCategoryList(id)
    }

    private val emptyMember = Member(
        fullname = "",
        jobTitle = "",
        description = "",
        nickname = "",
        email = "",
        location = "",
        phoneNumber = null,
        birthDate = null,
        gender = Gender.PREFER_NOT_TO_SAY,
        userImage = "",
        color = mutableListOf(),
        chats = mutableListOf()
    )

    var currentTask = mutableStateOf(
        Task(
            "",
            "",
            mutableListOf(),
            null,
            State.PENDING,
            Priority.LOW,
            mutableListOf(),
            Date(),
            Date(),
            mutableListOf(Comment("", emptyMember.id, Date())),
            mutableListOf(
                History(emptyMember.id, Action.CREATION, Date(), "")
            ),
            0,
            mutableMapOf(),
            mutableListOf()
        )
    )
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set


    var tag by mutableStateOf(mutableListOf<Tag>())
        private set

    var category by mutableStateOf(Category("", -1))
        private set

    var state by mutableStateOf(State.PENDING)
        private set

    var priority by mutableStateOf(Priority.LOW)
        private set

    var teamMembers by mutableStateOf(mutableListOf<Long>())
        private set

    var dueDate: Date? by mutableStateOf(
        null
    )
        private set

    var attachments by mutableStateOf(
        mutableListOf<
                Attachment>()
    )
        private set

    fun addAttachment(attachments: MutableList<Attachment>) {
        val newList = this.attachments.toMutableList()
        newList.addAll(attachments)
        this.attachments = newList
    }

    fun deleteAttachment(index: Int) {
        val newList = this.attachments.toMutableList()
        newList.removeAt(index)
        this.attachments = newList
    }

    fun addAttachmentFromButton(task: Task, attachment: List<Attachment>, memberId: Long) =
        taskModel.addAttachment(task, attachment, memberId)

    var url by mutableStateOf(mutableListOf<String>())
        private set

    fun addUrl(url: String) {
        val newList = this.url.toMutableList()
        newList.add(url)
        this.url = newList
    }

    fun deleteUrl(index: Int) {
        val newList = this.url.toMutableList()
        newList.removeAt(index)
        this.url = newList
    }

    var titleError by mutableStateOf("")
        private set

    var descriptionError by mutableStateOf("")
        private set

    private var tagError by mutableStateOf("")

    private var categoryError by mutableStateOf("")

    private var stateError by mutableStateOf("")

    private var priorityError by mutableStateOf("")

    private var teamMembersError by mutableStateOf("")

    var dueDateError by mutableStateOf("")
        private set

    private val _comments = MutableStateFlow(listOf<Comment>())
    val comments: StateFlow<List<Comment>> = _comments

    fun updateAttachment(attachments: MutableList<Attachment>) {
        this.attachments = attachments
    }

    fun updateTitle(t: String) {
        title = t
    }

    fun updateDescription(d: String) {
        description = d
    }

    var deletedTags = mutableListOf<Tag>()
    fun updateTag(newTags: MutableList<Tag>) {
        if (newTags.isNotEmpty()) {
            if (newTags[0].name == "") {
                tag.clear()
            } else {
                tag = newTags

            }
        } else {
            tag.clear()
        }
    }

    var deletedCategories = mutableListOf<Category>()
    fun updateCategory(newCategory: Category) {
        category = if (newCategory.id == (-1).toLong()) {
            Category("", -1)
        } else {
            newCategory
        }
    }

    fun updateStateById(id: Long, s: State) = taskModel.updateStateById(id, s, currentTask)

    fun updateStateCreation(s: State) {
        state = s
    }

    fun updatePriority(p: Priority) {
        priority = p
    }

    fun updateTeamMembers(mIds: MutableList<Long>) {
        if (mIds.isEmpty()) {
            teamMembers.clear()
        } else {
            teamMembers = mIds.toMutableList()
            //teamMembers = memberList.filter { it.id in mIds }.toMutableList()
        }
    }

    fun updateUrl(url: MutableList<String>) {
        this.url = url
    }

    fun updateDueDate(d: Date?) {
        dueDate = d
    }

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    init {
        viewModelScope.launch {
            taskModel.getTaskById(idTask).collect { newTask ->
                _task.value = newTask
            }
        }
    }


    fun updateIdTask(newId: Long) {
        idTask = newId
        viewModelScope.launch {
            taskModel.getTaskById(idTask).collect { newTask ->
                _task.value = newTask
                _comments.value = newTask.comments
            }

        }
    }

    //validation functions
    private fun checkTitle() {
        if (title.isBlank()) {
            titleError = "Title cannot be empty"
        } else {
            title = title.trim().split(" ").filter { it.isNotEmpty() }
                .joinToString(" ") { it.trim() }
            titleError = ""
        }
    }

    private fun checkDescription() {
        if (description.isBlank()) {
            descriptionError = "Description cannot be empty"
        } else {
            description = description.trim().split(" ").filter { it.isNotEmpty() }
                .joinToString(" ") { it.trim() }
            descriptionError = ""
        }
    }

    private fun checkDueDate() {
        dueDateError = if (dueDate == null) {
            "Due date cannot be empty"
        } else {
            ""
        }
    }

    private fun checkMembers() {
        if (teamMembers.isEmpty()) {
            state = State.PENDING
        } else {
            teamMembersError = ""
        }
    }

    fun validate(teamId: Long): Boolean {
        checkTitle()
        checkDescription()
        checkDueDate()
        checkMembers()
        var tagIds = mutableListOf<Long>()
        if (tag.isNotEmpty()) {
            tagIds = tag.map { it.id }.toMutableList()
        }
        if (titleError.isBlank() && descriptionError.isBlank() && dueDateError.isBlank() && teamMembersError.isBlank()) {
            if (!isEditing) {
                val newTask = Task(
                    title = title,
                    description = description,
                    tag = tagIds,
                    category = if (category.name != "") category.id else -1L,
                    state = state,
                    priority = priority,
                    members = teamMembers,
                    creationDate = Date(),
                    dueDate = dueDate!!,
                    comments = mutableListOf(),
                    histories = mutableListOf(
                        History(
                            memberLogged.value.id,
                            Action.CREATION,
                            Calendar.getInstance().time,
                            Action.CREATION.getAction("")
                        )
                    ),
                    idTeam = teamId,
                    mapReview = mutableMapOf(),
                    attachment = attachments,
                    url = url
                )
                addTask(newTask)
            } else {
                val removedMembers = getRemovedMembers()
                val addedMembers = getAddedMembers()
                val updateDescription = updateHistoryMessage()
                val newHistory = History(
                    memberLogged.value.id,
                    Action.UPDATE_TASK,
                    Calendar.getInstance().time,
                    Action.UPDATE_TASK.getAction(updateDescription),
                )
                val updateMembersHistory = History(
                    memberLogged.value.id,
                    Action.UPDATE_MEMBERS,
                    Calendar.getInstance().time,
                    "",
                    addedMembers,
                    removedMembers
                )

                val newHistories = mutableListOf<History>()

                if (addedMembers.isNotEmpty() || removedMembers.isNotEmpty()) {
                    newHistories.add(updateMembersHistory)
                }
                if (updateDescription.isNotEmpty()) {
                    newHistories.add(newHistory)
                }

                val updatedTask = currentTask.value.copy(
                    title = title,
                    description = description,
                    tag = tagIds,
                    category = if (category.name != "") category.id else -1L,
                    state = state,
                    priority = priority,
                    members = teamMembers,
                    dueDate = dueDate!!,
                    histories = (currentTask.value.histories.toMutableList() + newHistories).toMutableList(),
                    url = url,
                    attachment = attachments
                )
                updatedTask.id = idEdit
                currentTask.value = updatedTask
                updateTask(idEdit)

                isEditing = false
            }
            cleanVariables()
            return true
        } else {
            return false
        }
    }

    private fun getAddedMembers(): List<Long> {
        val task = currentTask.value
        val addedMembers = mutableListOf<Long>()
        teamMembers.forEach {
            if (!task.members.contains(it)) {
                addedMembers.add(it)
            }
        }
        return addedMembers
    }

    private fun getRemovedMembers(): List<Long> {
        val task = currentTask.value
        val removedMembers = mutableListOf<Long>()
        task.members.forEach {
            if (!teamMembers.contains(it)) {
                removedMembers.add(it)
            }
        }
        return removedMembers
    }

    private fun updateHistoryMessage(): String {
        val task = currentTask.value
        var description = ""
        if (task.title != title) {
            description += "Title updated to: \"$title\"\n"
        }
        if (task.description != this.description) {
            description += "Description updated to: \"${this.description}\"\n"
        }
        var tagIds = mutableListOf<Long>()
        if (tag.isNotEmpty()) {
            tagIds = tag.map { it.id }.toMutableList()
        }
        if (task.tag != tagIds) {
            var tagsAdded = ""
            var tagsRemoved = ""
            tagIds.forEach {
                if (!task.tag.contains(it)) {
                    val tagName = tag.firstOrNull { tag -> tag.id == it }
                    tagsAdded += "${tagName?.name}, "
                }
            }
            task.tag.forEach { it ->
                if (!tagIds.contains(it)) {
                    if (!deletedTags.map { it.id }.contains(it)) {
                        val tagName = tag.firstOrNull { tag -> tag.id == it }
                        tagsRemoved += "${tagName?.name}, "
                    }
                }
            }
            if (tagsAdded.isNotEmpty()) {
                tagsAdded = tagsAdded.dropLast(2)
                description += "Tags added: $tagsAdded\n"
            }
            if (tagsRemoved.isNotEmpty()) {
                tagsRemoved = tagsRemoved.dropLast(2)
                description += "Tags removed: $tagsRemoved\n"
            }
        }


        if (task.category != category.id) {
            if (category.name != "") {
                val categoryString = category.name
                description += "Category updated to: $categoryString\n"
            } else {
                if (task.category != -1L && !deletedCategories.contains(category))
                    description += "Category ${category.name} removed\n"
            }
        }

        if (task.state != state) {
            val stateString = state.getStateString()
            description += "State updated to $stateString\n"
        }
        if (task.priority != priority) {
            description += "Priority updated to $priority\n"
        }
        if (task.dueDate != dueDate) {
            val date = dueDate?.let { formatter.format(it) }
            description += "Due date updated to $date\n"
        }
        if (task.url != url) {
            var urlsAdded = ""
            var urlsRemoved = ""
            url.forEach {
                if (!task.url.contains(it)) {
                    urlsAdded += "$it, "
                }
            }
            task.url.forEach {
                if (!url.contains(it)) {
                    urlsRemoved += "$it, "
                }
            }
            if (urlsAdded.isNotEmpty()) {
                urlsAdded = urlsAdded.dropLast(2)
                description += "Urls added: $urlsAdded\n"
            }
            if (urlsRemoved.isNotEmpty()) {
                urlsRemoved = urlsRemoved.dropLast(2)
                description += "Urls removed: $urlsRemoved\n"
            }
        }
        if (task.attachment != attachments) {
            var attachmentsAdded = ""
            var attachmentsRemoved = ""
            attachments.forEach {
                if (!task.attachment.contains(it)) {
                    attachmentsAdded += "${it.name}, "
                }
            }
            task.attachment.forEach {
                if (!attachments.contains(it)) {
                    attachmentsRemoved += "${it.name}, "
                }
            }
            if (attachmentsAdded.isNotEmpty()) {
                attachmentsAdded = attachmentsAdded.dropLast(2)
                description += "Attachments added: $attachmentsAdded\n"
            }
            if (attachmentsRemoved.isNotEmpty()) {
                attachmentsRemoved = attachmentsRemoved.dropLast(2)
                description += "Attachments removed: $attachmentsRemoved\n"
            }
        }
        return description
    }

    fun cleanVariables() {
        title = ""
        description = ""
        tag = mutableListOf()
        category = Category("", -1)
        state = State.PENDING
        priority = Priority.LOW
        teamMembers = mutableListOf()
        dueDate = null
        titleError = ""
        descriptionError = ""
        tagError = ""
        categoryError = ""
        stateError = ""
        priorityError = ""
        teamMembersError = ""
        dueDateError = ""
        isEditing = false
        idEdit = (-1).toLong()
        url = mutableListOf()
        attachments = mutableListOf()
    }

    fun deleteTaskById(id: Long) = taskModel.deleteTaskById(id)

    fun updateReviewByTaskId(taskId: Long, review: Float) =
        taskModel.updateReviewByTaskId(taskId, review)

}
