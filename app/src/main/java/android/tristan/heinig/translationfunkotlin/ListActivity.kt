package android.tristan.heinig.translationfunkotlin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import android.tristan.heinig.translationfunkotlin.view.SwipeToDeleteCallback
import android.tristan.heinig.translationfunkotlin.view.TranslationRecyclerViewAdapter
import android.tristan.heinig.translationfunkotlin.viewmodel.TranslationViewModel
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
      override fun onSwiped(pViewHolder: ViewHolder, direction: Int) {
        val position = pViewHolder.adapterPosition
        val item = mAdapter.mTranslationItems[position]
        mTranslationViewModel.remove(item)
        showUndoSnackbar(pViewHolder, position, item)
      }
    }
  }

  private fun showUndoSnackbar(pViewHolder: ViewHolder, pPosition: Int, pItem: TranslationItem) {
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
