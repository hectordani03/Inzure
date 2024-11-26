// PostRepository.kt
package io.inzure.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import io.inzure.app.data.model.Posts
import kotlinx.coroutines.tasks.await

class PostRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addPost(posts: Posts): Boolean {
        return try {
            db.collection("Posts")
                .add(posts)
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error adding post: ${e.message}")
            false
        }
    }

    suspend fun updatePost(posts: Posts): Boolean {
        return try {
            db.collection("Posts")
                .document(posts.id)
                .set(posts)
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating post: ${e.message}")
            false
        }
    }

    suspend fun deletePost(posts: Posts): Boolean {
        return try {
            db.collection("Posts")
                .document(posts.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error deleting post: ${e.message}")
            false
        }
    }

    fun getPosts(onPostsChanged: (List<Posts>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("Posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error fetching posts: ${error.message}")
                    return@addSnapshotListener
                }

                val postsList = snapshot?.documents?.mapNotNull { document ->
                    val posts = document.toObject(Posts::class.java)
                    posts?.copy(id = document.id)
                } ?: emptyList()

                onPostsChanged(postsList)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
