package it.polito.teamhub

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.polito.teamhub.model.CategoryModel
import it.polito.teamhub.model.ChatModel
import it.polito.teamhub.model.MemberModel
import it.polito.teamhub.model.TagModel
import it.polito.teamhub.model.TaskModel
import it.polito.teamhub.model.TeamModel
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

class Factory(context: Context) : ViewModelProvider.Factory {
    private val memberModel: MemberModel =
        (context.applicationContext as? TeamHubApplication)?.memberModel
            ?: throw IllegalArgumentException("Bad memberModel application class")

    private val taskModel: TaskModel =
        (context.applicationContext as? TeamHubApplication)?.taskModel
            ?: throw IllegalArgumentException("Bad taskModel application class")

    private val teamModel: TeamModel =
        (context.applicationContext as? TeamHubApplication)?.teamModel
            ?: throw IllegalArgumentException("Bad teamModel application class")

    private val tagModel: TagModel = (context.applicationContext as? TeamHubApplication)?.tagModel
        ?: throw IllegalArgumentException("Bad tagModel application class")

    private val categoryModel: CategoryModel =
        (context.applicationContext as? TeamHubApplication)?.categoryModel
            ?: throw IllegalArgumentException("Bad categoryModel application class")

    private val chatModel: ChatModel =
        (context.applicationContext as? TeamHubApplication)?.chatModel
            ?: throw IllegalArgumentException("Bad chatModel application class")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MemberViewModel::class.java) -> MemberViewModel(
                memberModel,
            ) as T

            modelClass.isAssignableFrom(TaskViewModel::class.java) -> TaskViewModel(
                taskModel, tagModel,
                categoryModel, memberModel
            ) as T


            modelClass.isAssignableFrom(TeamViewModel::class.java) -> TeamViewModel(
                teamModel,
                taskModel,
                tagModel,
                categoryModel,
                memberModel
            ) as T

            modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(
                chatModel
            ) as T

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(
                categoryModel,
            ) as T

            modelClass.isAssignableFrom(TagViewModel::class.java) -> TagViewModel(
                tagModel
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}