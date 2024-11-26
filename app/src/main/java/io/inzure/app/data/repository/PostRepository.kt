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

    data class PostWithUser(
        val post: Posts,
        val profileImage: String
    )

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

    fun getCarPosts(onPostsChanged: (List<PostWithUser>) -> Unit) {

        listenerRegistration?.remove()
        listenerRegistration = db.collection("Posts")
            .whereEqualTo("tipo", "Autos") // Filtra publicaciones donde el campo 'tipo' sea 'Autos'
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error fetching car posts: ${error.message}")
                    return@addSnapshotListener
                }

                val postsList = snapshot?.documents?.mapNotNull { document ->
                    val posts = document.toObject(Posts::class.java)
                    posts?.copy(id = document.id)
                } ?: emptyList()

                val postWithUserList = mutableListOf<PostWithUser>()

                postsList.forEach { post ->
                    db.collection("Users")
                        .document(post.userId)
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val firstName = userSnapshot.getString("firstName") ?: ""
                            val lastName = userSnapshot.getString("lastName") ?: ""
                            val profileImage = userSnapshot.getString("image") ?: ""

                            // Crea una nueva entrada en la lista combinada
                            val postWithUser = PostWithUser(
                                post = post,
                                profileImage = profileImage
                            )
                            postWithUserList.add(postWithUser)

                            // Llama al callback solo cuando se han procesado todas las publicaciones
                            if (postWithUserList.size == postsList.size) {
                                onPostsChanged(postWithUserList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("PostRepository", "Error fetching user data: ${e.message}")
                        }
                }
            }
    }

    fun getPersonalPosts(onPostsChanged: (List<PostWithUser>) -> Unit) {

        listenerRegistration?.remove()
        listenerRegistration = db.collection("Posts")
            .whereEqualTo("tipo", "Personal") // Filtra publicaciones donde el campo 'tipo' sea 'Personal'
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error fetching car posts: ${error.message}")
                    return@addSnapshotListener
                }

                val postsList = snapshot?.documents?.mapNotNull { document ->
                    val posts = document.toObject(Posts::class.java)
                    posts?.copy(id = document.id)
                } ?: emptyList()

                val postWithUserList = mutableListOf<PostWithUser>()

                postsList.forEach { post ->
                    db.collection("Users")
                        .document(post.userId)
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val firstName = userSnapshot.getString("firstName") ?: ""
                            val lastName = userSnapshot.getString("lastName") ?: ""
                            val profileImage = userSnapshot.getString("image") ?: ""

                            // Crea una nueva entrada en la lista combinada
                            val postWithUser = PostWithUser(
                                post = post,
                                profileImage = profileImage
                            )
                            postWithUserList.add(postWithUser)

                            // Llama al callback solo cuando se han procesado todas las publicaciones
                            if (postWithUserList.size == postsList.size) {
                                onPostsChanged(postWithUserList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("PostRepository", "Error fetching user data: ${e.message}")
                        }
                }
            }
    }

    fun getBusinessPosts(onPostsChanged: (List<PostWithUser>) -> Unit) {

        listenerRegistration?.remove()
        listenerRegistration = db.collection("Posts")
            .whereEqualTo("tipo", "Empresarial") // Filtra publicaciones donde el campo 'tipo' sea 'Personal'
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error fetching car posts: ${error.message}")
                    return@addSnapshotListener
                }

                val postsList = snapshot?.documents?.mapNotNull { document ->
                    val posts = document.toObject(Posts::class.java)
                    posts?.copy(id = document.id)
                } ?: emptyList()

                val postWithUserList = mutableListOf<PostWithUser>()

                postsList.forEach { post ->
                    db.collection("Users")
                        .document(post.userId)
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val firstName = userSnapshot.getString("firstName") ?: ""
                            val lastName = userSnapshot.getString("lastName") ?: ""
                            val profileImage = userSnapshot.getString("image") ?: ""

                            // Crea una nueva entrada en la lista combinada
                            val postWithUser = PostWithUser(
                                post = post,
                                profileImage = profileImage
                            )
                            postWithUserList.add(postWithUser)

                            // Llama al callback solo cuando se han procesado todas las publicaciones
                            if (postWithUserList.size == postsList.size) {
                                onPostsChanged(postWithUserList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("PostRepository", "Error fetching user data: ${e.message}")
                        }
                }
            }
    }
    fun removeListener() {
        listenerRegistration?.remove()
    }
}
