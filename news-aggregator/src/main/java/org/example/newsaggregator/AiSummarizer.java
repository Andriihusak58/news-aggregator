package org.example.newsaggregator;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AiSummarizer {

    public static String summarize(String textToSummarize) {
        try {
            String apiKey = System.getenv("ANTHROPIC_API_KEY");

            String prompt = "Fasse diese Nachricht in maximal 35 Wörtern zusammen. "
                    + "Schreibe in einfachem Deutsch auf B1-Niveau. "
                    + "Nenne nur die eine wichtigste Tatsache. "
                    + "Antworte ohne Überschrift und ohne Formatierung, nur mit dem Zusammenfassungstext: " + textToSummarize;

            ObjectMapper mapper = new ObjectMapper();

            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);

            ArrayNode messages = mapper.createArrayNode();
            messages.add(message);

            ObjectNode requestJson = mapper.createObjectNode();
            requestJson.put("model", "claude-haiku-4-5-20251001");
            requestJson.put("max_tokens", 3000);
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
            String summary = root.get("content").get(0).get("text").asString();

            return summary;

        } catch (IOException e) {
            System.out.println("Error_IO: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.out.println("Error_Inter: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String test = summarize("Die Spritpreise steigen wieder nach Ende des Tankrabatts.");
        System.out.println("Саммарі: " + test);
    }
}