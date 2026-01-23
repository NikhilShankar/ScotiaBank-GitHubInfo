package rebirth.nixaclabs.sbgithubinfo.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.formatDate(): String? {
    return try {
        val zonedDateTime = ZonedDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        null
    }
}