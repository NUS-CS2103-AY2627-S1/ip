package ff15.ai;

import java.time.Duration;
import java.util.List;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.exception.AuthenticationException;
import dev.langchain4j.exception.ModelNotFoundException;
import dev.langchain4j.exception.RateLimitException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import ff15.FF15Exception;

/**
 * Talks to a remote LLM through the LangChain4j library. Groq is used, through
 * its OpenAI-compatible API, as it has a free tier.
 *
 * <p>The API key is read from the {@value #API_KEY_VARIABLE} environment
 * variable rather than written into the code. The key is as good as a password,
 * so it must never end up in the repository for anyone to copy.
 *
 * <p>Based on the SE-EDU tutorial "Adding AI Features to a Java App":
 * https://se-education.org/guides/tutorials/addingAiToJavaApp.html
 */
public class LangChainAiHelper implements AiHelper {
    /** The environment variable the API key is read from. */
    public static final String API_KEY_VARIABLE = "LLM_API_KEY";

    /** Where Groq serves its OpenAI-compatible API. */
    private static final String BASE_URL = "https://api.groq.com/openai/v1";

    /**
     * Which of Groq's models answers. Groq retires models every few months (the
     * tutorial's llama-3.3-70b-versatile went on Aug 16 2026), so when this stops
     * working, pick a replacement from https://console.groq.com/docs/deprecations.
     */
    private static final String MODEL_NAME = "openai/gpt-oss-120b";

    /** How long to wait for a reply before giving up, so a dead network cannot hang FF15 for long. */
    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final ChatModel model;

    /**
     * Creates a helper that sends its prompts to Groq.
     *
     * @param apiKey the key Groq issued, which identifies whose account pays for each prompt.
     */
    public LangChainAiHelper(String apiKey) {
        model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(BASE_URL)
                .modelName(MODEL_NAME)
                .timeout(TIMEOUT)
                .build();
    }

    /**
     * Returns a helper using the key in {@value #API_KEY_VARIABLE}, or, when that
     * is not set, a stand-in that tells the user how to set it. Missing the key
     * is expected (not everyone wants the AI), so it must not stop FF15 starting.
     */
    public static AiHelper fromEnvironment() {
        String apiKey = System.getenv(API_KEY_VARIABLE);
        if (apiKey == null || apiKey.isBlank()) {
            return new UnavailableAiHelper("I need an AI key for that. Set " + API_KEY_VARIABLE
                    + " to your Groq API key, then restart me. The user guide says how.");
        }
        return new LangChainAiHelper(apiKey.strip());
    }

    /**
     * Sends the prompt to Groq and returns the reply. The library reports every
     * failure as an unchecked exception; these are caught here and turned into
     * an {@link FF15Exception}, so a bad key or a lost connection reaches the
     * user as a message rather than as a crash.
     */
    @Override
    public String getAiResponse(String systemPrompt, String userPrompt) throws FF15Exception {
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        SystemMessage.from(systemPrompt),
                        UserMessage.from(userPrompt)))
                .build();
        try {
            return model.chat(request).aiMessage().text();
        } catch (AuthenticationException e) {
            throw new FF15Exception("The AI didn't accept your key. Check " + API_KEY_VARIABLE
                    + " is set right, then restart me.");
        } catch (ModelNotFoundException e) {
            throw new FF15Exception("The AI model I use (" + MODEL_NAME + ") has been retired. "
                    + "Nobody told me. Nobody tells me anything. FF15 needs an update.");
        } catch (RateLimitException e) {
            throw new FF15Exception("The AI says we've asked too much. Give it a minute, then try again.");
        } catch (RuntimeException e) {
            throw new FF15Exception("I called the AI, but it didn't pick up: " + e.getMessage());
        }
    }
}
