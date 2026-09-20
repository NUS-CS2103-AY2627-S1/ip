package loki.exception;

import loki.ui.Ui;

/**
 * Represents recoverable errors caused by invalid user input.
 *
 * <p>Each instance receives a randomly selected flavor message from
 * {@link Ui#getRandomExceptionMessage()}.</p>
 */
public class LokiExceptions extends Exception {
    private static final String UPDATE_USAGE = "Usage: update <task number> <field> [<field>...]\n"
            + "Fields: todo=/title; deadline=/title,/by; event=/title,/from,/to\n"
            + "Examples:\n"
            + "update 1 /title Buy groceries\n"
            + "update 2 /by 2019-06-06\n"
            + "update 3 /from 2019-08-06 1400 /to 2019-08-06 1600";

    /**
     * Creates an input error with a randomly selected Loki message.
     */
    public LokiExceptions() {
        super(Ui.getRandomExceptionMessage());
    }

    /**
     * Creates an input error with a random Loki message and a specific detail.
     *
     * @param message the detail describing the error
     */
    public LokiExceptions(String message) {
        super(Ui.getRandomExceptionMessage() + "\n" + message);
    }

    /**
     * Creates an error for an empty command.
     *
     * @return the empty-input error.
     */
    public static LokiExceptions emptyInput() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for an unrecognized command.
     *
     * @return the unknown-command error.
     */
    public static LokiExceptions unknownCommand() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for a missing or invalid task number.
     *
     * @return the invalid-task-number error.
     */
    public static LokiExceptions invalidTaskNumber() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for an incorrectly formatted to-do command.
     *
     * @return the invalid-to-do error.
     */
    public static LokiExceptions invalidToDo() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for an incorrectly formatted deadline command.
     *
     * @return the invalid-deadline error.
     */
    public static LokiExceptions invalidDeadline() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for an incorrectly formatted event command.
     *
     * @return the invalid-event error.
     */
    public static LokiExceptions invalidEvent() {
        return new LokiExceptions();
    }

    /**
     * Creates an error for an incorrectly formatted update command.
     *
     * @return the invalid-update error with its stable usage text.
     */
    public static LokiExceptions invalidUpdate() {
        return new LokiExceptions(UPDATE_USAGE);
    }

}
