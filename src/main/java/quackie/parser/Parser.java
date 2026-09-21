package quackie.parser;

import quackie.CommandType;
import quackie.command.AddCommand;
import quackie.command.Command;
import quackie.command.DeleteCommand;
import quackie.command.ExitCommand;
import quackie.command.FindCommand;
import quackie.command.ListCommand;
import quackie.command.MarkCommand;
import quackie.command.UnknownCommand;
import quackie.command.UnmarkCommand;
import quackie.command.UpdateCommand;
import quackie.task.Deadline;
import quackie.task.Event;
import quackie.task.Task;
import quackie.task.TaskList;
import quackie.task.ToDo;

/**
 * Interprets raw commands and creates the corresponding task objects.
 */
public class Parser {
    /** Creates a parser for raw Quackie commands. */
    public Parser() {
    }

    /**
     * Creates an executable command from raw user input.
     *
     * @param command the complete command entered by the user
     * @param tasks the current task list, used to validate task indexes
     * @return the executable command represented by the input
     * @throws IllegalArgumentException if a task-creation command is malformed
     */
    public Command parse(String command, TaskList tasks) {
        assert command != null : "Command to parse must not be null";
        assert tasks != null : "Task list used for parsing must not be null";

        String normalisedCommand = normaliseWhitespace(command);
        return switch (parseCommandType(normalisedCommand)) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseFindKeyword(normalisedCommand));
            case DELETE -> new DeleteCommand(parseTaskIndex(normalisedCommand, "delete", tasks));
            case MARK -> new MarkCommand(parseTaskIndex(normalisedCommand, "mark", tasks));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(normalisedCommand, "unmark", tasks));
            case UPDATE -> parseUpdateCommand(normalisedCommand, tasks);
            case EVENT, DEADLINE, TODO -> new AddCommand(parseTask(normalisedCommand));
            case UNKNOWN -> new UnknownCommand();
        };
    }

    /**
     * Identifies the command represented by raw user input.
     *
     * @param input the complete command entered by the user
     * @return the command type represented by the input
     */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param command the complete task-creation command
     * @return the task described by the command
     * @throws IllegalArgumentException if the command is malformed
     */
    public Task parseTask(String command) {
        String normalisedCommand = normaliseWhitespace(command);
        return switch (parseCommandType(normalisedCommand)) {
            case EVENT -> parseEvent(normalisedCommand);
            case DEADLINE -> parseDeadline(normalisedCommand);
            case TODO -> parseToDo(normalisedCommand);
            default -> throw new IllegalArgumentException("Command does not create a task");
        };
    }

    /**
     * Converts a task number in a mark, unmark, or delete command into a zero-based index.
     *
     * @param command the complete command entered by the user
     * @param keyword the command keyword, such as {@code mark}, {@code unmark}, or {@code delete}
     * @param tasks the current task list
     * @return the corresponding zero-based index, or {@code -1} for an invalid task number
     */
    public int parseTaskIndex(String command, String keyword, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(command.substring(keyword.length()).trim());
            return taskNumber >= 1 && taskNumber <= tasks.size() ? taskNumber - 1 : -1;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param command the complete find command
     * @return the non-empty search keyword
     * @throws IllegalArgumentException if the keyword is missing
     */
    private String parseFindKeyword(String command) {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Parses the task number and replacement task from an update command.
     *
     * @param command the complete update command
     * @param tasks the current task list
     * @return a command that replaces the selected task
     * @throws IllegalArgumentException if the index or replacement task is invalid
     */
    private Command parseUpdateCommand(String command, TaskList tasks) {
        String updateDetails = command.substring("update".length()).trim();
        int separator = updateDetails.indexOf(' ');
        if (separator < 1) {
            throw new IllegalArgumentException(
                    "An update needs a task number and replacement todo, deadline, or event command.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(updateDetails.substring(0, separator));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a valid task number.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new IllegalArgumentException("Please provide a valid task number.");
        }

        String replacementCommand = updateDetails.substring(separator + 1).trim();
        CommandType replacementType = parseCommandType(replacementCommand);
        if (replacementType != CommandType.TODO
                && replacementType != CommandType.DEADLINE
                && replacementType != CommandType.EVENT) {
            throw new IllegalArgumentException(
                    "An update needs a replacement todo, deadline, or event command.");
        }
        return new UpdateCommand(taskNumber - 1, parseTask(replacementCommand));
    }

    /**
     * Parses an event command and validates its required details.
     *
     * @param command the complete event command
     * @return the parsed event task
     * @throws IllegalArgumentException if the description or time details are missing
     */
    private Task parseEvent(String command) {
        String eventDetails = command.substring("event".length()).trim();
        if (countOccurrences(eventDetails, " /from ") > 1
                || countOccurrences(eventDetails, " /to ") > 1) {
            throw new IllegalArgumentException(
                    "An event needs exactly one /from time and one /to time.");
        }
        int fromMarker = eventDetails.indexOf(" /from ");
        int toMarker = eventDetails.indexOf(" /to ", fromMarker + " /from ".length());
        String eventDescription = fromMarker >= 0
                ? eventDetails.substring(0, fromMarker) : eventDetails;
        String from = fromMarker >= 0 && toMarker >= 0
                ? eventDetails.substring(fromMarker + " /from ".length(), toMarker) : "";
        String to = toMarker >= 0 ? eventDetails.substring(toMarker + " /to ".length()) : "";
        if (eventDescription.isBlank() || from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("An event needs a description, /from time, and /to time.");
        }
        return new Event(eventDescription.trim(), from.trim(), to.trim());
    }

    /**
     * Parses a deadline command and validates its required details.
     *
     * @param command the complete deadline command
     * @return the parsed deadline task
     * @throws IllegalArgumentException if the description or deadline value is invalid
     */
    private Task parseDeadline(String command) {
        String deadlineDetails = command.substring("deadline".length()).trim();
        if (countOccurrences(deadlineDetails, " /by ") > 1) {
            throw new IllegalArgumentException("A deadline needs exactly one /by date or time.");
        }
        int byMarker = deadlineDetails.indexOf(" /by ");
        String deadlineDescription = byMarker >= 0
                ? deadlineDetails.substring(0, byMarker) : deadlineDetails;
        String by = byMarker >= 0
                ? deadlineDetails.substring(byMarker + " /by ".length()) : "";
        if (deadlineDescription.isBlank() || by.isBlank()) {
            throw new IllegalArgumentException("A deadline needs a description and a /by date or time.");
        }
        try {
            return new Deadline(deadlineDescription.trim(), by.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Please enter a valid deadline date (yyyy-MM-dd)"
                    + " or date and time (d/M/yyyy HHmm).");
        }
    }

    /**
     * Parses a ToDo command and validates its description.
     *
     * @param command the complete ToDo command
     * @return the parsed ToDo task
     * @throws IllegalArgumentException if the description is empty
     */
    private Task parseToDo(String command) {
        String description = command.substring("todo".length()).trim();
        if (description.isBlank()) {
            throw new IllegalArgumentException("A ToDo needs a description.");
        }
        return new ToDo(description);
    }

    /** Collapses surrounding and repeated whitespace without changing command meaning. */
    private String normaliseWhitespace(String command) {
        return command.strip().replaceAll("\\s+", " ");
    }

    /** Counts non-overlapping occurrences of a marker in a command. */
    private int countOccurrences(String value, String marker) {
        int count = 0;
        int searchFrom = 0;
        while ((searchFrom = value.indexOf(marker, searchFrom)) >= 0) {
            count++;
            searchFrom += marker.length();
        }
        return count;
    }
}
