package com.hamurcuabi.pdfviewer

interface PdfViewListener {
    var onLoad: (() -> Unit)?
    var onError: ((throwable: Throwable) -> Unit)?
    var onPageChange: ((currentPage: Int, totalPage: Int) -> Unit)?
}