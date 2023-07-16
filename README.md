# PdfViewer
![](https://img.shields.io/badge/maven%20central-1.1.0-blue)

PdfViewer is a lightweight and easy-to-use library for loading PDF views in your Android applications. With PdfViewer, developers can effortlessly display PDF files from local files or base64 strings, without requiring any additional permissions. This library simplifies the process of integrating PDF viewing functionality into your app, providing a seamless experience for your users.

<a href="https://central.sonatype.com/artifact/io.github.hamurcuabi/PdfViewer" target="_blank">Visit maven central</a> for more information and documentation.

## Features

- Load PDF views from local files or base64 strings.
- No additional permissions required.
- Simple and intuitive API for easy integration.
- Lightweight and optimized for performance.
- Adjustable space between pages
- Usefull callbacks (onLoad,onError,onPageChange)

## Installation

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		mavenCentral()
	}
}
```

Add this to your module's `build.gradle` file (make sure the version matches the maven badge above):

```gradle
dependencies {
	 implementation("io.github.hamurcuabi:PdfViewer:1.1.0")
}
```

in your XML layout file, add the PdfViewer component

```
      <com.hamurcuabi.pdfviewer.PdfView
        android:id="@+id/pdfViewer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:isSnapEnabled="true"
        app:pageHeightType="wrapContent"
        app:verticalSpace="@dimen/vertical_space"/>
```
In your Java/Kotlin code, initialize the PdfView and load a PDF file or base64 string

    private fun showPdf() {
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

        	loadPdfWithFile(file = YOUR_FILE)
            // OR
		loadPdfWithBase64(base64String = YOUR_STRING)
        }
    }

## Additional Attributes
```
        <attr format="reference" name="verticalSpace"/> // Space between pages
        <attr format="boolean" name="isSnapEnabled"/> // Snap control fro scrolling
        <attr format="enum" name="pageHeightType"> // Math or wrap height of pdf 
            <enum name="matchParent" value="0"/>
            <enum name="wrapContent" value="1"/>
        </attr>
```    

    
