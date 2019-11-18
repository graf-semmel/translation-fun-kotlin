package com.grafsemmel.translationtun.domain.repository

import androidx.lifecycle.LiveData
import com.grafsemmel.translationtun.domain.model.ActiveTranslationState
import com.grafsemmel.translationtun.domain.model.TranslationItem

interface TranslationRepository {
    fun getActiveTranslation(): LiveData<ActiveTranslationState>
    fun getMostRecentTranslations(): LiveData<List<TranslationItem>>
    fun getMostViewedTranslations(): LiveData<List<TranslationItem>>
    fun insert(item: TranslationItem)
    fun delete(item: TranslationItem)
    fun update(item: TranslationItem)
    fun translate(text: String, sourceLngCode: String, targetLngCode: String)
}