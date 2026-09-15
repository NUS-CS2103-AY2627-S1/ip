package duke.ui;

import java.util.ArrayList;

import duke.task.Task;

/**
 * A test double for {@link Ui} that captures method calls for assertions.
 * Keeps test output clean by not printing to stdout.
 */
public class CapturingUi extends Ui {
    private String lastErrorMessage;
    private String lastMessage;

    private boolean isGoodbyeShown;

    private Task lastAddedTask;
    private Integer lastAddedTaskCount;

    private Task lastRemovedTask;
    private Integer lastRemovedTaskCount;

    private Integer lastMarkedTaskNumber;
    private Boolean isLastMarkedTaskDone;

    private ArrayList<Task> lastShownTaskList;
    private ArrayList<Task> lastShownMatchingTasks;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public boolean isGoodbyeShown() {
        return isGoodbyeShown;
    }

    public Task getLastAddedTask() {
        return lastAddedTask;
    }

    public Integer getLastAddedTaskCount() {
        return lastAddedTaskCount;
    }

    public Task getLastRemovedTask() {
        return lastRemovedTask;
    }

    public Integer getLastRemovedTaskCount() {
        return lastRemovedTaskCount;
    }

    public Integer getLastMarkedTaskNumber() {
        return lastMarkedTaskNumber;
    }

    public Boolean isLastMarkedTaskDone() {
        return isLastMarkedTaskDone;
    }

    public ArrayList<Task> getLastShownTaskList() {
        return lastShownTaskList;
    }

    public ArrayList<Task> getLastShownMatchingTasks() {
        return lastShownMatchingTasks;
    }

    @Override
    public void showError(String message) {
        lastErrorMessage = message;
    }

    @Override
    public void showMessage(String message) {
        lastMessage = message;
    }

    @Override
    public void showGoodbye() {
        isGoodbyeShown = true;
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        lastAddedTask = task;
        lastAddedTaskCount = taskCount;
    }

    @Override
    public void showTaskRemoved(Task task, int taskCount) {
        lastRemovedTask = task;
        lastRemovedTaskCount = taskCount;
    }

    @Override
    public void showMarkedTask(int taskNumber, boolean isDone) {
        lastMarkedTaskNumber = taskNumber;
        isLastMarkedTaskDone = isDone;
    }

    @Override
    public void showTaskList(ArrayList<Task> tasks) {
        lastShownTaskList = tasks;
    }

    @Override
    public void showMatchingTasks(ArrayList<Task> tasks) {
        lastShownMatchingTasks = tasks;
    }

    @Override
    public void showLine() {
        // No-op: keep test output clean.
    }

    @Override
    public void showWelcome() {
        // No-op.
    }

    @Override
    public void showEmptyInputError() {
        // No-op.
    }
}
