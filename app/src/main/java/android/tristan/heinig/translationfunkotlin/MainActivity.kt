package android.tristan.heinig.translationfunkotlin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog.Builder
import android.support.v7.app.AppCompatActivity
import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import android.tristan.heinig.translationfunkotlin.repository.ActiveTranslationState
import android.tristan.heinig.translationfunkotlin.repository.ActiveTranslationState.STATE
import android.tristan.heinig.translationfunkotlin.utils.NetworkUtils
import android.tristan.heinig.translationfunkotlin.utils.ViewUtils
import android.tristan.heinig.translationfunkotlin.view.TranslationFilterListAdapter
import android.tristan.heinig.translationfunkotlin.viewmodel.TranslationViewModel
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_card_most_recent.*
import kotlinx.android.synthetic.main.include_card_most_viewed.*

class MainActivity : AppCompatActivity() {

  private lateinit var mTranslationViewModel: TranslationViewModel
  private lateinit var mTranslationFilterListAdapter: TranslationFilterListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    mTranslationViewModel = ViewModelProviders.of(this).get(TranslationViewModel::class.java)
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
    mTranslationViewModel.activeTranslation.observe(this, Observer { pActiveTranslationState ->
      if (pActiveTranslationState != null && pActiveTranslationState.state !== STATE.FAILED && pActiveTranslationState.translationItem !== null) {
        val myAlertBuilder = buildTranslationDialog(pActiveTranslationState)
        myAlertBuilder.show()
      } else {
        val snackbar = Snackbar.make(input_search, getString(R.string.snack_translation_failed), Snackbar.LENGTH_LONG)
        snackbar.show()
      }
      input_search.isEnabled = true
      input_search.setText("")
    })
  }

  private fun buildTranslationDialog(pPTranslationItemState: ActiveTranslationState): Builder {
    val translationItem = pPTranslationItemState.translationItem
    val myAlertBuilder = Builder(this@MainActivity)
    myAlertBuilder.setTitle(R.string.dialog_title)
    translationItem?.let {
      with(it) {
        myAlertBuilder.setMessage("$source: $text\n\n$target: $translation")
        if (pPTranslationItemState.state === STATE.SAVED) {
          myAlertBuilder.setPositiveButton(R.string.dialog_btn_save) { dialog, which ->
            mTranslationViewModel.save(it)
          }
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

  private fun subscribeToMostViewedTranslations(pItemCount: Int) {
    mTranslationViewModel.mostViewedTranslations.observe(this,
      Observer { pTranslationItems -> updateTranslationContainer(pTranslationItems, container_most_viewed, pItemCount) })
  }

  private fun updateTranslationContainer(pTranslationItems: List<TranslationItem>?, pViewGroup: ViewGroup, pItemCount: Int) {
    pTranslationItems?.let { items ->
      pViewGroup.removeAllViews()
      var index = 0
      while (index < pItemCount && index < items.size) {
        pViewGroup.addView(ViewUtils.createTranslationView(layoutInflater, pViewGroup, items[index]))
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
