package clsl.parser;

import java.time.LocalDate;

/**
 * Stores the information extracted from one user command.
 */
public class ParsedCommand {
    /** The commands supported by the application. */
    public enum Type {
        LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, ON, FIND, BYE
    }

    private final Type type;
    private final String description;
    private final int taskIndex;
    private final LocalDate firstDate;
    private final LocalDate secondDate;

    private ParsedCommand(Type type, String description, int taskIndex, LocalDate firstDate,
            LocalDate secondDate) {
        this.type = type;
        this.description = description;
        this.taskIndex = taskIndex;
        this.firstDate = firstDate;
        this.secondDate = secondDate;
    }

    /**
     * Creates a command that requires no additional information.
     *
     * @param type Type of command.
     * @return A command with no additional information.
     */
    public static ParsedCommand of(Type type) {
        return new ParsedCommand(type, null, -1, null, null);
    }

    /**
     * Creates a command that contains a task-list index.
     *
     * @param type Type of command.
     * @param taskIndex Zero-based index of the task.
     * @return A command containing the task-list index.
     */
    public static ParsedCommand withTaskIndex(Type type, int taskIndex) {
        return new ParsedCommand(type, null, taskIndex, null, null);
    }

    /**
     * Creates a command that contains a task description.
     *
     * @param type Type of command.
     * @param description Description of the task.
     * @return A command containing the task description.
     */
    public static ParsedCommand withDescription(Type type, String description) {
        return new ParsedCommand(type, description, -1, null, null);
    }

    /**
     * Creates a command that contains a description and one date.
     *
     * @param type Type of command.
     * @param description Description of the task.
     * @param date Date associated with the command.
     * @return A command containing the description and date.
     */
    public static ParsedCommand withDescriptionAndDate(Type type, String description, LocalDate date) {
        return new ParsedCommand(type, description, -1, date, null);
    }

    /**
     * Creates an event command that contains a description, start date, and end date.
     *
     * @param description Description of the event.
     * @param start Start date of the event.
     * @param end End date of the event.
     * @return An event command containing all event details.
     */
    public static ParsedCommand withEventDetails(String description, LocalDate start, LocalDate end) {
        return new ParsedCommand(Type.EVENT, description, -1, start, end);
    }

    /** Returns the kind of command entered by the user. */
    public Type getType() {
        return type;
    }

    /** Returns the task description, when the command has one. */
    public String getDescription() {
        return description;
    }

    /** Returns the zero-based task-list index, when the command has one. */
    public int getTaskIndex() {
        return taskIndex;
    }

    /** Returns the command's date, or an event's start date. */
    public LocalDate getFirstDate() {
        return firstDate;
    }

    /** Returns an event's end date. */
    public LocalDate getSecondDate() {
        return secondDate;
    }
}
