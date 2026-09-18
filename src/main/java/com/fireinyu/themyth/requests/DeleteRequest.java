package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to delete an existing Task.
 *
 * @see com.fireinyu.themyth.tasks.Task
 * @see Response
 */
public class DeleteRequest extends Request {
    /**
     * Creates a request to delete a task by its displayed index.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public DeleteRequest(List<String> posArgs, Map<String, String> kwargs) {
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
