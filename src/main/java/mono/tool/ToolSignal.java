package mono.tool;

/**
 * Describes what the application should do after a tool finishes.
 */
public enum ToolSignal {
    /** Continue accepting commands. */
    CONTINUE,

    /** Stop accepting commands and end the session. */
    EXIT
}
