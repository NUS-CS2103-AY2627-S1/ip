package com.fireinyu.themyth.requests.events;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.requests.InputFieldParser;
import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.responses.Response;

/**
 * InterruptEvent posted immediately before the app terminates.
 *
 * @see Request
 * @see Response
 */
public class CloseRequest extends InterruptEvent {
    /**
     * Initializes a CloseRequest event triggered by app termination.
     *
     * @see Request
     */
    public CloseRequest() {
        super(new String[]{});
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
