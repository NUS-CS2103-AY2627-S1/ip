package com.fireinyu.themyth.responses;

import com.fireinyu.themyth.exceptions.TweakingException;

/**
 * Reports a recoverable request or lifecycle error without requesting application exit.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 * @see com.fireinyu.themyth.TheMyth
 * @see TweakingException
 */
public class ExceptionResponse extends Response {

    /**
     * Initializes an ExceptionResponse in response to a thrown TweakingException.
     * Sets the mood to {@link Response.Mood#ANGRY}.
     *
     * @param cause thrown TweakingException.
     * @see TweakingException
     */
    public ExceptionResponse(TweakingException cause) {
        super(cause.getMessage(), false, Mood.ANGRY);
    }
}
