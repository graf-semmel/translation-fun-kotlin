package com.grafsemmel.translationfun.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.grafsemmel.translationfun.data.source.database.TranslationDatabase
import com.grafsemmel.translationfun.data.source.database.entity.TranslationEntity
import com.grafsemmel.translationtun.domain.model.TranslationItem
import com.grafsemmel.translationtun.domain.source.TranslationDataSource

class LocalTranslationSource(application: Application) : TranslationDataSource {
    private val mTranslationDao = TranslationDatabase.getInstance(application).translationDao()

    override fun getAll(): LiveData<List<TranslationItem>> = mTranslationDao.getAll().toDomain()

    override fun getAllOrderedByDate(): LiveData<List<TranslationItem>> = mTranslationDao.getAllOrderedByDate().toDomain()

    override fun getAllOrderedByViews(): LiveData<List<TranslationItem>> = mTranslationDao.getAllOrderedByViews().toDomain()

    override fun getByText(text: String) = mTranslationDao.getByText(text)?.toDomain()

    override fun insert(item: TranslationItem) = mTranslationDao.insert(item.toDomain())

    override fun update(item: TranslationItem) = mTranslationDao.update(item.toDomain())

    override fun delete(item: TranslationItem) = mTranslationDao.delete(item.text)

    override fun getMostRecent(limit: Int): LiveData<List<TranslationItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMostViewed(limit: Int): LiveData<List<TranslationItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun TranslationItem.toDomain(): TranslationEntity = TranslationEntity(
            text,
            translation,
            source,
            target,
            date, views
    )

    private fun List<TranslationEntity>.toDomain(): List<TranslationItem> = this.map { it.toDomain() }
    private fun LiveData<List<TranslationEntity>>.toDomain(): LiveData<List<TranslationItem>> =
            Transformations.map(this) { list -> list.toDomain() }
}