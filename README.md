# PdfViewer
![](https://img.shields.io/badge/maven%20central-1.0.0-green)

## Prerequisites

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		mavenCentral()
	}
}
```

## Dependency

Add this to your module's `build.gradle` file (make sure the version matches the JitPack badge above):

```gradle
dependencies {
	 implementation("io.github.hamurcuabi:PdfViewer:1.0.0")
}
```

## Xml
```
      <com.hamurcuabi.pdfviewer.PdfView
        android:id="@+id/pdfViewer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:isSnapEnabled="true"
        app:pageHeightType="wrapContent"
        app:verticalSpace="@dimen/vertical_space"/>
```
## Fragment/Activity
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

            loadPdfWithFile(file = file)
        }
    }

## Attributes
```
        <attr format="reference" name="verticalSpace"/> // Space between pages
        <attr format="boolean" name="isSnapEnabled"/> // Snap control fro scrolling
        <attr format="enum" name="pageHeightType"> // Math or wrap height of pdf 
            <enum name="matchParent" value="0"/>
            <enum name="wrapContent" value="1"/>
        </attr>
```    

    
