package com.hamurcuabi.pdfviewer

import android.content.Context
import android.content.res.Resources
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.hamurcuabi.pdfviewer.adapter.MarginItemDecoration
import com.hamurcuabi.pdfviewer.adapter.PdfAdapter
import java.io.File


class PdfView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    @DimenRes
    private var spaceSize: Int

    private var currentPage: Int = -1

    private var isZoomEnabled: Boolean = false

    private var isSnapEnabled: Boolean = false

    private var pdfViewListener: PdfViewListener? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PdfView).run {
            spaceSize = getResourceId(R.styleable.PdfView_verticalSpace, R.dimen.pdf_viewer_default_vertical_space)
            isSnapEnabled = getBoolean(R.styleable.PdfView_isSnapEnabled, false)
            recycle()
        }
    }

    private var itemDecoration: RecyclerView.ItemDecoration = MarginItemDecoration(
        spaceSize = resources.getDimensionPixelSize(
            spaceSize
        )
    )

    private val pdfPageView: RecyclerView by lazy {
        RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                itemDecoration
            )
            if (isSnapEnabled) {
                val snapHelper: SnapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(this)
            }
        }
    }

    private fun loadPdfView(pdfView: View) {
        removeAllViews()
        addView(pdfView)

        pdfView.updateLayoutParams {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun setupRenderer(file: File?) {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val adapter = PdfAdapter(
            renderer = pdfRenderer,
            currentPageWith = width,
            isZoomEnabled = isZoomEnabled
        )

        pdfPageView.adapter = adapter

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = pdfPageView.layoutManager as? LinearLayoutManager

                if (layoutManager?.orientation == LinearLayoutManager.VERTICAL) {

                    val currentScrolledPage =
                        layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (currentScrolledPage != currentPage && currentScrolledPage != -1) {
                        currentPage = currentScrolledPage
                        pdfViewListener?.onPageChange?.invoke(currentScrolledPage + 1, pdfRenderer.pageCount)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = pdfPageView.layoutManager as? LinearLayoutManager

                if (layoutManager?.orientation == LinearLayoutManager.HORIZONTAL) {
                    val currentScrolledPage =
                        layoutManager.findFirstVisibleItemPosition()

                    if (currentScrolledPage != currentPage && currentScrolledPage != -1) {
                        currentPage = currentScrolledPage
                        pdfViewListener?.onPageChange?.invoke(currentScrolledPage + 1, pdfRenderer.pageCount)
                    }
                }
            }
        }
        pdfPageView.addOnScrollListener(scrollListener)
    }

    fun loadPdfWithFile(file: File?, isZoomEnabled: Boolean) {
        if (file == null) {
            pdfViewListener?.onError?.invoke(Exception("File cannot be null"))
            return
        }

        this.isZoomEnabled = isZoomEnabled

        runCatching {
            loadPdfView(pdfPageView)
            setupRenderer(file)
            pdfViewListener?.onLoad?.invoke()
        }.onFailure {
            pdfViewListener?.onError?.invoke(it)
        }
    }

    fun setPdfViewListener(pdfViewListener: PdfViewListener) {
        this.pdfViewListener = pdfViewListener
    }
}

