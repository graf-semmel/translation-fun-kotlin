package com.grafsemmel.translationfun

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.utils.NetworkUtils
import com.grafsemmel.translationfun.utils.ViewUtils
import com.grafsemmel.translationfun.view.TranslationFilterListAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_card_most_recent.*
import kotlinx.android.synthetic.main.include_card_most_viewed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mTranslationViewModel by viewModel<TranslationViewModel>()
    private lateinit var mTranslationFilterListAdapter: TranslationFilterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSearchInput()
        subscribeToModel()
    }

    private fun initSearchInput() {
        val sourceLngCode = getString(R.string.source_lng_code)
        val targetLngCode = getString(R.string.target_lng_code)
        input_search.setOnEditorActionListener(createOnEditorActionListener(sourceLngCode, targetLngCode))
        mTranslationFilterListAdapter = TranslationFilterListAdapter()
        input_search.setAdapter<TranslationFilterListAdapter>(mTranslationFilterListAdapter)
    }

    private fun createOnEditorActionListener(sourceLngCode: String, targetLngCode: String): OnEditorActionListener {
        return OnEditorActionListener { pTextView, pActionId, pKeyEvent ->
            var handled = false
            if (pActionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = pTextView.text.toString()
                if (text.isEmpty()) {
                    ViewUtils.showShortToast(this@MainActivity, R.string.input_empty)
                    return@OnEditorActionListener false
                }
                if (!NetworkUtils.isConnected(this@MainActivity)) {
                    ViewUtils.showShortToast(this@MainActivity, R.string.system_no_network)
                    return@OnEditorActionListener false
                }
                mTranslationViewModel.translate(text, sourceLngCode, targetLngCode)
                pTextView.text = getString(R.string.search_input_translate)
                pTextView.isEnabled = false
                handled = true
            }
            handled
        }
    }

    private fun subscribeToModel() {
        val itemCount = resources.getInteger(R.integer.card_item_count)
        subscribeToMostViewedTranslations(itemCount)
        subscribeToMostRecentTranslations(itemCount)
        subscribeToActiveTranslation()
    }

    private fun subscribeToActiveTranslation() {
        mTranslationViewModel.activeTranslation.observe(this, Observer { state ->
            when (state) {
                is ActiveTranslationState.Saved -> buildTranslationDialog(state.item, true).show()
                is ActiveTranslationState.Updated -> buildTranslationDialog(state.item, false).show()
                ActiveTranslationState.Failed -> Snackbar.make(input_search, getString(R.string.snack_translation_failed), Snackbar.LENGTH_LONG).show()
            }
            input_search.isEnabled = true
            input_search.setText("")
        })
    }

    private fun buildTranslationDialog(item: TranslationItem, isSaved: Boolean): AlertDialog.Builder {
        val myAlertBuilder = AlertDialog.Builder(this@MainActivity)
        myAlertBuilder.setTitle(R.string.dialog_title)
        with(item) {
            myAlertBuilder.setMessage("$source: $text\n\n$target: $translation")
            if (isSaved) {
                myAlertBuilder.setPositiveButton(R.string.dialog_btn_save) { dialog, which ->
                    mTranslationViewModel.save(item)
                }
            }
        }
        myAlertBuilder.setNegativeButton(R.string.dialog_btn_back) { dialog, which -> dialog.dismiss() }
        return myAlertBuilder
    }

    private fun subscribeToMostRecentTranslations(pItemCount: Int) {
        mTranslationViewModel.mostRecentTranslations.observe(this, Observer { pTranslationItems ->
            updateTranslationContainer(pTranslationItems, container_most_recent, pItemCount)
            // calling for most recent translation returns the complete list of translation, which we can use for auto completion in out search input
            pTranslationItems?.run { mTranslationFilterListAdapter.setTranslations(pTranslationItems) }
        })
    }

    private fun subscribeToMostViewedTranslations(itemCount: Int) {
        mTranslationViewModel.mostViewedTranslations.observe(
                this, Observer { pTranslationItems -> updateTranslationContainer(pTranslationItems, container_most_viewed, itemCount) }
        )
    }

    private fun updateTranslationContainer(items: List<TranslationItem>?, viewGroup: ViewGroup, itemCount: Int) {
        items?.let { items ->
            viewGroup.removeAllViews()
            var index = 0
            while (index < itemCount && index < items.size) {
                viewGroup.addView(ViewUtils.createTranslationView(layoutInflater, viewGroup, items[index]))
                index++
            }
        }
    }

    fun showRecent(view: View) {
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra(ListActivity.EXTRA_SORT_TYPE, ListActivity.SORT_TYPE_DATE)
        startActivity(intent)
    }

    fun showMostViewed(view: View) {
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra(ListActivity.EXTRA_SORT_TYPE, ListActivity.SORT_TYPE_VIEWS)
        startActivity(intent)
    }
}
