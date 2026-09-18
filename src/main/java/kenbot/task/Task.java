package kenbot.task;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a task, whether it has been completed, and any tags on it.
 *
 * <p>Specific kinds of tasks extend this class to add their own details. They do
 * so by answering {@link #getTypeCode()}, {@link #getDisplayDetails()} and
 * {@link #getStorableDetails()} rather than by wrapping {@link #toString()}, so
 * that this class can put the tags at the end of the line, after whatever the
 * subclass contributed.</p>
 */
public class Task {
    private final String description;

    /**
     * The tags on this task.
     *
     * <p>A {@link Set} so that adding the same tag twice changes nothing, and a
     * {@link LinkedHashSet} specifically so the tags are shown in the order they
     * were added rather than in some order that shifts between runs.</p>
     */
    private final Set<Tag> tags = new LinkedHashSet<>();

    private boolean isDone;

    /**
     * Creates an incomplete, untagged task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the description supplied when this task was created.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status character used when displaying this task.
     *
     * @return {@code "X"} if this task is done, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Attaches a tag to this task.
     *
     * @param tag the tag to attach
     * @return true if the tag was added, false if the task already had it
     */
    public boolean addTag(Tag tag) {
        return tags.add(tag);
    }

    /**
     * Removes a tag from this task.
     *
     * @param tag the tag to remove
     * @return true if the tag was removed, false if the task did not have it
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    /**
     * Returns this task's tags for reading, without exposing the real set.
     *
     * <p>Guarded the same way {@code TaskList.getTasks()} is: a caller cannot add
     * or remove a tag behind this class's back.</p>
     *
     * @return the tags, in the order they were added
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns whether this is the same task as another.
     *
     * <p>Compared on the kind of task, its description and its dates. Whether it
     * is done is left out, because ticking something off does not make it a
     * different task, and so are the tags, because labelling one copy would
     * otherwise let an identical second copy in.</p>
     *
     * <p>Case matters: "read book" and "Read book" are treated as different, so
     * a deliberate second task is never silently refused.</p>
     *
     * @param other the task to compare with
     * @return true if the two describe the same thing
     */
    public boolean isSameTask(Task other) {
        return getTypeCode().equals(other.getTypeCode())
                && description.equals(other.description)
                && getStorableDetails().equals(other.getStorableDetails());
    }

    /**
     * Returns the letter this kind of task is saved under.
     *
     * @return the save-file type code, which each kind of task overrides
     */
    protected String getTypeCode() {
        return "?";
    }

    /**
     * Returns what this kind of task adds to the line shown on screen, such as
     * a deadline's {@code (by: ...)}.
     *
     * @return the extra text, already spaced, or an empty string if there is none
     */
    protected String getDisplayDetails() {
        return "";
    }

    /**
     * Returns the save-file fields this kind of task adds after its description,
     * already separated by {@code |} if there is more than one.
     *
     * @return the extra fields, or an empty string if there are none
     */
    protected String getStorableDetails() {
        return "";
    }

    /**
     * Returns this task as one line of text for the save file.
     *
     * <p>Kept separate from {@link #toString()} on purpose: that method is for a
     * person reading the console, while this one is for the program reading the
     * file back later. It therefore uses plain fields separated by {@code |}
     * instead of brackets and labels, and writes the done state as {@code 1} or
     * {@code 0} rather than the {@code X} shown on screen.</p>
     *
     * <p>The tags are written last, and only when there are any, so a task
     * without tags produces exactly the line it produced before tags existed and
     * a save file written by an older version still reads back.</p>
     *
     * @return the whole save-file line for this task
     */
    public String toStorable() {
        StringBuilder line = new StringBuilder(getTypeCode())
                .append(" | ").append(isDone ? "1" : "0")
                .append(" | ").append(description);

        String details = getStorableDetails();
        if (!details.isEmpty()) {
            line.append(" | ").append(details);
        }
        if (!tags.isEmpty()) {
            line.append(" | ").append(joinTags(Tag::getName));
        }
        return line.toString();
    }

    /**
     * Returns this task as it should be shown on screen.
     *
     * @return the type and done markers, the description, this kind of task's
     *         own details, and any tags
     */
    @Override
    public String toString() {
        String shown = "[" + getTypeCode() + "][" + getStatusIcon() + "] "
                + description + getDisplayDetails();
        return tags.isEmpty() ? shown : shown + " " + joinTags(Tag::toString);
    }

    /**
     * Joins the tags into one space-separated piece of text.
     *
     * @param form how to write a single tag: with its {@code #} for the screen,
     *             without it for the save file
     * @return the tags as text
     */
    private String joinTags(Function<Tag, String> form) {
        return tags.stream().map(form).collect(Collectors.joining(" "));
    }
}
