package com.google.mlkit.codelab.objectdetection

import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.gson.Gson
import com.google.mlkit.codelab.objectdetection.databinding.ActivityLabelBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.io.File
import java.io.FileWriter
import java.io.IOException
import com.google.gson.GsonBuilder





class LabelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabelBinding
    private lateinit var currentPhotoPath: String
    lateinit var listDataArray: Array<String>
    private lateinit var dataBoxArray: Array<String>
    private lateinit var imageName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.checkButton.setOnClickListener {
            Log.e("finish", "finish")
            saveDataToCSV()

        }

        currentPhotoPath = intent.getStringExtra("image")!!
        imageName = intent.getStringExtra("imageName")!!
        val ot = intent.getIntExtra("ot", 0)

        var bitmap = BitmapFactory.decodeFile(currentPhotoPath)

        bitmap = when (ot) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }

        runObjectDetection(bitmap, binding.image1)

    }

    /**
     * ML Kit Object Detection function. We'll add ML Kit code here in the codelab.
     */
    private fun runObjectDetection(bitmap: Bitmap, imageRun: ImageView) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        objectDetector.process(image).addOnSuccessListener { results ->
            debugPrint(results, bitmap)

            // Parse ML Kit's DetectedObject and create corresponding visualization data
            val detectedObjects = results.map {
                var text = "Unknown"

                // We will show the top confident detection result if it exist
                if (it.labels.isNotEmpty()) {
                    val firstLabel = it.labels.first()
                    text = "${firstLabel.text}, ${firstLabel.confidence.times(100).toInt()}%"
                }
                BoxWithText(it.boundingBox, text)
            }

            // Draw the detection result on the input bitmap
            val visualizedResult = drawDetectionResult(bitmap, detectedObjects)

            // Show the detection result on the app screen
//            imageRun.setImageBitmap(visualizedResult)
        }.addOnFailureListener {
            Log.e(MainActivity.TAG, it.message.toString())
        }
    }

    /**
     * Rotate the given bitmap.
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


    /**
     * Draw bounding boxes around objects together with the object's name.
     */
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<BoxWithText>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = 96F
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }

    /**
     * Print out the object detection result to Logcat.
     */
    private fun debugPrint(detectedObjects: List<DetectedObject>, bitmap: Bitmap) {
        var use_f = 0
        dataBoxArray = Array(3) { "" }
        detectedObjects.forEachIndexed { index, detectedObject ->
            val box = detectedObject.boundingBox
            var imageS = binding.image1
            when (use_f) {
                0 -> imageS = binding.image1
                1 -> imageS = binding.image2
                2 -> imageS = binding.image3
            }
            imageS.setImageBitmap(Bitmap.createBitmap(bitmap,
                box.left,
                box.top,
                box.right - box.left,
                box.bottom - box.top))
            if (use_f < 3) dataBoxArray[use_f] = box.toString()
            use_f += 1

//                Log.e("FUCK", my_box.toString())
//            binding.image1.setImageBitmap(Bitmap.createBitmap(bitmap,my_box.left,my_box.top,my_box.right-my_box.left,my_box.bottom-my_box.top))
//                Log.d(MainActivity.TAG, "Detected object: $index")
//                Log.d(MainActivity.TAG, " trackingId: ${detectedObject.trackingId}")
//                Log.d(MainActivity.TAG, " boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")
//                detectedObject.labels.forEach {
//                    Log.d(MainActivity.TAG, " categories: ${it.text}")
//                    Log.d(MainActivity.TAG, " confidence: ${it.confidence}")


        }
    }

    private fun saveDataToCSV() {
        listDataArray = Array(3 * 3) { "" }
        listDataArray[0] = binding.label11.editableText.toString()
        listDataArray[1] = binding.label12.editableText.toString()
        listDataArray[2] = binding.label13.editableText.toString()
        listDataArray[3] = binding.label21.editableText.toString()
        listDataArray[4] = binding.label22.editableText.toString()
        listDataArray[5] = binding.label23.editableText.toString()
        listDataArray[6] = binding.label31.editableText.toString()
        listDataArray[7] = binding.label32.editableText.toString()
        listDataArray[8] = binding.label33.editableText.toString()

        val data: MutableList<Array<String>> = mutableListOf()
        for (i in 0..2) {
            data.add(arrayOf(dataBoxArray[i],
                listDataArray[i * 3],
                listDataArray[i * 3 + 1],
                listDataArray[i * 3 + 2]))
        }
//
//        val gson = Gson()
//        gson.toJson(data, FileWriter(createCsvFile()))

        FileWriter(createCsvFile()).use { writer ->
            val gson = GsonBuilder().create()
            gson.toJson(data, writer)
        }

    }


    /**
     * Create a file to pass to a camera app for storing captured image.
     */
    @Throws(IOException::class)
    private fun createCsvFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File.createTempFile(
            imageName, /* prefix */
            ".csv", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
        }
    }


}


