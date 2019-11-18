package com.grafsemmel.translationfun.ui.recent

import androidx.lifecycle.LiveData
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.ui.popular.AbstractTranslationListFragment

class RecentFragment : AbstractTranslationListFragment() {
    override fun getLiveDataToObserve(): LiveData<List<TranslationItem>> = viewModel.mostRecentTranslations
}