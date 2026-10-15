package et.ui;

import et.task.Task;
import et.task.TaskList;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles ET's console input and output.
 *
 * <p>Keeping presentation code here lets the application logic describe what
 * happened without also needing to know how each message is formatted.</p>
 */
public class Ui {
    /** Separates consecutive console messages. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands entered through the console without closing standard input. */
    private final Scanner scanner;

    /** Receives each complete message produced by ET. */
    private final Consumer<String> output;

    /** Creates a UI that reads from and writes to the console. */
    public Ui() {
        this(new Scanner(System.in), System.out::println);
    }

    /**
     * Creates an output-only UI for a graphical or test client.
     *
     * @param output the destination for each complete ET message
     */
    public Ui(Consumer<String> output) {
        this(null, output);
    }

    /** Creates a UI with the supplied input and output channels. */
    private Ui(Scanner scanner, Consumer<String> output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Returns whether another complete command can be read from the console.
     *
     * @return {@code true} when another command is available
     */
    public boolean hasNextCommand() {
        assert scanner != null : "Console input must be available";
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return the next command without leading or trailing whitespace
     */
    public String readCommand() {
        assert scanner != null : "Console input must be available";
        return scanner.nextLine().trim();
    }

    /** Displays ET's welcome message. */
    public void showWelcome() {
        String banner = " _____ _____\n"
                + "| ____|_   _|\n"
                + "|  _|   | |\n"
                + "| |___  | |\n"
                + "|_____| |_|";
        output.accept(DIVIDER + "\n" + banner + "\n" + getWelcomeMessage() + "\n" + DIVIDER);
    }

    /**
     * Returns the greeting shown at the start of a conversation.
     *
     * @return ET's greeting without console decoration
     */
    public String getWelcomeMessage() {
        return "Oh! Hello, Earth friend. I'm ET, your slightly homesick task helper.\n"
                + "I collect tasks, deadlines, and curious little plans while I wait for my ride home.\n"
                + "What shall we remember together?";
    }

    /** Displays a divider between command interactions. */
    public void showDivider() {
        output.accept(DIVIDER);
    }

    /** Displays ET's goodbye message. */
    public void showGoodbye() {
        output.accept("     Bye for now, Earth friend. Keep looking up!");
    }

    /** Displays the tasks currently in the task list. */
    public void showTaskList(TaskList tasks) {
        showTasks("     I found these in our little Earth mission:", tasks.getTasks());
    }

    /**
     * Displays a chronological view of the task list using each task's canonical number.
     *
     * @param tasks the task list to display without rearranging
     */
    public void showSortedTaskList(TaskList tasks) {
        StringBuilder message = new StringBuilder("     I lined up your tasks by Earth time:");
        for (int taskNumber : tasks.getChronologicallySortedTaskNumbers()) {
            message.append("\n     ").append(taskNumber).append('.').append(tasks.getTask(taskNumber - 1));
        }
        output.accept(message.toString());
    }

    /**
     * Displays the tasks whose descriptions matched a search keyword.
     *
     * @param matchingTasks the tasks to display in matching order
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showTasks("     My scanner found these matching tasks:", matchingTasks);
    }

    /**
     * Displays a heading followed by a numbered sequence of tasks.
     *
     * @param heading the message shown before the tasks
     * @param tasks the tasks to display in order
     */
    private void showTasks(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n     ").append(i + 1).append('.').append(tasks.get(i));
        }
        output.accept(message.toString());
    }

    /** Displays confirmation that a task was marked as completed. */
    public void showTaskMarked(Task task) {
        output.accept("     Beep! This task is complete:\n       " + task);
    }

    /** Displays confirmation that a task was marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        output.accept("     Oh! This task needs another turn:\n       " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        output.accept("     Poof! I let this task drift away:\n       " + task
                + "\n" + formatTaskCount(remainingTaskCount));
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        output.accept("     Ooh, a new Earth task! I've tucked it safely into the list:\n       " + task
                + "\n" + formatTaskCount(taskCount));
    }

    /**
     * Returns ET's summary of the number of tasks currently stored.
     *
     * @param taskCount the number of tasks in the list
     * @return a grammatically correct task-count message
     */
    private String formatTaskCount(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        return "     My list now holds " + taskCount + " " + taskLabel + ".";
    }

    /** Displays a user-facing error message. */
    public void showError(String message) {
        output.accept("     " + message);
    }

    /** Explains that saved tasks could not be loaded. */
    public void showLoadingError() {
        showError("Oh... my memory box would not open, so I'm starting with an empty list.");
    }

    /** Explains that a changed task list could not be saved. */
    public void showSavingError() {
        showError("Oh... I changed your task, but my memory box would not save it.");
    }
}
