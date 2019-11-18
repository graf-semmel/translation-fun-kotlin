package com.grafsemmel.translationfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grafsemmel.translationtun.domain.model.ActiveTranslationState
import com.grafsemmel.translationtun.domain.model.TranslationItem
import com.grafsemmel.translationtun.domain.repository.TranslationRepository

class TranslationViewModel(private val repository: TranslationRepository) : ViewModel() {
    val mostRecentTranslations: LiveData<List<TranslationItem>> = repository.getMostRecentTranslations()
    val mostViewedTranslations: LiveData<List<TranslationItem>> = repository.getMostViewedTranslations()
    val activeTranslation: LiveData<ActiveTranslationState> = repository.getActiveTranslation()

    fun save(pTranslationItem: TranslationItem) = repository.insert(pTranslationItem)

    fun remove(pTranslationItem: TranslationItem) = repository.delete(pTranslationItem)

    fun restore(pTranslationItem: TranslationItem) = repository.insert(pTranslationItem)

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String) = repository.translate(pText, pSourceLngCode, pTargetLngCode)
}
