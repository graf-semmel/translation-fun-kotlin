package com.grafsemmel.translationfun.domain.source

import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Maybe
import io.reactivex.Observable

interface LocalTranslationSource {
    fun getAllOrderedByDate(): Observable<List<TranslationItem>>
    fun getAllOrderedByViews(): Observable<List<TranslationItem>>
    fun getByText(text: String): Maybe<TranslationItem>
    fun insert(item: TranslationItem)
    fun update(item: TranslationItem)
    fun delete(item: TranslationItem)
    fun delete(text: String)
}