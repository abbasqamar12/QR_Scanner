package com.vmstechs.hpqrscanner.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.vmstechs.hpqrscanner.R
import com.vmstechs.hpqrscanner.databinding.ActivityCameraBinding
import com.vmstechs.hpqrscanner.register.RegistrationActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    // private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraBinding: ActivityCameraBinding


    /* companion object {
         private const val CAMERA_REQUEST_CODE = 101
         private val REQUIRED_PERMISSIONS =
             arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(cameraBinding.root)


        // val captureButton = findViewById(R.id.txtCaptureImage)

        // Request camera permissions
        checkForCameraAndRecordAudioPermissions()
        // Capture button click listener
        cameraBinding.txtCaptureImage.setOnClickListener {
            takePhoto()
        }

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Bind the camera lifecycle to the camera provider
            val cameraProvider = cameraProviderFuture.get()

            // Build the preview
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = cameraBinding.previewView.surfaceProvider
            }

            // Set the image capture settings
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(android.util.Size(1024, 1024)) // Set HD resolution
                .build()

            // Select the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePhoto() {
        // Create a file to save the image
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object to store the captured image
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Error: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    Toast.makeText(
                        this@CameraActivity,
                        "Image saved: $savedUri",
                        Toast.LENGTH_SHORT
                    ).show()

                    RegistrationActivity.profileImageUri = savedUri
                    finish()
                }
            }
        )
    }


    private fun checkForCameraAndRecordAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Start Camera Here
            Log.d("PERMISSION", "Congo! CAMERA and RECORD_AUDIO  Permission granted")
            startCamera()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val cameraGranted =
                result.getOrDefault(Manifest.permission.CAMERA, false)
            val recordAudioGranted =
                result.getOrDefault(Manifest.permission.RECORD_AUDIO, false)

            if (cameraGranted) {
                // FINE_LOCATION permission is granted
                Log.d("PERMISSION", "Permissions granted: CAMERA")
                // getMyLocation()
            } else if (recordAudioGranted) {
                //Only COARSE_LOCATION permission is granted
                Log.d("PERMISSION", "Permissions granted: RECORD_AUDIO")
            } else {
                // No Location access granted
                Log.d("PERMISSION", "Permissions granted: NO_ACCESS")
            }
        }
}