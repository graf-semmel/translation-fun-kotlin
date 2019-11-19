package com.grafsemmel.translationfun.data.model

import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ActiveTranslation {
    private val _source: PublishSubject<ActiveTranslationState> = PublishSubject.create()
    val state: Observable<ActiveTranslationState> = _source

    fun saved(item: TranslationItem) = _source.onNext(ActiveTranslationState.New(item))

    fun updated(item: TranslationItem) = _source.onNext(ActiveTranslationState.Update(item))

    fun failed() = _source.onNext(ActiveTranslationState.Error)
}
