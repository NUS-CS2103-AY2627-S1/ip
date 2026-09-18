package com.fireinyu.themyth.responses;

/**
 * Response to a Request from the user or an event. Produced by the active ChatMode.
 * Also includes instructions for whether to continue executing the app.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 * @see com.fireinyu.themyth.TheMyth
 */
public class Response {
    /**
     * Represents the emotional state or mood associated with a response.
     */
    public enum Mood {
        /** Neutral or standard mood. */
        NORMAL,
        /** Delighted or celebratory mood. */
        HAPPY,
        /** Annoyed or irritated mood. */
        ANGRY
    }

    private String body;
    private boolean exit;
    private Mood mood;

    /**
     * Initializes a Response with a message body.
     * The app will continue to execute user cycle after the Response is executed
     *
     * @param body message body.
     * @see String
     */
    public Response(String body) {
        this(body, false, Mood.NORMAL);
    }

    /**
     * Initializes a Response with a message body and mood.
     * The app will continue to execute user cycle after the Response is executed
     *
     * @param body message body.
     * @param mood emotional mood of the response.
     */
    public Response(String body, Mood mood) {
        this(body, false, mood);
    }

    /**
     * Initializes a Response with a message body.
     * Depending on <i>exit</i>, the app will either terminate with a CloseRequest or
     * continue to execute user cycle after the Response is executed
     *
     * @param body message body.
     * @param exit whether the app should terminate.
     * @see String
     * @see com.fireinyu.themyth.requests.events.CloseRequest
     */
    public Response(String body, boolean exit) {
        this(body, exit, Mood.NORMAL);
    }

    /**
     * Initializes a Response with a message body, exit flag, and mood.
     *
     * @param body message body.
     * @param exit whether the app should terminate.
     * @param mood emotional mood of the response.
     */
    public Response(String body, boolean exit, Mood mood) {
        this.body = body;
        this.exit = exit;
        this.mood = mood;
    }

    /**
     * Returns the message body of this Response.
     *
     * @return message body of this Response.
     * @see String
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns true if the active ChatMode should exit after handling this Response.
     *
     * @return true if the active ChatMode  should exit after handling this Response.
     * @see com.fireinyu.themyth.chatmodes.ChatMode
     */
    public boolean doExit() {
        return exit;
    }

    /**
     * Returns the emotional mood associated with this response.
     *
     * @return the {@link Mood} of this response.
     */
    public Mood getMood() {
        return mood;
    }
}
