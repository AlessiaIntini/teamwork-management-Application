package it.polito.teamhub.model

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TeamModel(
    private val memberModel: MemberModel,
    private val chatModel: ChatModel
) {

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    val memberLogged = memberModel.memberLogged
    fun deleteChatByTeamId(teamId: Long) = chatModel.deleteChatByTeamId(teamId)
    private fun deleteChat(memberId: Long, chatId: Long) = memberModel.deleteChat(memberId, chatId)

    fun getChatIdByTeamId(teamId: Long) = chatModel.getChatIdByTeamId(teamId)
    private fun addChat(chat: Chat) = chatModel.addChat(chat)

    private fun addChatTeam(memberIds: List<Long>, chatId: Long) =
        memberModel.addChatTeam(memberIds, chatId)

    private fun addChat(memberId: Long, chatId: Long) = memberModel.addChat(memberId, chatId)

    private fun deleteMessage(chatId: Long, messageId: Long) =
        chatModel.deleteMessage(chatId, messageId)

    fun addTeam(team: Team) {
        db.collection("teams")
            .document(team.id.toString())
            .set(team.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Added team with ID: ${team.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding team", e)
            }
        val newChat = Chat(
            members = mutableListOf(team.id),
            type = TypeProfileIcon.TEAM,
        )
        addChat(newChat)
        addChatTeam(team.members.map { it.idMember }, newChat.id)
    }

    fun updateTeam(team: Team, id: Long) {
        if (team.imageTeam != "") {
            if (team.imageTeam.startsWith("https")) {
                updateTeamDB(team, id)
                return
            }
            val imageRef = storageRef.child("images/team/${id}/profile.jpg")
            val uploadImage = imageRef.putFile(Uri.parse(team.imageTeam))
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
                    team.imageTeam = downloadUri.toString()
                    updateTeamDB(team, id)
                }
            }
        } else {
            updateTeamDB(team, id)
        }
    }

    private fun updateTeamDB(team: Team, id: Long) {
        var addedMembers: List<Long> = emptyList()
        var removedMembers: List<Long> = emptyList()

        val docRef = db.collection("teams").document(id.toString())

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val teamDBData = Team.fromMap(data)
                val teamUpdate = teamDBData.copy()
                teamUpdate.id = id
                teamUpdate.imageTeam = team.imageTeam
                teamUpdate.name = team.name
                teamUpdate.description = team.description
                teamUpdate.members = teamDBData.members.map { dbMember ->
                    val correspondingMember = team.members.find { it.idMember == dbMember.idMember }
                    if (correspondingMember != null) {
                        dbMember.copy(
                            idMember = correspondingMember.idMember,
                            fullname = correspondingMember.fullname,
                            role = correspondingMember.role,
                            timeParticipation = correspondingMember.timeParticipation,
                            isMember = true
                        )
                    } else {
                        dbMember.copy(isMember = false)
                    }
                }.toMutableList()
                val countIsMemberCurrent = teamDBData.members.filter { it.isMember }
                val countIsMemberNew = team.members.filter { it.isMember }
                if (countIsMemberNew != countIsMemberCurrent) {
                    val existingMembers =
                        teamDBData.members.filter { it.isMember }.map { it.idMember }
                    addedMembers =
                        team.members.filter { it.isMember }.map { it.idMember }
                            .filterNot { it in existingMembers }
                    removedMembers =
                        existingMembers.filterNot { it -> it in team.members.map { it.idMember } }
                }
                if (addedMembers.isNotEmpty() || removedMembers.isNotEmpty()) {
                    db.collection("chats")
                        .whereEqualTo("type", TypeProfileIcon.TEAM.toString())
                        .whereArrayContains("members", id)
                        .addSnapshotListener { value, _ ->
                            if (value != null) {
                                for (document in value.documents) {
                                    val chatMap = document.data
                                    if (chatMap != null) {
                                        val chatId = document["id"]
                                        if (chatId is Long) {
                                            if (addedMembers.isNotEmpty()) {
                                                addChatTeam(addedMembers, chatId)
                                                addedMembers.forEach { memberId ->
                                                    addChat(memberId, chatId)
                                                    deleteMessage(chatId, memberId)
                                                }
                                            }
                                            if (removedMembers.isNotEmpty()) {
                                                removedMembers.forEach { memberId ->
                                                    deleteMessage(chatId, memberId)
                                                    deleteChat(memberId, chatId)
                                                }
                                            }
                                            removedMembers = emptyList()
                                            addedMembers = emptyList()
                                        }
                                    }
                                    break
                                }
                            } else {
                                Log.d("Firestore", "get failed with ")
                            }
                        }
                }

                transaction.update(docRef, teamUpdate.toMap())
            }
        }.addOnSuccessListener {

            Log.d("Firestore", "Team with ID: $id successfully updated!")

        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error updating team", e)
        }
    }

    fun deleteTeam(id: Long) {
        db.collection("teams")
            .document(id.toString())
            .delete()
            .addOnSuccessListener {

                Log.d("Firestore", "Team with id: $id successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error deleting team", e) }
    }

    fun getTeamById(id: Long): Flow<Team> = callbackFlow {
        val listener = db.collection("teams")
            .document(id.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val t = value.data
                    if (t != null) {
                        val teamMap = Team.fromMap(t)
                        teamMap.id = id
                        trySend(teamMap)
                    } else {
                        trySend(Team())
                    }
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(Team())
                }

            }
        awaitClose { listener.remove() }
    }

    fun addMember(member: TeamMember, teamId: Long) {
        val docRef = db.collection("teams").document(teamId.toString())

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val teamDBData = Team.fromMap(data)
                val teamUpdate = teamDBData.copy()
                teamUpdate.id = teamId
                if (teamDBData.members.none { it.idMember == member.idMember }) {
                    teamUpdate.members = (teamDBData.members + member).toMutableList()
                } else if (teamDBData.members.find { it.idMember == member.idMember }?.isMember == false) {
                    teamUpdate.members = teamDBData.members.map {
                        if (it.idMember == member.idMember) it.copy(
                            fullname = member.fullname,
                            isMember = true
                        ) else it
                    }.toMutableList()
                }

                var addedMembers: List<Long> = listOf(member.idMember)
                db.collection("chats")
                    .whereEqualTo("type", TypeProfileIcon.TEAM.toString())
                    .whereArrayContains("members", teamId)
                    .addSnapshotListener { value, _ ->
                        if (value != null) {
                            for (document in value.documents) {
                                val chatMap = document.data
                                if (chatMap != null) {
                                    val chatId = document["id"]
                                    if (chatId is Long) {
                                        if (addedMembers.isNotEmpty()) {
                                            addChatTeam(addedMembers, chatId)
                                            addedMembers.forEach { memberId ->
                                                addChat(memberId, chatId)
                                                //addMemberDeletedOldMessage(chatId, memberId)
                                                deleteMessage(chatId, memberId)
                                            }
                                        }
                                        addedMembers = emptyList()
                                    }
                                }
                                break
                            }
                        } else {
                            Log.d("Firestore", "get failed with ")
                        }
                    }
                transaction.update(docRef, teamUpdate.toMap())
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Member with ID: ${member.idMember} successfully added to team!")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error adding member to team", e)
        }
    }

    fun removeMemberById(idMember: Long, teamId: Long) {
        val docRef = db.collection("teams").document(teamId.toString())
        docRef.get().addOnSuccessListener { teamDocument ->
            val data = teamDocument.data
            if (data != null) {
                val teamDBData = Team.fromMap(data)
                val teamUpdate = teamDBData.copy()
                teamUpdate.id = teamId
                teamUpdate.members = teamDBData.members.map {
                    if (it.idMember == idMember) {
                        it.copy(isMember = false, idMember = idMember)
                    } else it
                }.toMutableList()

                val batch1 = db.batch()

                // Update the team document
                batch1.set(docRef, teamUpdate.toMap())
                batch1.commit().addOnSuccessListener {
                    Log.d("Firestore", "Batch 1 write succeeded.")
                    db.collection("chats")
                        .whereEqualTo("type", TypeProfileIcon.TEAM.toString())
                        .whereArrayContains("members", teamId)
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (document in chatDocuments) {
                                val id = document["id"]
                                if (id is Long) {
                                    db.collection("members")
                                        .whereEqualTo("id", idMember)
                                        .get()
                                        .addOnSuccessListener { memberDocuments ->
                                            for (memberDocument in memberDocuments) {
                                                val member = Member.fromMap(memberDocument.data)
                                                member.let { it ->
                                                    it.chats =
                                                        it.chats?.filter { it != id }
                                                            ?.toMutableList()
                                                    it.id = idMember

                                                    val batch2 = db.batch()
                                                    // Update the member document
                                                    batch2.set(
                                                        db.collection("members")
                                                            .document(memberDocument.id), it.toMap()
                                                    )
                                                    batch2.commit().addOnSuccessListener {
                                                        Log.d(
                                                            "Firestore",
                                                            "Batch 2 write succeeded."
                                                        )
                                                        val docRefChat = db.collection("chats")
                                                            .document(id.toString())

                                                        docRefChat.get()
                                                            .addOnSuccessListener { documentChat ->
                                                                val dataChat = documentChat.data
                                                                if (dataChat != null) {
                                                                    val chat =
                                                                        Chat.fromMap(dataChat)
                                                                    chat.let {
                                                                        it.messages.forEach { message ->
                                                                            if (message.deleted?.containsKey(
                                                                                    idMember
                                                                                ) == true && message.receiver.containsKey(
                                                                                    idMember
                                                                                )
                                                                            ) {
                                                                                message.deleted[idMember] =
                                                                                    -1
                                                                                message.receiver[idMember] =
                                                                                    1
                                                                            } else {
                                                                                message.deleted?.set(
                                                                                    idMember,
                                                                                    -1
                                                                                )
                                                                            }
                                                                        }
                                                                        it.id = id

                                                                        val batch3 = db.batch()
                                                                        // Update the chat document
                                                                        batch3.set(
                                                                            docRefChat,
                                                                            it.toMap()
                                                                        )
                                                                        batch3.commit()
                                                                            .addOnSuccessListener {
                                                                                Log.d(
                                                                                    "Firestore",
                                                                                    "Batch 3 write succeeded."
                                                                                )
                                                                            }
                                                                            .addOnFailureListener { e ->
                                                                                Log.w(
                                                                                    "Firestore",
                                                                                    "Error writing batch 3",
                                                                                    e
                                                                                )
                                                                            }
                                                                    }
                                                                } else {
                                                                    Log.d(
                                                                        "Firestore",
                                                                        "No such document"
                                                                    )
                                                                }
                                                            }.addOnFailureListener { e ->
                                                                Log.w(
                                                                    "Firestore",
                                                                    "Error getting chat",
                                                                    e
                                                                )
                                                            }
                                                    }.addOnFailureListener { e ->
                                                        Log.w(
                                                            "Firestore",
                                                            "Error writing batch 2",
                                                            e
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }.addOnFailureListener { e ->
                            Log.w("Firestore", "Error getting chats", e)
                        }
                }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error writing batch 1", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error getting team", e)
        }
    }

    fun getTeamsByMemberId(idMember: Long): Flow<List<Team>> = callbackFlow {
        val listener = db.collection("teams")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val teams = mutableListOf<Team>()
                    for (team in value.documents) {
                        val teamData = team.data
                        if (teamData != null) {
                            val teamMap = Team.fromMap(teamData)
                            val id = team["id"]
                            if (id is Long && teamMap.members.any { it.idMember == idMember && it.isMember }) {
                                teamMap.id = id
                                teams.add(teamMap)
                            }
                        }
                    }
                    trySend(teams)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())

                }
            }
        awaitClose { listener.remove() }
    }

    fun getRoleOfMemberLoggedByTeamId(idTeam: Long): Flow<Role?> = callbackFlow {
        val listener = db.collection("teams")
            .whereEqualTo("id", idTeam)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val role = value.documents.firstOrNull()?.data?.let { it ->
                        val team = Team.fromMap(it)
                        team.members.find { it.idMember == memberLogged.value.id }?.role
                    }
                    trySend(role)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun changeRole(role: Role?, id: Long, teamId: Long) {
        val docRef = db.collection("teams").document(teamId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val teamDBData = Team.fromMap(data)
                val teamUpdate = teamDBData.copy()
                teamUpdate.id = teamId
                teamUpdate.members = teamDBData.members.map {
                    if (it.idMember == id) {
                        it.copy(role = role)
                    } else it
                }.toMutableList()
                transaction.update(docRef, teamUpdate.toMap())
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Role of member with ID: $id successfully updated!")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error updating member role", e)
        }
    }

    fun changeTimeParticipation(timeParticipation: TimeParticipation?, id: Long, teamId: Long) {
        val docRef = db.collection("teams").document(teamId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val teamDBData = Team.fromMap(data)
                val teamUpdate = teamDBData.copy()
                teamUpdate.id = teamId
                teamUpdate.members = teamDBData.members.map {
                    if (it.idMember == id) {
                        if (timeParticipation != null) {
                            it.copy(timeParticipation = timeParticipation)
                        } else it
                    } else it
                }.toMutableList()
                transaction.update(docRef, teamUpdate.toMap())
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Time participation of member with ID: $id successfully updated!")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error updating member time participation", e)
        }
    }

    fun getAllTeams(): Flow<List<Team>> = callbackFlow {
        val listener = db.collection("teams")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val teams = mutableListOf<Team>()
                    for (document in value.documents) {
                        val teamData = document.data
                        if (teamData != null) {
                            val team = Team.fromMap(teamData)
                            val id = document["id"]
                            if (id is Long) {
                                team.id = id
                                teams.add(team)
                            }
                        }
                    }
                    trySend(teams)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }
}
