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
	// write your code here
    Scanner scanner = new Scanner(System.in);
    System.out.print("Sup gamer. Are you interested in a specific PC part or a full build? ");
    System.out.println("Please answer by writing 'Full Build','GPU','CPU','Motherboard','PSU','Case','Cooler', or 'Ram'. ");
    String want = scanner.nextLine();
    System.out.println("That's pretty sway gamer. What is your budget?(please include currency type) ");
    String budget = scanner.nextLine();

    String question = formatQuestion(want, budget);

    String response = getChatGPTResponse(question);
    System.out.println(response);
    }

    private static String formatQuestion(String want, String budget) {
        String question = "I am a PC gamer and I'm looking to buy a new " + want + ". My budget is " + budget +
                ". What would you recommend that I buy?";
        return question;
    }

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
            return "Error getting response.";
        }
    }
}
