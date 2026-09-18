package fein;

import java.time.LocalDateTime;

import fein.task.Deadline;
import fein.task.Event;
import fein.task.Task;
import fein.task.Todo;

/** Converts user commands into tasks and command arguments. */
public class Parser {
    /** Creates a task from a user command. */
    public Task parseTask(String command) throws FeinException {
        // A command line is always supplied by the CLI or GUI before parsing begins.
        assert command != null : "A command must be present before parsing";
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseTodo(command);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadline(command);
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return parseEvent(command);
        }
        throw new FeinException("That command is not feining. Try `todo buy milk`, `list`, or `bye`.");
    }

    /** Parses a todo command into a task. */
    private Task parseTodo(String command) throws FeinException {
        String description = getRemainder(command, "todo");
        if (description.isEmpty()) {
            throw new FeinException("Tell me what you would like to remember. "
                    + "Try `todo buy milk` or `bye`.");
        }
        validateTaskText(description, "task description");
        return new Todo(description);
    }

    /** Parses a deadline command into a task. */
    private Task parseDeadline(String command) throws FeinException {
        String remainder = getRemainder(command, "deadline");
        if (remainder.isEmpty()) {
            throw new FeinException("Add a task description, for example: "
                    + "`deadline submit report /by Friday`.");
        }

        int separator = remainder.indexOf(" /by");
        if (separator < 0) {
            throw new FeinException("Almost there - add a due date, for example: "
                    + "`deadline submit report /by Friday`.");
        }
        if (remainder.indexOf(" /by", separator + 1) >= 0) {
            throw new FeinException("Use `/by` only once for a deadline.");
        }
        String description = remainder.substring(0, separator).trim();
        String by = remainder.substring(separator + " /by".length()).trim();
        if (description.isEmpty()) {
            throw new FeinException("Add a task description, for example: "
                    + "`deadline submit report /by Friday`.");
        }
        if (by.isEmpty()) {
            throw new FeinException("Add a date after `/by`, for example: "
                    + "`deadline submit report /by Friday`.");
        }
        validateTaskText(description, "task description");
        validateTaskText(by, "due date");
        validateDateTime(by, "due date");
        return new Deadline(description, by);
    }

    /** Parses an event command into a task. */
    private Task parseEvent(String command) throws FeinException {
        String remainder = getRemainder(command, "event");
        if (remainder.isEmpty()) {
            throw new FeinException("Add an event description, for example: "
                    + "`event team meeting /from Mon 2pm /to 4pm`.");
        }

        int fromSeparator = remainder.indexOf(" /from");
        int toSeparator = remainder.indexOf(" /to");
        if (fromSeparator < 0 || toSeparator < 0) {
            if (fromSeparator >= 0 && toSeparator < 0) {
                throw new FeinException("Add an end time after `/to`, for example: "
                        + "`event team meeting /from Mon 2pm /to 4pm`.");
            }
            throw new FeinException("Add a start and end time, for example: "
                    + "`event team meeting /from Mon 2pm /to 4pm`.");
        }
        if (toSeparator < fromSeparator) {
            throw new FeinException("Place `/from` before `/to` in an event.");
        }
        if (remainder.indexOf(" /from", fromSeparator + 1) >= 0
                || remainder.indexOf(" /to", toSeparator + 1) >= 0) {
            throw new FeinException("Use `/from` and `/to` only once for an event.");
        }
        String description = remainder.substring(0, fromSeparator).trim();
        String from = remainder.substring(fromSeparator + " /from".length(), toSeparator).trim();
        String to = remainder.substring(toSeparator + " /to".length()).trim();
        if (description.isEmpty()) {
            throw new FeinException("Add an event description, for example: "
                    + "`event team meeting /from Mon 2pm /to 4pm`.");
        }
        if (from.isEmpty()) {
            throw new FeinException("Add a start time after `/from`, for example: "
                    + "`event team meeting /from Mon 2pm /to 4pm`.");
        }
        if (to.isEmpty()) {
            throw new FeinException("Add an end time after `/to`, for example: "
                    + "`event team meeting /from Mon 2pm /to 4pm`.");
        }
        validateTaskText(description, "event description");
        validateTaskText(from, "start time");
        validateTaskText(to, "end time");
        validateDateTime(from, "start time");
        validateDateTime(to, "end time");
        validateEventOrder(from, to);
        return new Event(description, from, to);
    }

    /** Rejects text that would interfere with Fein's line-based save-file format. */
    private void validateTaskText(String value, String fieldName) throws FeinException {
        if (value.contains("|")) {
            throw new FeinException("A " + fieldName + " cannot contain `|`. Please use another character.");
        }
    }

    /** Rejects a date-looking value when it is not a real date and time. */
    private void validateDateTime(String value, String fieldName) throws FeinException {
        if (DateTimeParser.resemblesDateTime(value) && !DateTimeParser.isValidDateTime(value)) {
            throw new FeinException("The " + fieldName + " is not a valid date and time. "
                    + "Try `2/12/2026 1800`.");
        }
    }

    /** Rejects numeric event times when the end is not after the start. */
    private void validateEventOrder(String from, String to) throws FeinException {
        LocalDateTime start = DateTimeParser.parse(from);
        LocalDateTime end = DateTimeParser.parse(to);
        if (start != null && end != null && !end.isAfter(start)) {
            throw new FeinException("The event end time must be after the start time.");
        }
    }

    /** Returns the text after a command keyword. */
    private String getRemainder(String command, String keyword) {
        return command.length() > keyword.length()
                ? command.substring(keyword.length()).trim() : "";
    }

    /** Parses a mark, unmark, or delete command's task number. */
    public int parseTaskNumber(String command, String action) throws FeinException {
        // The action is supplied by Fein and must be non-blank for the substring below.
        assert command != null : "A command must be present before parsing its task number";
        assert action != null && !action.isBlank() : "A task-number action must be specified";
        String value = command.length() > action.length()
                ? command.substring(action.length()).trim() : "";
        if (value.isEmpty()) {
            throw new FeinException("Provide a task number, for example: `" + action + " 1`.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new FeinException("Use a task number, for example: `" + action + " 1`.");
        }
    }

    /** Returns the keyword from a find command. */
    public String parseFindKeyword(String command) throws FeinException {
        // This method is only called for commands that have already been identified as find commands.
        assert command != null : "A command must be present before parsing its keyword";
        String keyword = command.length() > "find".length()
                ? command.substring("find".length()).trim() : "";
        if (keyword.isEmpty()) {
            throw new FeinException("Tell me what to find, for example: `find book`.");
        }
        return keyword;
    }
}
