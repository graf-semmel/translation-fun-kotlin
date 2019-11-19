package com.grafsemmel.translationfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TranslationViewModel(private val repository: TranslationRepository) : ViewModel() {
    private var disposable: CompositeDisposable? = CompositeDisposable()
    private val _mostRecentTranslations = MutableLiveData<List<TranslationItem>>()
    val mostRecentTranslations: LiveData<List<TranslationItem>> = _mostRecentTranslations
    val mostViewedTranslations: LiveData<List<TranslationItem>> = repository.getMostViewedTranslations()
    val activeTranslation: LiveData<ActiveTranslationState> = repository.getActiveTranslation()

    init {
        disposable?.add(
                repository.getMostRecentTranslations()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { _mostRecentTranslations.postValue(it) })
    }

    fun save(item: TranslationItem) = repository.insert(item)

    fun remove(item: TranslationItem) = repository.delete(item)

    fun restore(item: TranslationItem) = repository.insert(item)

    fun translate(text: String) = repository.translate(text)

    override fun onCleared() {
        super.onCleared()
        disposable?.clear()
        disposable = null
    }
}