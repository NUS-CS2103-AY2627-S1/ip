package anniechat.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represents a task with a description and completion status. */
public abstract class Task {
    private static final Pattern TAG_PATTERN =
            Pattern.compile("(?<!\\S)#[A-Za-z0-9_-]+");

    /** Original command or description used to create this task. */
    protected String desc;

    /** Whether this task has been marked as completed. */
    protected boolean isDone;

    /** The optional tag associated with this task. */
    private final String tag;

    static int taskCount = 0;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param desc original command or description for this task.
     */
    public Task(String desc) {
        this.desc = desc;
        isDone = false;
        tag = extractTag(desc);
        taskCount++;
    }

    /**
     * Returns the total number of task objects created in this run.
     *
     * @return total number of task objects created.
     */
    public static int taskCount() {
        return taskCount;
    }

    /** Decreases the total task count after a task is deleted. */
    public static void removeTask() {
        taskCount--;
    }

    /** Marks this task as completed. */
    public void markDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if this task is completed.
     */
    public boolean checkStatus() {
        return isDone;
    }

    /**
     * Returns the total number of task objects created in this run.
     *
     * @return total number of task objects created.
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Returns the display description of this task.
     *
     * @return display description of this task.
     */
    public String getTaskDesc() {
        return desc;
    }

    /**
     * Returns the optional tag written in this task.
     *
     * @return the tag including its {@code #} prefix, or an empty string if
     *         this task has no tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Checks whether this task has the supplied tag.
     *
     * @param requestedTag tag to look for, including or excluding {@code #}.
     * @return true if the task has the requested tag, ignoring letter case.
     */
    public boolean hasTag(String requestedTag) {
        String normalizedTag = requestedTag.startsWith("#")
                ? requestedTag
                : "#" + requestedTag;
        return tag.equalsIgnoreCase(normalizedTag);
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code [x]} if completed, otherwise {@code [ ]}.
     */
    public String statusIcon() {
        return isDone ? "[x]" : "[ ]";
    }

    /**
     * Returns the icon representing this task type.
     *
     * @return one-letter icon for this task type.
     */
    public abstract String getTaskIcon();

    /**
     * Returns this task in the format used by storage.
     *
     * @return serialized representation of this task.
     */
    public abstract String toSaveFormat();

    /**
     * Extracts the one supported tag from the original task command.
     *
     * @param taskText original task command.
     * @return the first tag, or an empty string when none is present.
     * @throws IllegalArgumentException if more than one tag is present.
     */
    private static String extractTag(String taskText) {
        Matcher matcher = TAG_PATTERN.matcher(taskText);
        if (!matcher.find()) {
            return "";
        }

        String firstTag = matcher.group();
        if (matcher.find()) {
            throw new IllegalArgumentException("Only one tag is allowed per task.");
        }
        return firstTag;
    }
}
