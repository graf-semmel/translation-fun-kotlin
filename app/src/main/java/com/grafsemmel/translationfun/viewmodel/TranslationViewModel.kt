package com.grafsemmel.translationfun.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.repository.ActiveTranslationState
import com.grafsemmel.translationfun.repository.TranslationRepository
import com.grafsemmel.translationtun.domain.model.TranslationItem

class TranslationViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: TranslationRepository = TranslationRepository(application)
    val mostRecentTranslations: LiveData<List<TranslationItem>>
    val mostViewedTranslations: LiveData<List<TranslationItem>>
    val activeTranslation: LiveData<ActiveTranslationState>

    init {
        mostRecentTranslations = mRepository.getMostRecentTranslations()
        mostViewedTranslations = mRepository.getMostViewedTranslations()
        activeTranslation = mRepository.activeTranslation
    }

    fun save(pTranslationItem: TranslationItem) = mRepository.insert(pTranslationItem)

    fun remove(pTranslationItem: TranslationItem) = mRepository.delete(pTranslationItem)

    fun restore(pTranslationItem: TranslationItem) = mRepository.insert(pTranslationItem)

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String) =
            mRepository.translate(pText, pSourceLngCode, pTargetLngCode)
}
