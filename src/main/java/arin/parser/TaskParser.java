package arin.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import arin.exception.ArinException;
import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;

/**
 * Validates task-creation commands and constructs their corresponding tasks.
 */
public final class TaskParser {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";

    private TaskParser() {
    }

    /**
     * Parses a to-do, deadline, or event command.
     *
     * @param command Complete command entered by the user.
     * @return Task described by the command.
     * @throws ArinException If the command is unknown or its details are invalid.
     */
    public static Task parse(String command) throws ArinException {
        if (command.contains("|") || command.contains("\n") || command.contains("\r")) {
            throw new ArinException("Please keep a task on one line without the reserved | character.");
        }
        command = command.trim();
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseTodo(command.substring("todo".length()).trim());
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadline(command.substring("deadline".length()).trim());
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return parseEvent(command.substring("event".length()).trim());
        }

        throw new ArinException("I don't recognise that command. Try todo, deadline, event, list, mark, unmark, "
                + "delete, find, sort, or bye.");
    }

    /**
     * Parses the description of a to-do command.
     *
     * @param description Trimmed task description.
     * @return To-do task with the supplied description.
     * @throws ArinException If the description is empty.
     */
    private static Todo parseTodo(String description) throws ArinException {
        if (description.isEmpty()) {
            throw new ArinException("Please include a description after 'todo'.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline's description and ISO calendar date.
     *
     * @param details Trimmed text following the deadline command word.
     * @return Deadline with the supplied description and date.
     * @throws ArinException If a required part is missing or the date is invalid.
     */
    private static Deadline parseDeadline(String details) throws ArinException {
        int byIndex = details.indexOf(DEADLINE_SEPARATOR);
        if (byIndex == -1) {
            throw new ArinException("Use: deadline <description> /by <date and time>.");
        }

        String description = details.substring(0, byIndex).trim();
        String dateText = details.substring(byIndex + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new ArinException("A deadline needs both a description and a due date.");
        }

        try {
            LocalDate date = LocalDate.parse(dateText);
            return new Deadline(description, date);
        } catch (DateTimeParseException e) {
            throw new ArinException("Please use the date format yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /**
     * Parses an event's description and start and end time descriptions.
     *
     * @param details Trimmed text following the event command word.
     * @return Event with the supplied description and time range.
     * @throws ArinException If a required part is missing or the separators are out of order.
     */
    private static Event parseEvent(String details) throws ArinException {
        int fromIndex = details.indexOf(EVENT_START_SEPARATOR);
        int toIndex = details.indexOf(EVENT_END_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new ArinException("Use: event <description> /from <start> /to <end>.");
        }

        String description = details.substring(0, fromIndex).trim();
        String start = details.substring(fromIndex + EVENT_START_SEPARATOR.length(), toIndex).trim();
        String end = details.substring(toIndex + EVENT_END_SEPARATOR.length()).trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new ArinException("An event needs a description, start time, and end time.");
        }
        if (start.contains("/from") || end.contains("/from") || end.contains("/to")) {
            throw new ArinException("Use each event separator /from and /to exactly once.");
        }
        return new Event(description, start, end);
    }
}
