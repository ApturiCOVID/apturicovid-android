package lv.spkc.apturicovid.utils

data class ApiError(
        var title: String,
        var code: Int? = null,
        var httpCode: Int? = null,
        var messages: Map<String, List<String>>? = mapOf()
): Throwable() {
    override val message: String
            get() = getErrorMessage()

    fun getErrorMessage(): String {
        return messages?.values?.firstOrNull()?.firstOrNull() ?: title
    }
}
