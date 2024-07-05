package it.polito.teamhub

import android.app.Application
import com.google.firebase.FirebaseApp
import it.polito.teamhub.model.CategoryModel
import it.polito.teamhub.model.ChatModel
import it.polito.teamhub.model.MemberModel
import it.polito.teamhub.model.TagModel
import it.polito.teamhub.model.TaskModel
import it.polito.teamhub.model.TeamModel

class TeamHubApplication : Application() {
    val memberModel by lazy { MemberModel() }
    val chatModel by lazy { ChatModel(memberModel) }
    val tagModel by lazy { TagModel() }
    val categoryModel by lazy { CategoryModel() }
    lateinit var taskModel: TaskModel
    val teamModel: TeamModel by lazy { TeamModel(memberModel, chatModel) }

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        taskModel = TaskModel(memberModel, this)
    }
}