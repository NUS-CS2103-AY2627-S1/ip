package com.fireinyu.themyth.responses;

/**
 * Response to a Request to exit the app.
 * Exits the app after execution.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 * @see com.fireinyu.themyth.TheMyth
 */
public class ExitResponse extends Response {

    /**
     * Initializes an ExitResponse indicating an action that will terminate the app.
     *
     * @param body exit message.
     * @see String
     */
    public ExitResponse(String body) {
        super(body, true);
    }
}
