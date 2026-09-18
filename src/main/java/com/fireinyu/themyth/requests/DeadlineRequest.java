package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to create a new DeadlineRequest due by a certain datetime.
 *
 * @see com.fireinyu.themyth.tasks.DeadlineTask
 * @see Response
 */
public class DeadlineRequest extends Request {

    /**
     * Creates a request to add a task with a deadline.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public DeadlineRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs, Map.of());
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING, InputFieldParser.STRING);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of("by", InputFieldParser.DATETIME);
    }
}
