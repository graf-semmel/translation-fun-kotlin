package com.grafsemmel.translationfun.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.grafsemmel.translationtun.domain.model.TranslationItem

class TranslationFilterListAdapter : BaseAdapter(), Filterable {
    private var mFilteredTranslations: MutableList<TranslationItem> = ArrayList()
    private val mTranslationFilter = TranslationFilter()
    val filteredTranslations: List<TranslationItem>?
        get() = mFilteredTranslations
    var allTranslations: List<TranslationItem> = ArrayList()
        private set

    override fun getCount(): Int = mFilteredTranslations.size

    fun setTranslations(pTranslationItems: List<TranslationItem>) {
        allTranslations = pTranslationItems
        mFilteredTranslations = ArrayList(allTranslations)
        notifyDataSetChanged()
    }

    override fun getItem(pPosition: Int): TranslationItem = mFilteredTranslations[pPosition]

    override fun getItemId(pPosition: Int): Long = pPosition.toLong()

    override fun getView(pPosition: Int, pView: View?, pViewGroup: ViewGroup): View {
        val view: View? = pView ?: LayoutInflater.from(pViewGroup.context).inflate(
                android.R.layout.simple_dropdown_item_1line, pViewGroup,
                false
        )
        (view as TextView).text = getItem(pPosition).text
        return view
    }

    override fun getFilter(): Filter = mTranslationFilter

    private inner class TranslationFilter : Filter() {
        override fun convertResultToString(pResultValue: Any): CharSequence = (pResultValue as TranslationItem).text

        override fun performFiltering(pCharSequence: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            val suggestions = ArrayList<TranslationItem>()
            if (pCharSequence != null) {
                for (customer in allTranslations) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (customer.text.toLowerCase().contains(pCharSequence.toString().toLowerCase())) {
                        suggestions.add(customer)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size

            return results
        }

        override fun publishResults(pCharSequence: CharSequence?, pFilterResults: Filter.FilterResults?) {
            mFilteredTranslations.clear()
            if (pFilterResults != null) mFilteredTranslations.addAll(pFilterResults.values as ArrayList<TranslationItem>)
            notifyDataSetChanged()
        }
    }
}
