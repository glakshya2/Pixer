package com.glakshya2.pixer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class TextExtractor extends Activity {

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TextExtractor", "New Text Extractor");
        // Get Uri of screenshot from previous class
        Log.i("TextExtractor", "Fetching Uri from ScreenshotRetriever's Intent");
        uri = getIntent().getData();
        getBitMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Closing TextExtractor Activity
        Log.i("TextExtractor", "Closing TextExtractor Activity");
        finish();
    }

    public void getBitMap() {
        // Try to create bitmap from screenshot
        Log.i("TextExtractor", "Attempting to create bitmap");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Log.i("TextExtractor", "Bitmap Created");
            getTextFromImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getTextFromImage(Bitmap bitmap) {
        // Extracting text from bitmap
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        // Check if text recognizer is active;
        if (!recognizer.isOperational()) {
            // Letting user know that there is an issue with TextRecognizer
            Log.i("TextExtractor", "TextRecognizer is not active");
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        } else {
            // Beginning Text Extraction Process
            Log.i("TextExtractor", "Beginning text Extraction");
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> sparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            // Extracting and adding text to StringBuilder line by line
            for (int i = 0; i < sparseArray.size(); i++) {
                TextBlock textBlock = sparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            String text = stringBuilder.toString();
            Log.i("TextExtractor", "Extracted Text: \n" + text);
            // Creating intent to move program to SpeakText
            Log.i("TextExtractor", "Creating intent to move to SpeakText");
            Intent intent = new Intent(TextExtractor.this, SpeakText.class);
            intent.putExtra("key", text);
            startActivity(intent);
        }
    }
}