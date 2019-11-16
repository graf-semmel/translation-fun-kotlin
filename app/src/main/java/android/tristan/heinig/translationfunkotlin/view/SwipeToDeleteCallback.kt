package android.tristan.heinig.translationfunkotlin.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback(private val mDeleteDrawable: Drawable,
                                     private val mBackgroundColor: Int) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val mClearPaint: Paint = Paint()
    private val mIntrinsicWidth: Int = mDeleteDrawable.intrinsicWidth
    private val mBackground: ColorDrawable = ColorDrawable()
    private val mIntrinsicHeight: Int = mDeleteDrawable.intrinsicHeight

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMove(pRecyclerView: RecyclerView, pViewHolder: RecyclerView.ViewHolder, pViewHolder1: RecyclerView.ViewHolder): Boolean =
            false

    override fun onChildDraw(pCanvas: Canvas, pRecyclerView: RecyclerView, pViewHolder: RecyclerView.ViewHolder, pDX: Float, pDY: Float,
                             pActionState: Int, pIsCurrentlyActive: Boolean) {
        super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive)
        val itemView = pViewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = pDX == 0f && !pIsCurrentlyActive

        if (isCancelled) {
            clearCanvas(pCanvas, itemView.right + pDX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive)
            return
        }

        mBackground.color = mBackgroundColor
        mBackground.setBounds(itemView.right + pDX.toInt(), itemView.top, itemView.right, itemView.bottom)
        mBackground.draw(pCanvas)
        val deleteIconTop = itemView.top + (itemHeight - mIntrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - mIntrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - mIntrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + mIntrinsicHeight

        mDeleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        mDeleteDrawable.draw(pCanvas)

        super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float?, top: Float?, right: Float?, bottom: Float?) =
            c.drawRect(left!!, top!!, right!!, bottom!!, mClearPaint)

    override fun getSwipeThreshold(pViewHolder: RecyclerView.ViewHolder): Float = 0.7f
}

