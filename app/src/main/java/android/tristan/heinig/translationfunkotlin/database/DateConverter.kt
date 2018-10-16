package android.tristan.heinig.translationfunkotlin.database

import android.arch.persistence.room.TypeConverter
import java.util.*

object DateConverter {

  @TypeConverter
  @JvmStatic
  fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }

  @TypeConverter
  @JvmStatic
  fun toTimestamp(date: Date?): Long? = date?.time
}