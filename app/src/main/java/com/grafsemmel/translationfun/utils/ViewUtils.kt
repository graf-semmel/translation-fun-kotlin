package com.grafsemmel.translationfun.utils

import android.content.Context
import android.widget.Toast

object ViewUtils {
    fun showShortToast(context: Context, resource: Int) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }
}