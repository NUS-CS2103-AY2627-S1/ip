package panda.ui;

import java.util.List;
import java.util.Scanner;

import panda.exception.PandaException;
import panda.task.Task;
import panda.task.TaskList;

/**
 * Handles all command-line input and output for Panda.
 * Keeping presentation here allows the application coordinator to focus on
 * command execution instead of console formatting details.
 * <p>
 * Each method returns a string that the gui is able to display to the user
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = """
             ____    _    _   _ ____    _
            |  _ \\  / \\  | \\ | |  _ \\  / \\
            | |_) |/ _ \\ |  \\| | | | |/ _ \\
            |  __// ___ \\| |\\  | |_| / ___ \\
            |_|  /_/   \\_\\_| \\_|____/_/   \\_\\
            """;

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Panda's greeting.
     */
    public String showWelcome() {
        String welcomeMessage = formatLines(
                BANNER.stripTrailing(), "", "Hello! I'm Panda.", "What can I do for you?")
                .stripTrailing();
        System.out.println(DIVIDER);
        System.out.println(welcomeMessage);
        return welcomeMessage;
    }

    /**
     * Reports whether another command is available from the input stream.
     *
     * @return true when another complete input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays errors found while loading stored tasks.
     *
     * @param errors the loading errors to present.
     */
    public void showLoadingErrors(List<PandaException> errors) {
        showDivider();
        for (PandaException error : errors) {
            showError(error);
        }
        showDivider();
    }

    /**
     * Displays a divider between command responses.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays all supplied tasks under the standard task-list heading.
     *
     * @param numberedTasks the tasks and their one-based numbers.
     * @return the formatted task-list response.
     */
    public String showTaskList(List<TaskList.NumberedTask> numberedTasks) {
        return showNumberedTasks("Here are the tasks in your list:", numberedTasks);
    }

    /**
     * Displays all supplied tasks under the search-result heading.
     *
     * @param numberedTasks the matching tasks and their original one-based numbers.
     * @return the formatted matching-task response.
     */
    public String showMatchingTaskList(List<TaskList.NumberedTask> numberedTasks) {
        return showNumberedTasks(
                "Here are the matching tasks in your list:", numberedTasks);
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task the task that was marked.
     */
    public String showMarked(Task task) {
        String response = formatLines(
                "Nice! I've marked this task as done:",
                String.format("  [X] %s", task.getName()));
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was marked as unfinished.
     *
     * @param task the task that was unmarked.
     */
    public String showUnmarked(Task task) {
        String response = formatLines(
                "OK, I've marked this task as not done yet:",
                String.format("  [ ] %s", task.getName()));
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task's name was changed.
     *
     * @param task the updated task.
     * @return the formatted update confirmation.
     */
    public String showUpdated(Task task) {
        String status = task.isDone() ? "X" : " ";
        String response = formatLines(
                "Got it. I've updated this task:",
                String.format("  [%s][%s] %s", task.getType().getMarker(), status,
                        task.getDisplayText()));
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task the removed task.
     * @param remainingTaskCount the number of tasks left in the list.
     */
    public String showDeleted(Task task, int remainingTaskCount) {
        String status = task.isDone() ? "X" : " ";
        String response = formatLines(
                "Noted. I've removed this task:",
                String.format("  [%s][%s] %s", task.getType().getMarker(), status,
                        task.getDisplayText()),
                formatTaskCount(remainingTaskCount));
        System.out.print(response);
        return response;
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the new task.
     * @param taskCount the new total number of tasks.
     */
    public String showAdded(Task task, int taskCount) {
        String response = formatLines(
                "Got it. I've added this task:",
                String.format("  [%s][ ] %s", task.getType().getMarker(), task.getDisplayText()),
                formatTaskCount(taskCount));
        System.out.print(response);
        return response;
    }

    /**
     * Displays an expected user-facing error.
     *
     * @param exception the expected application error.
     */
    public String showError(PandaException exception) {
        System.out.println(exception.getMessage());
        return exception.getMessage();
    }

    /**
     * Displays Panda's closing message.
     */
    public String showGoodbye() {
        showDivider();
        System.out.println("Bye. Hope to see you again soon!");
        showDivider();
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats the total task count after a list mutation.
     *
     * @param taskCount the current number of tasks.
     * @return the grammatically correct task-count message.
     */
    private String formatTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return String.format("Now you have %d %s in the list.", taskCount, taskNoun);
    }

    /**
     * Displays numbered tasks below the supplied heading.
     */
    private String showNumberedTasks(String heading,
            List<TaskList.NumberedTask> numberedTasks) {
        String[] lines = new String[numberedTasks.size() + 1];
        lines[0] = heading;
        for (int i = 0; i < numberedTasks.size(); i++) {
            TaskList.NumberedTask numberedTask = numberedTasks.get(i);
            lines[i + 1] = formatNumberedTask(numberedTask);
        }

        String response = formatLines(lines);
        System.out.print(response);
        return response;
    }

    /**
     * Formats one task with its original one-based task number.
     */
    private String formatNumberedTask(TaskList.NumberedTask numberedTask) {
        Task task = numberedTask.task();
        String status = task.isDone() ? "X" : " ";
        return String.format("%d.[%s][%s] %s", numberedTask.number(),
                task.getType().getMarker(), status, task.getDisplayText());
    }

    /**
     * Joins response lines and terminates the formatted text with a line separator.
     *
     * @param lines the response lines in display order.
     * @return the formatted multi-line response.
     */
    private String formatLines(String... lines) {
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }
}
