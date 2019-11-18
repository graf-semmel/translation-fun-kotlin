package com.grafsemmel.translationfun.ui.list

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.R
import com.grafsemmel.translationfun.domain.model.TranslationItem
import com.grafsemmel.translationfun.view.SwipeToDeleteCallback
import com.grafsemmel.translationfun.view.TranslationRecyclerViewAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class AbstractTranslationListFragment : Fragment() {
    protected val viewModel by sharedViewModel<TranslationViewModel>()
    private var adapter: TranslationRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TranslationRecyclerViewAdapter(requireContext())
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : SwipeToDeleteCallback(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!,
                ContextCompat.getColor(requireContext(), R.color.colorAccent)
        ) {
            override fun onSwiped(pViewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = pViewHolder.adapterPosition
                adapter?.list?.get(position)?.let {
                    viewModel.remove(it)
                    showUndoSnackbar(pViewHolder, position, it)
                }
            }
        }
        ).attachToRecyclerView(recycler_view)
    }

    private fun showUndoSnackbar(viewHolder: RecyclerView.ViewHolder, position: Int, item: TranslationItem) {
        with(Snackbar.make(viewHolder.itemView, getString(R.string.snack_translation_item_removed), Snackbar.LENGTH_LONG)) {
            setAction(R.string.snack_undo) {
                viewModel.restore(item)
                recycler_view.scrollToPosition(position)
            }
            setActionTextColor(Color.YELLOW)
            show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLiveDataToObserve().observe(this, Observer { items -> items?.let { adapter?.list = it } })
    }

    abstract fun getLiveDataToObserve(): LiveData<List<TranslationItem>>
}