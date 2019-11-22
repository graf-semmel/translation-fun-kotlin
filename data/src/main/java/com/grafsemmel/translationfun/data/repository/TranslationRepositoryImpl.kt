package com.grafsemmel.translationfun.data.repository

import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import com.grafsemmel.translationfun.domain.source.LocalTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class TranslationRepositoryImpl(
        private val localSource: LocalTranslationSource,
        private val remoteSource: RemoteTranslationSource,
        private val sourceLngCode: String,
        private val targetLngCode: String
) : TranslationRepository {
    override fun getMostRecentTranslations(): Observable<List<TranslationItem>> = localSource.getAllOrderedByDate()

    override fun getMostViewedTranslations(): Observable<List<TranslationItem>> = localSource.getAllOrderedByViews()

    override fun insert(item: TranslationItem) = runOnDisk { localSource.insert(item) }

    override fun delete(item: TranslationItem) = runOnDisk { localSource.delete(item) }

    override fun update(item: TranslationItem) = runOnDisk { localSource.update(item) }

    override fun translate(text: String): Single<ActiveTranslationState> = localSource.getByText(text)
            .map { dbEntity ->
                // update if exists in database
                ActiveTranslationState.Update(dbEntity.copy(views = dbEntity.views + 1, date = Date()).also {
                    update(it)
                }) as ActiveTranslationState
            }
            .switchIfEmpty(remoteSource.translate(text, sourceLngCode, targetLngCode).map { ActiveTranslationState.New(it) })
            .onErrorReturn { ActiveTranslationState.Error }

    private fun runOnDisk(task: () -> Unit) {
        AppExecutors.diskIO().execute(task)
    }
}
