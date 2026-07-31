package org.example.newsaggregator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramSender {

    public static void sendMessage(String text, String chatId, String threadId) {
        try {
            String token = System.getenv("TELEGRAM_BOT_TOKEN");
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            String url = "https://api.telegram.org/bot" + token
                    + "/sendMessage?chat_id=" + chatId
                    + "&text=" + encodedText
                    + "&disable_web_page_preview=true";

            if (threadId != null && !threadId.isEmpty()) {
                url += "&message_thread_id=" + threadId;
            }

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