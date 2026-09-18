package habi;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Parses and validates commands entered by the user. */
public class Parser {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String NOTE_COMMAND = "note";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";
    private static final String TAB_ERROR =
            "OOPS! HABI cannot save text containing tab characters.";

    /** Creates a command parser. */
    public Parser() {
    }

    /**
     * Returns the command word at the start of a non-empty command.
     *
     * @param command Trimmed command entered by the user.
     * @return The first word of the command.
     * @throws HabiException If the command is empty.
     */
    public static String getKeyword(String command) throws HabiException {
        if (command.isEmpty()) {
            throw new HabiException("OOPS! Please enter a command.");
        }
        int firstSpace = command.indexOf(' ');
        return firstSpace < 0 ? command : command.substring(0, firstSpace);
    }

    /**
     * Parses a todo command with a non-empty description.
     *
     * @param command Todo command entered by the user.
     * @return The parsed todo.
     * @throws HabiException If the description is empty.
     */
    public static Todo parseTodo(String command) throws HabiException {
        String description = getArguments(command, TODO_COMMAND);
        if (description.isEmpty()) {
            throw new HabiException("OOPS! The todo description cannot be empty.");
        }
        validatePersistedText(description);
        return new Todo(description);
    }

    /**
     * Parses a note command with non-empty text.
     *
     * @param command Note command entered by the user.
     * @return The parsed note.
     * @throws HabiException If the note text is empty.
     */
    public static Note parseNote(String command) throws HabiException {
        String content = getArguments(command, NOTE_COMMAND);
        if (content.isEmpty()) {
            throw new HabiException("OOPS! The note cannot be empty.");
        }
        validatePersistedText(content);
        return new Note(content);
    }

    /**
     * Parses a deadline command containing an ISO date.
     *
     * @param command Deadline command entered by the user.
     * @return The parsed deadline.
     * @throws HabiException If the description, separator, or date is invalid.
     */
    public static Deadline parseDeadline(String command) throws HabiException {
        String arguments = getArguments(command, DEADLINE_COMMAND);
        int byPosition = arguments.indexOf(DEADLINE_SEPARATOR);
        int byStart = byPosition + DEADLINE_SEPARATOR.length();
        if (byPosition <= 0 || byPosition != arguments.lastIndexOf(DEADLINE_SEPARATOR)
                || byStart >= arguments.length()) {
            throw new HabiException("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
        String description = arguments.substring(0, byPosition).trim();
        String by = arguments.substring(byStart).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new HabiException("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
        validatePersistedText(description);
        validatePersistedText(by);
        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException exception) {
            throw new HabiException("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
    }

    /**
     * Parses an event command containing non-empty start and end values.
     *
     * @param command Event command entered by the user.
     * @return The parsed event.
     * @throws HabiException If a required event value is missing.
     */
    public static Event parseEvent(String command) throws HabiException {
        String arguments = getArguments(command, EVENT_COMMAND);
        int fromPosition = arguments.indexOf(EVENT_FROM_SEPARATOR);
        int fromStart = fromPosition + EVENT_FROM_SEPARATOR.length();
        int toPosition = arguments.indexOf(EVENT_TO_SEPARATOR);
        int toStart = toPosition + EVENT_TO_SEPARATOR.length();
        boolean isInvalid = fromPosition <= 0 || toPosition < fromStart
                || fromPosition != arguments.lastIndexOf(EVENT_FROM_SEPARATOR)
                || toPosition != arguments.lastIndexOf(EVENT_TO_SEPARATOR)
                || toStart >= arguments.length();
        if (isInvalid) {
            throw new HabiException("OOPS! Use: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromPosition).trim();
        String from = arguments.substring(fromStart, toPosition).trim();
        String to = arguments.substring(toStart).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new HabiException("OOPS! Use: event DESCRIPTION /from START /to END");
        }
        validatePersistedText(description);
        validatePersistedText(from);
        validatePersistedText(to);
        return new Event(description, from, to);
    }

    /**
     * Rejects a command that supplies text after a keyword that has no parameters.
     *
     * @param command Command to validate.
     * @param keyword Command keyword at the start of the command.
     * @throws HabiException If the command contains an argument.
     */
    public static void requireNoArguments(String command, String keyword) throws HabiException {
        if (!getArguments(command, keyword).isEmpty()) {
            throw new HabiException("OOPS! " + keyword + " does not take any arguments.");
        }
    }

    /**
     * Parses and validates a one-based task number.
     *
     * @param command Command containing the task number.
     * @param keyword Command word preceding the number.
     * @param taskCount Number of tasks available.
     * @return The corresponding zero-based task index.
     * @throws HabiException If the number is missing, malformed, or out of range.
     */
    public static int parseTaskIndex(String command, String keyword, int taskCount)
            throws HabiException {
        return parseItemIndex(command, keyword, taskCount, "task");
    }

    /**
     * Parses and validates a one-based note number.
     *
     * @param command Command containing the note number.
     * @param noteCount Number of notes available.
     * @return The corresponding zero-based note index.
     * @throws HabiException If the number is missing, malformed, or out of range.
     */
    public static int parseNoteIndex(String command, int noteCount) throws HabiException {
        return parseItemIndex(command, "delete-note", noteCount, "note");
    }

    /**
     * Parses and validates a one-based item number for a command.
     *
     * @param command Command containing the item number.
     * @param keyword Command word preceding the number.
     * @param itemCount Number of items available.
     * @param itemName Singular name of the item.
     * @return The corresponding zero-based item index.
     * @throws HabiException If the number is missing, malformed, or out of range.
     */
    private static int parseItemIndex(String command, String keyword, int itemCount,
            String itemName) throws HabiException {
        String argument = getArguments(command, keyword);
        if (argument.isEmpty()) {
            throw new HabiException(
                    "OOPS! Please provide a " + itemName + " number for " + keyword + ".");
        }
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber < 1 || taskNumber > itemCount) {
                throw new HabiException(
                        "OOPS! " + capitalize(itemName) + " number " + taskNumber
                                + " is out of range.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new HabiException("OOPS! The " + itemName + " number must be a whole number.");
        }
    }

    /**
     * Parses a find command with a non-empty keyword.
     *
     * @param command Find command entered by the user.
     * @return The keyword to find.
     * @throws HabiException If the keyword is empty.
     */
    public static String parseFindKeyword(String command) throws HabiException {
        String keyword = getArguments(command, FIND_COMMAND);
        if (keyword.isEmpty()) {
            throw new HabiException("OOPS! The find keyword cannot be empty.");
        }
        return keyword;
    }

    /**
     * Returns the text following a command keyword.
     *
     * @param command Command entered by the user.
     * @param keyword Command keyword at the start of the command.
     * @return Trimmed command arguments.
     */
    private static String getArguments(String command, String keyword) {
        return command.substring(keyword.length()).trim();
    }

    /**
     * Rejects tabs because they delimit fields in HABI's tab-separated data file.
     *
     * @param text Command text that will be written to storage.
     * @throws HabiException If the text contains a tab character.
     */
    private static void validatePersistedText(String text) throws HabiException {
        if (text.contains("\t")) {
            throw new HabiException(TAB_ERROR);
        }
    }

    /**
     * Returns an item name with an uppercase initial for response text.
     *
     * @param itemName Lowercase singular item name.
     * @return Item name with an uppercase initial.
     */
    private static String capitalize(String itemName) {
        return itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
    }
}
