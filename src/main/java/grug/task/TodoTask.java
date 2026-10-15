package grug.task;

import java.time.LocalDate;

/**
 * Represents a Task without any date/time attached to it
 * e.g. _visit new theme park_.
 */
public class TodoTask extends Task {

    /**
     * Creates a new {@link TodoTask} that is incomplete.
     *
     * @param details The details of the task.
     */
    public TodoTask(String details) {
        super(details);
    }

    @Override
    public boolean doesOccurOn(LocalDate date) {
        return false;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
