package com.glakshya2.pixer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CropAndConvert extends Activity{

    private Uri uri;
    private List<Rect> detectedRects;
    private List<Bitmap> bitmapArray;

    private List<byte[]> JPEGArray;
    private Bitmap bitmap;
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.i("Main Activity", "New CropAndConvert");
        uri = getIntent().getData();
        detectedRects = getIntent().getParcelableArrayListExtra("RectList");
        bitmapArray = new ArrayList<Bitmap>();
        JPEGArray = new ArrayList<byte[]>();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cropBitmap();
    }

    public void cropBitmap(){
        Log.i("MainActivity", "Size: " + detectedRects.size());
        for(int i = 0; i < detectedRects.size(); i++){
            bitmapArray.add(Bitmap.createBitmap(bitmap, detectedRects.get(i).left, detectedRects.get(i).top, detectedRects.get(i).width(), detectedRects.get(i).height()));
        }
        convertToJPEG();
    }

    public void convertToJPEG(){
        for(int i = 0; i < bitmapArray.size(); i++){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapArray.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] jpegData = stream.toByteArray();
            JPEGArray.add(jpegData);
        }
        getCaptions();
    }

    public void getCaptions(){

        GenerateCaption captionGenerator = new GenerateCaption();
        // create a CaptionListener to receive the generated captions
        GenerateCaption.CaptionListener listener = new GenerateCaption.CaptionListener() {
            @Override
            public void onError(Throwable throwable) {
                // Do nothing
            }

            @Override
            public void onAllCaptionsGenerated(List<String> captions) {
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < captions.size(); i++){
                    String newString = captions.get(i);
                    newString = newString.substring(21, newString.length()-3);
                    stringBuilder.append("A picture of ");
                    stringBuilder.append(newString);
                    if(i<captions.size()-1){
                        stringBuilder.append("and ");
                    }
                }
                Intent intent = new Intent(CropAndConvert.this, SpeakText.class);
                intent.putExtra("key", stringBuilder.toString());
                startActivity(intent);
            }
        };
        captionGenerator.generateCaption(JPEGArray, listener);
    }
}
