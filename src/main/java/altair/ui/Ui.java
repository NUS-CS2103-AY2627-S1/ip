package altair.ui;

import java.util.List;
import java.util.Scanner;

import altair.task.Task;

/**
 * Handles interaction with the user.
 *
 * <p>The {@code format*} methods build each response as a plain string; the
 * {@code show*} methods print those strings to the console for the text UI.
 * Keeping the wording in one place means both the text UI and the JavaFX GUI
 * (which calls the {@code format*} methods through
 * {@link altair.Altair#getResponse(String)}) show exactly the same messages.</p>
 */
public class Ui {

    /** The horizontal divider printed between the user's input and each response. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** The ASCII-art logo shown once when the program starts. */
    private static final String BANNER = "   _____  .__   __         .__        \n"
            + "  /  _  \\ |  | _/  |______ |__|______ \n"
            + " /  /_\\  \\|  | \\   __\\__  \\|  \\_  __ \\\n"
            + "/    |    \\  |__|  |  / __ \\|  ||  | \\/\n"
            + "\\____|__  /____/|__| (____  /__||__|  \n"
            + "        \\/                \\/          ";

    /** The source of typed commands for the text UI. */
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command line is available to read.
     *
     * @return {@code true} if {@link #readCommand()} can return another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line entered by the user.
     *
     * @return the raw text of the next line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the divider line used to separate responses. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Prints the start-up greeting, framed by divider lines. */
    public void showWelcome() {
        System.out.println(formatWelcome());
    }

    /**
     * Prints an error explanation, indented to line up with other responses.
     *
     * @param message the explanation to show.
     */
    public void showError(String message) {
        System.out.println(formatError(message));
    }

    /** Prints the farewell shown in response to the {@code bye} command. */
    public void showGoodbye() {
        System.out.println(formatGoodbye());
    }

    /**
     * Prints the whole task list, numbered from one.
     *
     * @param tasks the tasks to display, in list order.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(formatTaskList(tasks));
    }

    /**
     * Prints the tasks that matched a {@code find} search, numbered from one.
     *
     * @param tasks the matching tasks, in list order.
     */
    public void showFoundTasks(List<Task> tasks) {
        System.out.println(formatFoundTasks(tasks));
    }

    /**
     * Prints the confirmation shown after a new task is added.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks now in the list.
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println(formatAdded(task, taskCount));
    }

    /**
     * Prints the confirmation shown after a task is marked as done.
     *
     * @param task the task that was marked.
     */
    public void showMarked(Task task) {
        System.out.println(formatMarked(task));
    }

    /**
     * Prints the confirmation shown after a task is marked as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showUnmarked(Task task) {
        System.out.println(formatUnmarked(task));
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks left in the list.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(formatDeleted(task, taskCount));
    }

    /**
     * Builds the start-up greeting, framed by divider lines.
     *
     * @return the greeting text, without a trailing newline.
     */
    public static String formatWelcome() {
        return SEPARATOR + "\n" + BANNER + "\n"
                + "Greetings. Altair, at your service. How may I be of assistance?" + "\n" + SEPARATOR;
    }

    /**
     * Builds an error explanation, indented to line up with other responses.
     *
     * @param message the explanation to show.
     * @return the indented explanation.
     */
    public static String formatError(String message) {
        return "    I'm afraid that won't do. " + message;
    }

    /**
     * Builds the farewell shown in response to the {@code bye} command.
     *
     * @return the farewell text.
     */
    public static String formatGoodbye() {
        return "    Very well. I shall be here when next you require me.";
    }

    /**
     * Builds the whole task list, numbered from one.
     *
     * @param tasks the tasks to display, in list order.
     * @return the numbered list, one task per line.
     */
    public static String formatTaskList(List<Task> tasks) {
        StringBuilder text = new StringBuilder("     The following are your tasks");
        for (int i = 0; i < tasks.size(); i++) {
            text.append("\n     ").append(i + 1).append(".").append(tasks.get(i));
        }
        return text.toString();
    }

    /**
     * Builds the result of a {@code find} search, numbered from one.
     *
     * @param tasks the matching tasks, in list order.
     * @return the matches, or a line saying nothing matched.
     */
    public static String formatFoundTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "     Nothing of that description turns up, I'm afraid.";
        }
        StringBuilder text = new StringBuilder("     Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            text.append("\n     ").append(i + 1).append(".").append(tasks.get(i));
        }
        return text.toString();
    }

    /**
     * Builds the confirmation shown after a new task is added.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks now in the list.
     * @return the confirmation text.
     */
    public static String formatAdded(Task task, int taskCount) {
        return "    Alright, I have added it to your list:\n"
                + "      " + task + "\n"
                + "    Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Builds the warning shown when a new task duplicates one already in the
     * list, asking the user to confirm before it is added.
     *
     * @param existing the existing task the new one duplicates.
     * @return the warning text, ending with a y/n prompt.
     */
    public static String formatDuplicateWarning(Task existing) {
        return "    I believe this already appears on the books:\n"
                + "      " + existing + "\n"
                + "    Add it anyway? (y/n)";
    }

    /**
     * Builds the confirmation shown after the user declines to add a flagged
     * duplicate task.
     *
     * @return the confirmation text.
     */
    public static String formatDuplicateDeclined() {
        return "    Very well. I shall leave it be.";
    }

    /**
     * Builds the confirmation shown after a task is marked as done.
     *
     * @param task the task that was marked.
     * @return the confirmation text.
     */
    public static String formatMarked(Task task) {
        return "     Duly noted. Marked as complete:\n"
                + "       " + task;
    }

    /**
     * Builds the confirmation shown after a task is marked as not done.
     *
     * @param task the task that was unmarked.
     * @return the confirmation text.
     */
    public static String formatUnmarked(Task task) {
        return "     As you wish. Returned to the unfinished pile:\n"
                + "       " + task;
    }

    /**
     * Builds the confirmation shown after a task is deleted.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks left in the list.
     * @return the confirmation text.
     */
    public static String formatDeleted(Task task, int taskCount) {
        return "    Consider it done. I have removed the following:\n"
                + "      " + task + "\n"
                + "    Now you have " + taskCount + " tasks in the list.";
    }
}
