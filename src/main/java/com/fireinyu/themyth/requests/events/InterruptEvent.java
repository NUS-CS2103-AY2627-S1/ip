package com.fireinyu.themyth.requests.events;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.responses.Response;

/**
 * Request posted from the app instead of the user.
 * Represents application lifecycle events handled synchronously by the active chat mode.
 *
 * @see Request
 * @see Response
 */
public abstract class InterruptEvent extends Request {

    /**
     * Creates an internal request with the supplied event arguments.
     */
    protected InterruptEvent(String[] params) {
        super(InterruptEvent.getArgs(params), Map.of());
    }

    /**
     * Prepends the internal command name to the event arguments.
     */
    private static List<String> getArgs(String[] params) {
        String[] arguments = new String[params.length + 1];
        arguments[0] = "interrupt";
        System.arraycopy(params, 0, arguments, 1, params.length);
        return List.of(arguments);
    }

}
