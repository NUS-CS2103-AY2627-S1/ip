package benjamin.task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A single thing the user wants to keep track of.
 *
 * <p>This base class holds what every task has, namely a description, whether
 * it is done, and any tags. Subclasses add whatever else their kind of task
 * needs, such as a due date, and decide how the task is shown and saved.
 */
public abstract class Task {
    /** What the user wants to be reminded of. */
    protected String description;

    /** Whether the user has finished this task. */
    protected boolean isDone;

    /**
     * Labels attached to this task.
     *
     * <p>A set, so tagging twice with the same label changes nothing, and one
     * that remembers insertion order, so tags are shown in the order they were
     * added rather than in an order the user did not choose.
     */
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Creates a task that is not done yet and carries no tags.
     *
     * @param description what the user typed as the task description.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the mark shown inside the status box.
     *
     * @return {@code X} when the task is done, or a space when it is not.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Records that this task has been completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Records that this task still has to be done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns true if this task falls on the given date. Plain tasks carry no
     * date, so they never do; dated task types override this.
     *
     * @param date the day being asked about.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns true if the other task describes the same thing as this one.
     *
     * <p>Only the type, description and dates are compared. Whether a task is
     * done, and what it is tagged with, are not part of what makes it the same
     * task, so marking one copy done would not stop it being a duplicate.
     *
     * @param other the task being compared against.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass() && identity().equals(other.identity());
    }

    /**
     * Returns the text that decides whether two tasks are the same thing.
     * Dated task types append their dates.
     */
    protected String identity() {
        return description.trim().toLowerCase();
    }

    /**
     * Attaches a label to this task.
     *
     * @param tag the label, with or without a leading hash.
     * @return true if it was added, or false if the task already had it.
     */
    public boolean addTag(String tag) {
        return tags.add(normalize(tag));
    }

    /**
     * Removes a label from this task.
     *
     * @param tag the label, with or without a leading hash.
     * @return true if it was removed, or false if the task did not have it.
     */
    public boolean removeTag(String tag) {
        return tags.remove(normalize(tag));
    }

    /**
     * Returns true if this task carries the given label.
     *
     * @param tag the label, with or without a leading hash.
     */
    public boolean hasTag(String tag) {
        return tags.contains(normalize(tag));
    }

    /** Returns the labels on this task, in the order they were added. */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if the description or any tag contains the given text.
     *
     * <p>Capitalization is ignored, so searching for {@code Book} finds a task
     * described as {@code read book}. A leading hash is ignored as well, so
     * both {@code find #fun} and {@code find fun} reach a task tagged
     * {@code #fun}.
     *
     * @param keyword the text being searched for.
     */
    public boolean hasKeyword(String keyword) {
        if (description.toLowerCase().contains(keyword.toLowerCase())) {
            return true;
        }

        String bareKeyword = normalize(keyword);

        return tags.stream().anyMatch(tag -> tag.contains(bareKeyword));
    }

    /**
     * Returns the line used to represent this task in the save file.
     * Subclasses prepend their own type letter and append {@link #getTagsField()}.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the task as shown to the user, such as {@code [X] read book}. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the tags as they are shown to the user, such as
     * {@code " #fun #urgent"}, or an empty string when there are none.
     *
     * <p>Subclasses append this last so that tags always follow any date.
     */
    protected String getTagSuffix() {
        if (tags.isEmpty()) {
            return "";
        }

        return " " + tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
    }

    /**
     * Returns the trailing save file field holding the tags, or an empty string
     * when there are none.
     *
     * <p>Leaving the field out entirely for an untagged task keeps its saved
     * record exactly as it was before tagging existed.
     */
    protected String getTagsField() {
        if (tags.isEmpty()) {
            return "";
        }

        return " | " + String.join(" ", tags);
    }

    /**
     * Returns the stored form of a tag, which drops any leading hash and folds
     * case, so that {@code #Fun}, {@code fun} and {@code #fun} are one tag.
     */
    private static String normalize(String tag) {
        String trimmed = tag.trim();
        String withoutHash = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;

        return withoutHash.toLowerCase();
    }
}
