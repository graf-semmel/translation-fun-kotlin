package com.grafsemmel.translationfun.domain.model

sealed class ActiveTranslationState {
    data class Saved(val item: TranslationItem) : ActiveTranslationState()
    data class Updated(val item: TranslationItem) : ActiveTranslationState()
    object Failed : ActiveTranslationState()
}