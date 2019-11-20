package com.grafsemmel.translationfun.data.repository

import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.model.ActiveTranslation
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import com.grafsemmel.translationfun.domain.source.LocalTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource.SimpleCallback
import io.reactivex.Observable
import java.util.*

class TranslationRepositoryImpl(
        private val localSource: LocalTranslationSource,
        private val remoteSource: RemoteTranslationSource,
        private val appExecutors: AppExecutors,
        private val sourceLngCode: String,
        private val targetLngCode: String
) : TranslationRepository {
    private val _activeTranslation = ActiveTranslation()

    override fun getActiveTranslation(): Observable<ActiveTranslationState> = _activeTranslation.state

    override fun getMostRecentTranslations(): Observable<List<TranslationItem>> = localSource.getAllOrderedByDate()

    override fun getMostViewedTranslations(): Observable<List<TranslationItem>> = localSource.getAllOrderedByViews()

    override fun insert(item: TranslationItem) = runOnDisk { localSource.insert(item) }

    override fun delete(item: TranslationItem) = runOnDisk { localSource.delete(item) }

    override fun update(item: TranslationItem) = runOnDisk { localSource.update(item) }

    override fun translate(text: String) {
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
//        val translation = localSource.getByText(text)
//        translation.
//        appExecutors.mainThread().execute {
//            when (translation) {
//                null -> callback.onNoResult()
//                else -> callback.onResult(translation)
//            }
//        }
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

    private fun runOnDisk(task: () -> Unit) {
        AppExecutors.diskIO().execute(task)
    }
}
