package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to create a new TodoTask.
 *
 * @see com.fireinyu.themyth.tasks.TodoTask
 * @see Response
 */
public class TodoRequest extends Request {
    /**
     * Creates a request to add a task without a deadline.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public TodoRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs);
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING, InputFieldParser.STRING);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of();
    }

}
