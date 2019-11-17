package com.grafsemmel.translationfun

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.database.entity.TranslationItem
import com.grafsemmel.translationfun.view.SwipeToDeleteCallback
import com.grafsemmel.translationfun.view.TranslationRecyclerViewAdapter
import com.grafsemmel.translationfun.viewmodel.TranslationViewModel
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private lateinit var mAdapter: TranslationRecyclerViewAdapter
    private lateinit var mTranslationViewModel: TranslationViewModel

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

        mTranslationViewModel = ViewModelProviders.of(this).get(TranslationViewModel::class.java)
        subscribeToModel(mTranslationViewModel, intent.getIntExtra(EXTRA_SORT_TYPE, SORT_TYPE_DATE))
    }

    private fun createSwipeToDeleteCallback(deleteDrawable: Drawable, backgroundColor: Int): SwipeToDeleteCallback {
        return object : SwipeToDeleteCallback(deleteDrawable, backgroundColor) {
            override fun onSwiped(pViewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = pViewHolder.adapterPosition
                val item = mAdapter.mTranslationItems[position]
                mTranslationViewModel.remove(item)
                showUndoSnackbar(pViewHolder, position, item)
            }
        }
    }

    private fun showUndoSnackbar(pViewHolder: RecyclerView.ViewHolder, pPosition: Int, pItem: TranslationItem) {
        with(Snackbar.make(pViewHolder.itemView, getString(R.string.snack_translation_item_removed), Snackbar.LENGTH_LONG)) {
            setAction(R.string.snack_undo) {
                mTranslationViewModel.restore(pItem)
                recycler_view.scrollToPosition(pPosition)
            }
            setActionTextColor(Color.YELLOW)
            show()
        }
    }

    private fun subscribeToModel(pTranslationViewModel: TranslationViewModel, pSortType: Int) {
        val observer = Observer<List<TranslationItem>> { pTranslationItems -> pTranslationItems?.let { mAdapter.mTranslationItems = it } }
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
