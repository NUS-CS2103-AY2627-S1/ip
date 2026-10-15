package ben;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base class for anything Ben is tracking: a description plus a
 * done/not-done status. Subclasses add their own extra fields (a
 * deadline's "by" date, an event's "from"/"to" times) and override
 * {@link #getTypeIcon()} to identify themselves in the list.
 * <p>
 * C-Tagging: a task may also carry free-form tags (e.g. {@code #urgent}),
 * shown at the end of its display line and persisted as a trailing field.
 * {@link #toString()} and {@link #serialize()} are template methods:
 * subclasses customise them via {@link #extraInfo()} and
 * {@link #extraFields()} instead of overriding them directly, which keeps
 * "where do tags go" (the very end) in one place regardless of task type.
 */
abstract class Task {
    private final String description;
    private boolean isDone;
    /** Insertion-ordered so tags display in the order the user added them; no duplicates. */
    private final Set<String> tags = new LinkedHashSet<>();

    Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    void markAsNotDone() {
        isDone = false;
    }

    /** Returns {@code "X"} if this task is done, or a single space otherwise. */
    String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns whether this task has been marked done. */
    boolean isDone() {
        return isDone;
    }

    /** Returns the task's description text (without any type or status markers). */
    String getDescription() {
        return description;
    }

    /** Adds a tag (without the leading '#') to this task; adding the same tag twice has no extra effect. */
    void addTag(String tag) {
        tags.add(tag);
    }

    /** Returns this task's tags (without '#'), in the order they were added. */
    Set<String> getTags() {
        return tags;
    }

    /**
     * Returns whether this task and {@code other} would look identical to
     * the user: same task type, same description (case-insensitive) and
     * the same subclass fields (e.g. the same deadline date, or the same
     * event from/to). Done-status and tags are ignored, since two tasks
     * differing only in those are not "the same task" typed in twice.
     * <p>
     * A-MoreErrorHandling: used to reject an exact-duplicate task instead
     * of silently accepting the same thing twice.
     */
    final boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass()
                && description.strip().equalsIgnoreCase(other.description.strip())
                && extraFields().equalsIgnoreCase(other.extraFields());
    }

    /** One-letter tag identifying the task type: "T", "D", or "E". */
    abstract String getTypeIcon();

    /**
     * Extra text a subclass wants shown after the description, e.g. a
     * deadline's {@code " (by: ...)"}. Empty for task types with none.
     */
    String extraInfo() {
        return "";
    }

    /**
     * Extra " | "-separated fields a subclass wants saved after the base
     * "type | done | description" fields, e.g. a deadline's date. Empty for
     * task types with none.
     */
    String extraFields() {
        return "";
    }

    /**
     * Renders this task as one line for the data file: the base fields,
     * then any subclass fields, then a trailing tags field if there are any
     * (comma-separated, e.g. {@code | urgent,errand}).
     */
    final String serialize() {
        String base = getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
        String tagsField = tags.isEmpty() ? "" : " | " + String.join(",", tags);
        return base + extraFields() + tagsField;
    }

    /**
     * Rebuilds a task from one line produced by {@link #serialize()}.
     *
     * @throws BenException if the line does not match any known format
     */
    static Task deserialize(String line) throws BenException {
        String[] parts = line.split(" \\| ");
        try {
            boolean isDone = parts[1].equals("1");
            Task task;
            switch (parts[0]) {
                case "T":
                    task = new Todo(parts[2]);
                    applyTagsIfPresent(task, parts, 3);
                    break;
                case "D":
                    task = new Deadline(parts[2], parts[3]);
                    applyTagsIfPresent(task, parts, 4);
                    break;
                case "E":
                    task = new Event(parts[2], parts[3], parts[4]);
                    applyTagsIfPresent(task, parts, 5);
                    break;
                default:
                    throw new BenException("Skipping unrecognised saved task: " + line);
            }
            // Every branch above either assigns task or throws; this just
            // documents that invariant so a future case added without an
            // assignment fails loudly instead of NPE-ing below.
            assert task != null : "task should have been assigned by the switch above";
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BenException("Skipping corrupted saved task: " + line);
        }
    }

    /**
     * Adds the tags saved at {@code parts[tagsIndex]} to {@code task}, if
     * that field is present. Older save files (from before C-Tagging) have
     * no such field, so its absence is not an error.
     */
    private static void applyTagsIfPresent(Task task, String[] parts, int tagsIndex) {
        if (parts.length > tagsIndex && !parts[tagsIndex].isBlank()) {
            for (String tag : parts[tagsIndex].split(",")) {
                task.addTag(tag);
            }
        }
    }

    @Override
    public final String toString() {
        String tagsSuffix = tags.isEmpty() ? "" : " #" + String.join(" #", tags);
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description + extraInfo() + tagsSuffix;
    }
}
