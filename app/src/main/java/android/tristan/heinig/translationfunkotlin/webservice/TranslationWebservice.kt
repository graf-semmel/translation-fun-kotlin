package android.tristan.heinig.translationfunkotlin.webservice

import android.tristan.heinig.translationfunkotlin.AppExecutors
import com.google.cloud.translate.Translate
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateException
import com.google.cloud.translate.TranslateOptions

class TranslationWebservice private constructor(pApiKey: String, pCallback: WebserviceInitialisationCallback) {
    private val mAppExecutors: AppExecutors = AppExecutors.instance
    private var mPTranslateClient: Translate? = null
    private val API_TRANSLATION_TYPE = "text"
    var isInitialised = false
        private set

    init {
        runOnNetworkThread(Runnable {
            val translateClient = TranslateOptions.getDefaultInstance().toBuilder().setApiKey(pApiKey).build().service
            runOnMainThread(Runnable {
                mPTranslateClient = translateClient
                isInitialised = true
                pCallback.onInitialised()
            })
        })
    }

    fun translate(pText: String, pSourceLngCode: String, pTargetLngCode: String, pCallback: SimpleCallback<String>) = when (isInitialised) {
        true -> runOnNetworkThread(Runnable {
            try {
                // format has to be text, otherwise we get special chars issues
                val translation = mPTranslateClient?.translate(
                        pText, TranslateOption.sourceLanguage(pSourceLngCode),
                        TranslateOption.targetLanguage(pTargetLngCode),
                        TranslateOption.format(API_TRANSLATION_TYPE)
                )
                when (translation) {
                    null -> runOnMainThread(Runnable { pCallback.onNoResult() })
                    else -> runOnMainThread(Runnable { pCallback.onResult(translation.translatedText) })
                }
            } catch (pException: TranslateException) {
                pException.printStackTrace()
                runOnMainThread(Runnable { pCallback.onNoResult() })
            }
        })
        else -> pCallback.onNoResult()
    }

    private fun runOnMainThread(pRunnable: Runnable) = mAppExecutors.mainThread().execute(pRunnable)

    private fun runOnNetworkThread(pRunnable: Runnable) = mAppExecutors.networkIO().execute(pRunnable)

    interface SimpleCallback<T> {
        fun onResult(translation: T)

        fun onNoResult()
    }

    interface WebserviceInitialisationCallback {
        fun onInitialised()

        fun onError()
    }

    companion object {
        private var INSTANCE: TranslationWebservice? = null

        fun getInstance(pApiKey: String, pCallback: WebserviceInitialisationCallback): TranslationWebservice =
                INSTANCE ?: TranslationWebservice(pApiKey, pCallback).also { INSTANCE = it }
    }
}
