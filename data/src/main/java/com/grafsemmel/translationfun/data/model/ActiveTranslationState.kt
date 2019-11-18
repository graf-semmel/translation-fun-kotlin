package com.grafsemmel.translationfun.data.model

import androidx.lifecycle.MutableLiveData
import com.grafsemmel.translationtun.domain.model.ActiveTranslationState
import com.grafsemmel.translationtun.domain.model.TranslationItem

class ActiveTranslation : MutableLiveData<ActiveTranslationState>() {
    fun saved(item: TranslationItem) = postValue(ActiveTranslationState.Saved(item))

    fun updated(item: TranslationItem) = postValue(ActiveTranslationState.Updated(item))

    fun failed() = postValue(ActiveTranslationState.Failed)
}
