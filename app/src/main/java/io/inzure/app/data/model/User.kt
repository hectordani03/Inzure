package io.inzure.app.data.model

data class User(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    var interests: String = "",
    var birthDate: String = "",
    var image: String = "", 
    var role: String = ""
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}


