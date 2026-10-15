package bloop.ui;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import bloop.exception.DukeException;
import bloop.task.Task;
import bloop.task.TaskList;

/** Handles all console output formatting for Orbit. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____  _                   \n"
            + "| __ )| | ___   ___  _ __  \n"
            + "|  _ \\| |/ _ \\ / _ \\| '_ \\ \n"
            + "| |_) | | (_) | (_) | |_) |\n"
            + "|____/|_|\\___/ \\___/| .__/ \n"
            + "                    |_|    ";
    private final Consumer<String> messageConsumer;
    private final Consumer<String> errorConsumer;

    /** Creates a console user interface. */
    public Ui() {
        this(null, null);
    }

    /** Creates a user interface that sends messages to the supplied consumer. */
    public Ui(Consumer<String> messageConsumer) {
        this(messageConsumer, messageConsumer);
    }

    /** Creates a user interface with separate handlers for ordinary and error messages. */
    public Ui(Consumer<String> messageConsumer, Consumer<String> errorConsumer) {
        this.messageConsumer = messageConsumer;
        this.errorConsumer = errorConsumer;
    }

    /** Displays the chatbot greeting. */
    public void showWelcome() {
        if (messageConsumer == null) {
            showDivider();
            System.out.println(BANNER);
            System.out.println("Orbit online. Your task mission is ready.");
            System.out.println("What shall we launch?");
            showDivider();
        } else {
            showMessage("Orbit online. Your task mission is ready.", "What shall we launch?");
        }
    }

    /** Displays a divider between conversation turns. */
    public void showDivider() {
        if (messageConsumer == null) {
            System.out.println(DIVIDER);
        }
    }

    /** Displays a user-facing exception message. */
    public void showError(DukeException exception) {
        if (errorConsumer == null) {
            showMessage(exception.getMessage());
        } else {
            errorConsumer.accept(exception.getMessage());
        }
    }

    /** Displays the farewell message. */
    public void showGoodbye() {
        showMessage("Orbit signing off. Clear skies ahead!");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage("Mission logged:", "  " + task,
                "You now have " + taskCount + " missions on your radar.");
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessage("Mission removed from your radar:", "  " + task,
                "You now have " + taskCount + " missions on your radar.");
    }

    /** Displays confirmation that a task's completion status changed. */
    public void showStatusUpdated(Task task, boolean isDone) {
        showMessage(isDone ? "Mission complete:"
                : "Mission returned to active orbit:", "  " + task);
    }

    /** Displays every task in the list. */
    public void showTaskList(TaskList tasks) {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> formatTaskLine(index, tasks.get(index)))
                .collect(Collectors.joining());
        showMessage("Mission control overview:" + taskLines);
    }

    /** Displays tasks whose descriptions contain a supplied keyword. */
    public void showFoundTasks(TaskList tasks, String keyword) {
        String taskLines = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).containsKeyword(keyword))
                .mapToObj(index -> formatTaskLine(index, tasks.get(index)))
                .collect(Collectors.joining());
        showMessage("Signals matching \"" + keyword + "\":" + taskLines);
    }

    /** Returns one numbered task line for a task's zero-based list index. */
    private String formatTaskLine(int index, Task task) {
        return "\n" + (index + 1) + "." + task;
    }

    /** Displays one or more lines as a single chatbot message. */
    private void showMessage(String... lines) {
        String message = String.join("\n", lines);
        if (messageConsumer == null) {
            System.out.println(" " + message);
        } else {
            messageConsumer.accept(message);
        }
    }
}
