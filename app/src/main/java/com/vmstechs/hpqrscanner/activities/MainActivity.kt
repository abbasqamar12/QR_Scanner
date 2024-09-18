package com.vmstechs.hpqrscanner.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.vmstechs.hpqrscanner.R
import com.vmstechs.hpqrscanner.camera.CameraActivity
import com.vmstechs.hpqrscanner.databinding.ActivityMainBinding
import com.vmstechs.hpqrscanner.register.RegistrationActivity
import com.vmstechs.hpqrscanner.user_details.ResultActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private var isScanned = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


       // checkCameraPermission()

        checkForCameraAndRecordAudioPermissions()

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()


        mainBinding.txtRegisterNow.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegistrationActivity::class.java
                )
            )
        }
    }



/*    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Intent().also {
                it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", packageName, null)
                startActivity(it)
                finish()
            }
        }
    }*/

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                // Handle exception
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (!isScanned) {
                            when (barcode.valueType) {
                                Barcode.TYPE_URL -> {
                                    val url = barcode.url?.url
                                    // Handle the URL
                                    Log.d("MY_QR", "QR Data(URL): $url")
                                    isScanned = true
                                    setScanResult(url)
                                    break
                                }

                                Barcode.TYPE_TEXT -> {
                                    val text = barcode.displayValue
                                    // Handle the text
                                    Log.d("MY_QR", "QR Data(Text): $text")
                                    isScanned = true
                                    setScanResult(text)
                                    break
                                }
                                // Handle other types of barcodes
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun setScanResult(response: String?) {
        //mainBinding.txtResult.text = "Response: $response"
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("RESPONSE", response)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()
        startCamera()
        isScanned = false
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
