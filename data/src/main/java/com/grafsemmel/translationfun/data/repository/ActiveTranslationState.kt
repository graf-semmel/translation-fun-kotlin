package com.grafsemmel.translationfun.data.repository

import androidx.lifecycle.MutableLiveData
import com.grafsemmel.translationfun.data.repository.ActiveTranslationState.STATE
import com.grafsemmel.translationtun.domain.model.TranslationItem

class ActiveTranslation : MutableLiveData<ActiveTranslationState>() {
    fun saved(item: TranslationItem) = postValue(ActiveTranslationState(item, STATE.SAVED))

    fun updated(item: TranslationItem) = postValue(ActiveTranslationState(item, STATE.UPDATED))

    fun failed() = postValue(ActiveTranslationState(null, STATE.FAILED))
}

data class ActiveTranslationState(val item: TranslationItem?, val state: STATE) {
    enum class STATE {
        SAVED, FAILED, UPDATED
    }
}
