package ekud.ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Fetches a random dad joke from the API Ninjas Dad Jokes API.
 * Isolated from {@link ekud.Ekud} so HTTP, JSON parsing, and error handling
 * stay out of the chatbot's greeting and command loop.
 */
public class DadJokeFetcher {
    /**
     * API key for API Ninjas. Committed so that you do not have to register the key yourself.
     */
    private static final String API_KEY = "JH21nsBaZAijePjZZtBNwl27n6G6SbnPjHTCsB13";
    private static final String API_URL = "https://api.api-ninjas.com/v1/dadjokes";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    /**
     * Requests one random dad joke.
     * Returns {@code null} on any failure (rate/quota limit, timeout, network
     * error, unexpected response) so the chatbot can skip the joke and still
     * greet the user normally.
     *
     * @return the joke text, or {@code null} if it could not be fetched
     */
    public static String fetch() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-Api-Key", API_KEY)
                    .timeout(TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Non-200 covers API limit (429), auth errors, server errors, etc.
            if (response.statusCode() != 200) {
                return null;
            }
            String joke = extractJsonStringField(response.body(), "joke");
            if (joke == null || joke.isBlank()) {
                return null;
            }
            return joke;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            // Timeout, no network, or other unexpected problems: omit the joke.
            return null;
        }
    }

    /**
     * Reads a JSON string field from a small response without a JSON library.
     * The dad-jokes payload is a simple array: {@code [{"joke":"..."}]}.
     *
     * @param json      JSON text to search
     * @param fieldName name of the string field to extract
     * @return the unescaped field value, or {@code null} if it cannot be found
     */
    private static String extractJsonStringField(String json, String fieldName) {
        if (json == null || json.isBlank()) {
            return null;
        }
        String key = "\"" + fieldName + "\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex + key.length());
        if (colon < 0) {
            return null;
        }
        int index = colon + 1;
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        if (index >= json.length() || json.charAt(index) != '"') {
            return null;
        }
        index++;

        StringBuilder value = new StringBuilder();
        while (index < json.length()) {
            char current = json.charAt(index);
            if (current == '\\') {
                index = appendUnescaped(json, index, value);
            } else if (current == '"') {
                return value.toString();
            } else {
                value.append(current);
                index++;
            }
        }
        return null;
    }

    /**
     * Appends the character represented by a JSON escape at {@code backslashIndex}
     * and returns the index after that escape sequence.
     *
     * @param json           JSON text
     * @param backslashIndex index of the {@code \} character
     * @param output         builder receiving the unescaped character
     * @return index of the next character to process
     */
    private static int appendUnescaped(String json, int backslashIndex, StringBuilder output) {
        if (backslashIndex + 1 >= json.length()) {
            return backslashIndex + 1;
        }
        char escaped = json.charAt(backslashIndex + 1);
        if (escaped == 'u' && backslashIndex + 5 < json.length()) {
            String hex = json.substring(backslashIndex + 2, backslashIndex + 6);
            try {
                output.append((char) Integer.parseInt(hex, 16));
                return backslashIndex + 6;
            } catch (NumberFormatException e) {
                output.append(escaped);
                return backslashIndex + 2;
            }
        }
        switch (escaped) {
            case '"', '\\', '/' -> output.append(escaped);
            case 'b' -> output.append('\b');
            case 'f' -> output.append('\f');
            case 'n' -> output.append('\n');
            case 'r' -> output.append('\r');
            case 't' -> output.append('\t');
            default -> output.append(escaped);
        }
        return backslashIndex + 2;
    }
}
