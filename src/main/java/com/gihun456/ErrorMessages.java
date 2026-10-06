package com.gihun456;

/**
 * Stores error messages used throughout the application.
 */
public final class ErrorMessages {
    // Gihun456
    public static final String TODO_DESCRIPTION_EMPTY = "Player 456, this game needs a description.";
    public static final String UNSUPPORTED_OPERATION = "That game isn't available.";
    public static final String UNEXPECTED_ERROR = "Something went wrong. Let's try that again, Player 456.";

    // Ui
    public static final String ERROR_PREFIX = "ERROR: ";

    // Parser
    public static final String EMPTY_COMMAND = "Red light, you need to enter a command, Player 456.";
    public static final String DEADLINE_DESCRIPTION_EMPTY = "Player 456, this game needs a description.";
    public static final String DEADLINE_BY_MISSING = "This game needs a '/by'.";
    public static final String DEADLINE_DUE_DATE_EMPTY = "The due date of this game cannot be empty.";
    public static final String EVENT_DESCRIPTION_EMPTY = "Player 456, this game needs a description.";
    public static final String EVENT_FROM_TO_MISSING = "This game needs both '/from' and '/to'.";
    public static final String EVENT_FROM_BEFORE_TO = "This game must specify '/from' before '/to'.";
    public static final String EVENT_START_DATE_EMPTY = "This game needs a starting date.";
    public static final String EVENT_END_DATE_EMPTY = "This game needs an ending date.";
    public static final String EVENT_END_BEFORE_START =
            "That doesn't work, Player 456. The game can't end before it starts.";
    public static final String REMINDERS_ARGUMENTS = "This game does not accept arguments.";

    // TaskList
    public static final String INVALID_TASK_NUMBER = "This isn't a valid game number.";
    public static final String EMPTY_KEYWORD = "The keyword cannot be empty.";

    // Deadline
    public static final String DEADLINE_DATE_EMPTY = "The due date of this game cannot be empty.";
    public static final String INVALID_DEADLINE_FORMAT =
            "Careful, Player 456. This game requires the following date format: "
        + "dd/MM/yyyy or yyyy-MM-dd, optionally followed by HHmm.";

    // Event
    public static final String EVENT_DATE_EMPTY = "The date of this game cannot be empty.";
    public static final String INVALID_EVENT_DATE_FORMAT =
            "Careful, Player 456. This game requires the following date format: "
        + "dd/MM/yyyy or yyyy-MM-dd, optionally followed by HHmm.";
    public static final String INVALID_CONFIRMATION =
            "It's your turn, Player 456. Please answer 'yes' or 'no'.";

    // Storage
    public static final String CANNOT_ACCESS_FILE = "Unable to access games list.";
    public static final String CANNOT_PARSE_TASK = "Unable to parse game.";

    private ErrorMessages() {
    }

    // Operation
    /**
     * Creates an error message for an unsupported command.
     *
     * @param input Command text that was not recognised.
     * @return Error message containing the invalid command.
     */
    public static String invalidOperation(String input) {
        return "Player 456, that game isn't available: " + input;
    }
}
