package luckynoslacky;

/**
 * Describes the semantic tone used to present a chatbot response.
 */
public enum ResponseTone {
    /** A greeting, farewell, or general chatbot response. */
    NEUTRAL,
    /** A task-changing command completed successfully. */
    SUCCESS,
    /** The chatbot is presenting task information. */
    INFO,
    /** The user supplied invalid or incomplete input. */
    WARNING,
    /** The application could not load or save task data. */
    SYSTEM_ERROR
}
