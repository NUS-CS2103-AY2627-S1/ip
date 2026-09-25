package shan.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shan.command.AddCommand;
import shan.command.Command;
import shan.command.DeleteCommand;
import shan.command.ExitCommand;
import shan.command.FindCommand;
import shan.command.ListCommand;
import shan.command.MarkCommand;
import shan.command.OnCommand;
import shan.command.UndoCommand;
import shan.command.UnmarkCommand;
import shan.datetime.DateTimeParser;
import shan.exception.InvalidArgumentException;
import shan.exception.InvalidCommandException;
import shan.exception.MissingArgumentException;
import shan.task.Deadline;
import shan.task.Event;
import shan.task.ToDo;

/**
 * Interprets user commands and their arguments.
 */
public final class Parser {
    /**
     * Commands understood by Shan.
     */
    private enum CommandType {
        /** Exits Shan. */
        BYE,
        /** Lists all tasks. */
        LIST,
        /** Finds tasks whose descriptions contain a keyword. */
        FIND,
        /** Marks a task as completed. */
        MARK,
        /** Marks a task as not completed. */
        UNMARK,
        /** Reverses the most recent task-list change. */
        UNDO,
        /** Deletes a task. */
        DELETE,
        /** Shows dated tasks on a date or within a range. */
        ON,
        /** Adds a ToDo task. */
        TODO,
        /** Adds a Deadline task. */
        DEADLINE,
        /** Adds an Event task. */
        EVENT
    }

    private Parser() {
    }

    /**
     * Parses a complete input line into an executable command.
     *
     * @param inputLine Input entered by the user.
     * @return Command containing the parsed arguments.
     * @throws InvalidCommandException  If the command is blank or unknown.
     * @throws MissingArgumentException If a required argument is missing.
     * @throws InvalidArgumentException If an argument is invalid.
     */
    public static Command parse(String inputLine)
            throws InvalidCommandException, MissingArgumentException, InvalidArgumentException {
        if (inputLine == null || inputLine.isBlank()) {
            throw new InvalidCommandException("Enter a command.");
        }

        String[] tokens = inputLine.trim().split("\\s+", 2);
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(tokens[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidCommandException("I don't recognize that command.");
        }
        String arguments = tokens.length == 2 ? tokens[1] : "";
        return switch (commandType) {
            case BYE -> {
                rejectUnexpectedArguments("bye", arguments);
                yield new ExitCommand();
            }
            case LIST -> {
                rejectUnexpectedArguments("list", arguments);
                yield new ListCommand();
            }
            case FIND -> parseFindCommand(arguments);
            case MARK -> new MarkCommand(parseTaskNumber(arguments));
            case UNDO -> {
                rejectUnexpectedArguments("undo", arguments);
                yield new UndoCommand();
            }
            case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments));
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments));
            case ON -> parseOnCommand(arguments);
            case TODO -> new AddCommand(parseToDo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
        };
    }

    /**
     * Parses a keyword search into an executable command.
     *
     * @param argument Keyword entered by the user.
     * @return Command containing the normalized keyword.
     * @throws MissingArgumentException If the keyword is missing.
     */
    private static FindCommand parseFindCommand(String argument)
            throws MissingArgumentException {
        if (argument.isBlank()) {
            throw new MissingArgumentException("Specify a keyword to find.");
        }
        return new FindCommand(argument.trim());
    }

    /**
     * Parses a date query into an executable command.
     *
     * @param argument One date, or two dates separated by {@code /to}.
     * @return Command containing the parsed inclusive date range.
     * @throws MissingArgumentException If a required date is missing.
     * @throws InvalidArgumentException If a date or range is invalid.
     */
    private static OnCommand parseOnCommand(String argument)
            throws MissingArgumentException, InvalidArgumentException {
        DateRange dateRange = parseDateRange(argument);
        return new OnCommand(dateRange.startDate(), dateRange.endDate());
    }

    /**
     * Parses a task number supplied to a mark, unmark, or delete command.
     *
     * @param argument Task number entered by the user.
     * @return Parsed task number.
     * @throws MissingArgumentException If the task number is missing.
     * @throws InvalidArgumentException If the task number is not an integer.
     */
    static int parseTaskNumber(String argument)
            throws MissingArgumentException, InvalidArgumentException {
        if (argument == null || argument.isBlank()) {
            throw new MissingArgumentException("Specify a task number.");
        }
        String normalizedArgument = argument.trim();
        if (!normalizedArgument.matches("\\d+")) {
            throw new InvalidArgumentException(
                    "The task number must be a positive whole number.");
        }
        try {
            int taskNumber = Integer.parseInt(normalizedArgument);
            if (taskNumber < 1) {
                throw new InvalidArgumentException(
                        "The task number must be a positive whole number.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException(
                    "The task number must be a positive whole number.");
        }
    }

    /**
     * Parses a ToDo task description.
     *
     * @param taskName Name of the task.
     * @return Parsed ToDo task.
     * @throws MissingArgumentException If the description is empty.
     * @throws InvalidArgumentException If the description contains an unsupported character.
     */
    static ToDo parseToDo(String taskName)
            throws MissingArgumentException, InvalidArgumentException {
        if (taskName == null || taskName.isBlank()) {
            throw new MissingArgumentException("The task description cannot be empty.");
        }
        String normalizedTaskName = taskName.trim();
        validateFileFields(normalizedTaskName);
        return new ToDo(normalizedTaskName);
    }

    /**
     * Parses a Deadline description and date-time.
     *
     * @param arguments Task description followed by {@code /by} and the deadline.
     * @return Parsed Deadline task.
     * @throws MissingArgumentException If a required field or delimiter is missing.
     * @throws InvalidArgumentException If a field or date-time is invalid.
     */
    static Deadline parseDeadline(String arguments)
            throws MissingArgumentException, InvalidArgumentException {
        if (arguments == null || arguments.isBlank()) {
            throw new MissingArgumentException("The deadline description cannot be empty.");
        }

        int byIndex = findSingleParameterIndex(
                arguments, "/by", "Please specify a deadline using /by.");
        String[] deadlineDetails = splitAtParameter(arguments, "/by", byIndex);
        if (deadlineDetails[0].isBlank() || deadlineDetails[1].isBlank()) {
            throw new MissingArgumentException("The deadline description and date cannot be empty.");
        }

        String taskName = deadlineDetails[0].trim();
        String endDateInput = normalizeDateTimeInput(deadlineDetails[1]);
        validateFileFields(taskName, endDateInput);
        LocalDateTime endDate = DateTimeParser.parse(endDateInput);
        return new Deadline(taskName, endDate);
    }

    /**
     * Parses an Event description and date-time range.
     *
     * @param arguments Task description followed by {@code /from} and {@code /to} values.
     * @return Parsed Event task.
     * @throws MissingArgumentException If a required field or delimiter is missing.
     * @throws InvalidArgumentException If a field, date-time, or range is invalid.
     */
    static Event parseEvent(String arguments)
            throws MissingArgumentException, InvalidArgumentException {
        if (arguments == null || arguments.isBlank()) {
            throw new MissingArgumentException("The event description cannot be empty.");
        }

        int fromIndex = findSingleParameterIndex(
                arguments, "/from", "Specify the event start using /from.");
        int toIndex = findSingleParameterIndex(
                arguments, "/to", "Specify the event end using /to.");
        if (toIndex < fromIndex) {
            throw new InvalidArgumentException("Specify /from before /to.");
        }
        String taskName = arguments.substring(0, fromIndex).trim();
        String startDateInput = normalizeDateTimeInput(
                arguments.substring(fromIndex + "/from".length(), toIndex));
        String endDateInput = normalizeDateTimeInput(arguments.substring(toIndex + "/to".length()));
        if (taskName.isBlank() || startDateInput.isBlank() || endDateInput.isBlank()) {
            throw new MissingArgumentException(
                    "The event description, start, and end cannot be empty.");
        }
        validateFileFields(taskName, startDateInput, endDateInput);
        LocalDateTime startDate = DateTimeParser.parse(startDateInput);
        LocalDateTime endDate = DateTimeParser.parse(endDateInput);
        if (!endDate.isAfter(startDate)) {
            throw new InvalidArgumentException("The event end must be after its start.");
        }
        return new Event(taskName, startDate, endDate);
    }

    /**
     * Parses one date or an inclusive range of dates.
     *
     * @param argument One date, or two dates separated by {@code /to}.
     * @return Parsed inclusive date range.
     * @throws MissingArgumentException If a required date is missing.
     * @throws InvalidArgumentException If a date or range is invalid.
     */
    static DateRange parseDateRange(String argument)
            throws MissingArgumentException, InvalidArgumentException {
        if (argument == null || argument.isBlank()) {
            throw new MissingArgumentException("Specify a date using yyyy-MM-dd format.");
        }

        int toIndex = findOptionalSingleParameterIndex(argument, "/to");
        String[] dateInputs = toIndex < 0
                ? new String[] {argument} : splitAtParameter(argument, "/to", toIndex);
        if (dateInputs.length == 2 && (dateInputs[0].isBlank() || dateInputs[1].isBlank())) {
            throw new MissingArgumentException("Specify both range dates using /to.");
        }

        LocalDate startDate = DateTimeParser.parseDate(dateInputs[0].trim());
        LocalDate endDate = dateInputs.length == 1
                ? startDate
                : DateTimeParser.parseDate(dateInputs[1].trim());
        if (endDate.isBefore(startDate)) {
            throw new InvalidArgumentException(
                    "The range end date cannot be before its start date.");
        }
        return new DateRange(startDate, endDate);
    }

    /**
     * Rejects values containing the delimiter reserved by the save-file format.
     *
     * @param fields Task fields to validate.
     * @throws InvalidArgumentException If a field contains an unsupported character.
     */
    private static void validateFileFields(String... fields) throws InvalidArgumentException {
        for (String field : fields) {
            if (field.contains("|")) {
                throw new InvalidArgumentException("Task details cannot contain |.");
            }
            if (field.codePoints().anyMatch(Character::isISOControl)) {
                throw new InvalidArgumentException(
                        "Task details cannot contain control characters.");
            }
        }
    }

    /**
     * Normalizes harmless whitespace differences in a date-time argument.
     *
     * @param input Date-time text supplied by the user.
     * @return Trimmed date-time with each whitespace run replaced by one space.
     */
    private static String normalizeDateTimeInput(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Rejects arguments supplied to a command that does not accept any.
     *
     * @param commandName Command entered by the user.
     * @param arguments   Unexpected command arguments.
     * @throws InvalidArgumentException If any argument was supplied.
     */
    private static void rejectUnexpectedArguments(String commandName, String arguments)
            throws InvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new InvalidArgumentException(
                    String.format("The %s command does not accept arguments.", commandName));
        }
    }

    /**
     * Finds a required parameter that must occur exactly once.
     *
     * @param input          Input containing the parameter.
     * @param parameter      Slash-prefixed parameter to find.
     * @param missingMessage Message to use when the parameter is absent.
     * @return Index at which the parameter starts.
     * @throws MissingArgumentException If the parameter is absent.
     * @throws InvalidArgumentException If the parameter occurs more than once.
     */
    private static int findSingleParameterIndex(
            String input, String parameter, String missingMessage)
            throws MissingArgumentException, InvalidArgumentException {
        int parameterIndex = findOptionalSingleParameterIndex(input, parameter);
        if (parameterIndex < 0) {
            throw new MissingArgumentException(missingMessage);
        }
        return parameterIndex;
    }

    /**
     * Finds an optional parameter that may occur at most once.
     *
     * @param input     Input that may contain the parameter.
     * @param parameter Slash-prefixed parameter to find.
     * @return Parameter index, or {@code -1} when absent.
     * @throws InvalidArgumentException If the parameter occurs more than once.
     */
    private static int findOptionalSingleParameterIndex(String input, String parameter)
            throws InvalidArgumentException {
        Pattern parameterPattern = Pattern.compile(
                "(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
        Matcher matcher = parameterPattern.matcher(input);
        if (!matcher.find()) {
            return -1;
        }

        int parameterIndex = matcher.start();
        if (matcher.find()) {
            throw new InvalidArgumentException(
                    String.format("Specify %s only once.", parameter));
        }
        return parameterIndex;
    }

    /**
     * Splits input around a parameter whose location has already been validated.
     *
     * @param input          Input containing the parameter.
     * @param parameter      Slash-prefixed parameter.
     * @param parameterIndex Index at which the parameter starts.
     * @return Text before and after the parameter.
     */
    private static String[] splitAtParameter(
            String input, String parameter, int parameterIndex) {
        return new String[] {
            input.substring(0, parameterIndex),
            input.substring(parameterIndex + parameter.length())
        };
    }

    /**
     * Contains the inclusive dates of a parsed date query.
     *
     * @param startDate First date in the range.
     * @param endDate   Last date in the range.
     */
    public record DateRange(LocalDate startDate, LocalDate endDate) {
        /**
         * Returns whether this value represents more than one date.
         *
         * @return {@code true} when the start and end dates differ.
         */
        public boolean isRange() {
            return !this.startDate.equals(this.endDate);
        }
    }
}
