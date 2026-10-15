package nimbus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import nimbus.exception.NimbusException;

/**
 * Represents a task in the Nimbus application.
 * It can be of type TODO, DEADLINE, or EVENT.
 */
public abstract class Task {

    protected static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private String name;
    private boolean isDone;
    private TaskType type;

    /**
     * Constructs a Task object with a name and type.
     *
     * @param name The name of the task.
     * @param type The type of the task.
     */
    public Task(String name, TaskType type) {
        this.name = name;
        this.type = type;
        this.isDone = false;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return isDone;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Formats the task into a format suitable for file writing.
     *
     * @return The formatted file string.
     */
    public String toFileString() {
        return type.getType() + "|" + (isDone ? "1" : "0") + "|" + name;
    }

    @Override
    public String toString() {
        return "[" + type.getType() + "] " + (isDone ? "[X] " : "[ ] ") + name;
    }

    /**
     * Constructs a Task object from a formatted file string.
     *
     * @param fileString The formatted file string.
     * @return The constructed Task object.
     * @throws NimbusException If the file string is corrupted or cannot be parsed.
     */
    public static Task fromFileString(String fileString) throws NimbusException {
        try {
            String[] parts = fileString.split("\\|");
            Task task = createTaskFromParts(parts);
            applyDoneStatus(task, parts[1].equals("1"));
            return task;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new NimbusException("Unable to parse task from file. Recreating file...");
        }
    }

    private static Task createTaskFromParts(String[] parts) {
        TaskType type = TaskType.fromString(parts[0]);
        String name = parts[2];
        switch (type) {
            case TODO:
                return new Todo(name);
            case DEADLINE:
                String by = parts[3];
                return new Deadline(name, LocalDate.parse(by));
            case EVENT:
                String from = parts[3];
                String to = parts[4];
                return new Event(name, LocalDate.parse(from), LocalDate.parse(to));
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    private static void applyDoneStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        }
    }

    /**
     * Checks if this task is equal to another object.
     *
     * @param obj The object to compare with.
     * @return true if the other object is a Task with the same name and type, false
     *         otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Task)) {
            return false;
        }
        Task other = (Task) obj;
        return this.name.equals(other.name) && this.type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
