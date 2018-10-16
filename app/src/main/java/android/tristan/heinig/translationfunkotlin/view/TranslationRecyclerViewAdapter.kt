package android.tristan.heinig.translationfunkotlin.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.tristan.heinig.translationfunkotlin.R
import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import android.tristan.heinig.translationfunkotlin.view.TranslationRecyclerViewAdapter.TranslationViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_translation.view.*

class TranslationRecyclerViewAdapter(pContext: Context) : RecyclerView.Adapter<TranslationViewHolder>() {

  private val mInflater = LayoutInflater.from(pContext)
  var mTranslationItems: List<TranslationItem> = arrayListOf()
    set(pTranslationItems) {
      field = pTranslationItems
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(pViewGroup: ViewGroup, pViewType: Int): TranslationViewHolder =
    TranslationViewHolder(mInflater.inflate(R.layout.view_translation_card, pViewGroup, false))

  override fun onBindViewHolder(pViewHolder: TranslationViewHolder, pPosition: Int) {
    when (mTranslationItems.size) {
      0 -> pViewHolder.itemView.tv_text.setText(R.string.no_translations)
      else -> {
        val (text, translation) = mTranslationItems[pPosition]
        pViewHolder.itemView.tv_text.text = text
        pViewHolder.itemView.tv_translation.text = translation
      }
    }
  }

  override fun getItemCount(): Int = mTranslationItems.size

  inner class TranslationViewHolder internal constructor(pItemView: View) : RecyclerView.ViewHolder(pItemView)
}