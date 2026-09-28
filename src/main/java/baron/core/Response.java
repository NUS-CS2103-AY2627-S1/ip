package baron.core;

import baron.core.exception.BaronException;
import baron.core.task.Task;
import baron.core.task.TaskList;

/**
 * Creates the user-facing messages returned by Baron.
 */
class Response {
    private static final String INTRO = "Hi! My name is Baron.\nWhat can I do for you?";
    private static final String OUTRO = "Bye. Hope you have a wonderful day!";

    /**
     * Returns Baron's introductory message.
     *
     * @return The introductory message.
     */
    public static String respondWithIntro() {
        return INTRO;
    }

    /**
     * Returns Baron's farewell message.
     *
     * @return The farewell message.
     */
    public static String respondWithOutro() {
        return OUTRO;
    }

    /**
     * Returns a message containing all tasks in the specified list.
     *
     * @param tasks The tasks to display.
     * @param showRequiredTasks Whether to include prerequisite tasks.
     * @param showUnlockedTasks Whether to include dependent tasks.
     * @return The formatted task-list message.
     */
    public static String respondWithAllTasks(
            TaskList tasks, boolean showRequiredTasks, boolean showUnlockedTasks) {
        String message = "Here are the tasks in your list:";
        String tasksString = tasks.getNumberedTasks(tasks, showRequiredTasks, showUnlockedTasks);
        return String.join("\n", message, tasksString);
    }

    /**
     * Returns a message confirming that the specified task is complete.
     *
     * @param task The task that was marked complete.
     * @param allTasks The task list containing the marked task.
     * @return The confirmation message.
     */
    public static String respondWithMarkedTask(Task task, TaskList allTasks) {
        String message = "Nice! I've marked this task as done:";
        String taskString = task.getNumberedTask(allTasks, false, false);
        return String.join("\n", message, taskString);
    }

    /**
     * Returns a message confirming that the specified task is not complete.
     *
     * @param task The task that was marked incomplete.
     * @param allTasks The task list containing the unmarked task.
     * @return The confirmation message.
     */
    public static String respondWithUnmarkedTask(Task task, TaskList allTasks) {
        String message = "OK, I've marked this task as not done yet:";
        String taskString = task.getNumberedTask(allTasks, false, false);
        return String.join("\n", message, taskString);
    }

    /**
     * Returns a message confirming that a task was added.
     *
     * @param task The task that was added.
     * @param allTasks The updated task list.
     * @return The confirmation message.
     */
    public static String respondWithAddedTask(Task task, TaskList allTasks) {
        String message = "Got it. I've added this task:";
        String taskString = task.getNumberedTask(allTasks, false, false);
        String numTasks = "Now you have " + allTasks.size() + " tasks in the list";
        return String.join("\n", message, taskString, numTasks);
    }

    /**
     * Returns a message confirming that a task was removed.
     *
     * @param task The task that was removed.
     * @param allTasks The updated task list.
     * @return The confirmation message.
     */
    public static String respondWithDeletedTask(Task task, TaskList allTasks) {
        String message = "Noted. I've removed this task:";
        String taskString = task.getNumberedTask(allTasks, false, false);
        String numTasks = "Now you have " + allTasks.size() + " tasks in the list";
        return String.join("\n", message, taskString, numTasks);
    }

    /**
     * Returns a message containing the tasks that match a search.
     *
     * @param tasks The matching tasks to display.
     * @param allTasks The full task list, used to retain each task's displayed number.
     * @return The formatted search-results message.
     */
    public static String respondWithMatchingTasks(TaskList tasks, TaskList allTasks) {
        String message = "Here are the matching tasks in your list:";
        String tasksString = tasks.getNumberedTasks(allTasks, false, false);
        return String.join("\n", message, tasksString);
    }

    /**
     * Returns a message confirming the specified task's relationships.
     *
     * @param task The task whose relationships were specified.
     * @param allTasks The task list containing the specified task.
     * @return The confirmation message.
     */
    public static String respondWithSpecifiedTask(Task task, TaskList allTasks) {
        String message = "Noted. I've specified this task:";
        String taskString = task.getNumberedTask(allTasks, true, true);
        return String.join("\n", message, taskString);
    }

    /**
     * Returns the message associated with the specified command error.
     *
     * @param exception The command error.
     * @return The error message.
     */
    public static String respondWithBaronException(BaronException exception) {
        return exception.getMessage();
    }
}
