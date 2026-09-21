package peter.task;

import java.util.Arrays;
import java.util.Optional;

/**
 * The kinds of task the chatbot supports, each paired with the one-letter
 * icon that identifies it and the number of fields its data-file line holds.
 *
 * <p>The same letter is used both in the task list shown to the user and as
 * the first field of a line in the data file. Recording it here once, rather
 * than beside each reader of the data file, is what keeps the two uses in
 * step.
 */
public enum TaskType {
    /** A task with no date attached: type, status, and description. */
    TODO("T", 3),

    /** A task due by a particular date and time, which adds the due date. */
    DEADLINE("D", 4),

    /** A task running between two dates and times, which adds both ends. */
    EVENT("E", 5);

    private final String icon;
    private final int dataFieldCount;

    TaskType(String icon, int dataFieldCount) {
        this.icon = icon;
        this.dataFieldCount = dataFieldCount;
    }

    /**
     * Returns the one-letter icon for this task type.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns how many fields a line of this type holds in the data file,
     * counting the type letter and status flag.
     */
    public int getDataFieldCount() {
        return dataFieldCount;
    }

    /**
     * Returns the task type written with the given icon.
     *
     * @param icon one-letter icon read from a data-file line.
     * @return matching task type, or empty if no type uses that icon.
     */
    public static Optional<TaskType> fromIcon(String icon) {
        return Arrays.stream(values())
                .filter(taskType -> taskType.icon.equals(icon))
                .findFirst();
    }
}
