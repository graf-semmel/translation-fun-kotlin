package com.grafsemmel.translationfun.data.remote

import com.google.cloud.translate.Translate
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateException
import com.google.cloud.translate.TranslateOptions
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource

class TranslationWebservice(
        private val appExecutors: AppExecutors,
        apiKey: String
) : RemoteTranslationSource {
    private var client: Translate? = null
    private val API_TRANSLATION_TYPE = "text"
    var isInitialised = false
        private set

    init {
        runOnNetworkThread(Runnable {
            val translateClient = TranslateOptions.getDefaultInstance().toBuilder().setApiKey(apiKey).build().service
            runOnMainThread(Runnable {
                client = translateClient
                isInitialised = true
            })
        })
    }

    override fun translate(text: String, sourceLngCode: String, targetLngCode: String, callback: RemoteTranslationSource.SimpleCallback<String>) = when (isInitialised) {
        true -> runOnNetworkThread(Runnable {
            try {
                // format has to be text, otherwise we get special chars issues
                val translation = client?.translate(
                        text, TranslateOption.sourceLanguage(sourceLngCode),
                        TranslateOption.targetLanguage(targetLngCode),
                        TranslateOption.format(API_TRANSLATION_TYPE)
                )
                when (translation) {
                    null -> runOnMainThread(Runnable { callback.onNoResult() })
                    else -> runOnMainThread(Runnable { callback.onResult(translation.translatedText) })
                }
            } catch (pException: TranslateException) {
                pException.printStackTrace()
                runOnMainThread(Runnable { callback.onNoResult() })
            }
        })
        else -> callback.onNoResult()
    }

    private fun runOnMainThread(runnable: Runnable) = appExecutors.mainThread().execute(runnable)

    private fun runOnNetworkThread(runnable: Runnable) = appExecutors.networkIO().execute(runnable)
}
