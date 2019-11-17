package com.grafsemmel.translationfun.di

import com.grafsemmel.translationfun.data.source.LocalTranslationSourceImpl
import com.grafsemmel.translationfun.repository.TranslationRepository
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import com.grafsemmel.translationtun.domain.source.LocalTranslationSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LocalTranslationSource> { LocalTranslationSourceImpl(get()) }
    single<TranslationRepository> { TranslationRepository(get(), get()) }
    viewModel<TranslationViewModel> { TranslationViewModel(get()) }
}