// PostRepository.kt
package io.inzure.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import io.inzure.app.data.model.Post
import kotlinx.coroutines.tasks.await

class PostRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addPost(post: Post): Boolean {
        return try {
            db.collection("Posts")
                .add(post)
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error adding post: ${e.message}")
            false
        }
    }

    suspend fun updatePost(post: Post): Boolean {
        return try {
            db.collection("Posts")
                .document(post.id)
                .set(post)
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating post: ${e.message}")
            false
        }
    }

    suspend fun deletePost(post: Post): Boolean {
        return try {
            db.collection("Posts")
                .document(post.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error deleting post: ${e.message}")
            false
        }
    }

    fun getPosts(onPostsChanged: (List<Post>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("Posts")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error fetching posts: ${error.message}")
                    return@addSnapshotListener
                }

                val postsList = snapshot?.documents?.mapNotNull { document ->
                    val post = document.toObject(Post::class.java)
                    post?.copy(id = document.id)
                } ?: emptyList()

                onPostsChanged(postsList)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
