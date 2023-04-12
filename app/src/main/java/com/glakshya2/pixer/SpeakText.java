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
        Log.i("SpeakText", "New SpeakText");
        textToSpeech = new TextToSpeech(this, this);
        // Fetching text to be spoken through intent
        Log.i("SpeakText", "Fetching text from intent");
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("key")) {
            text = intent.getStringExtra("key");
        } else {
            // Could not find text to be spoken through intent
            Log.i("SpeakText", "Text not found");
            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak() {
        // Speaking
        if (textToSpeech != null) {
            Log.i("SpeakText", "Speaking");
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myUtteranceId");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "myUtteranceId");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Closing TextToSpeech Engine
        Log.i("SpeakText", "Closing TextToSpeech Engine");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        Log.i("SpeakText", "Closing SpeakText Activity");
        finish();
    }

    @Override
    public void onInit(int status) {
        // Checking if TextToSpeech Engine has been successfully initiated
        Log.i("SpeakText", "Checking TextToSpeech engine initialization status");
        if (status == TextToSpeech.SUCCESS) {
            // Setting language
            Log.i("SpeakText", "Setting language");
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Letting user know that selected language is not supported
                Log.i("SpeakText", "Selected language is not selected");
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                speak();
            }
        } else {
            // TextToSpeech engine failed to initialize
            Log.i("SpeakText", "TextToSpeech Engine failed to initialize");
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
