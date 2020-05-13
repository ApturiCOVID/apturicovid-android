package lv.spkc.apturicovid.model

data class ContactNumber(
    var number: String = "",
    var isThirdParty: Boolean = false
)