package it.polito.teamhub.model

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.teamhub.dataClass.task.Tag
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TagModel {

    private val db = Firebase.firestore

    fun getTags(): Flow<List<Tag>> = callbackFlow {
        val listener = db.collection("tags")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tags = mutableListOf<Tag>()
                    for (document in value.documents) {
                        val tagMap = document.data
                        if (tagMap != null) {
                            val tag = Tag.fromMap(tagMap)
                            val id = document["id"]
                            if (id is Long) {
                                tag.id = id
                                tags.add(tag)
                            }
                        }
                    }
                    trySend(tags)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getTagTeamId(teamId: Long): Flow<List<Tag>> = callbackFlow {
        val listener = db.collection("tags")
            .whereEqualTo("teamId", teamId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tags = mutableListOf<Tag>()
                    for (document in value.documents) {
                        val tagMap = document.data
                        if (tagMap != null) {
                            val tag = Tag.fromMap(tagMap)
                            val id = document["id"]
                            if (id is Long) {
                                tag.id = id
                                tags.add(tag)
                            }
                        }
                    }
                    trySend(tags)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getTagById(id: Long): Flow<Tag> = callbackFlow {
        val listener = db.collection("tags")
            .document(id.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tagMap = value.data
                    if (tagMap != null) {
                        val tag = Tag.fromMap(tagMap)
                        val tagId = value["id"]
                        if (tagId is Long) {
                            tag.id = tagId
                            trySend(tag)
                        }
                    } else {
                        trySend(Tag("", -1))
                    }
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(Tag("", -1))
                }
            }
        awaitClose { listener.remove() }
    }

    fun getListTagById(ids: List<Long>): Flow<List<Tag>> = callbackFlow {
        val idList = ids.ifEmpty {
            listOf(-1)
        }
        val listener = db.collection("tags")
            .whereIn("id", idList)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val tags = mutableListOf<Tag>()
                    for (document in value.documents) {
                        val tagMap = document.data
                        if (tagMap != null) {
                            val tag = Tag.fromMap(tagMap)
                            val id = document["id"]
                            if (id is Long) {
                                tag.id = id
                                tags.add(tag)
                            }
                        }
                    }
                    trySend(tags)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addTag(tag: Tag) {
        db.collection("tags")
            .document(tag.id.toString())
            .set(tag.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Added tag with ID: ${tag.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding tag", e)
            }
    }

    fun updateTagList(id: Long, newTag: Tag) {
        newTag.id = id
        db.collection("tags")
            .document(id.toString())
            .update(newTag.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Updated tag with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating tag", e)
            }
    }

    fun deleteTagList(id: Long) {
        db.collection("tags")
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Deleted tag with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting tag", e)
            }
    }

    fun deleteTagsOfTeam(teamId: Long) {
        db.collection("tags")
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    db.collection("tags").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Deleted tags with team ID: $teamId")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting tags", e)
                        }
                }
            }
    }
}