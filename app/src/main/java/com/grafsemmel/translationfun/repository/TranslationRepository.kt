package com.grafsemmel.translationfun.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grafsemmel.translationfun.AppExecutors
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.database.TranslationDatabase
import com.grafsemmel.translationfun.database.dao.TranslationDao
import com.grafsemmel.translationfun.database.entity.TranslationItem
import com.grafsemmel.translationfun.webservice.TranslationWebservice
import com.grafsemmel.translationfun.webservice.TranslationWebservice.SimpleCallback
import com.grafsemmel.translationfun.webservice.TranslationWebservice.WebserviceInitialisationCallback
import java.util.*

class TranslationRepository(pApplication: Application) {
    private val mAppExecutors: AppExecutors
    private val mTranslationDao: TranslationDao
    private val mTranslationWebservice: TranslationWebservice
    val mostRecentTranslations: LiveData<List<TranslationItem>>
    val mostViewedTranslations: LiveData<List<TranslationItem>>
    private val mActiveTranslation = MutableLiveData<ActiveTranslationState>()
    private val mInitialised = MutableLiveData<Boolean>()
    val activeTranslation: LiveData<ActiveTranslationState>
        get() = mActiveTranslation

    init {
        mInitialised.value = false
        mAppExecutors = AppExecutors.instance
        mTranslationDao = TranslationDatabase.getInstance(pApplication).mTranslationDao()
        mostRecentTranslations = mTranslationDao.getAllOrderedByDate()
        mostViewedTranslations = mTranslationDao.getAllOrderedByViews()
        mTranslationWebservice = TranslationWebservice
                .getInstance(pApplication.getString(R.string.api_key), object : WebserviceInitialisationCallback {
                    override fun onInitialised() {
                        mInitialised.value = true
                    }

                    override fun onError() {
                        mInitialised.value = false
                    }
                })
    }

    fun insert(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { mTranslationDao.insert(pTranslationItem) }

    fun delete(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { mTranslationDao.delete(pTranslationItem.text) }

    fun update(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { mTranslationDao.update(pTranslationItem) }

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String) {
        getTranslationByText(pText, object : SimpleCallback<TranslationItem> {
            override fun onResult(translation: TranslationItem) {
                translation.views = translation.views + 1
                translation.date = Date()
                update(translation)
                mActiveTranslation.value = ActiveTranslationState.updated(translation)
            }

            override fun onNoResult() {
                translateByGoogle(pText, pSourceLngCode, pTargetLngCode)
            }
        })
    }

    fun getTranslationByText(pText: String, pCallback: SimpleCallback<TranslationItem>) = mAppExecutors.diskIO().execute {
        val translation = mTranslationDao.getByText(pText)
        mAppExecutors.mainThread().execute {
            when (translation) {
                null -> pCallback.onNoResult()
                else -> pCallback.onResult(translation)
            }
        }
    }

    private fun translateByGoogle(pText: String, pSourceLngCode: String, pTargetLngCode: String) {
        when (mInitialised.value) {
            true -> mTranslationWebservice.translate(pText, pSourceLngCode, pTargetLngCode, object : SimpleCallback<String> {
                override fun onResult(translation: String) {
                    val translationItem = TranslationItem(pText, translation, pSourceLngCode, pTargetLngCode)
                    mActiveTranslation.value = ActiveTranslationState.saved(translationItem)
                }

                override fun onNoResult() {
                    mActiveTranslation.value = ActiveTranslationState.failed()
                }
            })
            else -> mActiveTranslation.value = ActiveTranslationState.failed()
        }
    }
}
