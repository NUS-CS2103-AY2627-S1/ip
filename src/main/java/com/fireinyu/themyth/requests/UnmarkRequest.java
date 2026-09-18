package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to mark a Task as not completed.
 *
 * @see com.fireinyu.themyth.tasks.Task
 * @see Response
 */
public class UnmarkRequest extends Request {
    /**
     * Creates a request to mark a task as incomplete.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public UnmarkRequest(List<String> posArgs, Map<String, String> kwargs) {
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
