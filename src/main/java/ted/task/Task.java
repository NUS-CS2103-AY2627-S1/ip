package ted.task;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A single task that Ted keeps track of.
 * Bundles a task's description with its done status, so that the two can no
 * longer drift apart the way two parallel arrays could.
 * <p>
 * This class is abstract because every task Ted stores is one of the specific
 * kinds ({@link Todo}, {@link Deadline}, {@link Event}); a plain "task" with no
 * kind is not something the user can create. Subclasses supply their own type
 * icon and add any extra detail to {@link #toString()}.
 */
public abstract class Task {
    /** Separator between fields in the save file. */
    public static final String SAVE_FIELD_SEPARATOR = " | ";

    /**
     * Format used to present dates and times to the user, shared by every kind
     * of task. English is fixed because the rest of Ted speaks English: left
     * to the computer's locale, a Chinese system would show the month and the
     * "PM" in Chinese in the middle of an English sentence, and even English
     * locales disagree on "PM" versus "pm".
     */
    protected static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a", Locale.ENGLISH);

    /** What the user wants to get done. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Labels the user has attached to this task, in the order they were added.
     * A set, so that attaching a tag the task already has changes nothing.
     */
    private final Set<Tag> tags = new LinkedHashSet<>();

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the user wants to get done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns what the user wants to get done.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon shown in place of a tick box.
     *
     * @return {@code "X"} if the task is done, a single space otherwise.
     */
    public String getStatusIcon() {
        // A cross for a finished task, a blank for one still to do.
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed, reversing {@link #markAsDone()}. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Attaches tags to this task. Tags it already has are left where they are,
     * so no tag is ever shown twice.
     *
     * @param newTags the tags to attach.
     */
    public void addTags(Collection<Tag> newTags) {
        tags.addAll(newTags);
    }

    /**
     * Detaches tags from this task. Tags it does not have are ignored.
     *
     * @param oldTags the tags to detach.
     */
    public void removeTags(Collection<Tag> oldTags) {
        tags.removeAll(oldTags);
    }

    /**
     * Returns whether this task has the given tag.
     *
     * @param tag the tag to look for.
     * @return {@code true} if the tag is attached to this task.
     */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * Returns whether this task has the same details as another, so that adding
     * it to a list that already holds the other would only clutter the list.
     * <p>
     * Case is ignored in the description, since "Read book" and "read book"
     * are the same job. The done status and tags are not compared: they change
     * over a task's life rather than describe what the task is. Subclasses
     * with dates extend this to compare them too.
     *
     * @param other the task to compare with.
     * @return {@code true} if both are the same kind of task with the same description.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass() && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns whether any of this task's dates falls at the given time of day,
     * whatever the date. A task with no dates, such as a todo, never does.
     *
     * @param time the time of day to look for, e.g. 16:20.
     * @return {@code true} if one of the task's date-times is at that time.
     */
    public boolean isAtTimeOfDay(LocalTime time) {
        return false;
    }

    /**
     * Converts this task into a single line of the save file format.
     * <p>
     * The line carries everything needed to rebuild this task: the type icon
     * ({@link #getTypeIcon()}), the done flag, any tags, the description, and
     * any type-specific detail. Todo uses the default implementation; subclasses
     * override it to pass their extra fields to {@link #toSaveLine(String...)}.
     *
     * @return one line of the save file, using {@code " | "} as the separator.
     */
    public String toSaveFormat() {
        return toSaveLine();
    }

    /**
     * Builds one save line from the fields every task shares and the extra
     * fields this kind of task adds.
     * <p>
     * The number of extra fields differs per task type (a todo has none, a
     * deadline one, an event two), so they are taken as varargs. Each caller
     * then names its own fields in order and this method alone deals with
     * escaping them and placing the separators.
     * <p>
     * Tags, if there are any, form one field straight after the done flag, e.g.
     * {@code T | 0 | #fun #school | read book}. A task without tags leaves the
     * field out, so its line is exactly what Ted wrote before tags existed.
     *
     * @param extraFields type-specific fields, in the order they are saved,
     *                    stored between the done flag and the description.
     * @return one line of the save file, using {@code " | "} as the separator.
     */
    protected String toSaveLine(String... extraFields) {
        StringBuilder line = new StringBuilder();
        line.append(getTypeIcon()).append(SAVE_FIELD_SEPARATOR)
                .append(isDone ? "1" : "0").append(SAVE_FIELD_SEPARATOR);
        if (!tags.isEmpty()) {
            // Tags need no escaping: after the # they are only letters and digits.
            line.append(formatTags()).append(SAVE_FIELD_SEPARATOR);
        }
        for (String field : extraFields) {
            line.append(encodeSaveField(field)).append(SAVE_FIELD_SEPARATOR);
        }
        // The description goes last, where it can safely contain separators.
        return line.append(encodeSaveField(description)).toString();
    }

    /**
     * Escapes characters that have a structural meaning in a save line.
     * Backslashes are escaped first so that decoding can distinguish a literal
     * backslash from one that protects a pipe character.
     *
     * @param field one task field to store.
     * @return the field with backslashes and pipe characters escaped.
     */
    protected static String encodeSaveField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the one-letter icon identifying this kind of task, e.g. {@code "T"}.
     *
     * @return the type icon shown before the status icon.
     */
    public abstract String getTypeIcon();

    /**
     * Returns this task as it should appear to the user, e.g. {@code [T][X] read book #fun}.
     * Overriding {@code toString} rather than writing a separate format method
     * lets a task be printed directly wherever it is needed. Subclasses that
     * carry extra detail append it to this result, so a deadline's due date
     * comes after its tags.
     */
    @Override
    public String toString() {
        String shown = "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
        return tags.isEmpty() ? shown : shown + " " + formatTags();
    }

    /**
     * Returns the tags the way they are both shown and saved, e.g. {@code #fun #school}.
     */
    private String formatTags() {
        return tags.stream()
                .map(Tag::toString)
                .collect(Collectors.joining(" "));
    }
}
