package kiki.ai;

import kiki.exception.KikiException;

/**
 * Provides read-only natural-language help about Kiki's commands.
 */
public interface AiService {
    /**
     * Answers a question about Kiki.
     *
     * @param question User's natural-language question.
     * @return AI-generated answer.
     * @throws KikiException If the service is unavailable or the request fails.
     */
    String ask(String question) throws KikiException;
}
