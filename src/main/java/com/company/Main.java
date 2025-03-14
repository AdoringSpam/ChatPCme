package com.company;
import okhttp3.*;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.Scanner;
public class Main {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) {
	// Scanner for user input
    Scanner scanner = new Scanner(System.in);
    System.out.print("Sup gamer. Are you interested in a specific PC part or a full build? ");
    System.out.println("Please answer by writing 'Full Build','GPU','CPU','Motherboard','PSU','Case','Cooler', or 'Ram'. ");
    // Save answer for first question
    String want = scanner.nextLine();
    System.out.println("That's pretty sway gamer. What is your budget?(please include currency type) ");
    // Save answer for second question
    String budget = scanner.nextLine();
    // Insert answer and budget strings to helper function to be combined into one string
    String question = formatQuestion(want, budget);
    // Send question to ChatGPT API and save as a string
    String response = getChatGPTResponse(question);
    // Print out ChatGPT answer to the user
    System.out.println(response);
    }
    // Helper function to create a question based on the user's responses
    private static String formatQuestion(String want, String budget) {
        String question = "I am a PC gamer and I'm looking to buy a new " + want + ". My budget is " + budget +
                ". What would you recommend that I buy?";
        return question;
    }
    // Function to send a question to ChatGPT API
    public static String getChatGPTResponse(String input) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo"); // or "gpt-4"
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", input))
        );

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject responseObject = new JSONObject(response.body().string());
            return responseObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                System.out.println("API Response Error: " + e.getMessage());
            }
            return "Error getting response.";
        }
    }
}
