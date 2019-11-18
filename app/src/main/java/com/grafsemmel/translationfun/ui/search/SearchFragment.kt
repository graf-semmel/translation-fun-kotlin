package com.grafsemmel.translationfun.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.domain.model.ActiveTranslationState
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.utils.NetworkUtils
import com.grafsemmel.translationfun.utils.ViewUtils
import com.grafsemmel.translationfun.view.TranslationFilterListAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var dialog: AlertDialog? = null
    private val viewModel by viewModel<TranslationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_search, container, false)

    private lateinit var mTranslationFilterListAdapter: TranslationFilterListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchInput()
        subscribeToActiveTranslation()
    }

    private fun initSearchInput() {
        val sourceLngCode = getString(R.string.source_lng_code)
        val targetLngCode = getString(R.string.target_lng_code)
        input_search.setOnEditorActionListener(createOnEditorActionListener(sourceLngCode, targetLngCode))
        mTranslationFilterListAdapter = TranslationFilterListAdapter()
        input_search.setAdapter<TranslationFilterListAdapter>(mTranslationFilterListAdapter)
    }

    private fun createOnEditorActionListener(sourceLngCode: String, targetLngCode: String): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { pTextView, pActionId, pKeyEvent ->
            var handled = false
            if (pActionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = pTextView.text.toString()
                if (text.isEmpty()) {
                    ViewUtils.showShortToast(requireContext(), R.string.input_empty)
                    return@OnEditorActionListener false
                }
                if (!NetworkUtils.isConnected(requireContext())) {
                    ViewUtils.showShortToast(requireContext(), R.string.system_no_network)
                    return@OnEditorActionListener false
                }
                viewModel.translate(text, sourceLngCode, targetLngCode)
                pTextView.text = getString(R.string.search_input_translate)
                pTextView.isEnabled = false
                handled = true
            }
            handled
        }
    }

    private fun subscribeToActiveTranslation() {
        viewModel.activeTranslation.observe(this, Observer { state ->
            when (state) {
                is ActiveTranslationState.Saved -> showSaveDialog(state.item, true)
                is ActiveTranslationState.Updated -> showSaveDialog(state.item, false)
                ActiveTranslationState.Failed -> Snackbar.make(input_search, getString(R.string.snack_translation_failed), Snackbar.LENGTH_LONG).show()
            }
            input_search.isEnabled = true
            input_search.setText("")
        })
    }

    private fun showSaveDialog(item: TranslationItem, isSaved: Boolean) {
        dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.dialog_title)
            with(item) { setMessage("$source: $text\n\n$target: $translation") }
            if (isSaved) {
                setPositiveButton(R.string.dialog_btn_save) { dialog, which ->
                    viewModel.save(item)
                }
            }
            setNegativeButton(R.string.dialog_btn_back) { dialog, which -> dialog.dismiss() }
        }.show()
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}