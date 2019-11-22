package com.grafsemmel.translationfun.domain.source

import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Single

interface RemoteTranslationSource {
    fun translate(text: String, sourceLngCode: String, targetLngCode: String): Single<TranslationItem>
}