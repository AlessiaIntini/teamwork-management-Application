package it.polito.teamhub.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.chat.Message
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatModel(private val memberModel: MemberModel) {
    private val db = Firebase.firestore

    private fun addChatMember(memberId: Long, chatId: Long, receiverId: Long = -1L) =
        memberModel.addChat(memberId, chatId, receiverId)

    fun deleteMessage(chatId: Long, memberId: Long) {
        val docRef = db.collection("chats").document(chatId.toString())

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val chat = Chat.fromMap(data)
                chat.let {
                    it.messages.forEach { message ->
                        if (message.deleted?.containsKey(memberId) == true && message.receiver.containsKey(
                                memberId
                            )
                        ) {
                            message.deleted.get(memberId)?.let {
                                message.deleted[memberId] = -1
                            }
                            message.receiver[memberId]?.let {
                                message.receiver[memberId] = 1
                            }
                        } else {
                            message.deleted?.set(memberId, -1)

                        }
                    }
                    it.id = chatId
                    transaction.set(docRef, it.toMap())
                }
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Message deleted successfully!")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error deleting message", e)
        }
    }


    fun getChats(): Flow<List<Chat>> = callbackFlow {
        val listener = db.collection("chats")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val chats = mutableListOf<Chat>()
                    for (document in value.documents) {
                        val chatMap = document.data
                        if (chatMap != null) {
                            val chat = Chat.fromMap(chatMap)
                            val id = document["id"]
                            if (id is Long) {
                                chat.id = id
                                chats.add(chat)
                            }
                        }
                    }
                    trySend(chats)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addChat(chat: Chat) {
        db.collection("chats")
            .document(chat.id.toString())
            .set(chat.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Add chat with ID: ${chat.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding chat", e)
            }
    }

    fun addMessage(id: Long, message: Message) {
        val docRef = db.collection("chats").document(id.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val chat = Chat.fromMap(data)
                val count = chat.messages.filter { it.receiver.isNotEmpty() }.count { message ->
                    message.deleted?.get(message.receiver.keys.first()) == -1
                }
                if ((chat.messages.isEmpty() || chat.messages.size == count) && chat.type == TypeProfileIcon.PERSON) {
                    addChatMember(
                        message.receiver.keys.first(),
                        id,
                        message.receiver.keys.first()
                    )
                }
                chat.let {
                    it.messages.add(message)
                    it.id = id
                    transaction.set(docRef, it.toMap())
                }
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Added message to chat with id: $id")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error adding message", exception)
        }
    }


    fun deleteChatByTeamId(teamId: Long) {
        db.collection("chats")
            .whereEqualTo("type", TypeProfileIcon.TEAM.toString())
            .whereArrayContains("members", teamId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("chats").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Chat successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting chat", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }

    fun getChatIdByTeamId(teamId: Long): Flow<Long> = callbackFlow {
        val listener = db.collection("chats")
            .whereEqualTo("type", TypeProfileIcon.TEAM.toString())
            .whereArrayContains("members", teamId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    for (document in value.documents) {
                        val chatMap = document.data
                        if (chatMap != null) {
                            val id = document["id"]
                            if (id is Long) {
                                trySend(id)
                            }
                        }

                        break
                    }
                } else {
                    Log.d("Firestore", "get failed with ")
                    trySend(-1)
                }
            }
        awaitClose { listener.remove() }
    }

    fun readMessage(chatId: Long, memberId: Long) {
        db.collection("chats")
            .whereEqualTo("id", chatId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val chat = Chat.fromMap(document.data)
                    chat.let {
                        it.messages.forEach { message ->
                            val idMessage = message.id
                            if (message.author != memberId && message.receiver[memberId] == 0) {
                                message.receiver[memberId] = 1
                                message.id = idMessage
                            }
                        }
                        it.id = chatId
                        db.collection("chats").document(document.id).set(chat.toMap())
                    }
                }
            }
        /*val docRef = db.collection("chats").document(chatId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data
            if (data != null) {
                val chat = Chat.fromMap(data)
                chat.let {
                    it.messages.forEach { message ->
                        val idMessage = message.id
                        if (message.author != memberId) {
                            message.receiver[memberId] = 1
                            message.id = idMessage
                        }
                    }
                    it.id = chatId
                    transaction.set(docRef, it.toMap())
                }
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Message read successfully!")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error reading message", exception)
        }*/
    }

    /*    fun getNotification(memberId: Long): Flow<Long> = callbackFlow {
            val listener = db.collection("chats")
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        var count = 0L
                        for (document in value.documents) {
                            val chat = document.data?.let { Chat.fromMap(it) }
                            chat?.let {
                                it.messages.forEach { message ->
                                    message.receiver[memberId]?.let { receiverValue ->
                                        message.deleted?.get(memberId)?.let { deletedValue ->
                                            if (receiverValue == 0 && deletedValue != -1) {
                                                count++
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        trySend(count)
                    } else {
                        Log.e("ERROR", error.toString())
                        trySend(-1)
                    }
                }
            awaitClose { listener.remove() }
        }*/
    fun getNotification(memberId: Long): Flow<Long> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null

        val memberDocRef = db.collection("members").whereEqualTo("id", memberId).limit(1).get()
        memberDocRef.addOnSuccessListener { document ->
            if (document != null) {
                val memberChats = document.documents[0].get("chats") as List<Long>
                if (memberChats.isNotEmpty()) {
                    listenerRegistration = db.collection("chats")
                        .whereIn("id", memberChats)
                        .addSnapshotListener { value, error ->
                            if (value != null) {
                                var count = 0L
                                for (document in value.documents) {
                                    val chat = document.data?.let { Chat.fromMap(it) }
                                    chat?.let {
                                        it.messages.forEach { message ->
                                            message.receiver[memberId]?.let { receiverValue ->
                                                message.deleted?.get(memberId)
                                                    ?.let { deletedValue ->
                                                        if (receiverValue == 0 && deletedValue != -1) {
                                                            count++
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }
                                trySend(count)
                            } else {
                                Log.e("ERROR", error.toString())
                                trySend(-1)
                            }
                        }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting member chats", exception)
        }

        awaitClose { listenerRegistration?.remove() }
    }

}