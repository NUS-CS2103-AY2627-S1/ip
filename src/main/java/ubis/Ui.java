package ubis;

import java.util.Arrays;

/**
 * Handles interactions with the user by displaying messages and banners to the console.
 */
public class Ui {
    private static final int LINE_LENGTH = 30;

    /**
     * Predefined system messages displayed to the user.
     */
    public enum Message {
        GOODBYE("Goodbye. See you soon!"),
        EMPTY_INPUT("Hi! You can type in a task name and I will keep track of it for you!\n"
                + "type \"help\" for information on commands."),
        ILLEGAL_INPUT("Sorry! Please ensure input does not contain any of the following characters: \n"
                + Arrays.toString(Parser.ILLEGAL_ARTIFACTS)),
        UNEXPECTED_ERROR("Sorry, Ubis encountered an unexpected error while processing your command. "
                + "Please try again; diagnostic details have been logged."),
        HELP("Here are Ubis's commands:\n"
                + "Replace values inside <angle brackets> with your own values.\n\n"
                + "CREATE TASKS\n"
                + "  todo <task name>\n"
                + "    Add a todo.\n"
                + "    Example: todo read a book\n\n"
                + "  deadline <task name> /by <YYYY-MM-DD>\n"
                + "    Add a task with a deadline.\n"
                + "    Example: deadline submit report /by 2026-09-30\n\n"
                + "  event <task name> /from <YYYY-MM-DD> /to <YYYY-MM-DD>\n"
                + "    Add an event with a start and end date.\n"
                + "    Example: event school camp /from 2026-10-01 /to 2026-10-03\n\n"
                + "VIEW AND MANAGE TASKS\n"
                + "  list\n"
                + "    Show all tasks.\n\n"
                + "  find <keyword>\n"
                + "    Show tasks whose names contain the keyword.\n\n"
                + "  mark <task number>\n"
                + "    Mark a task as done.\n\n"
                + "  unmark <task number>\n"
                + "    Mark a task as not done.\n\n"
                + "  delete <task number>\n"
                + "    Delete a task.\n\n"
                + "OTHER COMMANDS\n"
                + "  help\n"
                + "    Show this help message.\n\n"
                + "  bye\n"
                + "    Exit Ubis.");

        private final String message;

        Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /**
     * Returns the introductory welcome message for the chatbot.
     *
     * @return Welcome message string.
     */
    public static String getWelcomeMessage() {
        return "Hello! I am Ubis.\nWhat can I do for you?";
    }

    /**
     * Prints the welcome banner and introductory message to the console.
     */
    public static void welcome() {
        String banner = " _   _ ____ ___ ____  \n"
                + "| | | | __ )|_ _/ ___| \n"
                + "| | | |  _ \\ | |\\___ \\ \n"
                + "| |_| | |_) || | ___) |\n"
                + " \\___/|____/|___|____/ \n";

        printDashLine();
        System.out.println(banner);
        System.out.println(getWelcomeMessage());
        printDashLine();
    }

    /**
     * Prints a divider line of dashes to the console.
     */
    public static void printDashLine() {
        System.out.println("_".repeat(LINE_LENGTH));
    }

    /**
     * Prints a predefined message followed by a divider line.
     *
     * @param message Predefined Message enum value to display.
     */
    public static void printMessage(Message message) {
        System.out.println(message.getMessage());
        printDashLine();
    }

    /**
     * Prints a string message followed by a divider line.
     *
     * @param message Text message to be printed.
     */
    public static void printMessage(String message) {
        System.out.println(message);
        printDashLine();
    }
}
