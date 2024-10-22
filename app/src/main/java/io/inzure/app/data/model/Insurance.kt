package io.inzure.app.data.model

data class Insurance(
    var id: String = "",
    var name: String = "",
    var type: String = "",
    var price: Double = 0.0,  // Cambia a Double
    var description: String = "",
    var image: String = "",
    var active: Boolean = true
) {
    constructor() : this("", "", "", 0.0, "", "", true)
}
