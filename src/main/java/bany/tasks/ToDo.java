package bany.tasks;

import java.util.List;

/** Represents a task without a deadline or event time range. */
public class ToDo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description task description.
     * @param id task ID.
     */
    public ToDo(String description, int id) {
        super(id, description, List.of());
    }

    /**
     * Returns the full task type name.
     *
     * @return {@code TODO}.
     */
    @Override
    public String getType() {
        return TaskType.TODO.getType();
    }

    /**
     * Returns the abbreviated task type marker.
     *
     * @return {@code T}.
     */
    @Override
    public String getTypeShort() {
        return TaskType.TODO.getTypeShort();
    }

    @Override
    public List<String> getCriticalTags() {
        return List.of();
    }

    /**
     * Returns the date information appended to this task's display text.
     *
     * @return an empty string because a to-do has no date-time.
     */
    public String getDateString() {
        return "";
    }
}
