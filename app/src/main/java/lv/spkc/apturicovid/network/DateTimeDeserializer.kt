package lv.spkc.apturicovid.network

import com.google.gson.*
import org.joda.time.DateTime
import java.lang.reflect.Type

class DateTimeDeserializer : JsonDeserializer<DateTime> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime? {
        return DateTime.parse(json.asString)
    }
}