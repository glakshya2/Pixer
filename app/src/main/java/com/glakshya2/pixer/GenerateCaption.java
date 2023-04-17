package com.glakshya2.pixer;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenerateCaption {

    private final String API_URL = "https://api-inference.huggingface.co/models/nlpconnect/vit-gpt2-image-captioning";
    private final String API_TOKEN = "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";
    private final OkHttpClient client = new OkHttpClient();

    public interface CaptionListener {

        void onError(Throwable throwable);
        void onAllCaptionsGenerated(List<String> captions);
    }

    public void generateCaption(List<byte[]> imageList, CaptionListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<List<String>> task = new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                List<String> captions = new ArrayList<>();
                for (byte[] imageData : imageList) {
                    try {
                        MediaType mediaType = MediaType.parse("application/octet-stream");
                        RequestBody body = RequestBody.create(mediaType, imageData);
                        Request request = new Request.Builder()
                                .url(API_URL)
                                .post(body)
                                .addHeader("Authorization", "Bearer " + API_TOKEN)
                                .build();
                        Log.i("MainActivity", "Sending request");
                        Response response = client.newCall(request).execute();
                        Log.i("MainActivity", "Response Received");
                        String responseBody = response.body().string();
                        captions.add(responseBody);
                    } catch (IOException e) {
                        throw e;
                    }
                }
                return captions;
            }
        };
        Future<List<String>> future = executor.submit(task);
        executor.shutdown();
        try {
            List<String> captions = future.get();
            listener.onAllCaptionsGenerated(captions);
        } catch (ExecutionException | InterruptedException e) {
            listener.onError(e);
        }
    }

}
