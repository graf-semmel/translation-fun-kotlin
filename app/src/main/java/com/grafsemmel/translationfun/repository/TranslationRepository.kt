package com.grafsemmel.translationfun.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationtun.domain.model.TranslationItem
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import com.grafsemmel.translationtun.domain.source.RemoteTranslationSource
import com.grafsemmel.translationtun.domain.source.RemoteTranslationSource.SimpleCallback
import java.util.*

class TranslationRepository(
        private val localSource: LocalTranslationSource,
        private val remoteSource: RemoteTranslationSource,
        private val appExecutors: AppExecutors
) {
    private val mActiveTranslation = MutableLiveData<ActiveTranslationState>()
    val activeTranslation: LiveData<ActiveTranslationState>
        get() = mActiveTranslation

    fun getMostRecentTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByDate()

    fun getMostViewedTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByViews()

    fun insert(item: TranslationItem) = localSource.insert(item)

    fun delete(item: TranslationItem) = localSource.delete(item.text)

    fun update(item: TranslationItem) = localSource.update(item)

    fun translate(text: String, sourceLngCode: String, targetLngCode: String) {
        getTranslationByText(text, object : SimpleCallback<TranslationItem> {
            override fun onResult(translation: TranslationItem) {
                translation.copy(views = translation.views + 1, date = Date()).let {
                    update(it)
                    mActiveTranslation.value = ActiveTranslationState.updated(it)
                }
            }

            override fun onNoResult() {
                translateByGoogle(text, sourceLngCode, targetLngCode)
            }
        })
    }

    fun getTranslationByText(text: String, callback: SimpleCallback<TranslationItem>) = appExecutors.diskIO().execute {
        val translation = localSource.getByText(text)
        appExecutors.mainThread().execute {
            when (translation) {
                null -> callback.onNoResult()
                else -> callback.onResult(translation)
            }
        }
    }

    private fun translateByGoogle(pText: String, pSourceLngCode: String, pTargetLngCode: String) {
        remoteSource.translate(pText, pSourceLngCode, pTargetLngCode, object : SimpleCallback<String> {
            override fun onResult(translation: String) {
                val translationItem = TranslationItem(pText, translation, pSourceLngCode, pTargetLngCode, Date(), 0)
                mActiveTranslation.value = ActiveTranslationState.saved(translationItem)
            }

            override fun onNoResult() {
                mActiveTranslation.value = ActiveTranslationState.failed()
            }
        })
    }
}
