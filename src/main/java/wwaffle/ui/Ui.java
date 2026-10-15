package wwaffle.ui;

import java.util.List;
import java.util.Scanner;

import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Task;
import wwaffle.task.Todo;

/**
 * Handles terminal input and presentation for WWaffle.
 */
public class Ui {
    private static final String RESET = "\u001B[0m";
    private static final String COLUMBIA_BLUE = "\u001B[38;2;135;175;199m";
    private static final String BLUEBERRY_BLUE = "\u001B[38;2;0;65;194m";
    private static final String ROYAL_BLUE = "\u001B[38;2;65;105;225m";
    private static final String DAY_SKY_BLUE = "\u001B[38;2;130;202;255m";
    private static final String BABY_BLUE = "\u001B[38;2;149;185;199m";
    private static final String DENIM_BLUE = "\u001B[38;2;121;186;236m";
    private static final String[] WORDMARK = {
        " __        ____        ___    _____ _____ _     _____ ",
        " \\ \\      / /\\ \\      / / \\  |  ___|  ___| |   | ____|",
        "  \\ \\ /\\ / /  \\ \\ /\\ / / _ \\ | |_  | |_  | |   |  _|  ",
        "   \\ V  V /    \\ V  V / ___ \\|  _| |  _| | |___| |___ ",
        "    \\_/\\_/      \\_/\\_/_/   \\_\\_|   |_|   |_____|_____|"
    };
    private static final String DIVIDER =
            "──────────────────────────────────────────────────────────";

    private final Scanner scanner = new Scanner(System.in);
    private final boolean isColorEnabled;

    /**
     * Creates a UI, enabling colour unless the {@code wwaffle.color} system property is false.
     */
    public Ui() {
        this.isColorEnabled = Boolean.parseBoolean(System.getProperty("wwaffle.color", "true"));
    }

    /**
     * Shows the welcome banner and number of loaded tasks.
     *
     * @param taskCount Number of tasks loaded from storage.
     */
    public void showWelcome(int taskCount) {
        System.out.println();
        for (String line : WORDMARK) {
            System.out.println(applyColor(ROYAL_BLUE, line));
        }
        System.out.println();
        System.out.println(applyColor(DAY_SKY_BLUE, " PERSONAL TASK MANAGER")
                + applyColor(BABY_BLUE, "  ·  " + taskCount + " "
                        + getTaskWord(taskCount).toUpperCase() + " READY"));
        System.out.println(applyColor(COLUMBIA_BLUE, DIVIDER));
    }

    /**
     * Shows the command prompt and reads the next line of user input.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        System.out.print("\n" + applyColor(COLUMBIA_BLUE, "COMMAND ")
                + applyColor(DAY_SKY_BLUE, "› "));
        if (isColorEnabled) {
            System.out.print(BLUEBERRY_BLUE);
        }
        String command = scanner.nextLine();
        if (isColorEnabled) {
            System.out.print(RESET);
        }
        return command;
    }

    /**
     * Returns a concise welcome message for non-terminal interfaces.
     *
     * @param taskCount Number of tasks loaded from storage.
     * @return Plain-text welcome message.
     */
    public String getWelcomeMessage(int taskCount) {
        if (taskCount == 0) {
            return "Welcome back. ☕\nNo tasks yet.";
        }
        return "Welcome back. ☕\n" + taskCount + " " + getTaskWord(taskCount) + " loaded.";
    }

    /**
     * Formats all tasks with their one-based list numbers.
     *
     * @param tasks Tasks to format.
     * @return Plain-text task list.
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Nothing here. Nature is healing. 🥐";
        }

        StringBuilder message = new StringBuilder("Here are your little problems, neatly arranged. ☕");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(". ").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Formats a sorted task list and states the key used to arrange it.
     *
     * @param tasks Tasks in their newly sorted order.
     * @param sortKey Validated key used for sorting.
     * @return Plain-text sorted task list.
     */
    public String getSortedTaskListMessage(List<Task> tasks, String sortKey) {
        if (tasks.isEmpty()) {
            return "Nothing here. Nature is healing. 🥐";
        }

        StringBuilder message = new StringBuilder("Here are your little problems, neatly arranged by ")
                .append(sortKey.toUpperCase()).append(". ☕");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(". ").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Formats tasks that match a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Plain-text search result.
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Nothing. Absolutely nothing. Have a cookie instead. 🍪";
        }

        StringBuilder message = new StringBuilder("Found these little guys. 🔍");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(". ").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     * @return Plain-text confirmation.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        if (task instanceof Todo) {
            return "Task added. 🧁\n" + task + "\n" + taskCount + " " + getTaskWord(taskCount) + " total.";
        }
        if (task instanceof Deadline) {
            return "Deadline added. 🧁\n" + task + "\n" + taskCount + " " + getTaskWord(taskCount) + " total.";
        }
        if (task instanceof Event) {
            return "Event added. 🧁\n" + task + "\n" + taskCount + " " + getTaskWord(taskCount) + " total.";
        }
        return "Another task has joined your little list:\n" + task + "\nYou now have " + taskCount + " "
                + getTaskWord(taskCount) + ". Grab a coffee and get moving. ☕";
    }

    /**
     * Formats confirmation that a task's completion state changed.
     *
     * @param task Updated task.
     * @param isDone Whether the task is now complete.
     * @return Plain-text confirmation.
     */
    public String getTaskMarkedMessage(Task task, boolean isDone) {
        String heading = isDone ? "Task marked. 🤩" : "Task unmarked. 😑";
        return heading + "\n" + task;
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks remaining after deletion.
     * @return Plain-text confirmation.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Task deleted. ☀️\n" + task + "\nYou have " + taskCount + " "
                + getTaskWord(taskCount) + " left.";
    }

    /**
     * Formats a user-facing error message.
     *
     * @param message Error explanation.
     * @return Plain-text error message.
     */
    public String getErrorMessage(String message) {
        return "⚠️ " + message;
    }

    /**
     * Returns the farewell message for non-terminal interfaces.
     *
     * @return Plain-text farewell message.
     */
    public String getExitMessage() {
        return "Goodbye. 👋";
    }

    /**
     * Shows all tasks with their one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(applyColor(DAY_SKY_BLUE, getTaskListMessage(tasks)));
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks.
     */
    public void showMatchingTasks(List<Task> tasks) {
        System.out.println(applyColor(DAY_SKY_BLUE, getMatchingTasksMessage(tasks)));
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(applyColor(DAY_SKY_BLUE, getTaskAddedMessage(task, taskCount)));
    }

    /**
     * Shows confirmation that a task's completion state changed.
     *
     * @param task Updated task.
     * @param isDone Whether the task is now complete.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        System.out.println(applyColor(DAY_SKY_BLUE, getTaskMarkedMessage(task, isDone)));
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks remaining after deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(applyColor(DAY_SKY_BLUE, getTaskDeletedMessage(task, taskCount)));
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message Error explanation to display.
     */
    public void showError(String message) {
        System.out.println(applyColor(DAY_SKY_BLUE, "⚠️ " + message));
    }

    /**
     * Shows the standard error for a failed storage load.
     */
    public void showLoadingError() {
        showError("I couldn't load your saved tasks. Please check the task file before continuing. ⚠️");
    }

    /**
     * Shows the farewell message.
     */
    public void showExit() {
        System.out.println("\n" + applyColor(DAY_SKY_BLUE, getExitMessage()));
    }

    /**
     * Returns the concise greeting shared by all greeting commands.
     *
     * @return Greeting response.
     */
    public String getGreetingMessage() {
        return "Hey there. ☕";
    }

    /**
     * Returns a short acknowledgement of appreciation.
     *
     * @return Thanks response.
     */
    public String getThanksMessage() {
        return "Anytime. 🧁";
    }

    private String applyColor(String ansiColor, String text) {
        return isColorEnabled ? ansiColor + text + RESET : text;
    }

    private String getTaskWord(int count) {
        return count == 1 ? "task" : "tasks";
    }
}
