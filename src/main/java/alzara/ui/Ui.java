package alzara.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import alzara.task.Task;

/**
 * Handles all interaction with the user: reading raw input and producing
 * Alzara's responses. Keeping this in one place means the rest of the
 * program never calls {@code System.out}/{@code Scanner} directly.
 *
 * <p>Runs in one of two modes, chosen at construction. In console mode,
 * every response is printed straight to {@code System.out}, one line per
 * {@code println} call. In GUI mode, responses are instead accumulated in a
 * buffer for {@link #getAndClearResponse()} to return as a single string,
 * and the console-only banner/divider lines are skipped, since a chat
 * bubble doesn't need them.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final Scanner scanner;
    private final boolean isGuiMode;
    private final StringBuilder responseBuffer = new StringBuilder();
    private boolean isLastResponseError = false;

    /**
     * Creates a {@link Ui} in console mode.
     */
    public Ui() {
        this(false);
    }

    /**
     * Creates a {@link Ui} in either console or GUI mode.
     *
     * @param isGuiMode if true, responses are buffered for
     *         {@link #getAndClearResponse()} instead of printed, and
     *         console-only decorations are skipped
     */
    public Ui(boolean isGuiMode) {
        this.scanner = new Scanner(System.in);
        this.isGuiMode = isGuiMode;
    }

    /**
     * Reads the next full line of input typed by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns every response accumulated since the last call, then clears
     * the buffer. Only meaningful in GUI mode - console mode never
     * populates it.
     */
    public String getAndClearResponse() {
        String response = responseBuffer.toString().strip();
        responseBuffer.setLength(0);
        return response;
    }

    /**
     * Prints the horizontal divider used to separate one interaction from
     * the next. A no-op in GUI mode.
     */
    public void showLine() {
        if (isGuiMode) {
            return;
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Shows the startup banner (console only) and welcome message.
     */
    public void showWelcome() {
        if (!isGuiMode) {
            String banner = "    _    _     ______    _    ____       _    \n"
                    + "   / \\  | |   |__  /   / \\  |  _ \\     / \\   \n"
                    + "  / _ \\ | |     / /   / _ \\ | |_) |   / _ \\  \n"
                    + " / ___ \\| |___ / /_  / ___ \\|  _ <   / ___ \\ \n"
                    + "/_/   \\_\\_____/____|/_/   \\_\\_| \\_\\ /_/   \\_\\\n";
            showLine();
            System.out.print(banner);
        }
        print("And as it was foretold,\n"
                + "You find yourself face to face with the great Alzara.\n"
                + "What can I do for you?");
    }

    /**
     * Reports that the program is exiting.
     */
    public void showGoodbye() {
        print("Our audience has ended. Until we meet again.");
    }

    /**
     * Reports that a task was marked done, showing the task itself.
     *
     * @param task the task that was marked done
     */
    public void showTaskMarked(Task task) {
        print("You have satisfied the great Alzara.\n" + task);
    }

    /**
     * Reports that a task was marked not done, showing the task itself.
     *
     * @param task the task that was marked not done
     */
    public void showTaskUnmarked(Task task) {
        print("As I predicted...\n" + task);
    }

    /**
     * Reports that a new task was added, e.g. after a {@code todo}, {@code deadline}
     * or {@code event} command. {@code flavourText} is the command-specific line
     * shown above the task (e.g. "Do not miss the deadline.").
     */
    public void showTaskAdded(String flavourText, Task task, int taskCount) {
        print(flavourText + "\n" + task + "\nYou have " + taskCount + " tasks.");
    }

    /**
     * Reports that a task was removed, showing the removed task and how many
     * tasks remain.
     *
     * @param task the task that was removed
     * @param remainingCount the number of tasks left after the removal
     */
    public void showTaskDeleted(Task task, int remainingCount) {
        print("I have removed the task " + task + "\n" + remainingCount + " tasks remain.");
    }

    /**
     * Prints every task in {@code tasks}, numbered from 1, for the {@code list} command.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(ArrayList<Task> tasks) {
        print(formatTaskList("Behold, all that awaits you:", tasks));
    }

    /**
     * Prints an error message, e.g. an {@link alzara.AlzaraException}'s
     * {@code getMessage()}.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        print(message);
        isLastResponseError = true;
    }

    /**
     * Returns whether the most recent message shown was an error, i.e. the
     * last call among {@code show*} methods was {@link #showError}. Reset to
     * {@code false} by every other {@code show*} method.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Reports a save/load status message from {@link alzara.storage.Storage},
     * e.g. that the save file couldn't be read, or that some entries were
     * skipped as corrupted while loading. Shown the same way as any other
     * reply - not flagged as an error, since these describe the save file's
     * state rather than something wrong with the user's command.
     *
     * @param message the status message to display
     */
    public void showStorageMessage(String message) {
        print(message);
    }

    /**
     * Prints every task in {@code matches}, numbered from 1, for the {@code find} command.
     *
     * @param matches the matching tasks to display
     */
    public void showMatchingTasks(ArrayList<Task> matches) {
        print(formatTaskList("The great Alzara finds these:", matches));
    }

    /**
     * Prints every task scheduled on {@code date}, numbered from 1 and
     * sorted chronologically, for the {@code view} command. Prints a
     * distinct message instead of a bare heading when nothing is scheduled.
     *
     * @param date the queried date
     * @param scheduledTasks the matching tasks, already sorted by date
     */
    public void showScheduledTasks(LocalDate date, ArrayList<Task> scheduledTasks) {
        String formattedDate = date.format(DISPLAY_DATE_FORMAT);
        if (scheduledTasks.isEmpty()) {
            print("I see nothing on " + formattedDate + ".");
            return;
        }
        print(formatTaskList("I see all on " + formattedDate + ":", scheduledTasks));
    }

    /**
     * Builds a {@code heading} followed by every task in {@code tasks}, each
     * numbered from 1 on its own line.
     */
    private String formatTaskList(String heading, ArrayList<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Prints {@code message} to the console (one {@code println} call per
     * line, followed by the divider) in console mode, or appends it to the
     * response buffer in GUI mode. Also clears {@code isLastResponseError},
     * since a message that reaches here didn't come from {@link #showError}.
     *
     * @param message the message to print or buffer
     */
    private void print(String message) {
        isLastResponseError = false;
        if (isGuiMode) {
            if (responseBuffer.length() > 0) {
                responseBuffer.append('\n');
            }
            responseBuffer.append(message);
            return;
        }
        for (String line : message.split("\n", -1)) {
            System.out.println(line);
        }
        showLine();
    }
}
