package com.grafsemmel.translationfun.ui.popular

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.domain.model.TranslationItem

class PopularFragment : AbstractTranslationListFragment() {
    override fun getLiveDataToObserve(): LiveData<List<TranslationItem>> = viewModel.mostViewedTranslations
}