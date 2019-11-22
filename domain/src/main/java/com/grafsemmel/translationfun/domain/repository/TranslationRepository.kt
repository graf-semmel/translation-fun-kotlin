package com.grafsemmel.translationfun.domain.repository

import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Observable
import io.reactivex.Single

interface TranslationRepository {
    fun getMostRecentTranslations(): Observable<List<TranslationItem>>
    fun getMostViewedTranslations(): Observable<List<TranslationItem>>
    fun insert(item: TranslationItem)
    fun delete(item: TranslationItem)
    fun update(item: TranslationItem)
    fun translate(text: String): Single<ActiveTranslationState>
}