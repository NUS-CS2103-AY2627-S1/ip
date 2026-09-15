package sal;

import java.util.ArrayList;

/**
 * Represents a task with a description, a done/not-done status, and optional tags.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected ArrayList<String> tags;

    /**
     * Creates a new unfinished task with the given description and no tags.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns {@code X} if the task is done, or a space if it is not.
     *
     * @return Status icon for display.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Adds a tag to this task. Tags are stored without a leading {@code #}.
     *
     * @param tagName Tag name, already normalised (no leading {@code #}).
     * @throws SalException If the tag name is invalid or this task already has it.
     */
    public void addTag(String tagName) throws SalException {
        if (!isValidTagName(tagName)) {
            throw new SalException("Correct format: tag <task number> <tag>");
        }
        if (tags.contains(tagName)) {
            throw new SalException("This task already has the tag #" + tagName);
        }
        tags.add(tagName);
    }

    /**
     * Returns the tags on this task, in the order they were added.
     * Callers should treat the returned list as read-only.
     *
     * @return Tag names without a leading {@code #}.
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Returns whether {@code tagName} is a valid stored tag: letters, digits, hyphens, or underscores.
     *
     * @param tagName Candidate tag name, without a leading {@code #}.
     * @return {@code true} if the name can be stored as a tag.
     */
    public static boolean isValidTagName(String tagName) {
        return tagName != null && tagName.matches("[A-Za-z0-9_-]+");
    }

    /**
     * Appends this task's tags (shown as {@code #name}) to the given display text.
     *
     * @param base Task text without tags.
     * @return {@code base} with tags appended, or {@code base} unchanged if there are none.
     */
    protected String withTags(String base) {
        if (tags.isEmpty()) {
            return base;
        }
        StringBuilder result = new StringBuilder(base);
        for (String tag : tags) {
            result.append(" #").append(tag);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
