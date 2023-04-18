package com.glakshya2.pixer;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class SpeakText implements TextToSpeech.OnInitListener {

    private final TextToSpeech textToSpeech;
    private final String text;

    SpeakText(Context context, String text) {
        Log.i("MainActivity", "New SpeakText");
        textToSpeech = new TextToSpeech(context, this);
        this.text = text;
    }

    private void speak() {
        // Speaking
        if (textToSpeech != null) {
            Log.i("MainActivity", "Speaking");
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myUtteranceId");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "myUtteranceId");
        }
    }

    protected void finalize() {
        // Closing TextToSpeech Engine
        Log.i("MainActivity", "Closing TextToSpeech Engine");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        // Checking if TextToSpeech Engine has been successfully initiated
        Log.i("MainActivity", "Checking TextToSpeech engine initialization status");
        if (status == TextToSpeech.SUCCESS) {
            // Setting language
            Log.i("MainActivity", "Setting language");
            textToSpeech.setLanguage(Locale.US);
            speak();
        } else {
            // TextToSpeech engine failed to initialize
            Log.i("MainActivity", "TextToSpeech Engine failed to initialize");
        }
    }
}
