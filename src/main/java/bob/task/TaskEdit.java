package bob.task;

/**
 * Represents the changes one {@code edit} asks for: a new description, a new due
 * date, a new start or a new end, any of which may be left out.
 *
 * <p>The four are held together as one value rather than passed around as four
 * separate arguments, because three of them are dates: a method taking them side by
 * side would compile just as happily with the start and the end the wrong way round.
 *
 * <p>A detail that is not being changed is {@code null}. {@link java.util.Optional}
 * would say the same without {@code null}, but it is meant for return values rather
 * than for fields, so the plainer field is used and the meaning is written down here,
 * as {@link TaskDateTime} does for a date given without a time.
 *
 * <p>A record, because this is a plain carrier of values with nothing of its own to
 * protect, like {@link bob.storage.Storage.LoadResult Storage.LoadResult}: the compiler
 * writes its constructor and accessors.
 *
 * @param description the new description, or {@code null} to keep the old one.
 * @param by          the new due date, or {@code null} to keep the old one.
 * @param from        the new start, or {@code null} to keep the old one.
 * @param to          the new end, or {@code null} to keep the old one.
 */
public record TaskEdit(String description, TaskDateTime by, TaskDateTime from, TaskDateTime to) {

    /**
     * Creates the changes for one edit, which must change something.
     *
     * <p>{@link bob.parser.Parser Parser} refuses an edit that names no change, so an
     * empty one reaching here would be a mistake in the program rather than in what
     * the user typed.
     */
    public TaskEdit {
        assert description != null || by != null || from != null || to != null
                : "An edit that changes nothing";
    }

    /** Returns whether this edit gives a new due date. */
    public boolean hasDueDate() {
        return by != null;
    }

    /** Returns whether this edit gives a new start, a new end, or both. */
    public boolean hasStartOrEnd() {
        return from != null || to != null;
    }
}
