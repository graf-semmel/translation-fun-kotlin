package com.grafsemmel.translationfun

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.view.SwipeToDeleteCallback
import com.grafsemmel.translationfun.view.TranslationRecyclerViewAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import com.grafsemmel.translationtun.domain.model.TranslationItem
import kotlinx.android.synthetic.main.activity_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListActivity : AppCompatActivity() {
    private lateinit var mAdapter: TranslationRecyclerViewAdapter
    private val viewModel by viewModel<TranslationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mAdapter = TranslationRecyclerViewAdapter(this)
        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        val backgroundColor = ContextCompat.getColor(this, R.color.colorAccent)
        val deleteDrawable = ContextCompat.getDrawable(this, R.drawable.ic_delete)
        val itemTouchHelper = ItemTouchHelper(createSwipeToDeleteCallback(deleteDrawable!!, backgroundColor))
        itemTouchHelper.attachToRecyclerView(recycler_view)

        subscribeToModel(viewModel, intent.getIntExtra(EXTRA_SORT_TYPE, SORT_TYPE_DATE))
    }

    private fun createSwipeToDeleteCallback(deleteDrawable: Drawable, backgroundColor: Int): SwipeToDeleteCallback {
        return object : SwipeToDeleteCallback(deleteDrawable, backgroundColor) {
            override fun onSwiped(pViewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = pViewHolder.adapterPosition
                val item = mAdapter.list[position]
                viewModel.remove(item)
                showUndoSnackbar(pViewHolder, position, item)
            }
        }
    }

    private fun showUndoSnackbar(viewHolder: RecyclerView.ViewHolder, pPosition: Int, pItem: TranslationItem) {
        with(Snackbar.make(viewHolder.itemView, getString(R.string.snack_translation_item_removed), Snackbar.LENGTH_LONG)) {
            setAction(R.string.snack_undo) {
                viewModel.restore(pItem)
                recycler_view.scrollToPosition(pPosition)
            }
            setActionTextColor(Color.YELLOW)
            show()
        }
    }

    private fun subscribeToModel(pTranslationViewModel: TranslationViewModel, pSortType: Int) {
        val observer = Observer<List<TranslationItem>> { pTranslationItems -> pTranslationItems?.let { mAdapter.list = it } }
        when (pSortType) {
            SORT_TYPE_DATE -> {
                pTranslationViewModel.mostRecentTranslations.observe(this, observer)
                setTitle(R.string.label_most_recent)
            }
            else -> {
                pTranslationViewModel.mostViewedTranslations.observe(this, observer)
                setTitle(R.string.label_most_viewed)
            }
        }
    }

    companion object {
        const val EXTRA_SORT_TYPE = "extra.order"
        const val SORT_TYPE_DATE = 0
        const val SORT_TYPE_VIEWS = 1
    }
}
