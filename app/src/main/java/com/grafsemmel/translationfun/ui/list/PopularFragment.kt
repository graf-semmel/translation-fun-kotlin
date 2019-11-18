package com.grafsemmel.translationfun.ui.list

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.ui.list.AbstractTranslationListFragment

class PopularFragment : AbstractTranslationListFragment() {
    override fun getLiveDataToObserve(): LiveData<List<TranslationItem>> = viewModel.mostViewedTranslations
}