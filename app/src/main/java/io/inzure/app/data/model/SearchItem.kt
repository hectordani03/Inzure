package io.inzure.app.data.model

sealed class SearchItem {
    data class InsuranceItem(
        val imageRes: Int,
        val companyLogo: Int,
        val companyName: String,
        val description: String
    ) : SearchItem()

    data class ChatItem(
        val userName: String,
        val userCompany: String,
        val userImageRes: Int,
        val onClick: () -> Unit
    ) : SearchItem()
}
