package bingus.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import bingus.task.Task;
import bingus.task.TaskList;

/**
 * Formats user-interface responses.
 */
public class Ui {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /**
     * Returns Bingus's greeting for a new session.
     *
     * @return welcome message.
     */
    public String getWelcomeMessage() {
        return "Hi! I'm Bingus, your task-watching cat.\n"
                + "Tell me what you need to remember, and I'll keep an eye on it.";
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount total number of tasks after the addition.
     * @return task-added message.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it! I added this task:\n"
                + task.getDisplayString()
                + "\nYou now have " + getTaskCountDescription(taskCount) + " in your list.";
    }

    /**
     * Returns every task in the task list.
     *
     * @param tasks task list to display.
     * @return task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your task list is empty. Time for a cat nap!";
        }

        StringBuilder message = new StringBuilder("Here are the tasks I am watching:");
        for (int index = 0; index < tasks.size(); index++) {
            appendNumberedTask(message, index + 1, tasks.get(index));
        }
        return message.toString();
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount total number of tasks after deletion.
     * @return task-deleted message.
     */
    public String getDeleteTaskMessage(Task task, int taskCount) {
        return "Done! I removed this task:\n"
                + task.getDisplayString()
                + "\nYou now have " + getTaskCountDescription(taskCount) + " in your list.";
    }

    /**
     * Returns confirmation that a task was marked complete.
     *
     * @param task task whose completion status changed.
     * @return task-marked message.
     */
    public String getTaskMarkedMessage(Task task) {
        return "Purrfect! This task is complete:\n" + task.getDisplayString();
    }

    /**
     * Returns confirmation that a task was marked incomplete.
     *
     * @param task task whose completion status changed.
     * @return task-unmarked message.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "No problem! This task is active again:\n" + task.getDisplayString();
    }

    /**
     * Returns confirmation that a task was updated.
     *
     * @param task updated task.
     * @return task-updated message.
     */
    public String getTaskUpdatedMessage(Task task) {
        return "Got it! I updated this task:\n" + task.getDisplayString();
    }

    /**
     * Returns tasks associated with a specified date.
     *
     * @param tasks task list to filter.
     * @param date date used to filter tasks.
     * @return filtered task-list message.
     */
    public String getFilteredTaskListMessage(TaskList tasks, LocalDate date) {
        String formattedDate = date.format(DISPLAY_DATE_FORMAT);
        StringBuilder message = new StringBuilder(
                "Here are the tasks I am watching on " + formattedDate + ":");
        boolean hasMatchingTasks = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.matchesDate(date)) {
                hasMatchingTasks = true;
                appendNumberedTask(message, index + 1, task);
            }
        }

        if (!hasMatchingTasks) {
            return "I am not watching any tasks on " + formattedDate + ".";
        }
        return message.toString();
    }

    /**
     * Returns the farewell message.
     *
     * @return farewell message.
     */
    public String getByeMessage() {
        return "Bingus is curling up for a nap. See you soon!";
    }

    /**
     * Returns tasks whose descriptions match a search keyword.
     *
     * @param tasks matching tasks.
     * @param keyword keyword used to search for tasks.
     * @return matching-tasks message.
     */
    public String getMatchingTasksMessage(List<Task> tasks, String keyword) {
        if (tasks.isEmpty()) {
            return "I could not find anything matching `" + keyword + "`.";
        }

        StringBuilder message = new StringBuilder("I found these matching tasks:");
        for (int index = 0; index < tasks.size(); index++) {
            appendNumberedTask(message, index + 1, tasks.get(index));
        }
        return message.toString();
    }

    private void appendNumberedTask(StringBuilder message, int taskNumber, Task task) {
        message.append("\n")
                .append(taskNumber)
                .append(". ")
                .append(task.getDisplayString());
    }

    /**
     * Returns a task count with the correct singular or plural noun.
     *
     * @param taskCount number of tasks.
     * @return formatted task count.
     */
    private String getTaskCountDescription(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

}
