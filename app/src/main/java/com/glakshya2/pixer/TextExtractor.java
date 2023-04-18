package com.glakshya2.pixer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;

public class TextExtractor {

    private final Uri uri;
    private final ContentResolver contentResolver;
    private final Context context;

    TextExtractor(Uri uri, ContentResolver contentResolver, Context context) {
        Log.i("MainActivity", "New Text Extractor");
        this.uri = uri;
        this.contentResolver = contentResolver;
        this.context = context;
        getBitMap();
    }

    public void getBitMap() {
        // Try to create bitmap from screenshot
        Log.i("MainActivity", "Attempting to create bitmap");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            Log.i("MainActivity", "Bitmap Created");
            getTextFromImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getTextFromImage(Bitmap bitmap) {
        // Extracting text from bitmap
        TextRecognizer recognizer = new TextRecognizer.Builder(context).build();
        // Check if text recognizer is active;
        if (!recognizer.isOperational()) {
            // Letting user know that there is an issue with TextRecognizer
            Log.i("MainActivity", "TextRecognizer is not active");
        } else {
            // Beginning Text Extraction Process
            Log.i("MainActivity", "Beginning text Extraction");
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
            Log.i("MainActivity", "Extracted Text: \n" + text);
            new SpeakText(context, text);
        }
    }
}