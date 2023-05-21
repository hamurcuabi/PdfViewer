package com.hamurcuabi.pdfviewer.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.hamurcuabi.pdfviewer.databinding.ItemPdfPageBinding

class PdfViewerViewHolder(
    private val binding: ItemPdfPageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(bitmap: Bitmap, isZoomEnabled: Boolean) {
        binding.imageView.apply {
            isZoomable = isZoomEnabled
            setImageBitmap(bitmap)
        }
    }
}