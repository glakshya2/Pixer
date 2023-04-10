package com.glakshya2.pixer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

public class TextExtractor extends Activity {
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Main Activity", "New Text Extractor");
        uri = getIntent().getData();
        getBitMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
public void getBitMap(){
    try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        Log.i("Main Activity", "Got Bitmap");
        getTextFromImage(bitmap);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    @SuppressLint("SetTextI18n")
    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> sparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < sparseArray.size(); i++) {
                TextBlock textBlock = sparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            String text = stringBuilder.toString();
            Log.i("Main Activity", text);
            Intent intent = new Intent(TextExtractor.this, SpeakText.class);
            intent.putExtra("key", text);
            startActivity(intent);
        }
    }
}
