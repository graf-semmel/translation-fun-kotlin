package com.grafsemmel.translationfun.data.local

import com.grafsemmel.translationfun.data.local.database.TranslationDatabase
import com.grafsemmel.translationfun.data.local.database.entity.TranslationEntity
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.source.LocalTranslationSource
import io.reactivex.Maybe
import io.reactivex.Observable

class LocalTranslationSourceImpl(database: TranslationDatabase) : LocalTranslationSource {
    private val dao = database.translationDao()

    override fun getAllOrderedByDate(): Observable<List<TranslationItem>> = dao.getAllOrderedByDate().map { it.toDomain() }

    override fun getAllOrderedByViews(): Observable<List<TranslationItem>> = dao.getAllOrderedByViews().map { it.toDomain() }

    override fun getByText(text: String): Maybe<TranslationItem> = dao.getByText(text).map { it.toDomain() }

    override fun insert(item: TranslationItem) = dao.insert(item.toDomain())

    override fun update(item: TranslationItem) = dao.update(item.toDomain())

    override fun delete(item: TranslationItem) = delete(item.text)

    override fun delete(text: String) = dao.delete(text)

    private fun TranslationItem.toDomain(): TranslationEntity = TranslationEntity(
            text,
            translation,
            source,
            target,
            date,
            views
    )

    private fun List<TranslationEntity>.toDomain(): List<TranslationItem> = this.map { it.toDomain() }
}