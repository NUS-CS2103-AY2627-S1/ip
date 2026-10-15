package gongrilla.task;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import lombok.Getter;

/**
 * Represents the shared description and completion state of a task.
 */
public class Task {
    @Getter
    private final String name;
    private CompletionStatus completionStatus;

    private enum CompletionStatus {
        NOT_DONE("[ ]"),
        DONE("[X]");

        private final String marker;

        CompletionStatus(String marker) {
            this.marker = marker;
        }

        String getMarker() {
            return marker;
        }
    }

    /**
     * Creates an incomplete task with the given description.
     *
     * @param name description of the task.
     */
    public Task(String name) {
        this.name = Objects.requireNonNull(name, "Task description cannot be null.");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }
        this.completionStatus = CompletionStatus.NOT_DONE;
    }

    /**
     * Returns the marker used to display the task's completion state.
     *
     * @return {@code [X]} when complete, or {@code [ ]} otherwise.
     */
    public String getIsDoneStatus() {
        return completionStatus.getMarker();
    }

    /** Marks this task as completed. */
    public void markDone() {
        this.completionStatus = CompletionStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void unmarkDone() {
        this.completionStatus = CompletionStatus.NOT_DONE;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return completionStatus == CompletionStatus.DONE;
    }

    /**
     * Returns this task's category for command confirmations.
     *
     * @return todo for a basic task without a scheduled date.
     */
    public TaskType getType() {
        return TaskType.TODO;
    }

    /**
     * Converts the task's common fields to the format used in the data file.
     *
     * @return completion state and task description separated by pipes.
     */
    protected String commonDataFields() {
        int isDone = isDone() ? 1 : 0;
        return isDone + " | " + encodeField(name);
    }

    /**
     * Encodes text so separators and line breaks cannot corrupt the data file.
     *
     * @param value text to encode.
     * @return encoded text.
     */
    protected String encodeField(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Converts this task to one line suitable for saving to the data file.
     *
     * @return serialized task.
     */
    public String toDataString() {
        return "T2 | " + commonDataFields();
    }

    /**
     * Returns the task's display form, including its completion marker.
     *
     * @return user-facing task description.
     */
    @Override
    public String toString() {
        return getIsDoneStatus() + " " + this.name;
    }
}
