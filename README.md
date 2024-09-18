# QR Scanner Example for Android (Kotlin) using Google ML Kit Barcode Scanner

This project demonstrates how to implement a QR code scanner in an Android app using Kotlin and Google ML Kit's Barcode Scanning API. The app scans QR codes through the device's camera, extracts the encoded information, and displays it.

## Features

- Scan QR codes using Google ML Kit’s Barcode Scanner API.
- Extract and display data from QR codes (e.g., URLs, text, etc.).
- Minimal and easy-to-understand code for beginners.

## Prerequisites

Before getting started, ensure you have the following:

- Android Studio installed on your machine.
- Basic knowledge of Kotlin and Android development.
- An Android device or emulator to test the app.

## Libraries Used

- [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning): The core library that powers the QR code scanning functionality.

## Setup Instructions

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/abbasqamar12/QR_Scanner.git
```

### 2. Open the Project in Android Studio

Open Android Studio, then select `Open an Existing Project` and navigate to the folder where you cloned the project.

### 3. Add ML Kit Barcode Scanning Dependency

Add the Google ML Kit Barcode Scanning dependency in your `build.gradle` file:

```gradle
dependencies {
    implementation 'com.google.mlkit:barcode-scanning:17.0.0'
    implementation 'androidx.camera:camera-camera2:1.0.0'
    implementation 'androidx.camera:camera-lifecycle:1.0.0'
    implementation 'androidx.camera:camera-view:1.0.0'
    implementation 'androidx.core:core-ktx:1.3.2'
}
```

### 4. Sync Gradle

After adding the dependencies, sync your Gradle files to download the necessary libraries.

### 5. Grant Camera Permission

In your `AndroidManifest.xml`, add the following permission to allow the app to access the device's camera:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

Make sure to request the camera permission at runtime (required for Android 6.0 and above). You can do this in your Kotlin code like this:

```kotlin
ActivityCompat.requestPermissions(
    this,
    arrayOf(Manifest.permission.CAMERA),
    REQUEST_CAMERA_PERMISSION
)
```

### 6. Implement the QR Code Scanner

The barcode scanning functionality is implemented in the `MainActivity.kt` file. Here's a brief outline of how it works:

- The app uses the `CameraX` API to provide a live camera preview.
- Google ML Kit's Barcode Scanner API is used to detect QR codes from the camera stream.
- The app listens for barcode scanning results and displays the decoded information.

Example code to set up barcode scanning:

```kotlin
val options = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
    .build()

val scanner = BarcodeScanning.getClient(options)

val imageAnalysis = ImageAnalysis.Builder()
    .setTargetResolution(Size(1280, 720))
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .build()

imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    scannedResultTextView.text = "Scanned: $rawValue"
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
```

### 7. Test the App

- Build and run the app on your Android device or emulator.
- The app will automatically open the camera and start scanning for QR codes.
- Once a QR code is detected, the app will display the scanned information.

## Screenshots

1. **Main Screen:**
   ![Main Screen](screenshot1.png)

2. **QR Code Scanning:**
   ![Scanning Screen](screenshot2.png)

## Troubleshooting

- **Camera Not Working:** Make sure the app has the necessary camera permissions.
- **Slow Scanning:** Use a physical device instead of an emulator for better performance.
- **No QR Code Detected:** Ensure the QR code is properly in focus and well-lit.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Google ML Kit for providing the powerful barcode scanning functionality.

---

Feel free to customize this README according to your project's structure and specific requirements.
