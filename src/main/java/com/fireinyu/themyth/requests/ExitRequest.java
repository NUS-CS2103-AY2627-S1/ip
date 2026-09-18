package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to exit the app.
 *
 * @see com.fireinyu.themyth.tasks.EventTask
 * @see Response
 */
public class ExitRequest extends Request {
    /**
     * Creates a request to end the conversation.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public ExitRequest(List<String> posArgs, Map<String, String> kwargs) {
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
