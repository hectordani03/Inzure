// Post.kt
package io.inzure.app.data.model

data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
