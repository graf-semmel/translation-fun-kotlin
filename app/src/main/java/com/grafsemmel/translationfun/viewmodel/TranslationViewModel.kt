package com.grafsemmel.translationfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grafsemmel.translationfun.repository.ActiveTranslationState
import com.grafsemmel.translationfun.repository.TranslationRepository
import com.grafsemmel.translationtun.domain.model.TranslationItem

class TranslationViewModel(private val repository: TranslationRepository) : ViewModel() {
    val mostRecentTranslations: LiveData<List<TranslationItem>> = repository.getMostRecentTranslations()
    val mostViewedTranslations: LiveData<List<TranslationItem>> = repository.getMostViewedTranslations()
    val activeTranslation: LiveData<ActiveTranslationState> = repository.activeTranslation

    fun save(pTranslationItem: TranslationItem) = repository.insert(pTranslationItem)

    fun remove(pTranslationItem: TranslationItem) = repository.delete(pTranslationItem)

    fun restore(pTranslationItem: TranslationItem) = repository.insert(pTranslationItem)

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String) = repository.translate(pText, pSourceLngCode, pTargetLngCode)
}
