package korvus.tasks;

import korvus.utils.DateTimeParser;

/**
 * Subclass of task, contains the bare minimum.
 * To-Do is used throughout this file to prevent false auto-linting of to-do.
 */
public class ToDo extends Task {

    /**
     * Returns an instance of the To-Do, a subclass of Task.
     *
     * @param isDone Boolean on whether task is completed.
     * @param name Name of To-Do.
     * @param dateTimeParser Parser for converting String to DateTime. Useless for To-Do
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public ToDo(boolean isDone, String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(isDone, name, dateTimeParser);
    }

    /**
     * Returns an instance of the To-Do, a subclass of Task.
     *
     * @param name Name of To-Do.
     * @param dateTimeParser Parser for converting String to DateTime. Useless for To-Do
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public ToDo(String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(name, dateTimeParser);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String writeToStore() {
        return String.format("%s%s%s",
                TaskType.TODO.flag,
                DATA_SEP,
                super.writeToStore());
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
