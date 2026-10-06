package caesar.gui;

import caesar.exception.CaesarException;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Collects command responses for display in the JavaFX conversation window. */
public class GuiUi extends Ui {
    private final StringBuilder response = new StringBuilder();
    private String followUpResponse = "";

    /** Creates a user interface that stores responses instead of printing them. */
    public GuiUi() {
        super();
    }

    /** Clears the response collected from the previous command. */
    public void clearResponse() {
        response.setLength(0);
        followUpResponse = "";
    }

    /** Returns the latest command response and clears it for the next command. */
    public String consumeResponse() {
        String message = response.toString();
        response.setLength(0);
        return message;
    }

    /** Returns the follow-up response collected for a separate GUI dialog. */
    public String consumeFollowUpResponse() {
        String message = followUpResponse;
        followUpResponse = "";
        return message;
    }

    /** Records feedback after adding a task. */
    @Override
    public void showTaskAdded(Task task, TaskList tasks) {
        response.append(formatTaskAddedResponse(task, tasks));
        followUpResponse = formatDynamicComment(tasks);
    }

    /** Records feedback after deleting a task. */
    @Override
    public void showTaskDeleted(Task task, TaskList tasks) {
        response.append(formatTaskDeletedResponse(task, tasks));
    }

    /** Records feedback after marking a task complete. */
    @Override
    public void showTaskMarked(Task task) {
        response.append(formatTaskMarkedResponse(task));
    }

    /** Records feedback after returning a task to pending. */
    @Override
    public void showTaskUnmarked(Task task) {
        response.append(formatTaskUnmarkedResponse(task));
    }

    /** Records feedback after rescheduling a deadline or event. */
    @Override
    public void showTaskRescheduled(Task task) {
        response.append(formatTaskRescheduledResponse(task));
    }

    /** Records matching tasks for display in the JavaFX conversation window. */
    @Override
    public void showTaskFound(TaskList foundTasks) throws CaesarException {
        response.append(formatTaskFoundResponse(foundTasks));
    }

    /** Records overdue and soon-due pending dated tasks for the GUI. */
    @Override
    public void showReminders(TaskList tasks) {
        response.append(formatReminderResponse(tasks));
        followUpResponse = formatDynamicComment(tasks);
    }

    /** Records the result of clearing pending missed tasks for the GUI. */
    @Override
    public void showMissedTasksCleared(int clearedTaskCount) {
        response.append(formatMissedTasksClearedResponse(clearedTaskCount));
    }

    /** Records the available command formats for the JavaFX conversation window. */
    @Override
    public void showHelp() {
        response.append(formatHelpResponse());
    }

    /** Records the current tasks with one-based numbering. */
    @Override
    public void showTaskList(Iterable<Task> tasks) throws CaesarException {
        response.append(formatTaskListResponse(tasks));
    }

    /** Records the farewell message. */
    @Override
    public void showGoodbye() {
        response.append(formatGoodbyeResponse());
    }
}
