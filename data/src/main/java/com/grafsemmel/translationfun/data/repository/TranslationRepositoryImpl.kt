package com.grafsemmel.translationfun.data.repository

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.model.ActiveTranslation
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import com.grafsemmel.translationfun.domain.source.LocalTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource.SimpleCallback
import java.util.*

class TranslationRepositoryImpl(
        private val localSource: LocalTranslationSource,
        private val remoteSource: RemoteTranslationSource,
        private val appExecutors: AppExecutors
) : TranslationRepository {
    private val _activeTranslation = ActiveTranslation()

    override fun getActiveTranslation(): LiveData<ActiveTranslationState> = _activeTranslation

    override fun getMostRecentTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByDate()

    override fun getMostViewedTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByViews()

    override fun insert(item: TranslationItem) = localSource.insert(item)

    override fun delete(item: TranslationItem) = localSource.delete(item)

    override fun update(item: TranslationItem) = localSource.update(item)

    override fun translate(text: String, sourceLngCode: String, targetLngCode: String) {
        getTranslationByText(text, object : SimpleCallback<TranslationItem> {
            override fun onResult(translation: TranslationItem) {
                translation.copy(views = translation.views + 1, date = Date()).let {
                    update(it)
                    _activeTranslation.updated(it)
                }
            }

            override fun onNoResult() {
                translateByGoogle(text, sourceLngCode, targetLngCode)
            }
        })
    }

    private fun getTranslationByText(text: String, callback: SimpleCallback<TranslationItem>) = appExecutors.diskIO().execute {
        val translation = localSource.getByText(text)
        appExecutors.mainThread().execute {
            when (translation) {
                null -> callback.onNoResult()
                else -> callback.onResult(translation)
            }
        }
    }

    private fun translateByGoogle(text: String, sourceLngCode: String, targetLngCode: String) {
        remoteSource.translate(text, sourceLngCode, targetLngCode, object : SimpleCallback<String> {
            override fun onResult(translation: String) {
                val translationItem = TranslationItem(text, translation, sourceLngCode, targetLngCode, Date(), 0)
                _activeTranslation.saved(translationItem)
            }

            override fun onNoResult() {
                _activeTranslation.failed()
            }
        })
    }
}
