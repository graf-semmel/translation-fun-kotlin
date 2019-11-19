package com.grafsemmel.translationfun.domain.repository

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Observable

interface TranslationRepository {
    fun getActiveTranslation(): LiveData<ActiveTranslationState>
    fun getMostRecentTranslations(): Observable<List<TranslationItem>>
    fun getMostViewedTranslations(): LiveData<List<TranslationItem>>
    fun insert(item: TranslationItem)
    fun delete(item: TranslationItem)
    fun update(item: TranslationItem)
    fun translate(text: String)
}