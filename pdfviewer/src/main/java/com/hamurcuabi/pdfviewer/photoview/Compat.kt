package com.hamurcuabi.pdfviewer.photoview

import android.view.View

internal object Compat {

    @JvmStatic
    fun postOnAnimation(view: View, runnable: Runnable) {
        postOnAnimationJellyBean(view, runnable)
    }

    private fun postOnAnimationJellyBean(view: View, runnable: Runnable) {
        view.postOnAnimation(runnable)
    }
}
