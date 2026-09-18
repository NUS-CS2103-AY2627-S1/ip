package com.fireinyu.themyth.responses;

import com.fireinyu.themyth.exceptions.FatalException;

/**
 * Response to a Request from the user or an event, which resulted in a FatalException.
 * Exits the app after execution.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 * @see com.fireinyu.themyth.TheMyth
 * @see FatalException
 */
public class FatalResponse extends Response {

    /**
     * Initializes a FatalResponse in response to a thrown FatalException.
     *
     * @param cause thrown FatalException.
     * @see FatalException
     */
    public FatalResponse(FatalException cause) {
        super(cause.getMessage(), true);
    }
}
