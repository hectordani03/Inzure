package io.inzure.app.data.model

data class Insurer(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    var fiscalId: String = "",
    var direction: String = "",
    var licenseNumber: String = "",
    var companyName: String = "",
    var description: String = "",
    var birthDate: String = "",
    var image: String = "",
    var role: String = ""
) {
    constructor() : this("", "", "", "", "", "", "",
        "", "", "", "", "", "")
}
