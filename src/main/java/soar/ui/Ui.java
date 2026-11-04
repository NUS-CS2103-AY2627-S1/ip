package soar.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import soar.task.Task;

/**
 * Handles console input and presents Soar's messages to the user.
 */
public class Ui {
    /** Width of the line used to frame the chatbot's messages. */
    private static final int SEPARATOR_WIDTH = 60;

    /** Console input used to read commands. */
    private final Scanner scanner;

    /** Destination that receives each displayed line. */
    private final Consumer<String> output;

    /** Whether responses should be surrounded by console separators. */
    private final boolean isFramed;

    /**
     * Creates a user interface connected to standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out::println, true);
    }

    /** Creates a user interface with the specified input, output, and framing behavior. */
    private Ui(Scanner scanner, Consumer<String> output, boolean isFramed) {
        this.scanner = Objects.requireNonNull(scanner, "Input scanner must not be null");
        this.output = Objects.requireNonNull(output, "Output destination must not be null");
        this.isFramed = isFramed;
    }

    /**
     * Creates a user interface that sends unframed response lines to a consumer.
     *
     * @param output Destination that receives each response line.
     * @return user interface suitable for collecting a response
     */
    public static Ui createResponseUi(Consumer<String> output) {
        return new Ui(new Scanner(""), output, false);
    }

    /**
     * Shows the application banner and greeting.
     */
    public void showWelcome() {
        String banner = " ____                    \n"
                + "/ ___|  ___   __ _ _ __  \n"
                + "\\___ \\ / _ \\ / _` | '__| \n"
                + " ___) | (_) | (_| | |    \n"
                + "|____/ \\___/ \\__,_|_|    ";
        output.accept(separator());
        output.accept(banner);
        output.accept("Hey there! I'm Soar, your upbeat little sidekick!");
        output.accept("What exciting thing can I help you tackle today?");
        output.accept(separator());
    }

    /**
     * Reports whether another complete command is available from the user.
     *
     * @return {@code true} when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the user's next command.
     *
     * @return complete command line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the farewell response.
     */
    public void showGoodbye() {
        showFramed("Bye! Always soar towards your goals!");
    }

    /**
     * Shows an error raised while handling a command.
     *
     * @param message Error message to show.
     */
    public void showError(String message) {
        showFramed(message);
    }

    /**
     * Explains why startup cannot continue after loading task data.
     *
     * @param details Cause reported by storage or path validation.
     */
    public void showLoadingError(String details) {
        output.accept("I couldn't load the task data safely: " + details);
        output.accept("Please repair or move the data file, then restart Soar.");
        if (isFramed) {
            output.accept(separator());
        }
    }

    /**
     * Shows every task with its one-based list number.
     *
     * @param tasks Tasks to show in list order.
     */
    public void showTaskList(List<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Shows tasks whose descriptions match a find command.
     *
     * @param tasks Tasks that match the find result
     */
    public void showMatchingTasks(List<Task> tasks) {
        showNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Shows the confirmation for a newly added task.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showFramed(List.of(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list."));
    }

    /**
     * Shows the confirmation for a changed task completion state.
     *
     * @param task Task whose state changed.
     * @param isDone Whether the task is now complete.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showFramed(List.of(message, "  " + task));
    }

    /**
     * Shows the confirmation for a removed task.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks after the removal.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showFramed(List.of(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list."));
    }

    /**
     * Shows the task before and after a successful edit.
     *
     * @param originalTask Task before the edit.
     * @param updatedTask Task after the edit.
     */
    public void showTaskEdited(Task originalTask, Task updatedTask) {
        showFramed(List.of(
                "I've updated this task:",
                "  Before: " + originalTask,
                "  After:  " + updatedTask));
    }

    /**
     * Explains that valid edit values already match the selected task.
     *
     * @param task Unchanged task.
     */
    public void showTaskUnchanged(Task task) {
        showFramed(List.of(
                "That task already has those details:",
                "  " + task));
    }

    /**
     * Shows tasks matching a requested date, or explains that there are none.
     *
     * @param displayedDate Requested date formatted for display.
     * @param matches Matching task descriptions with their list numbers.
     */
    public void showTasksOnDate(String displayedDate, List<String> matches) {
        if (matches.isEmpty()) {
            showFramed("There are no deadlines or events on " + displayedDate + ".");
            return;
        }
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Here are the deadlines and events on " + displayedDate + ":");
        lines.addAll(matches);
        showFramed(lines);
    }

    /** Shows a heading followed by tasks numbered from one. */
    private void showNumberedTasks(String heading, List<Task> tasks) {
        List<String> lines = Stream.concat(
                Stream.of(heading),
                IntStream.range(0, tasks.size())
                        .mapToObj(index -> (index + 1) + "." + tasks.get(index)))
                .toList();
        showFramed(lines);
    }

    /** Shows one response surrounded by the standard separator. */
    private void showFramed(String line) {
        showFramed(List.of(line));
    }

    /** Shows response lines surrounded by the standard separator. */
    private void showFramed(List<String> lines) {
        if (isFramed) {
            output.accept(separator());
        }
        lines.forEach(output);
        if (isFramed) {
            output.accept(separator());
        }
    }

    /** Returns the line used to frame output without storing duplicate text. */
    private String separator() {
        return "_".repeat(SEPARATOR_WIDTH);
    }
}
