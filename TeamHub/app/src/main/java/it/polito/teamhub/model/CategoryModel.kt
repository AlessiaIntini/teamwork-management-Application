package it.polito.teamhub.model

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.teamhub.dataClass.task.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CategoryModel {

    private val db = Firebase.firestore

    fun getCategories(): Flow<List<Category>> = callbackFlow {
        val listener = db.collection("categories")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val categories = mutableListOf<Category>()
                    for (document in value.documents) {
                        val categoryMap = document.data
                        if (categoryMap != null) {
                            val category = Category.fromMap(categoryMap)
                            val id = document["id"]
                            if (id is Long) {
                                category.id = id
                                categories.add(category)
                            }
                        }
                    }
                    trySend(categories)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getCategoryTeamId(teamId: Long): Flow<List<Category>> = callbackFlow {
        val listener = db.collection("categories")
            .whereEqualTo("teamId", teamId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val categories = mutableListOf<Category>()
                    for (document in value.documents) {
                        val categoryMap = document.data
                        if (categoryMap != null) {
                            val category = Category.fromMap(categoryMap)
                            val id = document["id"]
                            if (id is Long) {
                                category.id = id
                                categories.add(category)
                            }
                        }
                    }
                    trySend(categories)
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getCategoryById(id: Long): Flow<Category> = callbackFlow {
        val listener = db.collection("categories")
            .document(id.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val categoryMap = value.data
                    if (categoryMap != null) {
                        val category = Category.fromMap(categoryMap)
                        val categoryId = value["id"]
                        if (categoryId is Long) {
                            category.id = categoryId
                            trySend(category)
                        }
                    } else {
                        trySend(Category("", -1))
                    }
                } else {
                    Log.e("ERROR", error.toString())
                    trySend(Category("", -1))
                }
            }
        awaitClose { listener.remove() }

    }

    fun addCategory(category: Category) {
        db.collection("categories")
            .document(category.id.toString())
            .set(category.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Added category with ID: ${category.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding category", e)
            }
    }

    fun updateCategoryList(id: Long, newCategory: Category) {
        newCategory.id = id
        db.collection("categories")
            .document(id.toString())
            .set(newCategory.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Updated category with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating category", e)
            }

    }

    fun deleteCategoryList(id: Long) {
        db.collection("categories")
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Deleted category with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting category", e)
            }
    }

    fun deleteCategoriesOfTeam(teamId: Long) {
        db.collection("categories")
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    db.collection("categories").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Deleted categories with team ID: $teamId")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting categories", e)
                        }
                }
            }
    }
}