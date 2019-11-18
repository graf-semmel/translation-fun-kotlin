package com.grafsemmel.translationfun.data.model

import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem

class ActiveTranslation : SingleLiveEvent<ActiveTranslationState>() {
    fun saved(item: TranslationItem) = postValue(ActiveTranslationState.New(item))

    fun updated(item: TranslationItem) = postValue(ActiveTranslationState.Update(item))

    fun failed() = postValue(ActiveTranslationState.Error)
}
