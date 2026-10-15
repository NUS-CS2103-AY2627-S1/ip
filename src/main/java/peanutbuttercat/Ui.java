package peanutbuttercat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles all console input/output presentation for the chatbot.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Displays the chatbot's welcome message. */
    public void showWelcome() {
        showWelcome(getWelcomeMessage());
    }

    /**
     * Displays the chatbot banner followed by a supplied startup message.
     *
     * @param welcomeMessage Startup message to display below the banner.
     */
    public void showWelcome(String welcomeMessage) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println(" /\\_/\\\n"
                + "( o.o )  PeanutButterCat\n"
                + " > u <");
        System.out.println(welcomeMessage);
        showLine();
    }

    /** Returns the chatbot's welcome message. */
    public String getWelcomeMessage() {
        return "Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper."
                + System.lineSeparator()
                + "Tell me what's on your plate, and I'll tuck it into the task jar.";
    }

    /** Displays the separator between interactions. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /** Displays the chatbot's farewell message. */
    public void showFarewell() {
        System.out.println(getFarewellMessage());
        showLine();
    }

    /** Returns the chatbot's farewell message. */
    public String getFarewellMessage() {
        return "The task jar is safe with me. Stay smooth, and see you soon!";
    }

    /** Displays a command-processing error. */
    public void showError(String message) {
        System.out.println(getErrorMessage(message));
    }

    /** Returns a command-processing error message. */
    public String getErrorMessage(String message) {
        return message;
    }

    /** Displays all tasks currently stored. */
    public void showTaskList(TaskList tasks) {
        System.out.println(getTaskListMessage(tasks));
    }

    /** Returns a formatted list of all tasks currently stored. */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Here's what's tucked in the task jar:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator()).append(i + 1).append('.').append(tasks.get(i));
        }
        return message.toString();
    }

    /** Displays tasks whose descriptions contain the searched keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println(getMatchingTasksMessage(matchingTasks));
    }

    /** Returns a formatted list of tasks matching a description keyword. */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        StringBuilder message = new StringBuilder("I sniffed out these matching tasks:");
        if (matchingTasks.isEmpty()) {
            return message.append(System.lineSeparator()).append("No matching crumbs found.").toString();
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append(System.lineSeparator()).append(i + 1).append('.').append(matchingTasks.get(i));
        }
        return message.toString();
    }

    /** Displays tasks whose deadline or event range includes the supplied date. */
    public void showTasksOnDate(LocalDate date, TaskList tasks) {
        System.out.println(getTasksOnDateMessage(date, tasks));
    }

    /** Returns a formatted list of tasks whose scheduled date includes the supplied date. */
    public String getTasksOnDateMessage(LocalDate date, TaskList tasks) {
        StringBuilder message = new StringBuilder("Here's what's on the plate for ")
                .append(date.format(DISPLAY_DATE)).append(':');
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                message.append(System.lineSeparator()).append(i + 1).append('.').append(task);
            }
        }
        return message.toString();
    }

    /** Displays confirmation after adding a task. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(getTaskAddedMessage(task, taskCount));
    }

    /** Returns a confirmation that a task was added. */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Spread the word - this task is in the jar:" + System.lineSeparator()
                + task + System.lineSeparator() + getTaskCountMessage(taskCount);
    }

    /** Displays confirmation after deleting a task. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(getTaskDeletedMessage(task, taskCount));
    }

    /** Returns a confirmation that a task was deleted. */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Scoop complete! I've removed this task from the jar:" + System.lineSeparator()
                + "  " + task + System.lineSeparator() + getTaskCountMessage(taskCount);
    }

    /** Displays confirmation after changing a task's completion status. */
    public void showTaskStatus(Task task, boolean isDone) {
        System.out.println(getTaskStatusMessage(task, isDone));
    }

    /** Returns a confirmation that a task's completion status changed. */
    public String getTaskStatusMessage(Task task, boolean isDone) {
        String message = isDone
                ? "Paw-some! That's one smooth finish:"
                : "Back on the plate! This task is active again:";
        return message + System.lineSeparator() + "  " + task;
    }

    /**
     * Returns the completion statistics for the last seven calendar days.
     *
     * @param completedTaskCount Number of tasks completed in the reporting period.
     * @param unknownDateTaskCount Number of completed tasks excluded because their date is unknown.
     * @return Formatted statistics message.
     */
    public String getStatisticsMessage(long completedTaskCount, long unknownDateTaskCount) {
        assert completedTaskCount >= 0 : "Completed task count must not be negative";
        assert unknownDateTaskCount >= 0 : "Unknown completion date count must not be negative";

        String taskWord = completedTaskCount == 1 ? "task" : "tasks";
        String message = "In the last 7 calendar days, you finished " + completedTaskCount + " "
                + taskWord + ". Nice spread!";
        if (unknownDateTaskCount == 0) {
            return message;
        }
        if (unknownDateTaskCount == 1) {
            return message + System.lineSeparator()
                    + "Note: 1 completed task has an unknown completion date and was not counted.";
        }
        return message + System.lineSeparator() + "Note: " + unknownDateTaskCount
                + " completed tasks have unknown completion dates and were not counted.";
    }

    private String getTaskCountMessage(int taskCount) {
        assert taskCount >= 0 : "Displayed task count must not be negative";

        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "The task jar now holds " + taskCount + " " + taskWord + ".";
    }
}
