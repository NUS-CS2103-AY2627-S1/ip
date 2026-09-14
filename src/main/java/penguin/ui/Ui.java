package penguin.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import penguin.PenguinIdentity;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.TaskSearchResult;

/**
 * Handles console interaction for Penguin.
 */
public class Ui {
    /** Separator printed between chatbot messages. */
    private static final String DIVIDER = "----------------------------------------------------------";

    /** ASCII-art banner displayed when the chatbot starts. */
    private static final String BANNER = " ____  _____ _   _  ____ _   _ ___ _   _ \n"
            + "|  _ \\| ____| \\ | |/ ___| | | |_ _| \\ | |\n"
            + "| |_) |  _| |  \\| | |  _| | | || ||  \\| |\n"
            + "|  __/| |___| |\\  | |_| | |_| || || |\\  |\n"
            + "|_|   |_____|_| \\_|\\____|\\___/|___|_| \\_|";

    /** Farewell displayed when the chatbot exits. */
    private static final String GOODBYE_MESSAGE =
            "Bye. Hope to see you again soon!\n"
                    + "Stay cool and keep making progress!";

    /** Scanner used to read user commands. */
    private final Scanner scanner;

    /** Stores the latest response for non-console clients such as the GUI. */
    private final StringBuilder response = new StringBuilder();

    /** Whether responses should also be printed to standard output. */
    private final boolean shouldPrintToConsole;

    /**
     * Creates a user interface using standard input.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a user interface with configurable console output.
     *
     * @param isConsoleOutputEnabled whether responses should be printed.
     */
    public Ui(boolean isConsoleOutputEnabled) {
        scanner = new Scanner(System.in);
        shouldPrintToConsole = isConsoleOutputEnabled;
    }

    /**
     * Displays the welcome message.
     */
    public void showWelcome() {
        System.out.printf("%s%n", DIVIDER);
        System.out.printf("%s%n", BANNER);
        System.out.printf("%s%n", DIVIDER);
        System.out.printf("%s%n", PenguinIdentity.GREETING_MESSAGE);
        System.out.printf("%s%n", DIVIDER);
    }

    /**
     * Reads one command from the user.
     *
     * @return trimmed user command, or {@code null} at end of input.
     */
    public String readCommand() {
        System.out.printf("%s", "You: ");
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /**
     * Displays the message divider.
     */
    public void showDivider() {
        System.out.printf("%s%n", DIVIDER);
    }

    /**
     * Displays a normal chatbot message.
     *
     * @param message message to display.
     */
    public void showMessage(String message) {
        record(PenguinIdentity.CHATBOT_NAME + ": " + message);
    }

    /**
     * Displays an error message.
     *
     * @param message error message to display.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        record(GOODBYE_MESSAGE);
    }

    /**
     * Stores a response and optionally prints it to the console.
     *
     * @param message response to store and display.
     */
    private void record(String message) {
        response.append(message).append(System.lineSeparator());
        if (shouldPrintToConsole) {
            System.out.printf("%s%n", message);
        }
    }

    /**
     * Clears the response buffer.
     */
    public void clearResponse() {
        response.setLength(0);
    }

    /**
     * Gets the latest buffered response.
     *
     * @return latest response without trailing whitespace.
     */
    public String getResponse() {
        return response.toString().trim();
    }

    /**
     * Displays all tasks or the empty-list message.
     *
     * @param taskList task list to display.
     */
    public void showTasks(TaskList taskList) {
        if (taskList.isEmpty()) {
            showMessage("Your task list is empty! Your iceberg is clear.");
            return;
        }

        showMessage("Here are your tasks! Fresh from the iceberg:");
        showTaskLines(taskList.getTasks());
    }

    /**
     * Displays tasks occurring on a date.
     *
     * @param date date being displayed.
     * @param results matching tasks with their original task numbers.
     */
    public void showTasksOnDate(LocalDate date, List<TaskSearchResult> results) {
        if (results.isEmpty()) {
            showMessage(String.format(
                    "No deadlines or events occur on %s. The waters are calm!",
                    formatDate(date)));
            return;
        }

        showMessage(String.format("Here are your tasks on %s! Let's dive in.",
                formatDate(date)));
        showSearchResultLines(results);
    }

    /**
     * Formats a date using the display format shown to users.
     *
     * @param date date to format.
     * @return date formatted for display.
     */
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH));
    }

    /**
     * Displays tasks matching a keyword or a no-match message.
     *
     * @param keyword keyword or phrase used for the search.
     * @param results matching tasks with their original task numbers.
     */
    public void showMatchingTasks(String keyword, List<TaskSearchResult> results) {
        if (results.isEmpty()) {
            showMessage(String.format(
                    "No tasks found when searching for %s. I checked every snowdrift!",
                    keyword));
            return;
        }

        showMessage(String.format(
                "Here are the matching tasks containing %s in your list. Ice work!",
                keyword));
        showSearchResultLines(results);
    }

    /**
     * Displays numbered task lines.
     *
     * @param tasks tasks to display.
     */
    private void showTaskLines(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            record((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays matching tasks using their numbers from the complete task list.
     *
     * @param results matching tasks and their original task numbers.
     */
    private void showSearchResultLines(List<TaskSearchResult> results) {
        for (TaskSearchResult result : results) {
            record(result.getTaskNumber() + ". " + result.getTask());
        }
    }
}
