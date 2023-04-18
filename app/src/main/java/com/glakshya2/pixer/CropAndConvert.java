package com.glakshya2.pixer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CropAndConvert {

    private final List<Rect> detectedRects;
    private final List<Bitmap> bitmapArray = new ArrayList<>();
    private final List<byte[]> JPEGArray = new ArrayList<>();
    private final Bitmap bitmap;
    private final Context context;

    CropAndConvert(Uri uri, List<Rect> detectedRects, ContentResolver contentResolver, Context context) {
        Log.i("MainActivity", "New CropAndConvert");
        this.detectedRects = detectedRects;
        this.context = context;
        // Attempt to get bitmap from uri
        Log.i("MainActivity", "Getting bitmap from uri");
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cropBitmap();
    }

    public void cropBitmap() {
        // Cropping bitmap and storing each bitmap separately for image processing
        Log.i("MainActivity", "Total number of rects = " + detectedRects.size());
        for (int i = 0; i < detectedRects.size(); i++) {
            bitmapArray.add(Bitmap.createBitmap(bitmap, detectedRects.get(i).left, detectedRects.get(i).top,
                    detectedRects.get(i).width(), detectedRects.get(i).height()));
        }
        convertToJPEG();
    }

    public void convertToJPEG() {
        // Converting each bitmap to JPEG to send to API
        Log.i("MainActivity", "Converting each bitmap to JPEG");
        for (int i = 0; i < bitmapArray.size(); i++) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapArray.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] jpegData = stream.toByteArray();
            JPEGArray.add(jpegData);
        }
        getCaptions();
    }

    public void getCaptions() {
        Log.i("MainActivity", "Initialize Generate Caption Class");
        GenerateCaption captionGenerator = new GenerateCaption();
        // create a CaptionListener to receive the generated captions
        GenerateCaption.CaptionListener listener = captions -> {
            // Converting received response into a single string for TextToSpeech
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < captions.size(); i++) {
                String newString = captions.get(i);
                // Cutting out only the required text
                newString = newString.substring(21, newString.length() - 3);
                stringBuilder.append("A picture of ");
                stringBuilder.append(newString);
                if (i < captions.size() - 1) {
                    stringBuilder.append("and ");
                }
            }
            // Passing text to TextToSpeech
            new SpeakText(context, stringBuilder.toString());
        };
        captionGenerator.generateCaption(JPEGArray, listener);
    }
}
