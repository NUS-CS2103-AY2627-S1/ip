package bany.commands;

/** Identifies the final state of command execution. */
public enum CommandOutcome {
    /** Command completed successfully. */
    SUCCESS,
    /** Command failed validation or execution. */
    ERROR,
    /** Command requests that the application close. */
    EXIT
}
