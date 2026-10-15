package eva;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends questions about Eva to a remote large language model.
 */
public class AiHelper {
    private static final URI SERVICE_URI = URI.create(
            "https://api.groq.com/openai/v1/chat/completions");
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";
    private static final Pattern CONTENT_PATTERN = Pattern.compile(
            "\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");

    private final String apiKey;

    /**
     * Creates an AI helper using the API key in {@code LLM_API_KEY}.
     */
    public AiHelper() {
        this(System.getenv("LLM_API_KEY"));
    }

    AiHelper(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Sends a system prompt and user question to the configured LLM.
     *
     * @param systemPrompt Instructions and Eva feature information.
     * @param userPrompt Question entered by the user.
     * @return Text returned by the LLM.
     * @throws EvaException If configuration or the remote request fails.
     */
    public String getAiResponse(String systemPrompt, String userPrompt)
            throws EvaException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new EvaException(
                    "AI help needs an LLM_API_KEY. "
                            + "See the User Guide for setup instructions.");
        }

        String requestBody = createRequestBody(systemPrompt, userPrompt);
        HttpRequest request = HttpRequest.newBuilder(SERVICE_URI)
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new EvaException(
                        "The AI service rejected the request. "
                                + "Check your API key and try again.");
            }
            return extractContent(response.body());
        } catch (IOException e) {
            throw new EvaException(
                    "I couldn't reach the AI service. "
                            + "Check your connection and try again.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EvaException("The AI request was interrupted.");
        }
    }

    private static String createRequestBody(
            String systemPrompt, String userPrompt) {
        return "{\"model\":\"" + MODEL_NAME + "\",\"messages\":["
                + "{\"role\":\"system\",\"content\":\""
                + escapeJson(systemPrompt) + "\"},"
                + "{\"role\":\"user\",\"content\":\""
                + escapeJson(userPrompt) + "\"}],\"temperature\":0.2}";
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String extractContent(String responseBody)
            throws EvaException {
        Matcher matcher = CONTENT_PATTERN.matcher(responseBody);
        if (!matcher.find()) {
            throw new EvaException(
                    "The AI service returned an unexpected response.");
        }
        return unescapeJson(matcher.group(1));
    }

    private static String unescapeJson(String text) throws EvaException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current != '\\') {
                result.append(current);
                continue;
            }
            if (++i >= text.length()) {
                throw new EvaException(
                        "The AI service returned an unexpected response.");
            }
            char escaped = text.charAt(i);
            switch (escaped) {
            case '\"':
            case '\\':
            case '/':
                result.append(escaped);
                break;
            case 'b':
                result.append('\b');
                break;
            case 'f':
                result.append('\f');
                break;
            case 'n':
                result.append('\n');
                break;
            case 'r':
                result.append('\r');
                break;
            case 't':
                result.append('\t');
                break;
            case 'u':
                i = appendUnicodeEscape(text, i, result);
                break;
            default:
                throw new EvaException(
                        "The AI service returned an unexpected response.");
            }
        }
        return result.toString();
    }

    private static int appendUnicodeEscape(
            String text, int escapeIndex, StringBuilder result)
            throws EvaException {
        int firstHexIndex = escapeIndex + 1;
        int endIndex = firstHexIndex + 4;
        if (endIndex > text.length()) {
            throw new EvaException(
                    "The AI service returned an unexpected response.");
        }
        try {
            int codePoint = Integer.parseInt(
                    text.substring(firstHexIndex, endIndex), 16);
            result.append((char) codePoint);
            return endIndex - 1;
        } catch (NumberFormatException e) {
            throw new EvaException(
                    "The AI service returned an unexpected response.");
        }
    }
}
