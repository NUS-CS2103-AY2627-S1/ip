package kaya.ui;

import kaya.task.Task;

/**
 * Shares Kaya's kopitiam phrases between the console and graphical interfaces.
 */
public final class Messages {
    public static final String GREETING = "Hey, I'm Kaya.\nGrab a kopi - let's sort out your day.";
    public static final String FAREWELL = "See you! One thing at a time, okay?";
    public static final String ERROR_PREFIX = "Hmm. ";
    public static final String TASKS_HEADING = "Here's what's on your plate:";
    public static final String EMPTY_LIST = "Your task list is empty. Time for a kopi break?";
    public static final String MATCHES_HEADING = "Here's what I found on your plate:";
    public static final String NO_MATCHES = "No matching tasks on your plate. Try another keyword.";

    private Messages() {
        // This utility class only provides shared messages and formatting methods.
    }

    /**
     * Formats tasks with one-based numbers, or an appropriate message when empty.
     *
     * @param heading the text displayed before a nonempty task list
     * @param emptyMessage the text displayed when no tasks are supplied
     * @param tasks the tasks to display
     * @return the formatted task list or empty-state message
     */
    public static String formatTasks(String heading, String emptyMessage, Iterable<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        int index = 1;
        for (Task task : tasks) {
            response.append('\n').append(index).append('.').append(task);
            index++;
        }
        return index == 1 ? emptyMessage : response.toString();
    }

    /**
     * Describes the total number of stored tasks, including completed tasks.
     *
     * @param count the number of tasks in the list
     * @return a task count with the appropriate singular or plural noun
     */
    public static String taskCount(int count) {
        return "You have " + count + (count == 1 ? " task" : " tasks") + " on your plate.";
    }
}
