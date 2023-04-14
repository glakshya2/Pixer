package com.glakshya2.pixer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObjectDetect extends Activity {

    private ObjectDetector objectDetector;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstance) {
        Log.i("Main Activity", "New ObjectDetector");
        super.onCreate(savedInstance);
        ObjectDetectorOptions objectDetectorOptions = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE).enableMultipleObjects().enableClassification().build();
        objectDetector = ObjectDetection.getClient(objectDetectorOptions);
        uri = getIntent().getData();
        getBitMap();
    }

    public void getBitMap() {
        // Try to create bitmap from screenshot
        Log.i("MainActivity", "Attempting to create bitmap");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Log.i("MainActivity", "Bitmap Created");
            detectImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detectImage(Bitmap bitmap) {
        if(bitmap == null){
            Log.i("MainActivity", "bitmap is null");
        } else {
            Log.i("MainActivity", "bitmap is not null");
        }
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        if(inputImage == null){
            Log.i("MainActivity", "image is null");
        } else {
            Log.i("MainActivity", "image is not null");
        }
        objectDetector.process(inputImage)
                .addOnSuccessListener(
                        detectedObjects -> {
                            StringBuilder stringBuilder = new StringBuilder();
                            List<Rect> detectedRects = new ArrayList<Rect>();
                            for (DetectedObject detected : detectedObjects) {
                                detectedRects.add(detected.getBoundingBox());
                                stringBuilder.append(detected.getLabels());
                            }
                            Intent intent = new Intent(ObjectDetect.this, SpeakText.class);
                            intent.putParcelableArrayListExtra("RectList", (ArrayList<? extends Parcelable>) detectedRects);
                            intent.setData(uri);
                            intent.putExtra("string", stringBuilder.toString());
                            startActivity(intent);
                        })
                .addOnFailureListener(
                        e -> e.printStackTrace());
    }
}