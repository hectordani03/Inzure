package io.inzure.app.data.model

data class User(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    var description: String = "",
    var birthDate: String = "",
    var image: String = "", 
    var role: String = "",
    var fiscalId: String = "",
    var companyName: String = "",
    var licenseNumber: String = "",
    var direction: String = "",
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "")
}


