package com.fireinyu.themyth.chatmodes;

import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.responses.Response;

/**
 * Request-Response model that echoes the user input.
 *
 * @see Request
 * @see Response
 */
public class EchoMode extends ChatMode {
    @Override
    protected Response respondToRemaining(Request request) {
        return new Response(String.format(
                "Oh honey, did you really just say \"%s\"? Spill every last drop of tea right now! ☕✨",
                request.getArg(0, String.class)));
    }
}
