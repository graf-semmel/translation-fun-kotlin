package com.grafsemmel.translationfun.di

import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.local.LocalTranslationSourceImpl
import com.grafsemmel.translationfun.data.local.database.TranslationDatabase
import com.grafsemmel.translationfun.data.remote.TranslationWebservice
import com.grafsemmel.translationfun.data.repository.TranslationRepositoryImpl
import com.grafsemmel.translationfun.domain.repository.TranslationRepository
import com.grafsemmel.translationfun.domain.source.LocalTranslationSource
import com.grafsemmel.translationfun.domain.source.RemoteTranslationSource
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppExecutors }
    single { TranslationDatabase.getInstance(get()) }
    single<LocalTranslationSource> { LocalTranslationSourceImpl(get(), get()) }
    single<RemoteTranslationSource> { TranslationWebservice(get(), androidContext().getString(R.string.api_key)) }
    single<TranslationRepository> {
        TranslationRepositoryImpl(
                get(),
                get(),
                get(),
                androidContext().getString(R.string.source_lng_code),
                androidContext().getString(R.string.target_lng_code)
        )
    }
    viewModel { TranslationViewModel(get()) }
}