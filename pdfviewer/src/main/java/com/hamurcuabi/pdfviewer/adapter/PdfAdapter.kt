package com.hamurcuabi.pdfviewer.adapter

import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamurcuabi.pdfviewer.databinding.ItemPdfPageBinding

internal class PdfAdapter(
    private val renderer: PdfRenderer,
    private val currentPageWith: Int,
    private val currentPageHeight: Int,
) : RecyclerView.Adapter<PdfViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPdfPageBinding.inflate(inflater, parent, false)
        binding.imageView.layoutParams.height = currentPageHeight
        return PdfViewerViewHolder(binding)
    }

    override fun getItemCount() = runCatching {
        renderer.pageCount
    }.getOrDefault(0)

    override fun onBindViewHolder(holder: PdfViewerViewHolder, position: Int) {
        runCatching {
            val currentPage = renderer.openPage(position)
            holder.bind(currentPage.renderAndClose(currentPageWith))
        }
    }
}
