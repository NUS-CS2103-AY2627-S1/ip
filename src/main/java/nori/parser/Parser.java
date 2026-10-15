package nori.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;
import nori.task.Task;
import nori.task.Todo;

/**
 * Interprets user commands and converts their arguments into application values.
 */
public class Parser {
    private static final DateTimeFormatter ISO_DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Determines the operation requested by a command.
     *
     * @param command trimmed user command.
     * @return matching command type, or {@link CommandType#UNKNOWN}
     */
    public CommandType parseCommandType(String command) {
        return CommandType.from(command);
    }

    /**
     * Creates the task described by a todo, deadline, or event command.
     *
     * @param command complete task-creation command.
     * @return task described by the command
     * @throws NoriException if a required argument is missing or invalid
     */
    public Task parseTask(String command) throws NoriException {
        // Match CommandType's whitespace handling without changing spaces inside descriptions.
        command = command.trim().replaceFirst("\\s+", " ");
        if (command.equals("todo")) {
            throw new NoriException("The description of a todo cannot be empty.");
        }
        if (command.startsWith("todo ")) {
            String description = command.substring(5).trim();
            if (description.isEmpty()) {
                throw new NoriException("The description of a todo cannot be empty.");
            }
            return new Todo(description);
        }
        if (command.equals("deadline")) {
            throw new NoriException("Use: deadline DESCRIPTION /by yyyy-MM-dd HHmm or d/M/yyyy HHmm");
        }
        if (command.startsWith("deadline ")) {
            return parseDeadline(command);
        }
        if (command.equals("event")) {
            throw new NoriException("Use: event DESCRIPTION /from START /to END");
        }
        return parseEvent(command);
    }

    /**
     * Converts a one-based task number in a command to a zero-based list index.
     *
     * @param command complete mark, unmark, or delete command.
     * @param commandWord command word whose argument is being parsed.
     * @param taskCount number of tasks currently available.
     * @return zero-based task index
     * @throws NoriException if the task number is absent, invalid, or out of range
     */
    public int parseTaskIndex(String command, String commandWord, int taskCount)
            throws NoriException {
        String indexText = command.substring(commandWord.length()).trim();
        if (indexText.isEmpty()) {
            throw new NoriException("Please provide a task number after " + commandWord + ".");
        }
        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(indexText);
        } catch (NumberFormatException exception) {
            throw new NoriException("The task number must be a whole number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NoriException("That task number is not in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command complete find command.
     * @return keyword to match against task descriptions
     * @throws NoriException if the keyword is empty
     */
    public String parseFindKeyword(String command) throws NoriException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NoriException("Please provide a keyword after find.");
        }
        return keyword;
    }

    /** Parses a deadline command and validates its date and time. */
    private Deadline parseDeadline(String command) throws NoriException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex < 0) {
            throw new NoriException(
                    "Use: deadline DESCRIPTION /by yyyy-MM-dd HHmm or d/M/yyyy HHmm");
        }
        String description = byIndex < 9 ? "" : command.substring(9, byIndex).trim();
        String by = command.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new NoriException("A deadline needs both a description and /by value.");
        }
        LocalDateTime dateTime = parseDeadlineDateTime(by);
        return new Deadline(description, dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    /** Parses a deadline value in either supported date-time format. */
    private LocalDateTime parseDeadlineDateTime(String byText) throws NoriException {
        try {
            return LocalDateTime.parse(byText, ISO_DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDateTime.parse(byText, SLASH_DATE_TIME_INPUT_FORMAT);
            } catch (DateTimeParseException nestedException) {
                throw new NoriException(
                        "Use yyyy-MM-dd HHmm or d/M/yyyy HHmm for deadline dates and times.");
            }
        }
    }

    /** Parses an event command and validates its description and range text. */
    private Event parseEvent(String command) throws NoriException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < fromIndex + 7) {
            throw new NoriException("Use: event DESCRIPTION /from START /to END");
        }
        String description = fromIndex < 6 ? "" : command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new NoriException("An event needs a description, /from value, and /to value.");
        }
        return new Event(description, from, to);
    }
}
