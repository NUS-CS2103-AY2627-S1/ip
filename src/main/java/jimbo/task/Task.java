package jimbo.task;

import jimbo.JimboException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The base abstract task class.
 */
public abstract class Task {
    protected static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HHmm");
    protected static final DateTimeFormatter FRIENDLY_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd yyyy hh:mma");
    protected static final String INVALID_DATE_TIME_MESSAGE = "invalid date/time, must be in format yyyy-MM-dd HHmm"
            + "\n" + "example: 2026-01-01 1300 for Jan 1 2026 01:00pm";


    private String data;
    private boolean isDone;
    private List<String> tags = new ArrayList<>();

    public Task(String data) {
        this.data = data;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Unmarks the task as done.
     */
    public void unmark() {
        isDone = false;
    }

    private String formatCheckmark() {
        if (isDone) {
            return "[X]";
        } else {
            return "[ ]";
        }
    }

    protected String formatTags() {
        if (tags.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(String.join(", ", tags));
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String toString() {
        return formatCheckmark() + " " + data;
    }

    protected String serialiseTags() {
        return String.join(",", tags);
    }

    /**
     * Serialises the task into a format suitable for storing in a file.
     *
     * @return The serialised task as a string.
     */
    public String serialise() {
        if (isDone) {
            return 1 + "|" + data;
        }
        return 0 + "|" + data;
    }

    protected static List<String> deserialiseTags(String data) {
        if (data.equals("")) {
            return new ArrayList<>();
        }
        String[] tagsArray = data.split(",");
        return Arrays.stream(tagsArray).toList();
    }

    /**
     * Deserialises the task stored in a file back into a Task object.
     *
     * @param data The serialised task data
     * @return The task.
     * @throws JimboException if the data is invalid.
     */
    public static Task deserialise(String data) throws JimboException {
        String[] params = data.split("\\|", -1);
        Task task;
        try {
            switch (params[0]) {
                case "T":
                    task = new Todo(params[2]);
                    task.addTags(deserialiseTags(params[3]));
                    break;
                case "D":
                    task = new Deadline(params[2], params[3]);
                    task.addTags(deserialiseTags(params[4]));
                    break;
                case "E":
                    task = new Event(params[2], params[3], params[4]);
                    task.addTags(deserialiseTags(params[5]));
                    break;
                default:
                    throw new JimboException("this task type seems invalid");
            }
        } catch (Exception e) {
            throw new JimboException("error while loading task, you might have to fix the save file");
        }
        if (params[1].equals("1")) {
            task.mark();
        }
        return task;
    }

    /**
     * Gets the task description.
     *
     * @return The task description.
     */
    public String getData() {
        return data;
    }

    /**
     * Adds a tag to the task.
     *
     * @param tag The tag to add.
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Adds a batch of tags to the task.
     *
     * @param tags The tags to add.
     */
    public void addTags(List<String> tags) {
        for (String tag : tags) {
            addTag(tag);
        }
    }

    /**
     * Removes a tag from a task.
     *
     * @param tag The tag to remove.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return Whether the task is marked as done.
     */
    public boolean isMarked() {
        return isDone;
    }
}
