package com.yalantis.lynnik.mlkittextrecognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// TODO add more images
// TODO show the recognized text under of the image
class MainActivity : AppCompatActivity() {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var imageView: ImageView? = null

    private val images = arrayOf(
        R.drawable.img_pixel_4a,
        R.drawable.img_lorem_ipsum
    )
    private var imageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUi()
        renderImage()
    }

    private fun setupUi() {
        imageView = findViewById(R.id.imageView)

        imageView?.setOnClickListener {
            updateImageIndex()
            renderImage()
        }
        findViewById<Button>(R.id.blocksButton).setOnClickListener {
            renderRecognizedBlocks()
        }
        findViewById<Button>(R.id.linesButton).setOnClickListener {
            renderRecognizedLines()
        }
        findViewById<Button>(R.id.elementsButton).setOnClickListener {
            renderRecognizedElements()
        }
        findViewById<Button>(R.id.symbolsButton).setOnClickListener {
            renderRecognizedSymbols()
        }
        findViewById<Button>(R.id.clearButton).setOnClickListener {
            renderImage()
        }
    }

    private fun provideImage(): Bitmap {
        val imageResId = images[imageIndex]
        return BitmapFactory.decodeResource(resources, imageResId)
    }

    private fun updateImageIndex() {
        imageIndex = if (imageIndex == images.size - 1) 0 else imageIndex + 1
    }

    private fun renderImage() {
        imageView?.setImageBitmap(provideImage())
    }

    private fun renderRecognizedBlocks() {
        val bitmap = provideImage()
        recognizeText(bitmap) { blocks ->
            val rectangles = blocks.mapNotNull { it.boundingBox }
            renderRecognizedRectangles(bitmap, rectangles)
        }
    }

    private fun renderRecognizedLines() {
        val bitmap = provideImage()
        recognizeText(bitmap) { blocks ->
            val lines = blocks.map { it.lines }.flatten()
            val rectangles = lines.mapNotNull { it.boundingBox }
            renderRecognizedRectangles(bitmap, rectangles)
        }
    }

    private fun renderRecognizedElements() {
        val bitmap = provideImage()
        recognizeText(bitmap) { blocks ->
            val lines = blocks.map { it.lines }.flatten()
            val elements = lines.map { it.elements }.flatten()
            val rectangles = elements.mapNotNull { it.boundingBox }
            renderRecognizedRectangles(bitmap, rectangles)
        }
    }

    private fun renderRecognizedSymbols() {
        val bitmap = provideImage()
        recognizeText(bitmap) { blocks ->
            val lines = blocks.map { it.lines }.flatten()
            val elements = lines.map { it.elements }.flatten()
            val symbols = elements.map { it.symbols }.flatten()
            val rectangles = symbols.mapNotNull { it.boundingBox }
            renderRecognizedRectangles(bitmap, rectangles)
        }
    }

    private fun recognizeText(
        bitmap: Bitmap,
        onRecognizedBlocks: (List<Text.TextBlock>) -> Unit
    ) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                if (visionText.textBlocks.isEmpty()) {
                    showToast("No text found")
                    return@addOnSuccessListener
                }
                onRecognizedBlocks(visionText.textBlocks)
            }
            .addOnFailureListener { exception ->
                showToast(exception.localizedMessage)
            }
    }

    private fun renderRecognizedRectangles(bitmap: Bitmap, rectangles: List<Rect>) {
        val bitmapPaint = Paint()
        val rectanglePaint = Paint()
            .apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 8f
            }

        val tempBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(tempBitmap)
            .apply { drawBitmap(bitmap, 0f, 0f, bitmapPaint) }
        for (rectangle in rectangles) {
            canvas.drawRect(rectangle, rectanglePaint)
        }

        val bitmapDrawable = BitmapDrawable(resources, tempBitmap)
        imageView?.setImageDrawable(bitmapDrawable)
    }

    private fun showToast(text: CharSequence?) {
        Toast
            .makeText(this, text, Toast.LENGTH_LONG)
            .show()
    }
}
