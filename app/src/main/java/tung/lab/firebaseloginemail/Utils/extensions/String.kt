package tung.lab.firebaseloginemail.Utils.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.uuid(): UUID = UUID.fromString(this )

fun String.toCalendar(pattern: String = "yyyy.MM.dd HH:mm:ss"): Calendar? {
    val format = SimpleDateFormat(pattern, Locale.ENGLISH)
    format.parse(this)
    try {
        val date = format.parse(this@toCalendar) ?: return null
        val result = Calendar.getInstance().apply {
            time = date
        }
        return result
    } catch (e: ParseException) {
        return null
    }
}
