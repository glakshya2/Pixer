package com.glakshya2.pixer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLabelling extends Activity {
    private ImageLabeler labeler;
    private Uri uri;
    private List<Bitmap> bitmapArray;
    private List<Rect> detectedRects;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        labeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build());
        detectedRects = getIntent().getParcelableArrayListExtra("RectList");
        bitmapArray = new ArrayList<Bitmap>();
        uri = getIntent().getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cropBitMap();
    }

    public void cropBitMap(){
        Log.i("MainActivity", "Size: " + detectedRects.size());
        for(Rect r: detectedRects){
            bitmapArray.add(Bitmap.createBitmap(bitmap, r.left, r.top, r.width(), r.height()));
        }
        getClassification();
    }

    public void getClassification() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Bitmap bitmap1: bitmapArray){
            stringBuilder.append("A picture of ");
            InputImage inputImage = InputImage.fromBitmap(bitmap1, 0);
            labeler.process(inputImage).addOnSuccessListener(imageLabels -> {
                if (imageLabels.size() > 0) {
                    for (ImageLabel label : imageLabels) {
                        stringBuilder.append(label.getText()).append("\n");
                    }
                }
            }).addOnFailureListener(e -> e.printStackTrace());
        }
        Intent intent = new Intent(ImageLabelling.this, SpeakText.class);
        intent.putExtra("key", stringBuilder.toString());
        startActivity(intent);
    }
}