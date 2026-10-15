package duke.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import duke.task.Task;
import duke.task.TaskList;

/**
 * Handles all messages shown to the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String CHATBOT_NAME = "geen";
    private static final String CHATBOT_BANNER = String.join("\n",
            "  ____ _____ _____ _   _",
            " / ___| ____| ____| \\ | |",
            "| |  _|  _| |  _| |  \\| |",
            "| |_| | |___| |___| |\\  |",
            " \\____|_____|_____|_| \\_|");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Prints the opening chatbot message.
     */
    public void showGreeting() {
        System.out.println(formatGreeting());
    }

    /**
     * Prints the closing chatbot message.
     */
    public void showGoodbye() {
        System.out.println(formatGoodbye());
    }

    /**
     * Prints an error message caused by invalid user input or storage problems.
     */
    public void showError(String message) {
        System.out.println(formatError(message));
    }

    /**
     * Prints a confirmation after adding a task.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after adding the task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(formatTaskAdded(task, taskCount));
    }

    /**
     * Prints a confirmation after deleting a task.
     *
     * @param deletedTask Task that was deleted.
     * @param taskCount Number of tasks after deleting the task.
     */
    public void showTaskDeleted(Task deletedTask, int taskCount) {
        System.out.println(formatTaskDeleted(deletedTask, taskCount));
    }

    /**
     * Prints a confirmation after marking a task as done.
     */
    public void showTaskMarked(Task task) {
        System.out.println(formatTaskMarked(task));
    }

    /**
     * Prints a confirmation after marking a task as not done.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(formatTaskUnmarked(task));
    }

    /**
     * Prints all stored tasks in the order they were added.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(formatTaskList(tasks));
    }

    /**
     * Prints tasks matching a find keyword.
     */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println(formatMatchingTasks(tasks));
    }

    /**
     * Returns the opening chatbot message.
     */
    public String formatGreeting() {
        return LINE + "\n"
                + CHATBOT_BANNER + "\n"
                + "Hello! I'm " + CHATBOT_NAME + ".\n"
                + "What can I do for you?\n"
                + LINE;
    }

    /**
     * Returns the closing chatbot message.
     */
    public String formatGoodbye() {
        return LINE + "\n"
                + "Bye. Hope to see you again soon!\n"
                + LINE;
    }

    /**
     * Returns an error message caused by invalid user input or storage problems.
     */
    public String formatError(String message) {
        return LINE + "\n"
                + "OOPS!!! " + message + "\n"
                + LINE;
    }

    /**
     * Returns a confirmation after adding a task.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return LINE + "\n"
                + "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.\n"
                + LINE;
    }

    /**
     * Returns a confirmation after deleting a task.
     */
    public String formatTaskDeleted(Task deletedTask, int taskCount) {
        return LINE + "\n"
                + "Noted. I've removed this task:\n"
                + "  " + deletedTask + "\n"
                + "Now you have " + taskCount + " tasks in the list.\n"
                + LINE;
    }

    /**
     * Returns a confirmation after marking a task as done.
     */
    public String formatTaskMarked(Task task) {
        return LINE + "\n"
                + "Nice! I've marked this task as done:\n"
                + "  " + task + "\n"
                + LINE;
    }

    /**
     * Returns a confirmation after marking a task as not done.
     */
    public String formatTaskUnmarked(Task task) {
        return LINE + "\n"
                + "OK, I've marked this task as not done yet:\n"
                + "  " + task + "\n"
                + LINE;
    }

    /**
     * Returns all stored tasks in the order they were added.
     */
    public String formatTaskList(TaskList tasks) {
        StringBuilder message = new StringBuilder();
        message.append(LINE).append("\n");
        message.append("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        message.append("\n").append(LINE);
        return message.toString();
    }

    /**
     * Returns tasks matching a find keyword.
     */
    public String formatMatchingTasks(TaskList tasks) {
        StringBuilder message = new StringBuilder();
        message.append(LINE).append("\n");
        message.append("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        message.append("\n").append(LINE);
        return message.toString();
    }

    /**
     * Returns deadlines and events scheduled on the given date.
     */
    public String formatSchedule(LocalDate date, TaskList tasks) {
        assert date != null : "Schedule date should not be null";
        assert tasks != null : "Scheduled task list should not be null";
        String displayDate = date.format(DISPLAY_DATE_FORMAT);
        if (tasks.size() == 0) {
            return LINE + "\n"
                    + "There are no scheduled tasks on " + displayDate + ".\n"
                    + LINE;
        }

        StringBuilder message = new StringBuilder();
        message.append(LINE).append("\n");
        message.append("Schedule for ").append(displayDate).append(":");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        message.append("\n").append(LINE);
        return message.toString();
    }

}
