package daddy.parser;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daddy.command.AddCommand;
import daddy.command.Command;
import daddy.command.DeleteCommand;
import daddy.command.ExitCommand;
import daddy.command.FindCommand;
import daddy.command.FreeCommand;
import daddy.command.ListCommand;
import daddy.command.ListOnDateCommand;
import daddy.command.MarkCommand;
import daddy.command.UnmarkCommand;
import daddy.exception.DaddyException;
import daddy.task.Deadline;
import daddy.task.Event;
import daddy.task.Task;
import daddy.task.Todo;

/**
 * Converts complete Daddy command lines into command objects.
 */
public class Parser {
    /** Parses date-only task values in day-month-year order. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /** Parses date-and-time task values in day-month-year order. */
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HHmm");
    /** Recognizes a whole-number duration followed by an hour or minute unit. */
    private static final Pattern FREE_DURATION_FORMAT = Pattern.compile("^(\\d+)([hHmM])$");
    /** Limits free-time requests to the length of Daddy's working day. */
    private static final Duration MAX_FREE_DURATION = Duration.ofHours(9);

    /** Creates a parser for Daddy command lines. */
    public Parser() {
    }

    /**
     * Creates a command object from one complete user command.
     *
     * @param input the complete command entered by the user
     * @return the command object that represents the user's request
     * @throws DaddyException if the command is unknown or its arguments are invalid
     */
    public Command parseCommand(String input) throws DaddyException {
        CommandType commandType = getCommandType(input);
        String arguments = getArguments(input, commandType);
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST, LIST_ON -> parseListCommand(commandType, arguments);
            case TODO, DEADLINE, EVENT -> parseAddCommand(commandType, arguments);
            case MARK, UNMARK -> parseTaskStateCommand(commandType, arguments);
            case DELETE -> parseDeleteCommand(arguments);
            case FIND -> parseFindCommand(arguments);
            case FREE -> parseFreeCommand(arguments);
            case MARK_MISSING_ARGUMENT -> throw new DaddyException(
                    "MARK what, my friend? Give Daddy a task number. Try: mark 1.");
            case UNMARK_MISSING_ARGUMENT -> throw new DaddyException(
                    "UNMARK what, my friend? Give Daddy a task number. Try: unmark 1.");
            case DELETE_MISSING_ARGUMENT -> throw new DaddyException(
                    "DELETE what, my friend? Choose a task number. Try: delete 1.");
            case FIND_MISSING_ARGUMENT -> throw new DaddyException(
                    "FIND what, my friend? Give Daddy a keyword. Try: find book.");
            case UNKNOWN -> throw new DaddyException("Easy, my friend—Daddy doesn't know what '" + input
                    + "' means. Try todo, deadline, event, list, find, free, mark, unmark, delete, or bye.");
        };
    }

    /**
     * Determines the type of a command without changing its argument text.
     *
     * @param input the complete command entered by the user
     * @return the recognized command type, or {@link CommandType#UNKNOWN}
     */
    private CommandType getCommandType(String input) {
        String lowerCaseInput = input.toLowerCase();
        if (input.equalsIgnoreCase("bye")) {
            return CommandType.BYE;
        } else if (input.equalsIgnoreCase("list")) {
            return CommandType.LIST;
        } else if (lowerCaseInput.startsWith("list on ")) {
            return CommandType.LIST_ON;
        } else if (input.equalsIgnoreCase("todo") || lowerCaseInput.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (input.equalsIgnoreCase("mark")) {
            return CommandType.MARK_MISSING_ARGUMENT;
        } else if (input.equalsIgnoreCase("unmark")) {
            return CommandType.UNMARK_MISSING_ARGUMENT;
        } else if (input.equalsIgnoreCase("deadline") || lowerCaseInput.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (input.equalsIgnoreCase("event") || lowerCaseInput.startsWith("event ")) {
            return CommandType.EVENT;
        } else if (input.equalsIgnoreCase("delete")) {
            return CommandType.DELETE_MISSING_ARGUMENT;
        } else if (input.equalsIgnoreCase("find")) {
            return CommandType.FIND_MISSING_ARGUMENT;
        } else if (input.equalsIgnoreCase("free") || lowerCaseInput.startsWith("free ")) {
            return CommandType.FREE;
        } else if (lowerCaseInput.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (lowerCaseInput.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (lowerCaseInput.startsWith("find ")) {
            return CommandType.FIND;
        } else if (lowerCaseInput.startsWith("mark ")) {
            return CommandType.MARK;
        } else {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Extracts and trims the text after a command keyword.
     *
     * @param input the complete command entered by the user
     * @param commandType the recognized command type
     * @return the argument text, or an empty string when the command has no arguments
     */
    private String getArguments(String input, CommandType commandType) {
        return switch (commandType) {
            case LIST_ON -> input.substring(8).trim();
            case TODO -> input.length() == 4 ? "" : input.substring(5).trim();
            case DEADLINE -> input.length() == 8 ? "" : input.substring(9).trim();
            case EVENT -> input.length() == 5 ? "" : input.substring(6).trim();
            case MARK, FIND -> input.substring(5).trim();
            case FREE -> input.length() == 4 ? "" : input.substring(5).trim();
            case UNMARK, DELETE -> input.substring(7).trim();
            default -> "";
        };
    }

    /**
     * Parses a date used by the {@code list on} command.
     *
     * @param dateText the user-entered date
     * @return the parsed date
     * @throws DaddyException if the date does not use {@code dd-MM-yyyy} format
     */
    private LocalDate parseListDate(String dateText) throws DaddyException {
        try {
            return LocalDate.parse(dateText, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new DaddyException("Keep the date clean, my friend: use dd-MM-yyyy. "
                    + "Try: list on 02-12-2019");
        }
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     *
     * @param taskNumber the task number entered by the user
     * @param command the command that uses the task number
     * @return the matching zero-based task index
     * @throws DaddyException if the task number is not numeric
     */
    private int parseTaskIndex(String taskNumber, String command) throws DaddyException {
        try {
            return Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new DaddyException("'" + taskNumber + "' is not a task number, my friend. "
                    + "Try: " + command + " 1.");
        }
    }

    /**
     * Creates a read-only list command from parsed command text.
     *
     * @param commandType the recognized list command type
     * @param arguments the text following the command keyword
     * @return a command that displays tasks
     * @throws DaddyException if a list date is invalid
     */
    private Command parseListCommand(CommandType commandType, String arguments) throws DaddyException {
        return switch (commandType) {
            case LIST -> new ListCommand();
            case LIST_ON -> new ListOnDateCommand(parseListDate(arguments));
            default -> throw new IllegalArgumentException("command does not list tasks");
        };
    }

    /**
     * Creates an add command from a task-creation command.
     *
     * @param commandType the recognized task-creation command type
     * @param taskDetails the task description and date details, when applicable
     * @return a command that adds the parsed task
     * @throws DaddyException if the task details are incomplete or incorrectly formatted
     */
    private Command parseAddCommand(CommandType commandType, String taskDetails) throws DaddyException {
        return new AddCommand(parseTask(commandType, taskDetails));
    }

    /**
     * Creates a command that changes the done status of one task.
     *
     * @param commandType the recognized task-status command type
     * @param taskNumber the user-entered one-based task number
     * @return a command that marks or unmarks the selected task
     * @throws DaddyException if the task number is not numeric
     */
    private Command parseTaskStateCommand(CommandType commandType, String taskNumber) throws DaddyException {
        return switch (commandType) {
            case MARK -> new MarkCommand(parseTaskIndex(taskNumber, "mark"));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(taskNumber, "unmark"));
            default -> throw new IllegalArgumentException("command does not change task status");
        };
    }

    /**
     * Creates a command that deletes one task.
     *
     * @param taskNumber the user-entered one-based task number
     * @return a command that deletes the selected task
     * @throws DaddyException if the task number is not numeric
     */
    private Command parseDeleteCommand(String taskNumber) throws DaddyException {
        return new DeleteCommand(parseTaskIndex(taskNumber, "delete"));
    }

    /**
     * Creates a command that finds tasks containing a keyword.
     *
     * @param keyword the description text to find
     * @return a command that displays matching tasks
     * @throws DaddyException if no keyword was supplied
     */
    private Command parseFindCommand(String keyword) throws DaddyException {
        if (keyword.isEmpty()) {
            throw new DaddyException("FIND what, my friend? Give Daddy a keyword. Try: find book.");
        }
        return new FindCommand(keyword);
    }

    /**
     * Creates a command that finds the earliest free slot of a requested duration.
     *
     * @param durationText the requested whole-number duration followed by {@code h} or {@code m}
     * @return a command that searches for the earliest matching free slot
     * @throws DaddyException if the duration is missing, malformed, zero, or longer than one working day
     */
    private Command parseFreeCommand(String durationText) throws DaddyException {
        if (durationText.isEmpty()) {
            throw new DaddyException("FREE for how long, my friend? Give Daddy a duration. Try: free 4h.");
        }

        Matcher matcher = FREE_DURATION_FORMAT.matcher(durationText);
        if (!matcher.matches()) {
            throw new DaddyException("Keep it simple, my friend: use a whole number ending in h or m. "
                    + "Try: free 4h or free 90m.");
        }

        try {
            long amount = Long.parseLong(matcher.group(1));
            if (amount == 0) {
                throw new DaddyException("A zero-minute break will not help you recover, my friend. "
                        + "Try: free 4h.");
            }
            Duration duration = matcher.group(2).equalsIgnoreCase("h")
                    ? Duration.ofHours(amount) : Duration.ofMinutes(amount);
            if (duration.compareTo(MAX_FREE_DURATION) > 0) {
                throw new DaddyException("Daddy's workday runs from 09:00 to 18:00, my friend. "
                        + "Keep the free slot at 9 hours or less.");
            }
            return new FreeCommand(duration);
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new DaddyException("That duration is too large, my friend. "
                    + "Try something smaller, such as: free 4h.");
        }
    }

    /**
     * Creates a task from the details of a task-creation command.
     *
     * @param commandType the type of task to create
     * @param taskDetails the task description and date details, when applicable
     * @return the task described by the command
     * @throws DaddyException if the task details are incomplete or incorrectly formatted
     */
    private Task parseTask(CommandType commandType, String taskDetails) throws DaddyException {
        return switch (commandType) {
            case TODO -> parseTodo(taskDetails);
            case DEADLINE -> parseDeadline(taskDetails);
            case EVENT -> parseEvent(taskDetails);
            default -> throw new IllegalArgumentException("command does not create a task");
        };
    }

    /**
     * Creates a todo task from its description.
     *
     * @param description the user-entered todo description
     * @return a todo with the supplied description
     * @throws DaddyException if the description is empty
     */
    private Task parseTodo(String description) throws DaddyException {
        if (description.isEmpty()) {
            throw new DaddyException("No plan, no progress, my friend. Tell Daddy what needs doing. "
                    + "Try: todo borrow book.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from its description and {@code /by} date or time.
     *
     * @param taskDetails the description and deadline details entered by the user
     * @return a deadline task with a parsed due date or time
     * @throws DaddyException if a description or deadline is missing or incorrectly formatted
     */
    private Task parseDeadline(String taskDetails) throws DaddyException {
        int byIndex = taskDetails.indexOf(" /by ");
        String description = byIndex >= 0 ? taskDetails.substring(0, byIndex).trim() : taskDetails;
        String deadline = byIndex >= 0 ? taskDetails.substring(byIndex + 5).trim() : "";
        if (description.isEmpty()) {
            throw new DaddyException("Deadlines do not lift themselves, my friend. Name the task. "
                    + "Try: deadline return book /by 02-12-2019.");
        }
        if (deadline.isEmpty()) {
            throw new DaddyException("A deadline needs a /by date or time, my friend. "
                    + "Try: deadline return book /by 02-12-2019.");
        }
        try {
            return new Deadline(description, parseDateTime(deadline));
        } catch (DateTimeParseException exception) {
            throw new DaddyException("Keep the deadline date clean, my friend: use dd-MM-yyyy or "
                    + "dd-MM-yyyy HHmm. Try: deadline return book /by 02-12-2019 1800.");
        }
    }

    /**
     * Creates an event from its description and {@code /from} and {@code /to} times.
     *
     * @param taskDetails the description and event time details entered by the user
     * @return an event task with parsed start and end times
     * @throws DaddyException if event details are missing or incorrectly formatted
     */
    private Task parseEvent(String taskDetails) throws DaddyException {
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = taskDetails.indexOf(" /to ");
        String description = fromIndex >= 0 ? taskDetails.substring(0, fromIndex).trim() : taskDetails;
        String from = fromIndex >= 0 && toIndex > fromIndex
                ? taskDetails.substring(fromIndex + 7, toIndex).trim() : "";
        String to = toIndex >= 0 ? taskDetails.substring(toIndex + 5).trim() : "";
        if (description.isEmpty()) {
            throw new DaddyException("Give the event a name, my friend, so Daddy can track it. "
                    + "Try: event meeting /from 02-12-2019 1400 /to 02-12-2019 1600.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new DaddyException("An event needs both /from and /to times, my friend. "
                    + "Try: event meeting /from 02-12-2019 1400 /to 02-12-2019 1600.");
        }
        try {
            LocalDateTime startDateTime = parseDateTime(from);
            LocalDateTime endDateTime = parseDateTime(to);
            if (!endDateTime.isAfter(startDateTime)) {
                throw new DaddyException("Time only moves forward, my friend. The event must end after "
                        + "it starts. Try: event meeting /from 02-12-2019 1400 /to 02-12-2019 1600.");
            }
            return new Event(description, startDateTime, endDateTime);
        } catch (DateTimeParseException exception) {
            throw new DaddyException("Keep event times clean, my friend: use dd-MM-yyyy or "
                    + "dd-MM-yyyy HHmm. Try: event meeting /from 02-12-2019 1400 "
                    + "/to 02-12-2019 1600.");
        }
    }

    /**
     * Parses a date or date-time accepted in task-creation commands.
     *
     * @param value the text to parse in {@code dd-MM-yyyy} or {@code dd-MM-yyyy HHmm} format
     * @return the parsed date-time, using midnight for a date without a time
     * @throws DateTimeParseException if the value matches neither supported format
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return LocalDate.parse(value, DATE_FORMAT).atTime(LocalTime.MIDNIGHT);
        }
    }
}
