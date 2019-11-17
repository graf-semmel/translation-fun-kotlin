package com.grafsemmel.translationfun.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.database.entity.TranslationItem
import kotlinx.android.synthetic.main.view_translation.view.*

object ViewUtils {
    fun createTranslationView(pLayoutInflater: LayoutInflater, parent: ViewGroup, pTranslationItem: TranslationItem): View {
        val view = pLayoutInflater.inflate(R.layout.view_translation, parent, false)
        view.tv_text.text = pTranslationItem.text
        view.tv_translation.text = pTranslationItem.translation
        return view
    }

    fun showShortToast(pContext: Context, pStringResource: Int) {
        Toast.makeText(pContext, pStringResource, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(pContext: Context, pStringResource: Int) {
        Toast.makeText(pContext, pStringResource, Toast.LENGTH_LONG).show()
    }
}
