package com.grafsemmel.translationfun.di

import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.local.LocalTranslationSourceImpl
import com.grafsemmel.translationfun.data.local.database.TranslationDatabase
import com.grafsemmel.translationfun.data.remote.TranslationWebservice
import com.grafsemmel.translationfun.data.repository.TranslationRepository
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import com.grafsemmel.translationtun.domain.source.RemoteTranslationSource
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LocalTranslationSource> { LocalTranslationSourceImpl(get(), get()) }
    single<RemoteTranslationSource> { TranslationWebservice(get(), androidContext().getString(R.string.api_key)) }
    single { TranslationDatabase.getInstance(get()) }
    single { AppExecutors }
    single { TranslationRepository(get(), get(), get()) }
    viewModel { TranslationViewModel(get()) }
}