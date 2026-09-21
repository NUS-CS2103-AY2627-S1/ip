package nova;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task managed by Nova.
 */
public class Task {

    /**
     * Represents the supported task types.
     */
    public enum Type {
        TODO,
        DEADLINE,
        EVENT
    }

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma");

    private final Type type;
    private String description;
    private final String extraInfo;
    private final LocalDateTime deadline;

    private boolean isDone;

    private Task(
            Type type,
            String description,
            String extraInfo,
            LocalDateTime deadline,
            boolean isDone) {
        assert type != null : "Task type must be present";
        assert description != null : "Task description must be present";
        assert extraInfo != null : "Task extra information must be present";
        assert (type == Type.DEADLINE) == (deadline != null)
                : "Only deadline tasks must have a deadline";

        this.type = type;
        this.description = description;
        this.extraInfo = extraInfo;
        this.deadline = deadline;
        this.isDone = isDone;
    }

    /**
     * Creates a todo task.
     *
     * @param description Task description.
     * @return New todo task.
     */
    public static Task todo(String description) {
        return new Task(
                Type.TODO,
                validateDescription(description),
                "",
                null,
                false);
    }

    /**
     * Creates a deadline task.
     *
     * @param description Task description.
     * @param deadline Deadline date and time.
     * @return New deadline task.
     */
    public static Task deadline(
            String description, LocalDateTime deadline) {
        return new Task(
                Type.DEADLINE,
                validateDescription(description),
                "",
                validateDeadline(deadline),
                false);
    }

    /**
     * Creates an event task.
     *
     * @param description Event description.
     * @param from Event start.
     * @param to Event end.
     * @return New event task.
     */
    public static Task event(
            String description, String from, String to) {
        return new Task(
                Type.EVENT,
                validateDescription(description),
                "(from: " + validateEventTime(from)
                        + " to: " + validateEventTime(to) + ")",
                null,
                false);
    }

    /**
     * Reconstructs a task from its stored representation.
     *
     * @param line Stored task.
     * @return Reconstructed task.
     */
    public static Task fromStorageString(String line) {
        String[] parts = line.split("\t", -1);

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid stored task.");
        }

        Type type = Type.valueOf(parts[0]);
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new IllegalArgumentException("Invalid stored task status.");
        }

        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        if (type == Type.DEADLINE) {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);

            return new Task(
                    type,
                    validateDescription(description),
                    "",
                    deadline,
                    isDone);
        }

        if (type == Type.TODO && !parts[3].isEmpty()) {
            throw new IllegalArgumentException("Invalid stored todo.");
        }
        if (type == Type.EVENT && parts[3].isBlank()) {
            throw new IllegalArgumentException("Invalid stored event.");
        }

        return new Task(type, validateDescription(description), parts[3], null, isDone);
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Updates the description while preserving all other task details.
     *
     * @param description Replacement description.
     * @throws IllegalArgumentException If the description is blank or contains storage delimiters.
     */
    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide a description for the update.");
        }
        if (description.contains("\t")
                || description.contains("\n")
                || description.contains("\r")) {
            throw new IllegalArgumentException(
                    "The description cannot contain tabs or line breaks.");
        }
        this.description = description.strip();
    }

    /**
     * Returns whether the task description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return True if the task description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns the representation used to store this task.
     *
     * @return Stored representation.
     */
    public String toStorageString() {
        String storedExtraInfo;

        if (type == Type.DEADLINE) {
            storedExtraInfo = deadline.toString();
        } else {
            storedExtraInfo = extraInfo;
        }

        return type.name()
                + "\t" + (isDone ? "1" : "0")
                + "\t" + description
                + "\t" + storedExtraInfo;
    }

    /**
     * Returns the representation shown to the user.
     *
     * @return Display representation.
     */
    public String toDisplayString() {
        String status = isDone ? "X" : " ";

        switch (type) {
            case TODO:
                return "[T][" + status + "] " + description;
            case DEADLINE:
                return "[D][" + status + "] "
                        + description
                        + " (by: "
                        + deadline.format(OUTPUT_DATE_FORMAT)
                        + ")";
            case EVENT:
                return "[E][" + status + "] "
                        + description
                        + " "
                        + extraInfo;
            default:
                return description;
        }
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Please provide a task description.");
        }
        if (description.contains("\t")
                || description.contains("\n")
                || description.contains("\r")) {
            throw new IllegalArgumentException(
                    "Descriptions cannot contain tabs or line breaks.");
        }
        return description.strip();
    }

    private static LocalDateTime validateDeadline(LocalDateTime deadline) {
        if (deadline == null) {
            throw new IllegalArgumentException("Please provide a deadline date and time.");
        }
        return deadline;
    }

    private static String validateEventTime(String eventTime) {
        if (eventTime == null || eventTime.isBlank()) {
            throw new IllegalArgumentException("Please provide both /from and /to.");
        }
        if (eventTime.contains("\t")
                || eventTime.contains("\n")
                || eventTime.contains("\r")) {
            throw new IllegalArgumentException("Event times must be on one line.");
        }
        return eventTime.strip();
    }
}
