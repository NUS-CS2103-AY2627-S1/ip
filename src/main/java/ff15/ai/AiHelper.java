package ff15.ai;

import ff15.FF15Exception;

/**
 * Sends one prompt to a large language model (LLM) and returns what it said back.
 *
 * <p>This is an interface, rather than the class that does the talking, so the
 * rest of FF15 never depends on the LLM library itself. {@link LangChainAiHelper}
 * is the real one; {@link UnavailableAiHelper} stands in when there is no model
 * to talk to; and a test can pass a lambda that returns a fixed reply, so the
 * AI commands can be tested without a network or an API key.
 */
@FunctionalInterface
public interface AiHelper {
    /**
     * Sends a prompt to the model and returns its reply.
     *
     * @param systemPrompt instructions that set up how the model should behave.
     * @param userPrompt what the user actually asked.
     * @return the text of the model's reply.
     * @throws FF15Exception if the model could not be reached, explained for the user to read.
     */
    String getAiResponse(String systemPrompt, String userPrompt) throws FF15Exception;
}
