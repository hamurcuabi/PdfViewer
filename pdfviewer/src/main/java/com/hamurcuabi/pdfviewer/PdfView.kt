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
import androidx.core.content.withStyledAttributes
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
    private var spaceSize: Int = R.dimen.pdf_viewer_default_vertical_space

    private var currentPage: Int = -1

    private var isSnapEnabled: Boolean = false

    private var pdfViewListener: PdfViewListener? = null

    private var pageHeight: Int = ViewGroup.LayoutParams.MATCH_PARENT

    init {
        context.withStyledAttributes(attrs, R.styleable.PdfView) {
            spaceSize = getResourceId(R.styleable.PdfView_verticalSpace, R.dimen.pdf_viewer_default_vertical_space)
            isSnapEnabled = getBoolean(R.styleable.PdfView_isSnapEnabled, false)
            val heightParams =
                getInt(R.styleable.PdfView_pageHeightType, PdfLayoutParams.MATCH_PARENT.type)
            setPageHeight(heightParams)
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
            currentPageHeight = pageHeight,
            currentPageWith = width
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

    fun loadPdfWithFile(file: File?) {
        if (file == null) {
            pdfViewListener?.onError?.invoke(Exception("File cannot be null"))
            return
        }

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

    private fun setPageHeight(type: Int) {
        val viewParams = when (PdfLayoutParams.fromValue(type)) {
            PdfLayoutParams.MATCH_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
            PdfLayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
        pageHeight = viewParams
    }

    enum class PdfLayoutParams(val type: Int) {
        MATCH_PARENT(0),
        WRAP_CONTENT(1);

        companion object {

            fun fromValue(type: Int): PdfLayoutParams {
                return values().firstOrNull { it.type == type } ?: MATCH_PARENT
            }
        }
    }
}

