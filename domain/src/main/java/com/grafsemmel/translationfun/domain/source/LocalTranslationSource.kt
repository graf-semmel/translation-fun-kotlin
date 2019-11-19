package com.grafsemmel.translationfun.domain.source

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Observable

interface LocalTranslationSource {
    fun getAll(): LiveData<List<TranslationItem>>
    fun getAllOrderedByDate(): Observable<List<TranslationItem>>
    fun getAllOrderedByViews(): LiveData<List<TranslationItem>>
    fun getByText(text: String): TranslationItem?
    fun getMostRecent(limit: Int): LiveData<List<TranslationItem>>
    fun getMostViewed(limit: Int): LiveData<List<TranslationItem>>
    fun insert(item: TranslationItem)
    fun update(item: TranslationItem)
    fun deleteAll()
    fun delete(item: TranslationItem)
    fun delete(text: String)
}