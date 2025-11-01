package com.example.sudokuclassicwithcamera;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenAI {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Async Gemini request with image + text
     */
    public static void getGeminiResponse(String prompt, Bitmap imageBitmap, GenAIResponseCallback callback) {
        executor.execute(() -> {
            String result;
            try {
                // Convert image to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                // Prepare JSON parts
                JSONObject imagePart = new JSONObject();
                imagePart.put("mimeType", "image/png");
                imagePart.put("data", base64Image);

                JSONObject imageContent = new JSONObject();
                imageContent.put("inlineData", imagePart);

                JSONObject textPart = new JSONObject();
                textPart.put("text", prompt);

                JSONArray parts = new JSONArray();
                parts.put(textPart);
                parts.put(imageContent);

                JSONObject contentObj = new JSONObject();
                contentObj.put("parts", parts);

                JSONArray contents = new JSONArray();
                contents.put(contentObj);

                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contents);

                // Build request URL properly
                String url = BuildConfig.GEMINI_URL + BuildConfig.GEMINI_MODEL +
                        ":generateContent?key=" + BuildConfig.GEMINI_KEY;

                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body().string();
                        result = extractTextFromGeminiResponse(responseStr);
                    } else {
                        result = "Error: " + response.code() + " - " + response.message();
                    }
                }
            } catch (Exception e) {
                result = "Exception: " + e.getMessage();
                e.printStackTrace();
            }

            // Return to UI thread
            String finalResult = result;
            mainHandler.post(() -> callback.onResponse(finalResult));
        });
    }

    /**
     * Extracts the text output from Gemini JSON response
     */
    private static String extractTextFromGeminiResponse(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray candidates = root.optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0) return "NULL";

            JSONObject firstCandidate = candidates.optJSONObject(0);
            if (firstCandidate == null) return "NULL";

            JSONObject content = firstCandidate.optJSONObject("content");
            if (content == null) return "NULL";

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.length() == 0) return "NULL";

            JSONObject part = parts.optJSONObject(0);
            if (part == null) return "NULL";

            String text = part.optString("text", "").trim();
            if (text.isEmpty()) return "NULL";

            // Gemini sometimes adds markdown or commentary — clean it
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();

            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "NULL";
        }
    }


    /**
     * Parses 9x9 Sudoku matrix from JSON text
     */
    public static int[][] parseMatrix(String response) {
        try {
            // Clean up Gemini response (remove unwanted parts)
            String cleaned = response
                    .replaceAll("[^\\[\\],0-9]", "") // keep only digits, commas, brackets
                    .trim();

            // Ensure valid JSON format — wrap in array if missing outer brackets
            if (!cleaned.startsWith("[")) {
                cleaned = "[" + cleaned;
            }
            if (!cleaned.endsWith("]")) {
                cleaned = cleaned + "]";
            }

            // Parse as JSONArray
            JSONArray outer = new JSONArray(cleaned);
            int rows = outer.length();
            int[][] matrix = new int[rows][9];

            for (int i = 0; i < rows; i++) {
                JSONArray inner = outer.getJSONArray(i);
                for (int j = 0; j < 9 && j < inner.length(); j++) {
                    matrix[i][j] = inner.optInt(j, 0);
                }
            }

            // Ensure valid Sudoku matrix
            if (rows == 9 && matrix[0].length == 9) return matrix;

            System.err.println("Invalid Sudoku size: " + rows + "x" + matrix[0].length);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to parse Gemini output: " + response);
            return null;
        }
    }


    /**
     * Callback interface for Gemini response
     */
    public interface GenAIResponseCallback {
        void onResponse(String response);
    }
}
