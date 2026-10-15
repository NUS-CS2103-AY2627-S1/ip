package bern.task;

import bern.datetime.DateTime;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given name.
     *
     * @param name The name of the task.
     */
    Todo(String name) {
        super(name);
    }

    /**
     * Returns a symbol that represents the Todo task type.
     *
     * @return The symbol representing the task type.
     */
    @Override
    protected String getSymbol() {
        return "T";
    }

    @Override
    protected boolean isOnDay(DateTime dateTime) {
        return false;
    }
}
