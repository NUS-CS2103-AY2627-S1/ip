package samantha.parser;

import java.time.LocalDate;
import java.util.Arrays;

import samantha.command.AddDeadlineCommand;
import samantha.command.AddEventCommand;
import samantha.command.AddNoteCommand;
import samantha.command.AddTodoCommand;
import samantha.command.Command;
import samantha.command.DeleteCommand;
import samantha.command.DeleteNoteCommand;
import samantha.command.EditNoteCommand;
import samantha.command.ExitCommand;
import samantha.command.FindCommand;
import samantha.command.HelpCommand;
import samantha.command.ListCommand;
import samantha.command.ListNotesCommand;
import samantha.command.MarkCommand;
import samantha.command.UndoCommand;
import samantha.command.UnmarkCommand;
import samantha.exception.InputException;
import samantha.exception.UnknownCommandException;
import samantha.model.DateTimeValue;

/**
 * Interprets the command word and arguments entered by the user.
 */
public class Parser {
    private static final int COMMAND_WORD_COUNT = 1;
    private static final int MAXIMUM_ARGUMENT_PARTS = 2;
    private static final int COMMAND_ARGUMENT_INDEX = 1;
    private static final int NOTE_TEXT_INDEX = 2;

    /**
     * Creates a parser for Samantha commands.
     */
    public Parser() {
    }

    /**
     * Represents the commands supported by Samantha.
     */
    private enum CommandType {
        BYE, HELP, LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, FIND,
        NOTE, NOTES, EDIT_NOTE, DELETE_NOTE, UNDO
    }

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param fullCommand complete command entered by the user
     * @return an executable command
     * @throws InputException if the command or its arguments are invalid
     */
    public static Command parse(String fullCommand) throws InputException {
        assert fullCommand != null : "A complete command must not be null";
        String[] parts = splitCommand(fullCommand);
        assert parts.length > 0 : "A split command must contain a command word";
        return switch (parseCommandType(parts[0])) {
            case BYE -> new ExitCommand();
            case HELP -> new HelpCommand();
            case LIST -> new ListCommand(parseListDate(parts));
            case TODO -> new AddTodoCommand(parseDescription(parts));
            case DEADLINE -> {
                String[] details = parseDeadlineDetails(parts);
                yield new AddDeadlineCommand(details[0], details[1]);
            }
            case EVENT -> {
                String[] details = parseEventDetails(parts);
                yield new AddEventCommand(details[0], details[1], details[2]);
            }
            case MARK -> new MarkCommand(parseTaskId(parts));
            case UNMARK -> new UnmarkCommand(parseTaskId(parts));
            case DELETE -> new DeleteCommand(parseTaskId(parts));
            case FIND -> new FindCommand(parseFindKeyword(parts));
            case NOTE -> new AddNoteCommand(parseDescription(parts));
            case NOTES -> new ListNotesCommand();
            case EDIT_NOTE -> new EditNoteCommand(parseNoteId(parts), parseNoteText(parts));
            case DELETE_NOTE -> new DeleteNoteCommand(parseNoteId(parts));
            case UNDO -> parseUndo(parts);
        };
    }

    /**
     * Converts a command word into its supported command type.
     *
     * @param word command word to interpret
     * @return the matching command type
     * @throws InputException if the word does not name a supported command
     */
    private static CommandType parseCommandType(String word) throws InputException {
        try {
            return CommandType.valueOf(word.replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException();
        }
    }

    /**
     * Parses the undo command and rejects unexpected arguments.
     *
     * @param parts words from the user command
     * @return an undo command
     * @throws InputException if extra arguments were supplied
     */
    private static Command parseUndo(String... parts) throws InputException {
        if (parts.length > COMMAND_WORD_COUNT) {
            throw new InputException("You entered too many parameters for this operation");
        }
        return new UndoCommand();
    }

    /**
     * Splits a raw user command into words, ignoring extra surrounding whitespace.
     *
     * @param input raw command entered by the user
     * @return the command words
     */
    public static String[] splitCommand(String input) {
        return input.trim().split("\\s+");
    }

    /**
     * Extracts the description following a command word.
     *
     * @param parts words from the user command
     * @return the description, which may be empty
     */
    public static String parseDescription(String... parts) {
        return String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
    }

    /**
     * Extracts and validates the keyword following a {@code find} command.
     *
     * @param parts words from the user command
     * @return the keyword to search for
     * @throws InputException if no keyword was supplied
     */
    public static String parseFindKeyword(String... parts) throws InputException {
        String keyword = parseDescription(parts).trim();
        if (keyword.isEmpty()) {
            throw new InputException("You forgot to mention the keyword to search for.");
        }
        return keyword;
    }

    /**
     * Extracts a deadline's description and its value after {@code /by}.
     *
     * @param parts words from a deadline command
     * @return a two-element array containing the description and deadline
     * @throws InputException if the command does not contain {@code /by}
     */
    public static String[] parseDeadlineDetails(String... parts) throws InputException {
        String[] segments = parseDescription(parts).split("\\s*/by\\s*", 2);
        if (segments.length < 2) {
            throw new InputException("You forgot to include /by for this deadline.");
        }
        assert segments.length == 2 : "A deadline must have exactly two segments";
        return new String[] {segments[0].trim(), segments[1].trim()};
    }

    /**
     * Extracts an event's description, start time, and end time.
     *
     * @param parts words from an event command
     * @return a three-element array containing the description, start time, and end time
     * @throws InputException if the command does not contain exactly one {@code /from}
     *         and one {@code /to}
     */
    public static String[] parseEventDetails(String... parts) throws InputException {
        String[] segments = parseDescription(parts).split("/from|/to");
        if (segments.length < 3) {
            throw new InputException("You forgot to include /from and /to for this event.");
        }
        if (segments.length > 3) {
            throw new InputException("An event can only have one /from and one /to.");
        }
        assert segments.length == 3 : "An event must have exactly three segments";
        return new String[] {segments[0].trim(), segments[1].trim(), segments[2].trim()};
    }

    /**
     * Parses the optional date argument supplied to {@code list}.
     *
     * @param parts words from a list command
     * @return the requested date, or {@code null} when no date was given
     * @throws InputException if too many arguments were supplied or the date is invalid
     */
    public static LocalDate parseListDate(String... parts) throws InputException {
        if (parts.length > MAXIMUM_ARGUMENT_PARTS) {
            throw new InputException("You entered too many parameters for this operation");
        }
        if (parts.length == COMMAND_WORD_COUNT) {
            return null;
        }
        return DateTimeValue.parseDate(parts[COMMAND_ARGUMENT_INDEX]);
    }

    /**
     * Parses the one-based task ID supplied to a task-ID command.
     *
     * @param parts words from the user command
     * @return the parsed task ID
     * @throws InputException if the command has a missing, extra, or non-numeric ID
     */
    public static int parseTaskId(String... parts) throws InputException {
        if (parts.length > MAXIMUM_ARGUMENT_PARTS) {
            throw new InputException("You entered too many parameters for this operation");
        }
        if (parts.length == COMMAND_WORD_COUNT) {
            throw new InputException("You forgot to mention the id of the task");
        }
        try {
            return Integer.parseInt(parts[COMMAND_ARGUMENT_INDEX]);
        } catch (NumberFormatException e) {
            throw new InputException("You need to enter a number for the task id.");
        }
    }

    /**
     * Parses the one-based note ID supplied to a note command.
     *
     * @param parts words from the user command
     * @return the parsed note ID
     * @throws InputException if the command has a missing, extra, or non-numeric ID
     */
    public static int parseNoteId(String... parts) throws InputException {
        if (parts.length > MAXIMUM_ARGUMENT_PARTS
                && !parts[0].equalsIgnoreCase("edit-note")) {
            throw new InputException("You entered too many parameters for this operation");
        }
        if (parts.length == COMMAND_WORD_COUNT) {
            throw new InputException("You forgot to mention the id of the note");
        }
        try {
            return Integer.parseInt(parts[COMMAND_ARGUMENT_INDEX]);
        } catch (NumberFormatException e) {
            throw new InputException("You need to enter a number for the note id.");
        }
    }

    /**
     * Extracts and validates replacement text following an edit-note command.
     *
     * @param parts words from the user command
     * @return replacement note content
     * @throws InputException if no replacement text was supplied
     */
    public static String parseNoteText(String... parts) throws InputException {
        if (parts.length <= NOTE_TEXT_INDEX) {
            throw new InputException("You forgot to mention the new note text.");
        }
        return String.join(" ", Arrays.copyOfRange(parts, NOTE_TEXT_INDEX, parts.length));
    }
}
