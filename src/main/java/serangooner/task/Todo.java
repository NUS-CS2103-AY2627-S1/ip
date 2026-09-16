package serangooner.task;

import java.util.Optional;

/**
 * Represents a task that has no associated date or time.
 */
public class Todo extends Task {
    /** Code identifying a todo in the save file. */
    public static final String SAVE_CODE = "T";
    private static final int SAVE_FIELD_COUNT = 3;

    /**
     * Constructs a todo with the given description.
     *
     * @param description Text describing what the task involves.
     * @throws serangooner.SerangoonerException If the description carries the save delimiter.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo encoded by the given fields of a saved line.
     *
     * @param fields Fields that one line of the save file was split into.
     * @return Todo the line describes, or nothing if it cannot be read.
     */
    public static Optional<Task> parseSaveFields(String[] fields) {
        return buildFromSaveFields(fields, SAVE_FIELD_COUNT,
                validFields -> new Todo(validFields[SAVE_INDEX_DESCRIPTION]));
    }

    @Override
    public String getTypeName() {
        return "todo";
    }

    @Override
    public String toSaveFormat() {
        return SAVE_CODE + SAVE_DELIMITER + super.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
