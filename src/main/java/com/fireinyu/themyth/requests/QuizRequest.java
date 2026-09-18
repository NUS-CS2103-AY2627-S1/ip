package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to display the current active quiz question and choices.
 *
 * @see com.fireinyu.themyth.chatmodes.QuizMode
 * @see Response
 */
public class QuizRequest extends Request {

    /**
     * Constructs a {@code QuizRequest} with the specified positional and keyword arguments.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public QuizRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs);
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of();
    }

}
