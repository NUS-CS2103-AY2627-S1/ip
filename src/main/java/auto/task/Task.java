package auto.task;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;

/** Represents a task with a description and completion status. */
public class Task {
    private final String name;
    private boolean isCompleted = false;

    /** Creates an incomplete task with the specified description. */
    public Task(String name) {
        this.name = name;
    }

    /** Marks this task as complete. */
    public void mark() {
        this.isCompleted = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        this.isCompleted = false;
    }

    /** Returns whether this task has been completed. */
    public boolean isCompleted() {
        return isCompleted;
    }

    /** Returns whether this task is scheduled on the given date. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the keyword, ignoring case.
     */
    public boolean matches(String keyword) {
        return name.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    protected String getName() {
        return name;
    }

    /** Encodes text so storage separators in user input remain unambiguous. */
    protected static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns this task in the durable storage format. */
    public String toDataString() {
        return String.format("T | %d | %s", isCompleted ? 1 : 0, encode(name));
    }

    /** Returns the task description prefixed by its completion-status marker. */
    @Override
    public String toString() {
        return String.format("[%s] %s",
                this.isCompleted ? "X" : " ",
                this.name);
    }
}
