package com.grafsemmel.translationfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grafsemmel.translationfun.data.model.SingleLiveEvent
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TranslationViewModel(private val repository: TranslationRepository) : ViewModel() {
    private var disposable: CompositeDisposable? = CompositeDisposable()
    private val _mostRecentTranslations = MutableLiveData<List<TranslationItem>>()
    val mostRecentTranslations: LiveData<List<TranslationItem>> = _mostRecentTranslations
    private val _mostViewedTranslations = MutableLiveData<List<TranslationItem>>()
    val mostViewedTranslations: LiveData<List<TranslationItem>> = _mostViewedTranslations
    private val _activeTranslation = SingleLiveEvent<ActiveTranslationState>()
    val activeTranslation: LiveData<ActiveTranslationState> = _activeTranslation

    init {
        repository.getMostRecentTranslations().onResult { _mostRecentTranslations.postValue(it) }
        repository.getMostViewedTranslations().onResult { _mostViewedTranslations.postValue(it) }
    }

    fun save(item: TranslationItem) = repository.insert(item)

    fun remove(item: TranslationItem) = repository.delete(item)

    fun restore(item: TranslationItem) = repository.insert(item)

    fun translate(text: String) = repository.translate(text)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                    { _activeTranslation.postValue(it) },
                    { /* do nothing */ }
            )

    override fun onCleared() {
        super.onCleared()
        disposable?.clear()
        disposable = null
    }

    private fun <T> Observable<T>.onResult(onResult: (T) -> Unit) {
        disposable?.add(this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { onResult.invoke(it) })
    }
}

