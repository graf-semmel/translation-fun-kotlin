package com.grafsemmel.translationfun.data.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.grafsemmel.translationfun.data.local.database.entity.TranslationEntity
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface TranslationDao {
    @Query("SELECT * from translation_item ORDER BY text")
    fun getAll(): LiveData<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY date DESC")
    fun getAllOrderedByDate(): Observable<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY views DESC")
    fun getAllOrderedByViews(): Observable<List<TranslationEntity>>

    @Insert
    fun insert(pTranslationEntity: TranslationEntity)

    @Update
    fun update(pTranslationEntity: TranslationEntity)

    @Query("SELECT * from translation_item WHERE text = :pText")
    fun getByText(pText: String): Maybe<TranslationEntity>

    @Query("SELECT * from translation_item ORDER BY date DESC LIMIT :limit")
    fun getMostRecent(limit: Int): LiveData<List<TranslationEntity>>

    @Query("SELECT * from translation_item ORDER BY views DESC LIMIT :limit")
    fun getMostViewed(limit: Int): LiveData<List<TranslationEntity>>

    @Query("DELETE FROM translation_item WHERE text=:query")
    fun delete(query: String)

    @Query("DELETE FROM translation_item")
    fun deleteAll()
}
