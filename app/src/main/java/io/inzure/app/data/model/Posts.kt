package io.inzure.app.data.model

data class Posts(
    var id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val userId: String = "",
    val tipo: String = "",
    var image: String = "",
    val date: String = "" // Ahora será una cadena formateada
) {
    constructor() : this("", "", "", "", "", "", "")
}
