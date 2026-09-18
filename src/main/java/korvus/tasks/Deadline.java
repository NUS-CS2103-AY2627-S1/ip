package korvus.tasks;

import java.time.LocalDateTime;

import korvus.utils.DateTimeParser;

/**
 * Subclass of task, contains due date.
 */
public class Deadline extends Task {
    private LocalDateTime due;

    /**
     * Returns an instance of the Deadline, a subclass of Task.
     *
     * @param isDone Boolean on whether task is completed.
     * @param name Name of Deadline.
     * @param due Datetime of when the task is due, as a String.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Deadline(boolean isDone, String name, String due,
                    DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(isDone, name, dateTimeParser);
        this.due = dateTimeParser.parseDateString(due);
    }

    /**
     * Returns an instance of the Deadline, a subclass of Task.
     *
     * @param name Name of Deadline.
     * @param due Datetime of when the task is due, as a String.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Deadline(String name, String due, DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(name, dateTimeParser);
        this.due = dateTimeParser.parseDateString(due);
    }

    /**
     * Returns an instance of the Deadline, a subclass of Task.
     *
     * @param name Name of Deadline.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Deadline(String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        this(name, null, dateTimeParser);
    }

    /**
     * Returns the due datetime.
     *
     * @return Due datetime as a LocalDateTime object.
     */
    public LocalDateTime getDue() {
        return due;
    }

    /**
     * Edits the due date in this deadline.
     *
     * @param due Datetime of when the task is due.
     */
    public void setDueDate(LocalDateTime due) {
        this.due = due;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String writeToStore() {
        return String.format("%s%s%s%s%s",
                TaskType.DEADLINE.flag,
                DATA_SEP,
                super.writeToStore(),
                DATA_SEP,
                dateTimeParser.convertDateToString(this.due));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return String.format("[D]%s (Due: %s)", super.toString(), dateTimeParser.convertDateToString(this.due));
    }
}
