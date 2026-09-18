package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to submit an answer to the currently active quiz question.
 *
 * @see com.fireinyu.themyth.chatmodes.QuizMode
 * @see Response
 */
public class AnswerRequest extends Request {

    /**
     * Constructs an {@code AnswerRequest} with the specified positional and keyword arguments.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public AnswerRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs);
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING, InputFieldParser.INT);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of();
    }
}
