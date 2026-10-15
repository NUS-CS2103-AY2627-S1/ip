package bern.task;

import java.util.ArrayList;

import bern.datetime.DateTime;

/**
 * Represents a named task that can be marked as complete or incomplete.
 */
public abstract class Task {
    private boolean done;
    private String name;

    /**
     * Creates an incomplete task with the given name.
     *
     * @param name The name of the task.
     */
    Task(String name) {
        this.name = name;
        this.done = false;
    }

    /**
     * Returns the task name.
     *
     * @return The task name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the task is complete.
     *
     * @return {@code true} if the task is complete; {@code false} otherwise.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Sets whether the task is complete.
     *
     * @param isDone {@code true} to mark the task complete; {@code false} to mark it incomplete.
     */
    public void setDone(boolean isDone) {
        done = isDone;
    }

    /**
     * Returns the task type, completion marker, and name for display.
     *
     * @return The task's display string; [X] means done and [ ] means incomplete.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s",
                getSymbol(),
                done ? "X" : " ",
                name
        );
    }

    /**
     * Returns the task fields in save-file order: task symbol, completion flag (1 or 0), and task name.
     * Subclasses append their additional fields to this list.
     *
     * @return The task fields in save-file order.
     */
    public ArrayList<String> toDataList() {
        ArrayList<String> dataList = new ArrayList<>();
        dataList.add(getSymbol());
        dataList.add(isDone() ? "1" : "0");
        dataList.add(getName());
        return dataList;
    }

    /**
     * Returns the one-letter task type symbol used for display and storage.
     *
     * @return The task type symbol.
     */
    protected abstract String getSymbol();

    /**
     * Returns whether the task is associated with the given calendar date, ignoring the supplied time.
     * Tasks without an associated date return {@code false}.
     *
     * @param dateTime The date to check, with an optional time that is ignored.
     * @return {@code true} if the task belongs in that day's schedule; {@code false} otherwise.
     */
    protected abstract boolean isOnDay(DateTime dateTime);
}
