package com.grafsemmel.translationfun.di

import com.grafsemmel.translationfun.data.source.LocalTranslationSourceImpl
import com.grafsemmel.translationfun.data.source.database.TranslationDatabase
import com.grafsemmel.translationfun.repository.TranslationRepository
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LocalTranslationSource> { LocalTranslationSourceImpl(get()) }
    single { TranslationDatabase.getInstance(get()) }
    single { TranslationRepository(get(), get()) }
    viewModel { TranslationViewModel(get()) }
}