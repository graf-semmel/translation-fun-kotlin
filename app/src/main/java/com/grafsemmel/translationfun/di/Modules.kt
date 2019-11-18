package com.grafsemmel.translationfun.di

import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.local.LocalTranslationSourceImpl
import com.grafsemmel.translationfun.data.local.database.TranslationDatabase
import com.grafsemmel.translationfun.data.remote.TranslationWebservice
import com.grafsemmel.translationfun.data.repository.TranslationRepositoryImpl
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import com.grafsemmel.translationtun.domain.repository.TranslationRepository
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import com.grafsemmel.translationtun.domain.source.RemoteTranslationSource
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppExecutors }
    single { TranslationDatabase.getInstance(get()) }
    single<LocalTranslationSource> { LocalTranslationSourceImpl(get(), get()) }
    single<RemoteTranslationSource> { TranslationWebservice(get(), androidContext().getString(R.string.api_key)) }
    single<TranslationRepository> { TranslationRepositoryImpl(get(), get(), get()) }
    viewModel { TranslationViewModel(get()) }
}