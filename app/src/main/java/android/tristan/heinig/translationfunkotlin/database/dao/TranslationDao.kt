package android.tristan.heinig.translationfunkotlin.database.dao

import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TranslationDao {
    @Query("SELECT * from translation_item ORDER BY text")
    fun getAll(): LiveData<List<TranslationItem>>

    @Query("SELECT * from translation_item ORDER BY date DESC")
    fun getAllOrderedByDate(): LiveData<List<TranslationItem>>

    @Query("SELECT * from translation_item ORDER BY views DESC")
    fun getAllOrderedByViews(): LiveData<List<TranslationItem>>

    @Insert
    fun insert(pTranslationItem: TranslationItem)

    @Update
    fun update(pTranslationItem: TranslationItem)

    @Query("SELECT * from translation_item WHERE text = :pText")
    fun getByText(pText: String): TranslationItem?

    @Query("SELECT * from translation_item ORDER BY date DESC LIMIT :limit")
    fun getMostRecent(limit: Int): LiveData<List<TranslationItem>>

    @Query("SELECT * from translation_item ORDER BY views DESC LIMIT :limit")
    fun getMostViewed(limit: Int): LiveData<List<TranslationItem>>

    @Query("DELETE FROM translation_item WHERE text=:pText")
    fun delete(pText: String)

    @Query("DELETE FROM translation_item")
    fun deleteAll()
}
