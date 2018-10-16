package android.tristan.heinig.translationfunkotlin.repository

import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem

class ActiveTranslationState private constructor(val translationItem: TranslationItem?, val state: STATE) {

  enum class STATE {
    SAVED, FAILED, UPDATED
  }

  companion object {
    fun saved(pTranslationItem: TranslationItem): ActiveTranslationState = ActiveTranslationState(pTranslationItem, STATE.SAVED)

    fun updated(pTranslationItem: TranslationItem): ActiveTranslationState = ActiveTranslationState(pTranslationItem, STATE.UPDATED)

    fun failed(): ActiveTranslationState = ActiveTranslationState(null, STATE.FAILED)
  }

}
