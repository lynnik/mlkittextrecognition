package com.yalantis.lynnik.mlkittextrecognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUi()

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_lorem_ipsum)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val blocks = visionText.textBlocks
                if (blocks.isEmpty()) {
                    showToast("No text found")
                    return@addOnSuccessListener
                }

                val bitmapPaint = Paint()
                val blocksPaint = Paint().apply {
                    color = Color.RED
                    style = Paint.Style.STROKE
                    strokeWidth = 8f
                }

                val tempBitmap =
                    Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

                val canvas = Canvas(tempBitmap)
                canvas.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
                for (block in blocks) {
                    block.boundingBox?.let { canvas.drawRect(it, blocksPaint) }
                }

                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageDrawable(BitmapDrawable(resources, tempBitmap))
            }
            .addOnFailureListener { exception ->
                showToast(exception.localizedMessage)
            }
    }

    private fun setupUi() {
        findViewById<Button>(R.id.blocksButton).setOnClickListener {
            // TODO
        }
        findViewById<Button>(R.id.linesButton).setOnClickListener {
            // TODO
        }
        findViewById<Button>(R.id.elementsButton).setOnClickListener {
            // TODO
        }
        findViewById<Button>(R.id.clearButton).setOnClickListener {
            // TODO
        }
    }

    private fun showToast(text: CharSequence?) {
        Toast
            .makeText(this, text, Toast.LENGTH_LONG)
            .show()
    }
}
