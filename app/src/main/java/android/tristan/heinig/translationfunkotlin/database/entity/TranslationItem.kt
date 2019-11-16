package android.tristan.heinig.translationfunkotlin.database.entity

import android.tristan.heinig.translationfunkotlin.database.DateConverter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "translation_item")
@TypeConverters(DateConverter::class)
data class TranslationItem(
        @PrimaryKey
        @ColumnInfo(name = "text")
        var text: String,
        @ColumnInfo(name = "translation")
        var translation: String,
        @ColumnInfo(name = "source")
        var source: String,
        @ColumnInfo(name = "target")
        var target: String,
        @ColumnInfo(name = "date")
        var date: Date = Date(),
        @ColumnInfo(name = "views")
        var views: Int = 1
) {
    // we need a default constructor for Room
    constructor() : this("", "", "", "", Date(), 0)

    constructor(text: String, translation: String, source: String, target: String) :
            this(text, translation, source, target, Date(), 1)
}
