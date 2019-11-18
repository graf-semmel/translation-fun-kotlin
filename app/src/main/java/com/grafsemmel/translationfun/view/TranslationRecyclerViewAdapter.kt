package com.grafsemmel.translationfun.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.view.TranslationRecyclerViewAdapter.TranslationViewHolder
import com.grafsemmel.translationtun.domain.model.TranslationItem
import kotlinx.android.synthetic.main.view_translation.view.*

class TranslationRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<TranslationViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    var list: List<TranslationItem> = arrayListOf()
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TranslationViewHolder =
            TranslationViewHolder(inflater.inflate(R.layout.view_translation_card, viewGroup, false))

    override fun onBindViewHolder(holder: TranslationViewHolder, position: Int) = when (list.size) {
        0 -> holder.itemView.tv_text.setText(R.string.no_translations)
        else -> {
            val (text, translation) = list[position]
            holder.itemView.tv_text.text = text
            holder.itemView.tv_translation.text = translation
        }
    }

    override fun getItemCount(): Int = list.size

    inner class TranslationViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view)
}