package com.grafsemmel.translationtun.domain.source

interface RemoteTranslationSource {
    fun translate(text: String, sourceLngCode: String, targetLngCode: String, callback: SimpleCallback<String>)
    interface SimpleCallback<T> {
        fun onResult(translation: T)

        fun onNoResult()
    }
}