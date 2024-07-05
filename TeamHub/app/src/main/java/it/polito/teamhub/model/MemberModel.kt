package it.polito.teamhub.model

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.member.Gender
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.gradientPairList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

class MemberModel {
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private val _memberLogged: MutableStateFlow<Member> = MutableStateFlow(
        Member(
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
            color = gradientPairList[0],
            chats = mutableListOf()
        )
    )
    val memberLogged: StateFlow<Member> = _memberLogged

    fun getMembers(): Flow<List<Member>> = callbackFlow {
        val listener = db.collection("members")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val members = mutableListOf<Member>()
                    for (document in value.documents) {
                        val memberMap = document.data
                        if (memberMap != null) {
                            val member = Member.fromMap(memberMap)
                            val id = document["id"]
                            if (id is Long) {
                                member.id = id
                                members.add(member)
                            }
                        }
                    }
                    trySend(members)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getAllMemberById(idList: MutableList<Long>): Flow<List<Member>> = callbackFlow {
        val listener = db.collection("members")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val members = mutableListOf<Member>()
                    for (document in value.documents) {
                        val memberMap = document.data
                        if (memberMap != null) {
                            val member = Member.fromMap(memberMap)
                            val id = document["id"]
                            if (id is Long && idList.contains(id)) {
                                member.id = id
                                members.add(member)
                            }
                        }
                    }
                    trySend(members)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getMemberById(idMember: Long): Flow<Member> = callbackFlow {
        val listener = db.collection("members")
            .whereEqualTo("id", idMember)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (document in value.documents) {
                        val memberMap = document.data
                        if (memberMap != null) {
                            val member = Member.fromMap(memberMap)
                            val id = document["id"]
                            if (id is Long) {
                                member.id = idMember
                                trySend(member)
                            }
                        }
                    }
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(
                        Member(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            null,
                            Gender.FEMALE,
                            "",
                            gradientPairList[0]
                        )
                    )
                }
            }
        awaitClose { listener.remove() }
    }

    fun registerNewMember(member: Member, documentId: String) {
        val docRef = db.collection("members").document(documentId)
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                member.id = document["id"] as Long
                docRef.set(member.toMap())
                    .addOnSuccessListener {
                        Log.d("MemberModel", "Registered member with ID: $documentId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("MemberModel", "Error registering member", e)
                    }
            } else {
                docRef.set(member.toMap())
                    .addOnSuccessListener {
                        Log.d("MemberModel", "Registered member with ID: $documentId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("MemberModel", "Error registering member", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("MemberModel", "Error checking document", e)
        }
    }

    fun updateMember(updatedMember: Member, listTeams: List<Long>) {
        if (updatedMember.userImage != "") {
            if (updatedMember.userImage.startsWith("https")) {
                updateMemberDB(updatedMember, listTeams)
                return
            }
            val imageRef = storageRef.child("images/profile/${updatedMember.id}/profile.jpg")
            val uploadImage = imageRef.putFile(Uri.parse(updatedMember.userImage))
            uploadImage.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updatedMember.userImage = downloadUri.toString()
                    updateMemberDB(updatedMember, listTeams)
                } else {
                    Log.e("ERROR", task.exception.toString())
                }

            }
        } else {
            updateMemberDB(updatedMember, listTeams)
        }
    }

    private fun updateMemberDB(updatedMember: Member, listTeams: List<Long>) {


        db.runTransaction { transaction ->
            var docRef =
                db.collection("members").document(FirebaseAuth.getInstance().currentUser!!.uid)
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val memberDBdata = Member.fromMap(data)
                val memberUpdate = memberDBdata.copy()
                memberUpdate.id = updatedMember.id
                memberUpdate.fullname = updatedMember.fullname
                memberUpdate.jobTitle = updatedMember.jobTitle
                memberUpdate.description = updatedMember.description
                memberUpdate.nickname = updatedMember.nickname
                memberUpdate.email = updatedMember.email
                memberUpdate.location = updatedMember.location
                memberUpdate.phoneNumber = updatedMember.phoneNumber
                memberUpdate.birthDate = updatedMember.birthDate
                memberUpdate.gender = updatedMember.gender
                memberUpdate.userImage = updatedMember.userImage
                memberUpdate.color = updatedMember.color
                memberUpdate.chats = updatedMember.chats
                memberUpdate.isDeleted = updatedMember.isDeleted
                if (listTeams.isNotEmpty()) {
                    db.collection("teams")
                        .whereIn("id", listTeams)
                        .addSnapshotListener { value, _ ->
                            if (value != null) {
                                for (document in value.documents) {
                                    val team = document.data?.let { Team.fromMap(it) }!!
                                    team.id = document["id"] as Long
                                    for (member in team.members) {
                                        if (member.idMember == updatedMember.id && member.isMember) {
                                            member.fullname = updatedMember.fullname
                                        }
                                    }
                                    db.collection("teams")
                                        .document(document.id)
                                        .set(team.toMap())
                                    Log.d("Firestore", "team ${document.id} updated")
                                }
                            } else {
                                Log.d("Firestore", "get failed with ")
                            }
                        }
                }
                transaction.update(docRef, updatedMember.toMap())
            }

        }.addOnSuccessListener {

            Log.d("Firestore", "Member with ID: ${updatedMember.id} successfully updated!")

        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error updating member", e)
        }
    }

    fun deleteMember(documentId: String, memberId: Long, logout: () -> Unit) {
        val updatedMember = Member(
            fullname = "Deleted Account",
            jobTitle = "",
            description = "",
            nickname = "",
            email = "",
            location = "",
            phoneNumber = null,
            birthDate = null,
            gender = Gender.PREFER_NOT_TO_SAY,
            userImage = "",
            color = mutableListOf(Gray2.toArgb().toLong(), Gray4.toArgb().toLong()),
            chats = mutableListOf(),
            isDeleted = true
        )
        updatedMember.id = memberId
        val teams = mutableListOf<Long>()
        val teamDeleted = mutableListOf<Long>()

        val batch = db.batch()

        // Update member document
        val memberRef = db.collection("members").document(documentId)
        batch.set(memberRef, updatedMember.toMap())

        // Get all teams
        db.collection("teams").get().addOnSuccessListener { teamDocuments ->
            for (teamDocument in teamDocuments) {
                val team = Team.fromMap(teamDocument.data)
                team.id = teamDocument["id"] as Long
                var isTeamUpdated = false
                var isAdmin = false

                // Update team members
                for (member in team.members) {
                    if (member.idMember == memberId) {
                        member.isMember = false
                        member.fullname = "Deleted Account"
                        isTeamUpdated = true
                        teams.add(team.id)
                        if (member.role == Role.ADMIN) {
                            isAdmin = true
                        }
                    }
                }

                // If the team was updated, write it back to the database
                if (isTeamUpdated) {
                    // If the team has no members left, delete the team
                    if (team.members.all { !it.isMember }) {
                        teamDeleted.add(team.id)
                        batch.delete(teamDocument.reference)
                    } else {
                        // If the deleted member was the admin, assign the role to another member
                        if (isAdmin) {
                            val newAdmin = team.members.firstOrNull { it.isMember }
                            if (newAdmin != null) {
                                newAdmin.role = Role.ADMIN
                            }
                        }
                        batch.set(teamDocument.reference, team.toMap())
                    }
                }
            }

            // Get all tasks
            db.collection("tasks").get().addOnSuccessListener { taskDocuments ->
                for (taskDocument in taskDocuments) {
                    val task = Task.fromMap(taskDocument.data)
                    task.id = taskDocument["id"] as Long
                    var isTaskUpdated = false

                    // If the task is not completed and the member is assignee, remove the member id
                    if (task.state != State.COMPLETED && task.members.contains(memberId)) {
                        task.members.remove(memberId)
                        isTaskUpdated = true
                    }

                    // If the task was updated, write it back to the database
                    if (isTaskUpdated) {
                        batch.set(taskDocument.reference, task.toMap())
                    }
                }

                db.collection("chats")
                    .whereEqualTo("type", "TEAM")
                    .get()
                    .addOnSuccessListener { chatDocuments ->
                        for (chatDocument in chatDocuments) {
                            val chat = Chat.fromMap(chatDocument.data)
                            chat.id = chatDocument["id"] as Long
                            if (teams.contains(chat.members[0])) {
                                chat.let {
                                    it.messages.forEach { message ->
                                        message.deleted?.get(memberId)?.let {
                                            message.deleted[memberId] = -1
                                        }
                                        message.receiver[memberId]?.let {
                                            message.receiver[memberId] = 1
                                        }
                                    }
                                    it.id = chat.id
                                    batch.set(chatDocument.reference, it.toMap())
                                }
                            }
                        }

                        // delete chats of deleted teams
                        db.collection("chats")
                            .whereEqualTo("type", "TEAM")
                            .get()
                            .addOnSuccessListener { chatDocs ->
                                for (chatDocument in chatDocs) {
                                    val chat = Chat.fromMap(chatDocument.data)
                                    chat.id = chatDocument["id"] as Long
                                    if (teamDeleted.contains(chat.members[0])) {
                                        batch.delete(chatDocument.reference)
                                    }
                                }


                                // Commit the batch
                                batch.commit().addOnSuccessListener {
                                    Log.d("Firestore", "Member successfully marked as deleted!")
                                    logout()
                                }.addOnFailureListener { e ->
                                    Log.w("Firestore", "Error marking member as deleted", e)
                                }
                            }
                    }
            }
        }
    }

    fun updateMemberLogged(member: Member) {
        _memberLogged.value = member
    }

    fun deleteChat(memberId: Long, chatId: Long) {
        db.collection("members")
            .whereEqualTo("id", memberId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val member = Member.fromMap(document.data)
                    member.let { it ->
                        it.chats = it.chats?.filter { it != chatId }?.toMutableList()
                        it.id = memberId
                        db.collection("members")
                            .document(document.id)
                            .set(it.toMap())
                    }
                }
            }
    }

    fun addChat(memberId: Long, chatId: Long, receiverId: Long = -1L) {
        /*var ids: Array<Long>
        if (receiverId == -1L) ids = arrayOf(memberId)
        else ids = arrayOf(memberId, receiverId)
        for (id in ids) {*/
        db.collection("members")
            .whereEqualTo("id", memberId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val member = Member.fromMap(document.data)
                    if (member.chats?.contains(chatId) != true) {
                        member.let {
                            it.chats?.add(chatId)
                            it.id = memberId
                            db.collection("members")
                                .document(document.id)
                                .set(it.toMap())
                        }
                    }
                }

            }
    }

    fun addChatTeam(memberIds: List<Long>, chatId: Long) {
        for (memberId in memberIds) {
            db.collection("members")
                .whereEqualTo("id", memberId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val member = Member.fromMap(document.data)
                        if (member.chats?.contains(chatId) != true) {
                            member.let {
                                it.chats?.add(chatId)
                                it.id = memberId
                                db.collection("members")
                                    .document(document.id)
                                    .set(it.toMap())
                            }
                        }
                    }
                }
        }
    }

    fun isMemberAlreadyRegistered(documentId: String) = callbackFlow {
        val documentReference = db.collection("members").document(documentId)
        val listener = documentReference.addSnapshotListener { value, error ->
            if (value != null && value.exists()) {
                val data = value.data
                if (data != null) {
                    val member = Member.fromMap(data)
                    val id = data["id"]
                    if (id is Long && !member.isDeleted) {
                        member.id = id
                        trySend(member)
                    } else {
                        trySend(false)
                    }
                }
            } else {
                Log.e("ERROR", error.toString())
                trySend(false)
            }
        }
        awaitClose { listener.remove() }
    }
}
