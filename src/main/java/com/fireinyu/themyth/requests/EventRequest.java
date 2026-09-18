package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.responses.Response;

/**
 * User request to create a new EventTask occurring between start and end times.
 *
 * @see com.fireinyu.themyth.tasks.EventTask
 * @see Response
 */
public class EventRequest extends Request {
    /**
     * Creates a request to add an event with start and end times.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public EventRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs);
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING, InputFieldParser.STRING);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of("from", InputFieldParser.DATETIME, "to", InputFieldParser.DATETIME);
    }
}
