package org.example.newsaggregator;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RuSummarizer {

    public static String translateTitle(String germanTitle) {
        return translate(germanTitle);
    }

    public static String translate(String germanText) {
        try {
            String apiKey = System.getenv("ANTHROPIC_API_KEY");

            String prompt =
                    """
                    Übersetze diesen deutschen Text ins Russische.
                    Bleibe neutral, verändere keine Fakten, keine eigene Meinung hinzufügen.
                    Gib nur den übersetzten Text zurück, ohne Erklärungen oder Anführungszeichen.

                    Text:
                    """ + germanText;

            ObjectMapper mapper = new ObjectMapper();

            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);

            ArrayNode messages = mapper.createArrayNode();
            messages.add(message);

            ObjectNode requestJson = mapper.createObjectNode();
            requestJson.put("model", "claude-haiku-4-5-20251001");
            requestJson.put("max_tokens", 1000);
            requestJson.set("messages", messages);

            String requestBody = mapper.writeValueAsString(requestJson);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            return root.get("content").get(0).get("text").asString();

        } catch (Exception e) {
            System.out.println("RuSummarizer помилка: " + e.getMessage());
            return null;
        }
    }
}