package floppy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Converts raw user input into commands that the chatbot can execute.
 */
public class Parser {
    /**
     * Parses a complete line of user input.
     *
     * @param commandText Raw command entered by the user.
     * @return Structured command ready for execution.
     * @throws FloppyException If the command is unknown or malformed.
     */
    public Command parse(String commandText) throws FloppyException {
        String command = commandText.trim();
        if (command.equals("bye")) {
            return new Command(CommandType.BYE);
        }
        if (command.equals("list")) {
            return new Command(CommandType.LIST);
        }
        if (isCommand(command, "mark")) {
            return new Command(CommandType.MARK, parseTaskIndex(command, "mark"));
        }
        if (isCommand(command, "unmark")) {
            return new Command(CommandType.UNMARK, parseTaskIndex(command, "unmark"));
        }
        if (isCommand(command, "delete")) {
            return new Command(CommandType.DELETE, parseTaskIndex(command, "delete"));
        }
        if (command.equals("find")) {
            throw new FloppyException("Give me a keyword to find.");
        }
        if (command.startsWith("find ")) {
            String keyword = command.substring("find ".length()).trim();
            if (keyword.isEmpty()) {
                throw new FloppyException("Give me a keyword to find.");
            }
            return new Command(keyword);
        }
        return new Command(parseTask(command));
    }

    /**
     * Parses the positive task number in an indexed command.
     *
     * @param command Full command entered by the user.
     * @param commandWord Command word before the task number.
     * @return Zero-based task index.
     * @throws FloppyException If the task number is missing or invalid.
     */
    private int parseTaskIndex(String command, String commandWord) throws FloppyException {
        assert command.startsWith(commandWord) : "The command must begin with its command word";

        String argument = command.substring(commandWord.length()).trim();
        try {
            int taskIndex = Integer.parseInt(argument) - 1;
            if (taskIndex < 0) {
                throw new NumberFormatException();
            }
            assert taskIndex >= 0 : "A validated task index must be non-negative";
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new FloppyException("Give me a valid task number after " + commandWord + ".");
        }
    }

    /**
     * Parses a todo, deadline, event, or period command into a task.
     *
     * @param command Full command entered by the user.
     * @return Task represented by the command.
     * @throws FloppyException If the task command is unknown or incomplete.
     */
    private Task parseTask(String command) throws FloppyException {
        if (command.equals("todo")) {
            throw new FloppyException("The todo description cannot be empty.");
        }
        if (isCommand(command, "todo")) {
            String description = command.substring("todo ".length()).trim();
            if (description.isEmpty()) {
                throw new FloppyException("The todo description cannot be empty.");
            }
            validateDescription(description);
            return new Todo(description);
        }
        if (isCommand(command, "deadline")) {
            return parseDeadline(command);
        }
        if (isCommand(command, "event")) {
            return parseEvent(command);
        }
        if (isCommand(command, "period")) {
            return parsePeriodTask(command);
        }
        throw new FloppyException("I don't recognise that command.");
    }

    /**
     * Parses a deadline command containing a description and due date.
     *
     * @param command Full deadline command.
     * @return Deadline represented by the command.
     * @throws FloppyException If the description or due date is invalid.
     */
    private Deadline parseDeadline(String command) throws FloppyException {
        int byIndex = command.indexOf(" /by ");
        if (!command.startsWith("deadline ") || byIndex < 0) {
            throw new FloppyException("Use: deadline DESCRIPTION /by yyyy-MM-dd.");
        }
        if (command.indexOf(" /by ", byIndex + " /by ".length()) >= 0) {
            throw new FloppyException("A deadline accepts exactly one /by date.");
        }
        String description = command.substring("deadline ".length(), byIndex).trim();
        String dueDateText = command.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty() || dueDateText.isEmpty()) {
            throw new FloppyException("A deadline needs both a description and due date.");
        }
        validateDescription(description);
        return new Deadline(description, parseDate(dueDateText));
    }

    /**
     * Parses an event command containing a description and date range.
     *
     * @param command Full event command.
     * @return Event represented by the command.
     * @throws FloppyException If the description or date range is invalid.
     */
    private Event parseEvent(String command) throws FloppyException {
        DateRangeDetails details = parseDateRangeDetails(command, "event", "event");
        return new Event(details.description(), details.startDate(), details.endDate());
    }

    /**
     * Parses a period command containing a description and completion range.
     *
     * @param command Full period command.
     * @return Period task represented by the command.
     * @throws FloppyException If the description or date range is invalid.
     */
    private PeriodTask parsePeriodTask(String command) throws FloppyException {
        DateRangeDetails details = parseDateRangeDetails(command, "period", "period task");
        return new PeriodTask(details.description(), details.startDate(), details.endDate());
    }

    /**
     * Parses and validates the common fields of a date-range task command.
     *
     * @param command Full command entered by the user.
     * @param commandWord Command word that identifies the task type.
     * @param taskName Human-readable task type used in error messages.
     * @return Validated description and date range.
     * @throws FloppyException If a required field or date is invalid.
     */
    private DateRangeDetails parseDateRangeDetails(
            String command, String commandWord, String taskName) throws FloppyException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ", Math.max(fromIndex, 0));
        if (!command.startsWith(commandWord + " ") || fromIndex < 0 || toIndex < 0) {
            throw new FloppyException(
                    "Use: " + commandWord + " DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.");
        }
        if (command.indexOf(" /from ", fromIndex + " /from ".length()) >= 0
                || command.indexOf(" /to ", toIndex + " /to ".length()) >= 0) {
            throw new FloppyException(
                    "The " + taskName + " command accepts exactly one /from date and one /to date.");
        }
        String description = command.substring((commandWord + " ").length(), fromIndex).trim();
        String startDateText = command.substring(fromIndex + " /from ".length(), toIndex).trim();
        String endDateText = command.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty()) {
            String capitalizedTaskName = Character.toUpperCase(taskName.charAt(0))
                    + taskName.substring(1);
            throw new FloppyException(capitalizedTaskName
                    + " needs a description, start date, and end date.");
        }
        validateDescription(description);
        LocalDate startDate = parseDate(startDateText);
        LocalDate endDate = parseDate(endDateText);
        if (endDate.isBefore(startDate)) {
            throw new FloppyException(
                    "The " + taskName + " end date cannot be before its start date.");
        }
        assert !endDate.isBefore(startDate) : "A validated date range must not end before it starts";
        return new DateRangeDetails(description, startDate, endDate);
    }

    /**
     * Parses an ISO date entered in {@code yyyy-MM-dd} format.
     *
     * @param dateText Date supplied by the user.
     * @return Parsed date.
     * @throws FloppyException If the date is not a valid ISO date.
     */
    private LocalDate parseDate(String dateText) throws FloppyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new FloppyException("Use dates in yyyy-MM-dd format, such as 2026-08-28.");
        }
    }

    /**
     * Returns whether an input contains the specified complete command word.
     *
     * @param command Trimmed command entered by the user.
     * @param commandWord Command word to match.
     * @return {@code true} if the input is the command word with optional arguments.
     */
    private boolean isCommand(String command, String commandWord) {
        return command.equals(commandWord) || command.startsWith(commandWord + " ");
    }

    /**
     * Rejects descriptions that cannot be represented by the storage format.
     *
     * @param description Task description to validate.
     * @throws FloppyException If the description contains a reserved separator.
     */
    private void validateDescription(String description) throws FloppyException {
        if (description.contains("|")) {
            throw new FloppyException("Task descriptions cannot contain the | character.");
        }
    }

    /** Holds the validated fields shared by tasks with a start and end date. */
    private record DateRangeDetails(String description, LocalDate startDate, LocalDate endDate) {
    }
}
