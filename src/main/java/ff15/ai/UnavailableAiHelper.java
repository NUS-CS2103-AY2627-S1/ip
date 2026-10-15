package ff15.ai;

import ff15.FF15Exception;

/**
 * Stands in for the AI when there is none to talk to, such as when no API key
 * has been set. Every prompt fails with an explanation of why, so the AI
 * commands report the problem to the user instead of crashing the program.
 */
public class UnavailableAiHelper implements AiHelper {
    private final String reason;

    /**
     * Creates a stand-in that turns down every prompt.
     *
     * @param reason why there is no AI, and what the user can do about it.
     */
    public UnavailableAiHelper(String reason) {
        this.reason = reason;
    }

    /** Always throws, carrying the reason given when this was created. */
    @Override
    public String getAiResponse(String systemPrompt, String userPrompt) throws FF15Exception {
        throw new FF15Exception(reason);
    }
}
