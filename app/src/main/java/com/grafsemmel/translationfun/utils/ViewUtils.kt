package com.grafsemmel.translationfun.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.domain.model.TranslationItem
import kotlinx.android.synthetic.main.view_translation.view.*

object ViewUtils {
    fun createTranslationView(inflater: LayoutInflater, parent: ViewGroup, item: TranslationItem): View {
        val view = inflater.inflate(R.layout.view_translation, parent, false)
        view.tv_text.text = item.text
        view.tv_translation.text = item.translation
        return view
    }

    fun showShortToast(context: Context, resource: Int) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context: Context, resource: Int) {
        Toast.makeText(context, resource, Toast.LENGTH_LONG).show()
    }
}