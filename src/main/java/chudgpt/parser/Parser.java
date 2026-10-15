package chudgpt.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chudgpt.command.AddTaskCommand;
import chudgpt.command.ChangeTaskPriorityCommand;
import chudgpt.command.ChangeTaskStatusCommand;
import chudgpt.command.Command;
import chudgpt.command.DeleteTaskCommand;
import chudgpt.command.ExitCommand;
import chudgpt.command.FindCommand;
import chudgpt.command.HiCommand;
import chudgpt.command.ListCommand;
import chudgpt.command.SaveCommand;
import chudgpt.exception.ChudException;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.ToDo;

/** Parses task commands and task-number arguments entered by the user. */
public class Parser {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final Pattern PRIORITY_TOKEN = Pattern.compile("(?<!\\S)/priority(?!\\S)");
    private static final String PRIORITY_SYNTAX_MESSAGE =
            "The /priority argument can only be specified once.";
    private static final String PRIORITY_MISSING_ARGUMENT_MESSAGE =
            "The /priority argument requires a priority value.";
    private static final String PRIORITY_WRONG_ORDER_MESSAGE =
            "The /priority argument must be the final argument.";

    /**
     * Parses a user command into an executable command object.
     *
     * @param command raw command entered by the user.
     * @return command represented by the input.
     * @throws ChudException if the input is blank or unsupported.
     */
    public Command parse(String command) throws ChudException {
        if (command == null || command.isBlank()) {
            throw new ChudException("Please enter a command.");
        }

        String[] parts = command.toLowerCase(Locale.ROOT).trim().split("\\s+", 2);

        String commandName = parts[0];
        String params = "";
        if (parts.length > 1) {
            params = parts[1];
        }

        return switch (commandName) {
            case "hi" -> createNoArgumentCommand(params, new HiCommand(), "Usage: hi.");
            case "bye" -> createNoArgumentCommand(params, new ExitCommand(), "Usage: bye.");
            case "list" -> createListCommand(params);
            case "save" -> createNoArgumentCommand(params, new SaveCommand(), "Usage: save.");
            case "todo", "deadline", "event" -> new AddTaskCommand(createTask(commandName, params));
            case "priority" -> createPriorityCommand(params);
            case "delete" -> new DeleteTaskCommand(parseSingleIndex(params, "Usage: delete <task number>."));
            case "mark" -> new ChangeTaskStatusCommand(parseSingleIndex(params, "Usage: mark <task number>."), true);
            case "unmark" -> new ChangeTaskStatusCommand(
                    parseSingleIndex(params, "Usage: unmark <task number>."), false);
            case "find" -> createFindCommand(params);
            default -> throw new ChudException("Invalid command.");
        };
    }

    /** Returns a no-argument command after rejecting unexpected parameters. */
    private Command createNoArgumentCommand(String params, Command command, String usage) throws ChudException {
        if (!params.isEmpty()) {
            throw new ChudException(usage);
        }
        return command;
    }

    /** Returns a find command after ensuring that a keyword was supplied. */
    private Command createFindCommand(String params) throws ChudException {
        if (params.isBlank()) {
            throw new ChudException("Usage: find <keyword>.");
        }
        return new FindCommand(params);
    }

    /** Removes an optional final priority clause before using the existing task parsers. */
    private Task createTask(String commandName, String params) throws ChudException {
        Priority priority = Priority.NONE;
        Matcher matcher = PRIORITY_TOKEN.matcher(params);
        if (matcher.find()) {
            int tokenStart = matcher.start();
            int tokenEnd = matcher.end();

            if (matcher.find()) {
                throw new ChudException(PRIORITY_SYNTAX_MESSAGE);
            }

            String value = params.substring(tokenEnd).trim();
            if (value.isEmpty()) {
                throw new ChudException(PRIORITY_MISSING_ARGUMENT_MESSAGE);
            }

            if (value.matches(".*\\s+.*")) {
                throw new ChudException(PRIORITY_WRONG_ORDER_MESSAGE);
            }

            priority = Priority.parse(value);
            params = params.substring(0, tokenStart).trim();
        }

        Task task = switch (commandName) {
            case "todo" -> new ToDo(validateDescription(params));
            case "deadline" -> createDeadlineTask(params);
            case "event" -> createEventTask(params);
            default -> throw new ChudException("Invalid command.");
        };
        return task.setPriority(priority);
    }

    /** Parses the two supported list command forms. */
    private Command createListCommand(String params) throws ChudException {
        if (params.isEmpty()) {
            return new ListCommand();
        }
        if (params.matches("/sort\\s+priority")) {
            return new ListCommand(true);
        }
        throw new ChudException("Usage: list or list /sort priority.");
    }

    /** Parses a task number and priority without changing or saving any task. */
    private Command createPriorityCommand(String params) throws ChudException {
        String[] arguments = params.split("\\s+");
        if (arguments.length != 2) {
            throw new ChudException("Usage: priority <task number> <priority>.");
        }
        int index = parseIndex(arguments[0]);
        Priority priority = Priority.parse(arguments[1]);
        return new ChangeTaskPriorityCommand(index, priority);
    }

    /** Parses the only argument of a task-number command. */
    private int parseSingleIndex(String params, String usage) throws ChudException {
        if (params.isBlank() || params.split("\\s+").length != 1) {
            throw new ChudException(usage);
        }
        return parseIndex(params);
    }

    /**
     * Parses a deadline task from its description and due date.
     *
     * @param params task description followed by a {@code /by} date.
     * @return parsed deadline task.
     * @throws ChudException if the deadline arguments are missing or invalid.
     */
    public Task createDeadlineTask(String params) throws ChudException {
        int byIndex = params.indexOf("/by");
        if (byIndex < 0) {
            throw new ChudException("OOPS!!! There must be a /by argument passed in.");
        }

        String description = params.substring(0, byIndex).trim();
        String submitBy = params.substring(byIndex + 3).trim();

        validateDescription(description);
        if (submitBy.isEmpty()) {
            throw new ChudException("Deadline date cannot be empty.");
        }
        return new Deadline(description, parseDate(submitBy));
    }

    /**
     * Parses an event task from its description and date range.
     *
     * @param params task description followed by {@code /from} and {@code /to} dates.
     * @return parsed event task.
     * @throws ChudException if the event arguments are missing or invalid.
     */
    public Task createEventTask(String params) throws ChudException {
        int fromIndex = params.indexOf("/from");
        int toIndex = params.indexOf("/to", fromIndex + 5);
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new ChudException("OOPS!!! Specify the /from argument before the /to argument");
        }

        String description = params.substring(0, fromIndex).trim();
        String start = params.substring(fromIndex + 5, toIndex).trim();
        String end = params.substring(toIndex + 3).trim();
        validateDescription(description);
        if (start.isEmpty() || end.isEmpty()) {
            throw new ChudException("Event start and end dates cannot be empty.");
        }
        LocalDate startDate = parseDate(start);
        LocalDate endDate = parseDate(end);
        validateEventDates(startDate, endDate);
        return new Event(description, startDate, endDate);
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     *
     * @param params task number entered by the user.
     * @return zero-based task index.
     * @throws ChudException if the value is not a supported positive task number.
     */
    public static int parseIndex(String params) throws ChudException {
        if (!params.matches("\\d+")) {
            throw new ChudException("Task number must be a positive whole number.");
        }
        try {
            int taskNumber = Integer.parseInt(params);
            if (taskNumber < 1) {
                throw new ChudException("Task numbers start from 1.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ChudException("Task number is too large.");
        }
    }

    /**
     * Validates and returns a task description supplied by a user or save file.
     *
     * @param description task description to validate.
     * @return the validated description.
     * @throws ChudException if the description is blank or unsafe to store.
     */
    public static String validateDescription(String description) throws ChudException {
        if (description == null || description.isBlank()) {
            throw new ChudException("Task description cannot be empty.");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ChudException("Task description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        if (description.indexOf('|') >= 0
                || description.codePoints().anyMatch(Character::isISOControl)) {
            throw new ChudException("Task description cannot contain '|' or control characters.");
        }
        return description;
    }

    /**
     * Ensures that an event ends after it starts.
     *
     * @param start event start date.
     * @param end event end date.
     * @throws ChudException if the end date is not later than the start date.
     */
    public static void validateEventDates(LocalDate start, LocalDate end) throws ChudException {
        if (!start.isBefore(end)) {
            throw new ChudException("Event end date must be later than its start date.");
        }
    }

    /**
     * Parses an ISO-8601 calendar date.
     *
     * @param value date in {@code yyyy-mm-dd} format.
     * @return parsed calendar date.
     * @throws ChudException if the value is not a valid date.
     */
    public static LocalDate parseDate(String value) throws ChudException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ChudException("OOPS!!! Input a date in the format yyyy-mm-dd");
        }
    }
}
