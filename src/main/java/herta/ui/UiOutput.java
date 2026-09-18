package herta.ui;

import herta.task.Task;

/**
 * Provides output operations used by Herta commands.
 */
public interface UiOutput {
    /** Message template used when reporting the number of active tasks. */
    String TASK_COUNT_MESSAGE = "That makes %d active %s. Try to keep up.";
    /** Message shown when the user ends the session. */
    String GOODBYE_MESSAGE = "Leaving already? Goodbye.";
    /** Format used for an unnumbered task display. */
    String INDENTED_TASK_FORMAT = "  %s";
    /** Format used for a numbered task display. */
    String NUMBERED_TASK_FORMAT = "%d. %s";

    /**
     * Displays a message to the user.
     *
     * @param message the message to display.
     */
    void showMessage(String message);

    /**
     * Displays a task to the user.
     *
     * @param task the task to display.
     */
    default void showTask(Task task) {
        showMessage(INDENTED_TASK_FORMAT.formatted(task));
    }

    /**
     * Displays a task with its one-based list number.
     *
     * @param taskNumber the one-based number shown to the user.
     * @param task the task to display.
     */
    default void showTask(int taskNumber, Task task) {
        showMessage(NUMBERED_TASK_FORMAT.formatted(taskNumber, task));
    }

    /**
     * Displays the number of tasks to the user.
     *
     * @param taskCount the number of tasks.
     */
    default void showTaskCount(int taskCount) {
        String taskNoun = getTaskNoun(taskCount);
        showMessage(TASK_COUNT_MESSAGE.formatted(taskCount, taskNoun));
    }

    /**
     * Returns the singular or plural noun for a task count.
     *
     * @param taskCount the task count.
     * @return {@code task} only for one, otherwise {@code tasks}.
     */
    static String getTaskNoun(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }

    /**
     * Displays Herta's goodbye message.
     */
    void showGoodbye();
}
