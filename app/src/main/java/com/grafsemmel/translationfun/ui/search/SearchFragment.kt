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
import com.grafsemmel.translationfun.utils.ViewUtils
import com.grafsemmel.translationfun.view.TranslationFilterListAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : Fragment() {
    private val viewModel by sharedViewModel<TranslationViewModel>()
    private var dialog: AlertDialog? = null
    private val searchAction: () -> Boolean = {
        val text = input_search.text.toString()
        when {
            text.isEmpty() -> {
                ViewUtils.showShortToast(requireContext(), R.string.input_empty)
                false
            }
            else -> {
                viewModel.translate(text)
                input_search.setText(getString(R.string.search_input_translate))
                input_search.isEnabled = false
                true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        setupObservers()
    }

    private fun setupSearch() {
        input_search.setOnEditorActionListener(createOnEditorActionListener())
        input_search.setAdapter<TranslationFilterListAdapter>(TranslationFilterListAdapter())
        iv_search.setOnClickListener { searchAction.invoke() }
    }

    private fun createOnEditorActionListener(): TextView.OnEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        var handled = false
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            handled = searchAction.invoke()
        }
        handled
    }

    private fun setupObservers() {
        viewModel.activeTranslation.observe(this, Observer { state ->
            when (state) {
                is ActiveTranslationState.New -> showSaveDialog(state.item, true)
                is ActiveTranslationState.Update -> showSaveDialog(state.item, false)
                ActiveTranslationState.Error -> Snackbar.make(input_search, getString(R.string.snack_translation_failed), Snackbar.LENGTH_LONG).show()
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
                setPositiveButton(R.string.dialog_btn_save) { _, _ -> viewModel.save(item) }
            }
            setNegativeButton(R.string.dialog_btn_back) { dialog, _ -> dialog.dismiss() }
        }.show()
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}