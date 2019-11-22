package com.grafsemmel.translationfun.data.remote

import com.google.cloud.translate.Translate
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateException
import com.google.cloud.translate.TranslateOptions
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource
import io.reactivex.Single
import java.util.*

class TranslationWebservice(private val apiKey: String) : RemoteTranslationSource {
    private val API_TRANSLATION_TYPE = "text"
    private val client: Translate by lazy { TranslateOptions.getDefaultInstance().toBuilder().setApiKey(apiKey).build().service }

    override fun translate(text: String, sourceLngCode: String, targetLngCode: String): Single<TranslationItem> = Single.fromCallable {
        try {
            // format has to be text, otherwise we get special chars issues
            val translation = client.translate(
                    text,
                    TranslateOption.sourceLanguage(sourceLngCode),
                    TranslateOption.targetLanguage(targetLngCode),
                    TranslateOption.format(API_TRANSLATION_TYPE)
            )
            when (translation) {
                null -> throw TranslationError(text)
                else -> TranslationItem(text, translation.translatedText, sourceLngCode, targetLngCode, Date(), 0)
            }
        } catch (exception: TranslateException) {
            throw TranslationError(text)
        }
    }

    class TranslationError(val textToTranslate: String) : Exception("Error while translation of: $textToTranslate.")
}
