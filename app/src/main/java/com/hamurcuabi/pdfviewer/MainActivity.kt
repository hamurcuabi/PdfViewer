package com.hamurcuabi.pdfviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hamurcuabi.pdfviewer.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

private const val TEST_FILE = "test.pdf"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readRawFile()

        val cacheFile = File(cacheDir, TEST_FILE)
        showPdf(cacheFile)
    }

    private fun readRawFile() {
        val inputStream = resources.openRawResource(R.raw.dummy)

        inputStream.use { inputStream ->
            val file = File(cacheDir, TEST_FILE)
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }

    private fun showPdf(file: File?) {
        binding.pdfViewer.apply {
            setPdfViewListener(object : PdfViewListener {

                override var onLoad: (() -> Unit)? = {
                    println("PdfViewListener: onLoad")
                }

                override var onError: ((throwable: Throwable) -> Unit)? = {
                    println("PdfViewListener: onError")
                }

                override var onPageChange: ((currentPage: Int, totalPage: Int) -> Unit)? = { currentPage, totalPage ->
                    println("PdfViewListener: onPageChange currentPage:$currentPage totalPage:$totalPage")
                }
            })

            loadPdfWithFile(file = file, isZoomEnabled = true)
        }
    }

}