package yuki.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;

/**
 * Formats Yuki's responses and handles command-line interaction with the user.
 */
public class Ui {
    /** Separates individual responses so they are easier to read. */
    private static final String SEPARATOR_LINE =
            "----------------------------------------------------------"
                    + "----------------------------------------------------------";
    /** The banner displayed when Yuki starts in the command-line interface. */
    private static final String BANNER = "__   __     _    _ \n"
            + "\\ \\ / /   _| | _(_)\n"
            + " \\ V / | | | |/ / |\n"
            + "  | || |_| |   <| |\n"
            + "  |_| \\__,_|_|\\_\\_|\n";
    /** The greeting displayed when Yuki starts. */
    private static final String WELCOME_MESSAGE =
            "...Hello. This is Yuki. What do you need?";
    /** Reads commands entered through standard input. */
    private final Scanner scanner;
    /** Whether responses should also be printed to standard output. */
    private final boolean isOutputEnabled;
    /** Most recent response generated for the user. */
    private String lastResponse;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this(true);
    }

    /** Creates a UI with the requested command-line output behavior. */
    private Ui(boolean isOutputEnabled) {
        scanner = new Scanner(System.in);
        this.isOutputEnabled = isOutputEnabled;
        lastResponse = "";
    }

    /**
     * Creates a UI that records responses without printing them.
     *
     * @return A UI suitable for a graphical interface.
     */
    public static Ui createSilentUi() {
        return new Ui(false);
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Returns the most recent response generated for the user. */
    public String getLastResponse() {
        return lastResponse;
    }

    /** Displays Yuki's command-line banner and greeting. */
    public void showWelcome() {
        showResponse(BANNER, WELCOME_MESSAGE);
    }

    /** Records Yuki's greeting without the command-line banner. */
    public void showWelcomeMessage() {
        showResponse(WELCOME_MESSAGE);
    }

    /** Displays Yuki's farewell. */
    public void showGoodbye() {
        showResponse("...Goodbye.");
    }

    /** Displays all tasks in their current order. */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("...There aren't any tasks in your list.");
            return;
        }
        showNumberedTaskList("Here... These are the tasks you have:", tasks);
    }

    /** Displays tasks whose descriptions matched a find command. */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("...There aren't any matching tasks.");
            return;
        }
        showNumberedTaskList("Here... These are the matching tasks in your list:", tasks);
    }

    /** Displays upcoming tasks with their original task-list numbers. */
    public void showUpcomingTasks(List<Task> tasks, List<Integer> taskNumbers) {
        List<String> lines = new ArrayList<>();
        lines.add("Here... These tasks are due in the next 24 hours:");
        for (int taskNumber : taskNumbers) {
            assert taskNumber >= 1 && taskNumber <= tasks.size()
                    : "An upcoming task number must identify an existing task";
            lines.add(taskNumber + "." + tasks.get(taskNumber - 1));
        }
        showResponse(lines.toArray(String[]::new));
    }

    /** Displays that no incomplete dated tasks are due soon. */
    public void showNoUpcomingTasks() {
        showResponse("You have no tasks due in the next 24 hours.");
    }

    /** Displays a task after its completion status has changed. */
    public void showTaskStatusChanged(String message, Task task) {
        showResponse(message, task.toString());
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        List<String> lines = new ArrayList<>();
        lines.add("Alright... I've added it.");
        lines.add("  " + task);
        lines.add("There are " + taskCount + " tasks now.");
        if (hasUnrecognizedReminderDate(task)) {
            lines.add("...I couldn't recognize that date, so this task won't appear in reminders.");
        }
        showResponse(lines.toArray(String[]::new));
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        showResponse(
                "Alright... I've removed it.",
                "  " + task,
                "There are " + taskCount + " tasks now.");
    }

    /** Displays an error caused while processing a command. */
    public void showError(String message) {
        showResponse("I couldn't process that. " + message);
    }

    /** Displays an error caused while loading saved tasks. */
    public void showLoadingError(String message) {
        lastResponse = "I couldn't load the saved tasks. " + message;
        if (isOutputEnabled) {
            System.out.println(lastResponse);
        }
    }

    /** Displays a heading and a one-based numbered list of tasks. */
    private void showNumberedTaskList(String heading, List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add(heading);
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showResponse(lines.toArray(String[]::new));
    }

    /** Returns whether a dated task cannot provide the date used for reminders. */
    private boolean hasUnrecognizedReminderDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().toReminderDateTime().isEmpty();
        }
        return task instanceof Event event
                && event.getFrom().toReminderDateTime().isEmpty();
    }

    /** Displays one response surrounded by separator lines. */
    private void showResponse(String... lines) {
        lastResponse = String.join(System.lineSeparator(), lines);
        if (isOutputEnabled) {
            System.out.println(SEPARATOR_LINE);
            System.out.println(lastResponse);
            System.out.println(SEPARATOR_LINE);
        }
    }
}
