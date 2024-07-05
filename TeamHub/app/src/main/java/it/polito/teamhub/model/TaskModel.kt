package it.polito.teamhub.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.Attachment
import it.polito.teamhub.dataClass.task.Comment
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import okhttp3.internal.format
import java.util.Date

class TaskModel(
    memberModel: MemberModel,
    val context: Context
) {
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    val memberLogged = memberModel.memberLogged

    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tasks = mutableListOf<Task>()
                    for (document in value.documents) {
                        val taskMap = document.data
                        if (taskMap != null) {
                            val task = Task.fromMap(taskMap)
                            val id = document["id"]
                            if (id is Long) {
                                task.id = id
                                tasks.add(task)
                            }
                        }
                    }
                    trySend(tasks)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getTasksByTeamId(teamId: Long): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .whereEqualTo("idTeam", teamId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tasks = mutableListOf<Task>()
                    for (document in value.documents) {
                        val taskMap = document.data
                        if (taskMap != null) {
                            val task = Task.fromMap(taskMap)
                            val id = document["id"]
                            if (id is Long) {
                                task.id = id
                                tasks.add(task)
                            }
                        }
                    }
                    trySend(tasks)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getTasksCompletedByTeamId(teamId: Long): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .whereEqualTo("idTeam", teamId)
            .whereEqualTo("state", State.COMPLETED.name)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tasks = mutableListOf<Task>()
                    for (document in value.documents) {
                        val taskMap = document.data
                        if (taskMap != null) {
                            val task = Task.fromMap(taskMap)
                            val id = document["id"]
                            if (id is Long) {
                                task.id = id
                                tasks.add(task)
                            }
                        }
                    }
                    trySend(tasks)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    // This function is for calendar view
    // Function to get the list of not completed tasks belonging to teams that have the logged member as component
    fun getMemberLoggedNotCompletedTasks(): Flow<List<Task>> {
        return getTasks().map { tasks ->
            tasks.filter { task ->
                task.state != State.COMPLETED && task.members.contains(memberLogged.value.id)
            }
        }
    }

    fun getTaskById(id: Long): Flow<Task> = callbackFlow {
        val listener = db.collection("tasks")
            .document(id.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val t = value.data
                    if (t != null) {
                        val taskMap = Task.fromMap(t)
                        taskMap.id = id
                        trySend(taskMap)
                    } else {
                        trySend(Task())
                    }
                } else {
                    Log.e("ERROR", error.toString())
                    // Consider throwing an exception or sending a specific error object
                    trySend(Task())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addTask(task: Task) {
        var numUploadedFile = 0
        val attachmentToUpload = mutableListOf<Attachment>()
        if (task.attachment.isEmpty()) {
            addTaskDB(task)
        } else {
            for (attachment in task.attachment) {
                if (attachment.url.startsWith("https://firebasestorage")) {
                    attachmentToUpload.add(
                        Attachment(
                            name = attachment.name,
                            url = attachment.url,
                            extension = attachment.extension
                        )
                    )
                    numUploadedFile++
                    if (numUploadedFile == task.attachment.size) {
                        task.attachment = attachmentToUpload
                        addTaskDB(task)
                    }
                }
                else{
                    val attachmentRef =
                        storageRef.child("attachments/${task.idTeam}/${task.id}/${attachment.name}")
                    val uploadFile = attachmentRef.putFile(Uri.parse(attachment.url))
                    uploadFile.continueWithTask { upload ->
                        if (!upload.isSuccessful) {
                            upload.exception?.let {
                                throw it
                            }
                        }
                        attachmentRef.downloadUrl
                    }.addOnCompleteListener { upload ->
                        if (upload.isSuccessful) {
                            val downloadUri = upload.result
                            attachmentToUpload.add(
                                Attachment(
                                    name = attachment.name,
                                    url = downloadUri.toString(),
                                    extension = attachment.extension
                                )
                            )
                        }
                        numUploadedFile++
                        if (numUploadedFile == task.attachment.size) {
                            task.attachment = attachmentToUpload
                            addTaskDB(task)
                        }
                    }
                }
            }
        }
    }

    private fun addTaskDB(task: Task) {
        db.collection("tasks")
            .document(task.id.toString())
            .set(task.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Added task with ID: ${task.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding task", e)
            }
    }

    fun addAttachment(task: Task, attachments: List<Attachment>, memberId: Long) {
        var numUploadedFile = 0
        val attachmentToUpload = mutableListOf<Attachment>()
        for (attachment in attachments) {
            val attachmentRef =
                storageRef.child("attachments/${task.idTeam}/${task.id}/${attachment.name}")
            val uploadFile = attachmentRef.putFile(Uri.parse(attachment.url))
            uploadFile.continueWithTask { upload ->
                if (!upload.isSuccessful) {
                    upload.exception?.let {
                        throw it
                    }
                }
                attachmentRef.downloadUrl
            }.addOnCompleteListener { upload ->
                if (upload.isSuccessful) {
                    val downloadUri = upload.result
                    attachmentToUpload.add(
                        Attachment(
                            name = attachment.name,
                            url = downloadUri.toString(),
                            extension = attachment.extension
                        )
                    )
                }
                numUploadedFile++
                if (numUploadedFile == attachments.size) {
                    task.attachment += attachmentToUpload
                    val newHistory = History(
                        memberId,
                        Action.ADD_ATTACHMENT,
                        Date(),
                        Action.ADD_ATTACHMENT.getAction(attachment.name)
                    )
                    task.histories.add(newHistory)
                    updateTaskDB(task.id, task)
                }
            }
        }
    }

    fun updateTask(id: Long, currentTask: Task) {
        var numUploadedFile = 0
        if (currentTask.attachment.isEmpty()) {
            updateTaskDB(id, currentTask)
        } else {
            for (attachment in currentTask.attachment) {
                numUploadedFile++
                val attachmentRef =
                    storageRef.child("attachments/${currentTask.idTeam}/${currentTask.id}/${attachment.name}")
                val uploadFile = attachmentRef.putFile(Uri.parse(attachment.url))
                uploadFile.continueWithTask { upload ->
                    if (!upload.isSuccessful) {
                        upload.exception?.let {
                            throw it
                        }
                    }
                    attachmentRef.downloadUrl
                }.addOnCompleteListener { upload ->
                    if (upload.isSuccessful) {
                        val downloadUri = upload.result
                        currentTask.attachment.find {
                            it.name == attachment.name && it.url == attachment.url && it.extension == attachment.extension
                        }?.let { it.url = downloadUri.toString() }
                    }
                    if (numUploadedFile == currentTask.attachment.size) {
                        updateTaskDB(id, currentTask)
                    }
                }
            }
        }
    }

    private fun updateTaskDB(id: Long, currentTask: Task) {
        db.collection("tasks")
            .document(id.toString())
            .update(currentTask.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Updated task with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating task", e)
            }
    }

    fun updateStateById(id: Long, s: State, currentTask: MutableState<Task>) {
        // Update the local task object and its state
        val history = History(
            memberLogged.value.id,
            Action.UPDATE_STATUS,
            Date(),
            Action.UPDATE_STATUS.getAction(s.getStateString())
        )
        val updatedTask = currentTask.value.copy(state = s)
        updatedTask.id = id
        updatedTask.histories.add(history)

        // Update the current task
        currentTask.value = updatedTask

        db.collection("tasks")
            .document(id.toString())
            .update(updatedTask.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Updated state of task with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating task state", e)
            }
    }

    fun addComment(id: Long, comment: Comment, history: History) {
        val docRef = db.collection("tasks").document(id.toString())

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val currentTask = Task.fromMap(data)
                val updatedTask =
                    currentTask.copy(
                        comments = (currentTask.comments + comment).toMutableList(),
                        histories = (currentTask.histories + history).toMutableList()
                    )
                updatedTask.id = id
                transaction.update(docRef, updatedTask.toMap())
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Added comment to task with ID: $id")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error adding comment", e)
        }
    }

    fun deleteTaskById(id: Long) {
        db.collection("tasks")
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Task with id: $id deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting task", e)
            }
    }

    fun deleteTagById(id: Long, name: String, memberId: Long) {
        db.collection("tasks")
            .whereArrayContains("tag", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    val currentTask = Task.fromMap(document.data)
                    val idTask = document.data["id"] as Long
                    val updatedTask = currentTask.copy(
                        tag = currentTask.tag.toMutableList().also { it.remove(id) })
                    updatedTask.id = idTask
                    val newHistory = History(
                        memberId,
                        Action.UPDATE_TASK,
                        Date(),
                        Action.UPDATE_TASK.getAction("Tag deleted: $name")
                    )
                    currentTask.histories.add(newHistory)

                    db.collection("tasks").document(docId).update(updatedTask.toMap())
                        .addOnSuccessListener {
                            Log.d("Firestore", "Deleted tag with id: $id from tasks")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting tag from tasks", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents", e)
            }
    }

    fun deleteCategoryById(id: Long, name: String, memberId: Long) {
        db.collection("tasks")
            .whereEqualTo("category", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    val currentTask = Task.fromMap(document.data)
                    val idTask = document.data["id"] as Long
                    val updatedTask = currentTask.copy(category = -1)
                    updatedTask.id = idTask
                    val newHistory = History(
                        memberId,
                        Action.UPDATE_TASK,
                        Date(),
                        Action.UPDATE_TASK.getAction("Category deleted: $name")
                    )
                    currentTask.histories.add(newHistory)

                    db.collection("tasks").document(docId).update(updatedTask.toMap())
                        .addOnSuccessListener {
                            Log.d("Firestore", "Deleted category with id: $id from tasks")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting category from tasks", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents", e)
            }
    }

    fun removeMemberFromTasks(idMember: Long, idTeam: Long, fullname: String, removed: Boolean) {
        db.collection("tasks")
            .whereEqualTo("idTeam", idTeam)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    val currentTask = Task.fromMap(document.data)
                    val idTask = document.data["id"] as Long
                    currentTask.id = idTask
                    if (currentTask.state != State.COMPLETED) {
                        currentTask.members.removeIf { it == idMember }
                    }
                    val newHistory =
                        if (!removed) History(
                            idMember,
                            Action.LEAVE_TEAM,
                            Date(),
                            Action.LEAVE_TEAM.getAction(fullname)
                        )
                        else History(
                            memberLogged.value.id,
                            Action.UPDATE_MEMBERS,
                            Date(),
                            "",
                            removedMembers = listOf(idMember)
                        )
                    currentTask.histories.add(newHistory)
                    db.collection("tasks").document(docId).update(currentTask.toMap())
                        .addOnSuccessListener {
                            Log.d("Firestore", "Removed member with id: $idMember from tasks")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error removing member from tasks", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents", e)
            }
    }

    fun updateReviewByTaskId(taskId: Long, review: Float) {
        val docRef = db.collection("tasks").document(taskId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val currentTask = Task.fromMap(data)
                val updatedTask =
                    currentTask.copy(
                        mapReview = currentTask.mapReview.toMutableMap().also {
                            it[memberLogged.value.id] = review
                        },
                        histories = (currentTask.histories + History(
                            memberLogged.value.id,
                            Action.ADD_REVIEW,
                            Date(),
                            Action.ADD_REVIEW.getAction(format("%.2f", review))
                        )).toMutableList()
                    )
                updatedTask.id = taskId
                transaction.update(docRef, updatedTask.toMap())
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Updated review of task with ID: $taskId")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error updating task review", e)
        }
    }

    fun deleteTasksByTeamId(idTeam: Long) {
        db.collection("tasks")
            .whereEqualTo("idTeam", idTeam)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    db.collection("tasks").document(docId).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Deleted task of team $idTeam from tasks")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting task", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents", e)
            }
    }
}