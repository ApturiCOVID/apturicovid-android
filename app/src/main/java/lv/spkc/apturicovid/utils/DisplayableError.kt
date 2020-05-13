package lv.spkc.apturicovid.utils

data class DisplayableError(
        var errorMessageId: Int,
        var errorDialogButtonId: Int
): Throwable()
