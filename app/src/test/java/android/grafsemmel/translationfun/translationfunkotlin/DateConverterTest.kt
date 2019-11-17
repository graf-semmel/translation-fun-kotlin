package android.grafsemmel.translationfun.translationfunkotlin

import com.grafsemmel.translationfun.data.source.database.entity.DateConverter
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class DateConverterTest {
    @Test
    fun convertToDateIsCorrect() {
        val timestamp = System.currentTimeMillis()
        val date = com.grafsemmel.translationfun.data.source.database.entity.DateConverter.toDate(timestamp)
        assertNotNull(date)
        assertEquals(timestamp, date!!.time)
    }

    @Test
    fun convertToTimestampIsCorrect() {
        val date = Date()
        val timestamp = com.grafsemmel.translationfun.data.source.database.entity.DateConverter.toTimestamp(date)
        assertNotNull(timestamp)
        assertEquals(date.time, timestamp!!.toLong())
    }

    @Test
    fun convertNullReturnsNull() {
        val timestamp = com.grafsemmel.translationfun.data.source.database.entity.DateConverter.toTimestamp(null)
        assertNull(timestamp)
        val date = com.grafsemmel.translationfun.data.source.database.entity.DateConverter.toDate(null)
        assertNull(date)
    }
}