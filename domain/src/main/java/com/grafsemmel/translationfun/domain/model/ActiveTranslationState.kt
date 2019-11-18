package com.grafsemmel.translationfun.domain.model

sealed class ActiveTranslationState {
    data class New(val item: TranslationItem) : ActiveTranslationState()
    data class Update(val item: TranslationItem) : ActiveTranslationState()
    object Error : ActiveTranslationState()
}