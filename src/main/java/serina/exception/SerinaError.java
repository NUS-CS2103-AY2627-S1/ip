package serina.exception;

import serina.SerinaDialogue;

/**
 * Represents Serina-specific errors and their user-facing behavior.
 */
public enum SerinaError {
    /** Indicates that no more tasks can be added. */
    MAX_TASKS(SerinaDialogue.address("the 100-task limit has been reached. Delete a task before adding another."),
            false),
    /** Indicates that the entered command is not supported. */
    UNKNOWN_COMMAND(
            SerinaDialogue.address("that order is unclear. Type help for available commands."), false),
    /** Indicates that a command contains arguments it does not accept. */
    UNEXPECTED_ARGUMENTS(SerinaDialogue.address("that command does not accept arguments. Type help for its format."),
            false),
    /** Indicates that the user submitted no command. */
    EMPTY_COMMAND(SerinaDialogue.address("enter a command, or type help for available commands."), false),
    /** Indicates that a command contains characters that cannot be stored safely. */
    INVALID_CHARACTERS(SerinaDialogue.address("task text cannot contain line breaks or control characters."), false),
    /** Indicates that a command parameter was supplied more than once. */
    DUPLICATE_PARAMETER(SerinaDialogue.address("specify each date parameter exactly once."), false),
    /** Indicates that an identical task already exists. */
    DUPLICATE_TASK(SerinaDialogue.address("that task is already on the roster."), false),
    /** Indicates that a task number is missing, malformed, or outside the list. */
    INVALID_TASK_NUMBER(SerinaDialogue.address("provide a valid task number. Use list to review the roster."),
            false),
    /** Indicates that a todo description is empty. */
    EMPTY_TODO(SerinaDialogue.address("a todo needs a description. Use: todo <task>"), false),
    /** Indicates that a deadline description is empty. */
    EMPTY_DEADLINE_DESCRIPTION(
            SerinaDialogue.address("a deadline needs a description. Use: deadline <task> /by <yyyy-MM-dd>"),
            false),
    /** Indicates that a deadline date is empty. */
    EMPTY_DEADLINE_BY(
            SerinaDialogue.address("a deadline needs a /by date. Use: deadline <task> /by <yyyy-MM-dd>"), false),
    /** Indicates that an event description is empty. */
    EMPTY_EVENT_DESCRIPTION(
            SerinaDialogue.address(
                    "an event needs a description. Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"),
            false),
    /** Indicates that an event start date is empty. */
    EMPTY_EVENT_FROM(
            SerinaDialogue.address(
                    "an event needs a /from date. Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"),
            false),
    /** Indicates that an event end date is empty. */
    EMPTY_EVENT_TO(
            SerinaDialogue.address(
                    "an event needs a /to date. Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"),
            false),
    /** Indicates that a find command has no keyword. */
    EMPTY_FIND_KEYWORD(SerinaDialogue.address("find needs a keyword. Use: find <keywords>"), false),
    /** Indicates that a deadline command has an invalid structure. */
    INVALID_DEADLINE_FORMAT(
            SerinaDialogue.address("use: deadline <task> /by <yyyy-MM-dd>"), false),
    /** Indicates that an event command has an invalid structure. */
    INVALID_EVENT_FORMAT(
            SerinaDialogue.address("use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"), false),
    /** Indicates that a date is invalid or has the wrong format. */
    INVALID_DATE(SerinaDialogue.address("use yyyy-MM-dd—for example, 2026-09-14."), false),
    /** Indicates that an event ends before it starts. */
    INVALID_EVENT_DATE_RANGE(SerinaDialogue.address("the event end date must be after its start date."),
            false),
    /** Indicates that a save file contains more tasks than Serina supports. */
    LOAD_TOO_MANY_TASKS(SerinaDialogue.address("the save file contains more than the supported 100 tasks."),
            false),
    /** Indicates that saved tasks could not be loaded. */
    LOAD_FAILED(SerinaDialogue.address("I couldn't load your saved tasks."), false),
    /** Indicates that task data is unavailable after a load failure. */
    STORAGE_UNAVAILABLE(SerinaDialogue.address(
            "saved tasks are unavailable. Repair the data file or its permissions, then restart Serina."), false),
    /** Indicates that tasks could not be saved. */
    SAVE_FAILED(SerinaDialogue.address(
            "I couldn't save your tasks, so the command was not applied. Check that the data folder is writable."),
            false);

    private final String message;
    private final boolean shouldExit;

    /**
     * Creates an error with its display message and exit behavior.
     *
     * @param message Explanation to show to the user.
     * @param shouldExit Whether Serina should exit after showing the message.
     */
    SerinaError(String message, boolean shouldExit) {
        this.message = message;
        this.shouldExit = shouldExit;
    }

    /**
     * Returns the message shown to the user.
     *
     * @return The user-facing error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether Serina should exit after this error.
     *
     * @return {@code true} if this error should end the application.
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
