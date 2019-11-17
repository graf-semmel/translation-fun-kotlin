package com.grafsemmel.translationfun.data.source.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.grafsemmel.translationfun.data.source.database.entity.TranslationEntity

@Dao
interface TranslationDao {
    @Query("SELECT * from translation_item ORDER BY text")
    fun getAll(): LiveData<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY date DESC")
    fun getAllOrderedByDate(): LiveData<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY views DESC")
    fun getAllOrderedByViews(): LiveData<List<TranslationEntity>>

    @Insert
    fun insert(pTranslationEntity: TranslationEntity)

    @Update
    fun update(pTranslationEntity: TranslationEntity)

    @Query("SELECT * from translation_item WHERE text = :pText")
    fun getByText(pText: String): TranslationEntity?

    @Query("SELECT * from translation_item ORDER BY date DESC LIMIT :limit")
    fun getMostRecent(limit: Int): LiveData<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY views DESC LIMIT :limit")
    fun getMostViewed(limit: Int): LiveData<List<TranslationEntity>>

    @Query("DELETE FROM translation_item WHERE text=:query")
    fun delete(query: String)

    @Query("DELETE FROM translation_item")
    fun deleteAll()
}
