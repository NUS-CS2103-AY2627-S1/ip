package bloop.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Priority;
import bloop.task.Todo;

/** Parses user commands and validates their arguments. */
public class Parser {
    private static final DateTimeFormatter EVENT_INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Returns the command type represented by the user input. */
    public static CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /** Returns a to-do task parsed from the user input. */
    public static Todo parseTodo(String input) throws DukeException {
        TaskDetails taskDetails = parseOptionalPriority(detailsAfter(input, CommandType.TODO));
        String description = taskDetails.details;
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(description, taskDetails.priority);
    }

    /** Returns a deadline task parsed from the user input. */
    public static Deadline parseDeadline(String input) throws DukeException {
        String details = detailsAfter(input, CommandType.DEADLINE);
        if (details.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a deadline cannot be empty.");
        }
        int byIndex = details.indexOf(" /by ");
        if (byIndex == -1) {
            throw new DukeException("OOPS!!! Use deadline <description> /by <date/time>.");
        }
        int priorityIndex = details.indexOf(" /priority");
        if (priorityIndex != -1 && priorityIndex < byIndex) {
            throw new DukeException("OOPS!!! Put /priority after the deadline date.");
        }
        TaskDetails taskDetails = parseOptionalPriority(details);
        details = taskDetails.details;
        byIndex = details.indexOf(" /by ");
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new DukeException("OOPS!!! The deadline date/time cannot be empty.");
        }
        try {
            return new Deadline(description, LocalDate.parse(by), taskDetails.priority);
        } catch (DateTimeParseException exception) {
            throw new DukeException("OOPS!!! Use a deadline date in yyyy-MM-dd format.");
        }
    }

    /** Returns an event task parsed from the user input. */
    public static Event parseEvent(String input) throws DukeException {
        String details = detailsAfter(input, CommandType.EVENT);
        if (details.isEmpty()) {
            throw new DukeException("OOPS!!! The description of an event cannot be empty.");
        }
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new DukeException("OOPS!!! Use event <description> /from <start> /to <end>.");
        }
        int priorityIndex = details.indexOf(" /priority");
        if (priorityIndex != -1 && priorityIndex < toIndex) {
            throw new DukeException("OOPS!!! Put /priority after the event end date/time.");
        }
        TaskDetails taskDetails = parseOptionalPriority(details);
        details = taskDetails.details;
        fromIndex = details.indexOf(" /from ");
        toIndex = details.indexOf(" /to ");
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = details.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new DukeException("OOPS!!! Both event start and end date/time are required.");
        }
        try {
            return new Event(description, LocalDateTime.parse(from, EVENT_INPUT_FORMAT),
                    LocalDateTime.parse(to, EVENT_INPUT_FORMAT), taskDetails.priority);
        } catch (DateTimeParseException exception) {
            throw new DukeException("OOPS!!! Use event date/times in yyyy-MM-dd HH:mm format.");
        }
    }

    /** Returns the keyword supplied to a find command. */
    public static String parseFindKeyword(String input) throws DukeException {
        String keyword = detailsAfter(input, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new DukeException("OOPS!!! Please specify a keyword to find.");
        }
        return keyword;
    }

    /** Returns the zero-based task index parsed from the user input. */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount) throws DukeException {
        String taskNumberText = detailsAfter(input, commandType);
        if (taskNumberText.isEmpty()) {
            throw new DukeException("OOPS!!! Please specify a task number to " + commandType.getKeyword() + ".");
        }
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new DukeException("OOPS!!! The task number must refer to an existing task.");
            }
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new DukeException("OOPS!!! The task number must be a whole number.");
        }
    }

    /** Returns the trimmed text after a command keyword. */
    private static String detailsAfter(String input, CommandType commandType) {
        // All callers first identify the command type; otherwise removing this keyword could hide a programming error.
        assert input.equals(commandType.getKeyword()) || input.startsWith(commandType.getKeyword() + " ")
                : "Input must begin with the command keyword";
        return input.substring(commandType.getKeyword().length()).trim();
    }

    /** Separates an optional trailing priority field from task details. */
    private static TaskDetails parseOptionalPriority(String details) throws DukeException {
        int priorityIndex = details.indexOf(" /priority");
        if (priorityIndex == -1) {
            return new TaskDetails(details, Priority.LOW);
        }
        String priorityText = details.substring(priorityIndex + " /priority".length()).trim();
        if (priorityText.isEmpty()) {
            throw new DukeException("OOPS!!! The priority cannot be empty.");
        }
        if (priorityText.contains(" /priority")) {
            throw new DukeException("OOPS!!! Specify priority only once.");
        }
        Priority priority = Priority.fromInput(priorityText);
        if (priority == null) {
            throw new DukeException("OOPS!!! Priority must be low, med, or high.");
        }
        return new TaskDetails(details.substring(0, priorityIndex).trim(), priority);
    }

    /** Contains parsed task details and their optional priority. */
    private static class TaskDetails {
        private final String details;
        private final Priority priority;

        TaskDetails(String details, Priority priority) {
            this.details = details;
            this.priority = priority;
        }
    }
}
