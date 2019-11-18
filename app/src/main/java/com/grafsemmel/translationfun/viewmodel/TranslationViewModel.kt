package com.grafsemmel.translationfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository

class TranslationViewModel(private val repository: TranslationRepository) : ViewModel() {
    val mostRecentTranslations: LiveData<List<TranslationItem>> = repository.getMostRecentTranslations()
    val mostViewedTranslations: LiveData<List<TranslationItem>> = repository.getMostViewedTranslations()
    val activeTranslation: LiveData<ActiveTranslationState> = repository.getActiveTranslation()

    fun save(item: TranslationItem) = repository.insert(item)

    fun remove(item: TranslationItem) = repository.delete(item)

    fun restore(item: TranslationItem) = repository.insert(item)

    fun translate(text: String) = repository.translate(text)
}