package com.hamurcuabi.pdfviewer.photoview

import android.graphics.RectF

interface OnMatrixChangedListener {
    /**
     * Callback for when the Matrix displaying the Drawable has changed. This could be because
     * the View's bounds have changed, or the user has zoomed.
     *
     * @param rect - Rectangle displaying the Drawable's new bounds.
     */
    fun onMatrixChanged(rect: RectF?)
}
