package edith.ui;

import java.util.Scanner;
import java.util.function.Consumer;

import edith.exception.EdithException;
import edith.task.Task;
import edith.task.TaskList;

/** Handles Edith's command input and formatted output. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Consumer<String> output;
    private final Scanner scanner;

    /** Creates a console interface that reads commands from standard input. */
    public Ui() {
        this(new Scanner(System.in), System.out::println);
    }

    /**
     * Creates an interface that sends each output line to the supplied destination.
     *
     * @param output destination that receives each line of chatbot output
     */
    public Ui(Consumer<String> output) {
        this(new Scanner(System.in), output);
    }

    private Ui(Scanner scanner, Consumer<String> output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return {@code true} if standard input has another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next complete command entered by the user.
     *
     * @return the next line from standard input
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Edith's welcome message. */
    public void showWelcome() {
        String banner = """
                 _____ ____ ___ _____ _   _
                | ____|  _ \\_ _|_   _| | | |
                |  _| | | | | |  | | | |_| |
                | |___| |_| | |  | | |  _  |
                |_____|____/___| |_| |_| |_|
                """;
        showLines(banner, "Hello! I'm EDITH.", "Type 'help' to list all commands.");
        showDivider();
    }

    /** Displays every command and its input format as a numbered list. */
    public void showHelp() {
        showLines(
                "Here are the commands you can use:",
                "1. help",
                "2. todo DESCRIPTION [/tags #TAG ...]",
                "3. deadline DESCRIPTION /by yyyy-MM-dd [HHmm] [/tags #TAG ...]",
                "4. event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm] [/tags #TAG ...]",
                "5. list",
                "6. find KEYWORD",
                "7. mark NUMBER",
                "8. unmark NUMBER",
                "9. delete NUMBER",
                "10. tag NUMBER #TAG [#TAG ...]",
                "11. untag NUMBER #TAG [#TAG ...]",
                "12. bye",
                "",
                "Notes:",
                "Replace UPPER_CASE words with your own text.",
                "Square brackets mark optional input; omit the brackets.",
                "NUMBER is the task's position in the full list, starting at 1.");
    }

    /** Displays the divider between command interactions. */
    public void showDivider() {
        output.accept(DIVIDER);
    }

    /** Displays the farewell message. */
    public void showGoodbye() {
        output.accept("Bye. Hope to see you again soon!");
    }

    /**
     * Displays an application error to the user.
     *
     * @param exception the error containing the message to display
     */
    public void showError(EdithException exception) {
        output.accept("OOPS!!! " + exception.getMessage());
    }

    /** Explains that saved tasks could not be restored and will not be overwritten. */
    public void showLoadingError() {
        output.accept("OOPS!!! I could not load your saved tasks. Fix the data file before using Edith.");
    }

    /**
     * Displays every task currently stored in the task list.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            output.accept("No tasks yet! Use todo, deadline, or event to create a new tasks");
            return;
        }
        output.accept("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Displays the tasks that match a find command. */
    public void showMatchingTasks(TaskList tasks) {
        output.accept("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Displays the supplied tasks as a one-based numbered list. */
    private void showNumberedTasks(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays each supplied line in order. */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.accept(line);
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task's completion state changed.
     *
     * @param task the task whose completion state changed
     * @param isDone whether the task is now completed
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String confirmation = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showLines(confirmation, "  " + task);
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task the task that was removed
     * @param taskCount the number of tasks after the removal
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays a tagged task or explains that every requested tag was already present. */
    public void showTagsAdded(Task task, boolean hasChanged) {
        String confirmation = hasChanged
                ? "Got it. I've added tags to this task:"
                : "No tags were added; this task already has them:";
        showLines(confirmation, "  " + task);
    }

    /** Displays a task after tag removal or explains that none of the requested tags was present. */
    public void showTagsRemoved(Task task, boolean hasChanged) {
        String confirmation = hasChanged
                ? "Noted. I've removed tags from this task:"
                : "No tags were removed; this task does not have them:";
        showLines(confirmation, "  " + task);
    }
}
