package yuno.command;

/**
 * Represents whether Yuno should continue accepting commands after executing a command.
 */
public enum CommandResult {
    /** Indicates that Yuno should accept another command. */
    CONTINUE,
    /** Indicates that Yuno should end the current session. */
    EXIT
}
