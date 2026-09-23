package sallman.task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A single task tracked by the chatbot, together with whether it is done.
 */
public class Task {

    /**
     * What separates the fields of one saved line. Defined here because the
     * task classes are what write those lines; {@link sallman.Storage} reads
     * the same constant back, so the two cannot drift apart.
     */
    public static final String SEPARATOR = " | ";

    /**
     * Marks the tag field at the end of a saved line. Without it, a line's
     * last field could not be told apart from a date, since a task may have
     * tags, dates, both or neither.
     */
    public static final String TAG_FIELD_PREFIX = "#";

    /** What separates one tag from the next inside the saved tag field. */
    private static final String TAG_SEPARATOR = ",";

    /**
     * What a tag may be made of. Whitespace is excluded because typed tags are
     * separated by it, and punctuation because saved tags share a line with the
     * field separator and are separated from each other by commas.
     */
    private static final Pattern TAG_PATTERN = Pattern.compile("[A-Za-z0-9_\\-]+");

    /** Description of what the task involves, as typed by the user. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Labels the user has attached to this task, kept in the order they were
     * added so the task shows them the same way every time. Stored in lower
     * case, so that {@code #Fun} and {@code #fun} are one tag rather than two.
     */
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Creates a task that is initially not done.
     *
     * @param description what the task involves
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns what this task involves, as the user described it.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true once the task has been marked done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date this task should be ordered by.
     * <p>
     * A plain task carries no date, so it has nothing to be ordered by.
     * Subclasses that hold dates return the one that decides when the task
     * comes up: a deadline is due on one date, an event starts on one.
     *
     * @return the date to sort by, empty for a task that carries no date
     */
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }

    /**
     * Returns the icon shown inside the status box of this task.
     *
     * @return {@code "X"} if the task is done, a space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns an independent copy of this task.
     * <p>
     * Undo remembers past states of the list, and a task is mutable, so a
     * remembered state has to hold copies. Sharing the tasks would let a later
     * mark or tag change the very state that undo is meant to restore.
     *
     * @return a task equal to this one that can be changed without affecting it
     */
    public Task copy() {
        return copyInto(new Task(description));
    }

    /**
     * Copies the state every task has into a new task of the right type.
     * <p>
     * Subclasses pass a new instance carrying their own fields, which spares
     * each of them from having to know about the done flag and the tags.
     *
     * @param copy a new task holding this one's description and any dates
     * @return the same task, with the done status and tags filled in
     */
    protected Task copyInto(Task copy) {
        copy.isDone = this.isDone;
        copy.tags.addAll(this.tags);
        return copy;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     * <p>
     * The comparison ignores case, so searching for "book" also finds "Book".
     *
     * @param keyword the text being searched for
     * @return true if the description contains it
     */
    public boolean hasKeyword(String keyword) {
        String wanted = keyword.toLowerCase(Locale.ROOT);
        return description.toLowerCase(Locale.ROOT).contains(wanted)
                || tags.stream().anyMatch(tag -> tag.contains(wanted));
    }

    /**
     * Returns whether another task describes the same piece of work as this one.
     * <p>
     * The kind of task, the description (ignoring case) and any dates must all
     * match. Whether it is done and how it is tagged are left out: those are
     * things that happen to a task, not what the task is. Dates are compared in
     * their saved form, which every kind of task already produces, so no
     * subclass needs its own comparison.
     *
     * @param other the task to compare with
     * @return true if the two would be duplicates in one list
     */
    public boolean isSameTask(Task other) {
        return getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description)
                && dateFields().equals(other.dateFields());
    }

    /**
     * Returns whether text can be used as a tag.
     * <p>
     * Both typed tags and tags read back from the data file are checked here,
     * so a hand-edited file cannot give a task a tag that {@code untag} would
     * then refuse to remove.
     *
     * @param tag the text to check, without a leading {@code #}
     * @return true if it is made only of letters, digits, hyphens and underscores
     */
    public static boolean isValidTag(String tag) {
        return TAG_PATTERN.matcher(tag).matches();
    }

    /**
     * Attaches a tag to this task, ignoring case and doing nothing if it is
     * already there.
     *
     * @param tag the label to attach, without its leading {@code #}
     * @return true if the tag was not already on this task
     */
    public boolean addTag(String tag) {
        return tags.add(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Removes a tag from this task, ignoring case.
     *
     * @param tag the label to remove, without its leading {@code #}
     * @return true if the task had that tag
     */
    public boolean removeTag(String tag) {
        return tags.remove(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the tags on this task, in the order they were added.
     *
     * @return the tags, which cannot be changed through the returned set
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns the tags as they are shown after a task, e.g. {@code " #fun"}.
     *
     * @return the tags each prefixed with {@code #}, or an empty string
     */
    private String tagSuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + tags.stream()
                .map(tag -> TAG_FIELD_PREFIX + tag)
                .collect(Collectors.joining(" "));
    }

    /**
     * Returns the tags as the final field of a saved line.
     * <p>
     * An untagged task usually has no tag field. The exception is a line whose
     * last field would itself start with the tag prefix, as a todo described as
     * {@code buy milk | #urgent} would: read back, that part would be taken for
     * tags. An empty tag field is written after it instead, so the description
     * comes back exactly as it was typed.
     *
     * @return the separator and tag field, or an empty string when none is needed
     */
    private String tagField() {
        if (!tags.isEmpty()) {
            return SEPARATOR + TAG_FIELD_PREFIX + String.join(TAG_SEPARATOR, tags);
        }
        String fields = description + dateFields();
        int lastSeparator = fields.lastIndexOf(SEPARATOR);
        boolean isMistakableForTags = lastSeparator >= 0
                && fields.substring(lastSeparator + SEPARATOR.length()).trim().startsWith(TAG_FIELD_PREFIX);
        return isMistakableForTags ? SEPARATOR + TAG_FIELD_PREFIX : "";
    }

    /**
     * Returns the fields a subclass saves between the description and the
     * tags, each already prefixed with a separator.
     * <p>
     * Dates are written here rather than by an overridden
     * {@link #toFileFormat()}, so that the tag field stays last on the line
     * whichever kind of task is being saved.
     *
     * @return the extra fields, empty for a task that holds no dates
     */
    protected String dateFields() {
        return "";
    }

    /**
     * Returns what a subclass shows after the description, e.g. a due date.
     * <p>
     * Kept separate from {@link #toString()} for the same reason as
     * {@link #dateFields()}: it lets the tags stay at the end of the line.
     *
     * @return the extra detail, empty for a task that holds no dates
     */
    protected String details() {
        return "";
    }

    /**
     * Returns whether this task falls on the given date.
     * <p>
     * A plain task carries no date, so it never does. Subclasses that hold
     * dates override this.
     *
     * @param date the date being asked about
     * @return true if this task is scheduled on that date
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task as one line of the data file, without a type marker.
     * Subclasses prepend their marker and append any dates they hold.
     *
     * @return the done flag and description, separated by {@code " | "}
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + SEPARATOR + description + dateFields() + tagField();
    }

    /**
     * Returns this task as it should appear to the user, e.g. {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + details() + tagSuffix();
    }
}
