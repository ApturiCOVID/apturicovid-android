package lv.spkc.apturicovid

import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset


@Throws(IOException::class)
fun Response.getBody(): String {
    val responseBody = body

    return responseBody?.let {
        val source = responseBody.source()
        source.request(java.lang.Long.MAX_VALUE) // request the entire body.
        val buffer = source.buffer()
        // clone buffer before reading from it
        buffer.clone().readString(Charset.forName("UTF-8"))
    } ?: "[empty]"
}