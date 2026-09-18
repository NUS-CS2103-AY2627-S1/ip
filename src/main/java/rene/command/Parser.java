package rene.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;
import rene.task.Todo;

/**
 * Converts user input into commands, tasks, and task numbers Rene can process.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "That falls outside my scope of operations. Standard procedures are: todo, deadline, "
                    + "event, list, mark, unmark, delete, find, help, or bye.";
    private static final String BLANK_INPUT_MESSAGE = "No directive detected. Try: help";

    /**
     * Creates a parser for Rene's supported command syntax.
     */
    public Parser() {
    }

    /**
     * Identifies the command type and separates its argument from its keyword.
     * Surrounding spaces are ignored so an accidental leading or trailing space
     * does not break an otherwise correct command.
     *
     * @param input the complete line entered by the user.
     * @return the parsed command.
     * @throws ReneException if the line is blank or the command keyword is not recognized.
     */
    public ParsedCommand parse(String input) throws ReneException {
        String line = input.strip();
        if (line.isEmpty()) {
            throw new ReneException(BLANK_INPUT_MESSAGE);
        }

        CommandType commandType = CommandType.fromInput(line);
        if (commandType == null) {
            throw new ReneException(UNKNOWN_COMMAND_MESSAGE);
        }

        String argument = line.substring(commandType.getKeyword().length()).trim();
        return new ParsedCommand(commandType, argument);
    }

    /**
     * Creates a task from a parsed task-creation command.
     * Extra spaces inside the argument are collapsed so they do not alter the stored details.
     *
     * @param command a todo, deadline, or event command.
     * @return the task described by the command.
     * @throws ReneException if a required task detail is absent, duplicated, or invalid.
     */
    public Task parseTask(ParsedCommand command) throws ReneException {
        String details = normalizeSpaces(command.argument());
        return switch (command.type()) {
            case TODO -> parseTodo(details);
            case DEADLINE -> parseDeadline(details);
            case EVENT -> parseEvent(details);
            default -> throw new ReneException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    /**
     * Returns the one-based task number supplied to a task-list command.
     *
     * @param command a mark, unmark, or delete command.
     * @return the supplied task number.
     * @throws ReneException if the argument is missing or not a whole number.
     */
    public int parseTaskNumber(ParsedCommand command) throws ReneException {
        String argument = command.argument();
        if (argument.isEmpty()) {
            throw new ReneException("A " + command.type().getKeyword() + " directive requires a task number. "
                    + "Try: " + command.type().getKeyword() + " 1");
        }
        if (!argument.matches("-?\\d+")) {
            throw new ReneException("A task position must be a whole number, e.g.: "
                    + command.type().getKeyword() + " 1");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new ReneException("That task number exceeds my processing capacity. Try: "
                    + command.type().getKeyword() + " 1");
        }
    }

    /**
     * Creates a todo after validating its description.
     */
    private Task parseTodo(String description) throws ReneException {
        requireText(description, "A todo requires a description. Try: todo read chapter 3");
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and due date.
     */
    private Task parseDeadline(String details) throws ReneException {
        String byMarker = ArgumentMarker.BY.getText();
        int firstBy = details.indexOf(byMarker);
        if (firstBy < 0) {
            throw new ReneException("A deadline requires /by. Try: deadline submit report /by 2026-08-31");
        }
        if (firstBy != details.lastIndexOf(byMarker)) {
            throw new ReneException("Only one /by is permitted per deadline. "
                    + "Try: deadline submit report /by 2026-08-31");
        }

        String description = details.substring(0, firstBy).trim();
        String byText = details.substring(firstBy + byMarker.length()).trim();
        requireText(description, "A deadline requires a description before /by.");
        requireText(byText, "A deadline requires a due date after /by.");
        try {
            return new Deadline(description, LocalDate.parse(byText));
        } catch (DateTimeParseException exception) {
            throw new ReneException("The due date must be a valid date in yyyy-MM-dd format. "
                    + "Try: deadline submit report /by 2026-08-31");
        }
    }

    /**
     * Creates an event after validating its description, start, and end values.
     */
    private Task parseEvent(String details) throws ReneException {
        String fromMarker = ArgumentMarker.FROM.getText();
        String toMarker = ArgumentMarker.TO.getText();
        int firstFrom = details.indexOf(fromMarker);
        int firstTo = details.indexOf(toMarker);
        if (firstFrom < 0 || firstTo < 0) {
            throw new ReneException("An event requires /from and /to. Try: event study group /from 2pm /to 4pm");
        }
        if (firstFrom != details.lastIndexOf(fromMarker) || firstTo != details.lastIndexOf(toMarker)) {
            throw new ReneException("An event permits one /from and one /to each. "
                    + "Try: event study group /from 2pm /to 4pm");
        }
        if (firstTo < firstFrom) {
            throw new ReneException("An event requires /from before /to. "
                    + "Try: event study group /from 2pm /to 4pm");
        }

        String description = details.substring(0, firstFrom).trim();
        String from = details.substring(firstFrom + fromMarker.length(), firstTo).trim();
        String to = details.substring(firstTo + toMarker.length()).trim();
        requireText(description, "An event requires a description before /from.");
        requireText(from, "An event requires a start time after /from.");
        requireText(to, "An event requires an end time after /to.");
        return new Event(description, from, to);
    }

    /**
     * Collapses runs of two or more spaces into single spaces and trims the ends.
     */
    private String normalizeSpaces(String text) {
        return text.trim().replaceAll("\\s{2,}", " ");
    }

    /**
     * Rejects a blank required command field.
     */
    private void requireText(String text, String message) throws ReneException {
        if (text.isEmpty()) {
            throw new ReneException(message);
        }
    }
}
