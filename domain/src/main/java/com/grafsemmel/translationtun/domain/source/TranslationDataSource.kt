package com.grafsemmel.translationtun.domain.source

import androidx.lifecycle.LiveData
import com.grafsemmel.translationtun.domain.model.TranslationItem

interface TranslationDataSource {
    fun getAll(): LiveData<List<TranslationItem>>
    fun getAllOrderedByDate(): LiveData<List<TranslationItem>>
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