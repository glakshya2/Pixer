package com.glakshya2.pixer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLabelling {
    private final ImageLabeler labeler;
    private final List<Bitmap> bitmapArray = new ArrayList<>();
    private final List<Rect> detectedRects;
    private final Bitmap bitmap;
    private final Context context;

    ImageLabelling(List<Rect> detectedRects, Uri uri, ContentResolver contentResolver, Context context) {
        Log.i("MainActivity", "New ImageLabelling");
        this.detectedRects = detectedRects;
        this.context = context;
        labeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cropBitMap();
    }

    public void cropBitMap() {
        Log.i("MainActivity", "Size: " + detectedRects.size());
        for (Rect r : detectedRects) {
            bitmapArray.add(Bitmap.createBitmap(bitmap, r.left, r.top, r.width(), r.height()));
        }
        getClassification();
    }

    public void getClassification() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Bitmap bitmap1 : bitmapArray) {
            stringBuilder.append("A picture of ");
            InputImage inputImage = InputImage.fromBitmap(bitmap1, 0);
            labeler.process(inputImage).addOnSuccessListener(imageLabels -> {
                if (imageLabels.size() > 0) {
                    for (ImageLabel label : imageLabels) {
                        stringBuilder.append(label.getText()).append("\n");
                    }
                }
            }).addOnFailureListener(Throwable::printStackTrace);
        }
        Log.i("MainActivity", "Text: " + stringBuilder);
        new SpeakText(context, stringBuilder.toString());
    }
}