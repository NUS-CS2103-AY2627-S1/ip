package kiki.ai;

import java.util.List;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import kiki.exception.KikiException;

/**
 * LangChain4j-backed AI help service using an OpenAI-compatible endpoint.
 */
public class LangChainAiService implements AiService {
    private static final String API_KEY_VARIABLE = "LLM_API_KEY";
    private static final String DEFAULT_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";
    private static final String SYSTEM_PROMPT = "You are Kiki, a concise task assistant. "
            + "Answer questions only about these commands: todo <description>, deadline <description> /by yyyy-MM-dd HHmm, "
            + "event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm, list, mark <number>, unmark <number>, "
            + "delete <number>, find <keyword>, check day <date>, check week <date>, sort, and bye. "
            + "Do not invent commands and do not claim to execute actions. Keep answers under three sentences.";

    private final ChatModel model;

    /**
     * Creates a service from environment configuration.
     */
    public LangChainAiService() {
        this(createModel());
    }

    LangChainAiService(ChatModel model) {
        this.model = model;
    }

    @Override
    public String ask(String question) throws KikiException {
        if (model == null) {
            throw new KikiException("AI help is unavailable. Set the LLM_API_KEY environment variable first.");
        }

        try {
            ChatRequest request = ChatRequest.builder()
                    .messages(List.of(SystemMessage.from(SYSTEM_PROMPT), UserMessage.from(question)))
                    .build();
            return model.chat(request).aiMessage().text();
        } catch (RuntimeException e) {
            throw new KikiException("AI help is temporarily unavailable. Please try again later.");
        }
    }

    private static ChatModel createModel() {
        String apiKey = System.getenv(API_KEY_VARIABLE);
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(System.getenv().getOrDefault("LLM_BASE_URL", DEFAULT_BASE_URL))
                .modelName(System.getenv().getOrDefault("LLM_MODEL", DEFAULT_MODEL))
                .build();
    }
}
