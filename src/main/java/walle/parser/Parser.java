package walle.parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import walle.exceptions.DuplicateArgException;
import walle.exceptions.InvalidDtFormatException;
import walle.exceptions.InvalidTaskTypeException;
import walle.exceptions.MissingArgException;
import walle.exceptions.MissingDescException;
import walle.exceptions.WALLEException;
import walle.task.Deadline;
import walle.task.Event;
import walle.task.Task;
import walle.task.ToDo;

/**
 * Makes sense of raw user input: classifies which command it is, and
 * extracts whatever data (an index, or a new {@link Task}) that command
 * needs.
 */
public class Parser {

    // Lengths of each command word plus its trailing space, e.g. "todo ".length().
    // Derived from the literal itself so they can't silently drift out of sync with it.
    private static final int TODO_PREFIX_LENGTH = "todo ".length();
    private static final int DEADLINE_PREFIX_LENGTH = "deadline ".length();
    private static final int EVENT_PREFIX_LENGTH = "event ".length();
    private static final int FIND_PREFIX_LENGTH = "find ".length();
    private static final int NOTE_PREFIX_LENGTH = "note ".length();

    /**
     * Classifies the given raw input into a {@link CommandType}.
     *
     * @param input the raw command string entered by the user.
     * @return the type of command the input represents.
     * @throws InvalidTaskTypeException if the input does not match any known command.
     */
    public CommandType parseCommandType(String input) throws InvalidTaskTypeException {
        if (input.equals("bye")) {
            return CommandType.BYE;
        } else if (input.equals("list")) {
            return CommandType.LIST;
        } else if (input.matches("mark \\d+")) {
            return CommandType.MARK;
        } else if (input.matches("unmark \\d+")) {
            return CommandType.UNMARK;
        } else if (input.matches("delete \\d+")) {
            return CommandType.DELETE;
        } else if (input.strip().equals("todo") || input.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (input.strip().equals("deadline") || input.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (input.strip().equals("event") || input.startsWith("event ")) {
            return CommandType.EVENT;
        } else if (input.strip().equals("find") || input.startsWith("find ")) {
            return CommandType.FIND;
        } else if (input.strip().equals("notes")) {
            return CommandType.LIST_NOTES;
        } else if (input.strip().equals("note") || input.startsWith("note ")) {
            return CommandType.NOTE;
        } else if (input.matches("deletenote \\d+")) {
            return CommandType.DELETE_NOTE;
        } else {
            throw new InvalidTaskTypeException(input.split(" ")[0]);
        }
    }

    /**
     * Extracts the 1-based index from a {@code mark}/{@code unmark}/{@code delete}/{@code deletenote} command.
     *
     * @param input the raw command string, e.g. {@code "mark 2"}.
     * @return the 1-based index the user referred to.
     */
    public int parseIndex(String input) {
        assert input.matches("(mark|unmark|delete|deletenote) \\d+")
                : "parseIndex should only be called on input parseCommandType already classified as "
                + "MARK/UNMARK/DELETE/DELETE_NOTE.";
        return Integer.valueOf(input.split(" ")[1]);
    }

    /**
     * Extracts the search keyword from a {@code find} command.
     *
     * @param input the raw command string, e.g. {@code "find book"}.
     * @return the keyword to search task descriptions for.
     * @throws MissingDescException if no keyword is given.
     */
    public String parseKeyword(String input) throws MissingDescException {
        assert input.strip().equals("find") || input.startsWith("find ")
                : "parseKeyword should only be called on input parseCommandType already classified as FIND.";
        if (input.strip().equals("find")) {
            throw new MissingDescException("find");
        }
        return input.substring(FIND_PREFIX_LENGTH);
    }

    /**
     * Extracts the note text from a {@code note} command.
     *
     * @param input the raw command string, e.g. {@code "note buy milk"}.
     * @return the text of the note to record.
     * @throws MissingDescException if no text is given.
     */
    public String parseNoteText(String input) throws MissingDescException {
        assert input.strip().equals("note") || input.startsWith("note ")
                : "parseNoteText should only be called on input parseCommandType already classified as NOTE.";
        if (input.strip().equals("note")) {
            throw new MissingDescException("note");
        }
        return input.substring(NOTE_PREFIX_LENGTH);
    }

    /**
     * Builds the {@link Task} described by a {@code todo}/{@code deadline}/{@code event} command.
     *
     * @param type  the command type, as classified by {@link #parseCommandType(String)}.
     * @param input the raw command string.
     * @return the new task to add.
     * @throws WALLEException if the description is missing, or a datetime is malformed.
     */
    public Task parseTask(CommandType type, String input) throws WALLEException {
        switch (type) {
            case TODO:
                return parseTodo(input);

            case DEADLINE:
                return parseDeadline(input);

            case EVENT:
                return parseEvent(input);

            default:
                throw new InvalidTaskTypeException(input.split(" ")[0]);
        }
    }

    // Build a ToDo from a "todo <description>" command
    private Task parseTodo(String input) {
        if (input.strip().equals("todo")) {
            throw new MissingDescException("todo");
        }
        return new ToDo(input.substring(TODO_PREFIX_LENGTH));
    }

    // Build a Deadline from a "deadline <description> /by <datetime>" command
    private Task parseDeadline(String input) {
        if (input.strip().equals("deadline")) {
            throw new MissingDescException("deadline");
        }
        String[] deadlineParts = input.substring(DEADLINE_PREFIX_LENGTH).split(" /by ");
        if (deadlineParts.length < 2) {
            throw new MissingArgException("deadline", "/by");
        }
        if (deadlineParts.length > 2) {
            throw new DuplicateArgException("deadline", "/by");
        }
        return new Deadline(deadlineParts[0], parseDateTime(deadlineParts[1]));
    }

    // Build an Event from an "event <description> /from <datetime> /to <datetime>" command
    private Task parseEvent(String input) {
        if (input.strip().equals("event")) {
            throw new MissingDescException("event");
        }
        String[] eventParts = input.substring(EVENT_PREFIX_LENGTH).split(" /from ");
        if (eventParts.length < 2) {
            throw new MissingArgException("event", "/from");
        }
        if (eventParts.length > 2) {
            throw new DuplicateArgException("event", "/from");
        }
        String[] eventTimes = eventParts[1].split(" /to ");
        if (eventTimes.length < 2) {
            throw new MissingArgException("event", "/to");
        }
        if (eventTimes.length > 2) {
            throw new DuplicateArgException("event", "/to");
        }
        return new Event(eventParts[0], parseDateTime(eventTimes[0]), parseDateTime(eventTimes[1]));
    }

    // Parse user's datetime input (format: yyyy-MM-dd HHmm) into a LocalDateTime
    private LocalDateTime parseDateTime(String userDt) throws InvalidDtFormatException {
        try {
            return LocalDateTime.parse(userDt, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
        } catch (DateTimeException e) {
            throw new InvalidDtFormatException();
        }
    }

}
