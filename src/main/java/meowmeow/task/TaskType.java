package meowmeow.task;

/**
 * The kind of {@link Task} a user can add: a plain {@code Todo}, a
 * {@code Deadline} due by some point, or an {@code Event} spanning a time
 * range. Each constant carries the single-letter tag used when rendering a
 * task (e.g. "[T]"), so the tag lives in one place instead of being
 * hardcoded in every subclass's {@code toString()}.
 */
public enum TaskType {
    /** A plain task with no date or time. */
    TODO("T"),
    /** A task due by a single point in time. */
    DEADLINE("D"),
    /** A task spanning a start and end point. */
    EVENT("E");

    private final String tag;

    TaskType(String tag) {
        this.tag = tag;
    }

    /** Returns the single-letter tag used when rendering a task, e.g. {@code "T"}. */
    public String getTag() {
        return tag;
    }

    /**
     * Returns the type whose {@link #getTag() tag} is {@code tag}, or
     * {@code null} if no type uses that tag. This is the exact inverse of
     * {@link #getTag()}, so {@link meowmeow.storage.Storage Storage} reads a
     * saved line back with the same letter {@link Task#toFileString()} wrote,
     * without repeating the letters as its own literals.
     *
     * @param tag the single-letter tag read from a save-file line.
     * @return the matching type, or {@code null} if the tag is unrecognised.
     */
    public static TaskType fromTag(String tag) {
        for (TaskType type : values()) {
            if (type.tag.equals(tag)) {
                return type;
            }
        }
        return null;
    }
}
