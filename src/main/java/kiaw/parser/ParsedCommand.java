package kiaw.parser;

import java.time.LocalDate;

/**
 * Represents a command after it has been interpreted by the parser.
 *
 * A parsed command stores the command type together with any information
 * needed to execute it, such as a description, task number, or dates.
 */
public class ParsedCommand {
    private final String commandType;
    private final String description;
    private final int taskNumber;
    private final LocalDate date;
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates a parsed command containing the supplied command information.
     *
     * @param commandType type of command
     * @param description task description, if applicable
     * @param taskNumber task number, if applicable
     * @param date deadline date, if applicable
     * @param from event start date, if applicable
     * @param to event end date, if applicable
     */
    public ParsedCommand(
            String commandType,
            String description,
            int taskNumber,
            LocalDate date,
            LocalDate from,
            LocalDate to
    ) {
        this.commandType = commandType;
        this.description = description;
        this.taskNumber = taskNumber;
        this.date = date;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the type of this command.
     *
     * @return command type
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns the task description associated with this command.
     *
     * @return task description, or null if not applicable
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task number associated with this command.
     *
     * @return task number
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns the deadline date associated with this command.
     *
     * @return deadline date, or null if not applicable
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the event start date associated with this command.
     *
     * @return event start date, or null if not applicable
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event end date associated with this command.
     *
     * @return event end date, or null if not applicable
     */
    public LocalDate getTo() {
        return to;
    }
}
