package com.fireinyu.themyth.exceptions;

import com.fireinyu.themyth.requests.Request;

/**
 * Thrown when the user inputs a command that is unsupported by the active ChatMode.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 */
public class InvalidCommandException extends TweakingException {

    /**
     * Initializes an InvalidCommandException caused by an invalid Request.
     *
     * @param request guilty invalid Request.
     * @see Request
     */
    public InvalidCommandException(Request request) {
        super(String.format("Sweetheart, \"%s\"? That is NOT in my fabulous repertoire! Try harder, babe! 💅",
                request.getArg(0, String.class)));
    }
}
