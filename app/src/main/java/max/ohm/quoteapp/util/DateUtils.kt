package max.ohm.quoteapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    private val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getCurrentIsoTimestamp(): String {
        return isoFormat.format(Date())
    }

    fun getCurrentDate(): String {
        return dateOnlyFormat.format(Date())
    }

    fun formatForDisplay(isoDate: String): String {
        return try {
            val date = isoFormat.parse(isoDate)
            date?.let { displayFormat.format(it) } ?: isoDate
        } catch (e: Exception) {
            isoDate
        }
    }

    fun isSameDay(date1: String, date2: String): Boolean {
        return try {
            val d1 = dateOnlyFormat.parse(date1)
            val d2 = dateOnlyFormat.parse(date2)
            d1 == d2
        } catch (e: Exception) {
            false
        }
    }
}
