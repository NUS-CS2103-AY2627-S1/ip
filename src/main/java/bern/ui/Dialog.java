package bern.ui;

import java.util.Scanner;

import bern.logic.TextResponse;

/**
 * Handles user-facing input prompts and output messages.
 */
public class Dialog {
    /**
     * Shared dialog instance, created on first access.
     */
    private static Dialog instance;

    /**
     * Message line printed after user-facing output.
     */
    private static final String MESSAGE_LINE = "____________________________________\n";
    private static final String MESSAGE_TASK_LOADED = "*Sizzle*. Loaded saved tasks. Use list to view them.";
    private static final String MESSAGE_GOODBYE = "Burning out...";

    /**
     * ASCII-art banner intended for chatbot identification.
     */
    private static final String CHATBOT_BANNER = """
             ____                 \s
            |  _ \\                \s
            | |_) | ___ _ __ _ __ \s
            |  _ < / _ \\ '__| '_ \\\s
            | |_) |  __/ |  | | | |
            |____/ \\___|_|  |_| |_|""";

    /**
     * Name displayed for the chatbot.
     */
    private static final String CHATBOT_NAME = "Bern Tokens";

    /**
     * Template used for the greeting.
     */
    private static final String TEMPLATE_GREETING = "*Sizzle* Hello! I'm %s. \n"
            + "Feed me tokens~";

    /**
     * Error message for an unknown command.
     */
    private static final String ERROR_KEYWORD_INVALID = "Command not recognised.\n"
            + "List of commands: bye, list, mark, unmark, todo, deadline, event, delete, find, schedule";

    private static final String ERROR_DIRECTORY = "Unable to create directory for save file. Environment is too wet.";
    private static final String ERROR_SAVE = "Unable to save task data. Was it burnt?";
    private static final String ERROR_LOAD = "Couldn't access a previous save for some reason. Burnt to cinders.";
    private static final String ERROR_LOAD_TASK = "Some tasks were unable to be retrieved. Burned.";

    private static final String ERROR_NO_TASKS = "There are no tasks. No tokens to burn :(";
    private static final String ERROR_TEMPLATE_INVALID_TASK_NUMBER = "Given task number must be from 1 to %d";
    private static final String ERROR_TEMPLATE_INCORRECT_KEYWORD_USAGE = "Incorrect usage of %s. Expected: %s";

    private static final String ERROR_TEMPLATE_INVALID_DATE_TIME = "%s is not a valid date or time";

    /**
     * Prevents callers from creating additional instances of the shared dialog.
     */
    private Dialog() {
    }

    /**
     * Creates the singleton instance of the Dialog class if it doesn't already exist, then returns it.
     *
     * @return An instance of the Dialog class.
     */
    public static Dialog getInstance() {
        if (instance == null) {
            instance = new Dialog();
        }

        return instance;
    }

    /**
     * Reads the next non-blank input line, strips leading and trailing whitespace, and prints a separator afterward.
     *
     * @param sc Scanner to receive input from.
     * @return The next non-blank input line with leading and trailing whitespace removed.
     * @throws java.util.NoSuchElementException If input ends before a non-blank line is read.
     * @throws IllegalStateException If the scanner is closed.
     */
    public String promptForInput(Scanner sc) {
        String input = "";

        while (input.isEmpty()) {
            input = sc.nextLine().strip();
        }

        System.out.print(MESSAGE_LINE);
        return input;
    }

    /**
     * Prints and returns the greeting presented on startup of the chatbot.
     *
     * @return The greeting message displayed to the user.
     */
    public TextResponse greetUser() {
        return printMessage(String.format(TEMPLATE_GREETING, CHATBOT_NAME));
    }

    /**
     * Prints and returns the message confirming that saved tasks were loaded on startup.
     *
     * @return The loaded-task message displayed to the user.
     */
    public TextResponse printTasksLoaded() {
        return printMessage(MESSAGE_TASK_LOADED);
    }

    /**
     * Prints and returns the farewell message used when the bot closes.
     *
     * @return The farewell message displayed to the user.
     */
    public TextResponse sayGoodbye() {
        return printMessage(MESSAGE_GOODBYE);
    }

    //TODO: reframe to keyword
    /**
     * Prints and returns a message showing correct use of a specified keyword.
     *
     * @param keyword A keyword to be used by the user.
     * @param expected The expected usage of this keyword.
     * @return The usage error message displayed to the user.
     */
    public TextResponse printIncorrectKeywordUsageError(Object keyword, Object expected) {
        return printMessage(String.format(ERROR_TEMPLATE_INCORRECT_KEYWORD_USAGE, keyword, expected));
    }

    /**
     * Prints and returns a message showing that there are no tasks.
     *
     * @return The no-tasks error message displayed to the user.
     */
    public TextResponse printNoTasksError() {
        return printMessage(ERROR_NO_TASKS);
    }

    /**
     * Prints and returns a message showing the range of valid task numbers.
     *
     * @param taskCount The number of valid tasks.
     * @return The invalid-task-number error message displayed to the user.
     */
    public TextResponse printInvalidTaskNumberError(int taskCount) {
        return printMessage(String.format(ERROR_TEMPLATE_INVALID_TASK_NUMBER, taskCount));
    }

    /**
     * Prints and returns a message showing the user's invalid date-time input.
     *
     * @param invalidDateTime The invalid date-time input given by the user.
     * @return The invalid-date-time error message displayed to the user.
     */
    public TextResponse printInvalidDateTimeError(String invalidDateTime) {
        return printMessage(String.format(ERROR_TEMPLATE_INVALID_DATE_TIME, invalidDateTime));
    }

    /**
     * Prints and returns an error for a directory that cannot be created.
     *
     * @return The directory-error message displayed to the user.
     */
    public TextResponse printDirectoryError() {
        return printMessage(ERROR_DIRECTORY);
    }

    /**
     * Prints and returns an error for task data that cannot be saved.
     *
     * @return The save-error message displayed to the user.
     */
    public TextResponse printSaveError() {
        return printMessage(ERROR_SAVE);
    }

    /**
     * Prints and returns an error for task data that cannot be loaded.
     *
     * @return The load-error message displayed to the user.
     */
    public TextResponse printLoadError() {
        return printMessage(ERROR_LOAD);
    }

    /**
     * Prints and returns an error for an individual task that cannot be loaded.
     *
     * @return The task-load-error message displayed to the user.
     */
    public TextResponse printLoadTaskError() {
        return printMessage(ERROR_LOAD_TASK);
    }

    /**
     * Prints and returns an error for an unknown command keyword.
     *
     * @return The invalid-keyword error message displayed to the user.
     */
    public TextResponse printKeywordInvalidError() {
        return printMessage(ERROR_KEYWORD_INVALID);
    }

    /**
     * Prints a message and separator to standard output and wraps the message in a text response.
     *
     * @param msg The message to be printed.
     * @return A response containing the message without the separator.
     */
    public TextResponse printMessage(String msg) {
        System.out.print(msg + "\n" + MESSAGE_LINE);
        return new TextResponse(msg);
    }
}
