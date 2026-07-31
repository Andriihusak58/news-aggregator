package org.example.newsaggregator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramSender {

    public static void sendMessage(String text) {
        try {
            String token = System.getenv("TELEGRAM_BOT_TOKEN");
            String chatId = System.getenv("TELEGRAM_CHAT_ID");

            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "https://api.telegram.org/bot" + token
                    + "/sendMessage?chat_id=" + chatId
                    + "&text=" + encodedText;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Telegram помилка: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Telegram помилка: " + e.getMessage());
        }
    }
}