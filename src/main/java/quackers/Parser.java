package quackers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets raw user commands and converts their arguments into task data.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Determines the type of a command.
     *
     * @param command the complete command entered by the user.
     * @return the matching command type
     * @throws QuackersException if the command keyword is unknown
     */
    public static CommandType parseCommandType(String command) throws QuackersException {
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (hasKeyword(command, "find")) {
            return CommandType.FIND;
        }
        if (hasKeyword(command, "mark")) {
            return CommandType.MARK;
        }
        if (hasKeyword(command, "unmark")) {
            return CommandType.UNMARK;
        }
        if (hasKeyword(command, "delete")) {
            return CommandType.DELETE;
        }
        if (hasKeyword(command, "todo")) {
            return CommandType.TODO;
        }
        if (hasKeyword(command, "note")) {
            return CommandType.NOTE;
        }
        if (hasKeyword(command, "deadline")) {
            return CommandType.DEADLINE;
        }
        if (hasKeyword(command, "event")) {
            return CommandType.EVENT;
        }
        throw new QuackersException("Quack? I don't know what that means :-(");
    }

    /**
     * Extracts a zero-based task index from a numbered command.
     *
     * @param command the complete command.
     * @param keyword the command keyword to remove.
     * @return the zero-based task index
     * @throws QuackersException if the task number is missing or not an integer
     */
    public static int parseTaskIndex(String command, String keyword) throws QuackersException {
        String numberText = command.substring(keyword.length()).trim();
        try {
            return Integer.parseInt(numberText) - 1;
        } catch (NumberFormatException error) {
            throw new QuackersException("Quack? Please enter a valid task number.");
        }
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command the complete find command.
     * @return the keyword to search for.
     * @throws QuackersException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws QuackersException {
        return parseTextAfterKeyword(command, "find", "Quack? Give me a keyword to find!");
    }

    /**
     * Creates a to-do from its command.
     *
     * @param command the complete to-do command.
     * @return the parsed to-do task
     * @throws QuackersException if the description is missing
     */
    public static Todo parseTodo(String command) throws QuackersException {
        return new Todo(parseTextAfterKeyword(command, "todo", "Quack? Give me a todo description!"));
    }

    /**
     * Creates a note from its command.
     *
     * @param command the complete note command
     * @return the parsed note
     * @throws QuackersException if the note text is missing
     */
    public static Note parseNote(String command) throws QuackersException {
        return new Note(parseTextAfterKeyword(command, "note", "Quack? Give me note text!"));
    }

    /**
     * Creates a deadline from its command.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline task
     * @throws QuackersException if a field or date is invalid
     */
    public static Deadline parseDeadline(String command) throws QuackersException {
        int byMarker = command.indexOf(" /by ");
        if (byMarker < 0) {
            throw new QuackersException("Quack? Use /by to give the deadline.");
        }

        String description = command.substring("deadline".length(), byMarker).trim();
        String byText = command.substring(byMarker + " /by ".length()).trim();
        requireText(description, "Quack? Give me a deadline description!");
        requireText(byText, "Quack? Give me a deadline date!");
        return new Deadline(description, parseDeadlineDate(byText));
    }

    /**
     * Creates an event from its command.
     *
     * @param command the complete event command.
     * @return the parsed event task
     * @throws QuackersException if a required field is missing
     */
    public static Event parseEvent(String command) throws QuackersException {
        int fromMarker = command.indexOf(" /from ");
        int toMarker = command.indexOf(" /to ");
        if (fromMarker < 0 || toMarker < 0 || toMarker < fromMarker) {
            throw new QuackersException("Quack? Use /from START /to END for an event.");
        }

        String description = command.substring("event".length(), fromMarker).trim();
        String from = command.substring(fromMarker + " /from ".length(), toMarker).trim();
        String to = command.substring(toMarker + " /to ".length()).trim();
        requireText(description, "Quack? Give me an event description!");
        requireText(from, "Quack? Give me an event start time!");
        requireText(to, "Quack? Give me an event end time!");
        return new Event(description, from, to);
    }

    private static boolean hasKeyword(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    private static LocalDate parseDeadlineDate(String dateText) throws QuackersException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException error) {
            throw new QuackersException("Quack? Use yyyy-MM-dd for the deadline date.");
        }
    }

    /**
     * Extracts non-empty text immediately following a command keyword.
     *
     * @param command the complete command
     * @param keyword the command keyword to remove
     * @param errorMessage the error to report if no text remains
     * @return the trimmed command argument
     * @throws QuackersException if the command has no argument text
     */
    private static String parseTextAfterKeyword(String command, String keyword, String errorMessage)
            throws QuackersException {
        String text = command.substring(keyword.length()).trim();
        requireText(text, errorMessage);
        return text;
    }

    private static void requireText(String text, String errorMessage) throws QuackersException {
        if (text.isEmpty()) {
            throw new QuackersException(errorMessage);
        }
    }
}
