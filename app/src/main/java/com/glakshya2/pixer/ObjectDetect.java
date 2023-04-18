package com.glakshya2.pixer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectDetect {

    private final ObjectDetector objectDetector;
    private final Uri uri;
    private final ContentResolver contentResolver;
    private final Context context;

    ObjectDetect(Uri uri, ContentResolver contentResolver, Context context) {
        Log.i("MainActivity", "New ObjectDetector");
        this.uri = uri;
        this.contentResolver = contentResolver;
        this.context = context;
        // Initialize ObjectDetect
        Log.i("MainActivity", "Initializing ObjectDetect");
        ObjectDetectorOptions objectDetectorOptions = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE).enableMultipleObjects().build();
        objectDetector = ObjectDetection.getClient(objectDetectorOptions);
        getBitMap();
    }

    public void getBitMap() {
        // Try to create bitmap from screenshot
        Log.i("MainActivity", "Attempting to create bitmap");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            Log.i("MainActivity", "Bitmap Created");
            detectImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detectImage(Bitmap bitmap) {
        // Use object detect to get bounding polygons for each object in the screenshot
        // in the form of Rect
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        Log.i("MainActivity", "Detecting Objets");
        objectDetector.process(inputImage)
                .addOnSuccessListener(
                        detectedObjects -> {
                            // Objects Detected, add detected Rect to ist to pass to CropAndConvert
                            List<Rect> detectedRects = new ArrayList<>();
                            for (DetectedObject detected : detectedObjects) {
                                detectedRects.add(detected.getBoundingBox());
                            }
                            new CropAndConvert(uri, detectedRects, contentResolver, context);
                        })
                .addOnFailureListener(
                        Throwable::printStackTrace);
    }

}