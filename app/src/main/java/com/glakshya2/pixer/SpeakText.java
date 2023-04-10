package com.glakshya2.pixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class SpeakText extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Main Activity", "InSpeakText");

        textToSpeech = new TextToSpeech(this, this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("key")) {
            text = intent.getStringExtra("key");

        } else {
            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak() {
        Log.i("Main Activity", "In Speak Function");
        if (textToSpeech != null) {
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myUtteranceId");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "myUtteranceId");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        finish();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                speak();
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
