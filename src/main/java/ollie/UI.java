package ollie;

import java.util.Scanner;

/**
 * Handles console input and output for Ollie.
 */
public class UI {
    static final String APP_NAME = "Ollie Mission Control";
    static final String SPEAKER_NAME = "OLLIE / CONTROL";

    private static final int HORIZONTAL_LINE_LENGTH = 60;
    private static final String HORIZONTAL_LINE = "-".repeat(HORIZONTAL_LINE_LENGTH);
    private static final String INDENTATION = "    ";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public UI() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Ollie's welcome message.
     */
    public void showWelcome() {
        System.out.println("OLLIE // MISSION CONTROL");
        System.out.println("Systems online. Let's put your day into orbit.");
        System.out.println("Type help to open the flight manual.");
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return Next user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a response from Ollie.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        String indentedResponse = INDENTATION
                + response.replace(System.lineSeparator(), System.lineSeparator() + INDENTATION);
        System.out.println(indentedResponse);
    }

    /**
     * Returns Ollie's goodbye message.
     *
     * @return Goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Mission complete. Safe travels—Ollie signing off.";
    }

    /**
     * Returns the supported command formats.
     *
     * @return Help message.
     */
    public String getHelpMessage() {
        return joinLines(
                "Flight manual — available commands:",
                "• help",
                "• list",
                "• find <keyword>",
                "• todo <description>",
                "• deadline <description>",
                "    /by <yyyy-MM-dd>",
                "• event <description>",
                "    /from <yyyy-MM-dd>",
                "    /to <yyyy-MM-dd>",
                "• mark <task number>",
                "• unmark <task number>",
                "• delete <task number>",
                "• bye");
    }

    private String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    /**
     * Returns all tasks with one-based numbering.
     *
     * @param tasks Tasks to include.
     * @return Task list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        return getNumberedTasksMessage("Current mission board:", tasks);
    }

    /**
     * Returns tasks that match a search keyword with one-based numbering.
     *
     * @param tasks Matching tasks to include.
     * @return Matching tasks message.
     */
    public String getMatchingTasksMessage(TaskList tasks) {
        return getNumberedTasksMessage("Tasks detected on radar:", tasks);
    }

    /**
     * Builds a message containing a heading followed by numbered tasks.
     *
     * @param heading Heading shown before the tasks.
     * @param tasks Tasks to include.
     * @return Numbered task message.
     */
    private String getNumberedTasksMessage(String heading, TaskList tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns a successful mark message.
     *
     * @return Mark confirmation.
     */
    public String getTaskMarkedMessage() {
        return "Telemetry confirmed — mission complete.";
    }

    /**
     * Returns a successful unmark message.
     *
     * @return Unmark confirmation.
     */
    public String getTaskUnmarkedMessage() {
        return "Mission reopened and returned to the flight plan.";
    }

    /**
     * Returns a successful deletion message.
     *
     * @return Deletion confirmation.
     */
    public String getTaskDeletedMessage() {
        return "Mission scrubbed from the flight plan.";
    }

    /**
     * Returns a successful task addition message.
     *
     * @return Addition confirmation.
     */
    public String getTaskAddedMessage() {
        return "Mission logged and ready for launch.";
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showResponse(getErrorMessage(message));
    }

    /**
     * Returns a user-facing error message.
     *
     * @param message Explanation of the error.
     * @return Formatted error message.
     */
    public String getErrorMessage(String message) {
        return "Course correction needed — " + message;
    }

    /**
     * Displays the horizontal output divider.
     */
    public void showDivider() {
        System.out.println(HORIZONTAL_LINE);
    }
}
