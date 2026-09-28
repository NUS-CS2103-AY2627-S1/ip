package kotha.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import kotha.KothaException;

/** Represents a task that can be completed or left incomplete. */
public class Task {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String CONSTRAINT_COMMAND = "constraint";
    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_FROM_MARKER = " /from ";
    private static final String EVENT_TO_MARKER = " /to ";
    private static final String CONSTRAINT_AFTER_MARKER = " /after ";
    private static final String CONSTRAINT_AFTER_DATE_MARKER = " /afterdate ";

    private static final DateTimeFormatter DAY_MONTH_FORMAT =
            new java.time.format.DateTimeFormatterBuilder()
                    .appendPattern("d/M")
                    .parseDefaulting(java.time.temporal.ChronoField.YEAR, Year.now().getValue())
                    .toFormatter();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    protected String description;
    protected boolean isDone;

    /** Creates an incomplete task with the supplied description. */
    public Task(String description) {
        assert description != null : "A task must have a description";
        this.description = description;
        this.isDone = false;
    }

    /** Creates a to-do task from a user command. */
    public static Task createTodo(String command) throws KothaException {
        if (command == null || !command.startsWith(TODO_COMMAND)) {
            throw new KothaException("A description is required after 'todo'.");
        }
        String description = command.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new KothaException("A description is required after 'todo'.");
        }
        return new ToDo(description);
    }

    /** Creates a deadline task from a user command. */
    public static Task createDeadline(String command) throws KothaException {
        if (command == null) {
            throw new KothaException("A deadline requires a description and a '/by' date.");
        }
        int byIndex = command.indexOf(DEADLINE_MARKER);
        if (byIndex <= DEADLINE_COMMAND.length() || command.indexOf(DEADLINE_MARKER, byIndex + 1) >= 0) {
            throw new KothaException(
                    "A deadline requires a description and a '/by' date.");
        }
        String description = command.substring(DEADLINE_COMMAND.length(), byIndex).trim();
        String byText = command.substring(byIndex + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new KothaException(
                    "A deadline requires a description and a '/by' date.");
        }
        LocalDateTime deadline = parseDateTime(byText);
        assert !description.isEmpty() : "Validated deadline description must not be empty";
        assert deadline != null : "A parsed deadline must exist";
        return new Deadline(description, deadline);
    }

    /** Creates an event task from a user command. */
    public static Task createEvent(String command) throws KothaException {
        if (command == null) {
            throw new KothaException("An event requires a description, '/from', and '/to' time.");
        }
        int fromIndex = command.indexOf(EVENT_FROM_MARKER);
        int toIndex = command.indexOf(EVENT_TO_MARKER);
        if (fromIndex <= EVENT_COMMAND.length() || toIndex <= fromIndex
                || command.indexOf(EVENT_FROM_MARKER, fromIndex + 1) >= 0
                || command.indexOf(EVENT_TO_MARKER, toIndex + 1) >= 0) {
            throw new KothaException(
                    "An event requires a description, '/from', and '/to' time.");
        }
        String description = command.substring(EVENT_COMMAND.length(), fromIndex).trim();
        String fromText = command.substring(fromIndex + EVENT_FROM_MARKER.length(), toIndex).trim();
        String toText = command.substring(toIndex + EVENT_TO_MARKER.length()).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new KothaException(
                    "An event requires a description, '/from', and '/to' time.");
        }
        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);
        if (!from.isBefore(to)) {
            throw new KothaException("An event must end after it starts.");
        }
        assert !description.isEmpty() : "Validated event description must not be empty";
        assert from != null && to != null : "A parsed event must have both endpoints";
        return new Event(description, from, to);
    }

    /** Creates a constraint task from a user command. */
    public static Task createConstraint(String command) throws KothaException {
        if (command == null) {
            throw new KothaException(
                    "A constraint requires a description and a '/after' or '/afterdate' trigger.");
        }
        boolean isDateTrigger = command.contains(CONSTRAINT_AFTER_DATE_MARKER);
        String marker = isDateTrigger ? CONSTRAINT_AFTER_DATE_MARKER : CONSTRAINT_AFTER_MARKER;
        int afterIndex = command.indexOf(marker);
        if (afterIndex <= CONSTRAINT_COMMAND.length()) {
            throw new KothaException(
                    "A constraint requires a description and a '/after' or '/afterdate' trigger.");
        }
        String description = command.substring(CONSTRAINT_COMMAND.length(), afterIndex).trim();
        String after = command.substring(afterIndex + marker.length()).trim();
        if (description.isEmpty() || after.isEmpty()) {
            throw new KothaException(
                    "A constraint requires a description and a '/after' or '/afterdate' trigger.");
        }
        if (isDateTrigger) {
            return new Constraint(description, parseDateTime(after));
        }
        return new Constraint(description, after);
    }

    /** Returns the status icon for this task. */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /** Marks the task as <b>done</b> and prints the updated status of that task. */
    public void markAsDone() {
        isDone = true;
        System.out.println("I have marked the following task as done:");
        System.out.println(this.toString());
    }

    /** Marks the task as <b>not done</b> and prints the updated status of that task. */
    public void markAsNotDone() {
        isDone = false;
        System.out.println("I have marked the following task as not done:");
        System.out.println(this.toString());
    }

    /** Returns the date and time represented by supported user input. */
    private static LocalDateTime parseDateTime(String dateTimeText) throws KothaException {
        String[] parts = dateTimeText.split("\\s+");
        try {
            if (parts.length == 1) {
                return LocalDateTime.of(parseDate(parts[0]), LocalTime.of(23, 59));
            }
            if (parts.length == 2) {
                return LocalDateTime.of(parseDate(parts[0]), LocalTime.parse(parts[1], TIME_FORMAT));
            }
        } catch (DateTimeParseException | ArithmeticException ignored) {
            // Invalid input.
        }
        throw new KothaException(
                "Use d/M, d/M/yy, or d/M/yyyy, optionally followed by HHmm.");
    }

    /** Returns the date represented by supported user input. */
    private static LocalDate parseDate(String dateText) {
        LocalDate date = tryParseDate(dateText, DateTimeFormatter.ofPattern("d/M/uuuu"));
        if (date != null) {
            return date;
        }
        date = tryParseDate(dateText, DateTimeFormatter.ofPattern("d/M/uu"));
        if (date != null) {
            return date;
        }
        return LocalDate.parse(dateText, DAY_MONTH_FORMAT);
    }

    /** Attempts to parse a date with the supplied format, returning null on mismatch. */
    private static LocalDate tryParseDate(String dateText, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(dateText, formatter);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    /** Returns the task description for persistent storage. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been marked as complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Restores the completion status without printing a user message. */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }

    /** Prints a confirmation that this task was added. */
    public void printAddText() {
        System.out.println("____________________________________________________________");
        System.out.println("Got it master I have added the following task: " + this.toString());

    }

    /** Prints a confirmation that this task was removed. */
    public void printRemoveText() {
        System.out.println("____________________________________________________________");
        System.out.println("Got it master I have removed the following task: " + this.toString());
    }
}
