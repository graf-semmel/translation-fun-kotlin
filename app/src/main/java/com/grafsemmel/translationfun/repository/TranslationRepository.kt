package com.grafsemmel.translationfun.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.webservice.TranslationWebservice
import com.grafsemmel.translationfun.webservice.TranslationWebservice.SimpleCallback
import com.grafsemmel.translationfun.webservice.TranslationWebservice.WebserviceInitialisationCallback
import com.grafsemmel.translationtun.domain.model.TranslationItem
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import java.util.*

class TranslationRepository(
        application: Application,
        private val localSource: LocalTranslationSource
) {
    private val mAppExecutors = AppExecutors.instance
    private var mInitialised: Boolean = false
    private val mTranslationWebservice = TranslationWebservice.getInstance(application.getString(R.string.api_key), object : WebserviceInitialisationCallback {
        override fun onInitialised() {
            mInitialised = true
        }

        override fun onError() {
            mInitialised = false
        }
    })
    private val mActiveTranslation = MutableLiveData<ActiveTranslationState>()
    val activeTranslation: LiveData<ActiveTranslationState>
        get() = mActiveTranslation

    fun getMostRecentTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByDate()

    fun getMostViewedTranslations(): LiveData<List<TranslationItem>> = localSource.getAllOrderedByViews()

    fun insert(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { localSource.insert(pTranslationItem) }

    fun delete(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { localSource.delete(pTranslationItem.text) }

    fun update(pTranslationItem: TranslationItem) = mAppExecutors.diskIO().execute { localSource.update(pTranslationItem) }

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String) {
        getTranslationByText(pText, object : SimpleCallback<TranslationItem> {
            override fun onResult(translation: TranslationItem) {
                translation.copy(views = translation.views + 1, date = Date()).let {
                    update(it)
                    mActiveTranslation.value = ActiveTranslationState.updated(it)
                }
            }

            override fun onNoResult() {
                translateByGoogle(pText, pSourceLngCode, pTargetLngCode)
            }
        })
    }

    fun getTranslationByText(pText: String, pCallback: SimpleCallback<TranslationItem>) = mAppExecutors.diskIO().execute {
        val translation = localSource.getByText(pText)
        mAppExecutors.mainThread().execute {
            when (translation) {
                null -> pCallback.onNoResult()
                else -> pCallback.onResult(translation)
            }
        }
    }

    private fun translateByGoogle(pText: String, pSourceLngCode: String, pTargetLngCode: String) {
        when (mInitialised) {
            true -> mTranslationWebservice.translate(pText, pSourceLngCode, pTargetLngCode, object : SimpleCallback<String> {
                override fun onResult(translation: String) {
                    val translationItem = TranslationItem(pText, translation, pSourceLngCode, pTargetLngCode, Date(), 0)
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
