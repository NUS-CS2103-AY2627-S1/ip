package min.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import min.exception.MinException;
import min.note.Note;
import min.task.Deadline;
import min.task.Event;
import min.task.Todo;
import min.ui.Messages;

/**
 * Parses and validates commands entered by the user.
 */
public class Parser {
    private static final String FILE_FIELD_SEPARATOR = " | ";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    /**
     * Identifies the command represented by the input.
     *
     * @param input The command entered by the user.
     * @return The identified command.
     * @throws MinException If the input does not begin with a valid command word.
     */
    public Command parseCommand(String input) throws MinException {
        return Arrays.stream(Command.values())
                .filter(command -> command.matches(input))
                .findFirst()
                .orElseThrow(() -> new MinException(
                        Messages.formatInvalidCommand(Command.getAllWords())));
    }

    /**
     * Parses a valid task number into a zero-based task index.
     *
     * @param input The command entered by the user.
     * @param command The command that requires a task number.
     * @param taskCount The number of tasks currently in the list.
     * @return The zero-based index of the requested task.
     * @throws MinException If the task number is missing, malformed, or outside the task list.
     */
    public int parseTaskIndex(String input, Command command, int taskCount)
            throws MinException {
        assert command == Command.MARK
                || command == Command.UNMARK
                || command == Command.DELETE
                : "Only mark, unmark, and delete commands use task indexes.";

        return parseItemIndex(
                extractArguments(input, command), taskCount, "task", command.getWord());
    }

    /**
     * Parses a valid note number into a zero-based note index.
     *
     * @param input The delete-note command entered by the user.
     * @param noteCount The number of notes currently displayed.
     * @return The zero-based index of the requested note.
     * @throws MinException If the note number is missing, malformed, or outside the note list.
     */
    public int parseNoteIndex(String input, int noteCount) throws MinException {
        return parseItemIndex(
                extractArguments(input, Command.DELETENOTE), noteCount, "note", "delete");
    }

    /**
     * Parses a valid item number into a zero-based item index.
     *
     * @param indexText The item number entered by the user.
     * @param itemCount The number of items the number may refer to.
     * @param itemName The singular name of the item, used in error messages.
     * @param action The action being performed, used in error messages.
     * @return The zero-based index of the requested item.
     * @throws MinException If the item number is missing, malformed, or outside the item list.
     */
    private int parseItemIndex(String indexText, int itemCount, String itemName, String action)
            throws MinException {
        assert itemCount >= 0 : "Item count must not be negative.";

        if (indexText.isEmpty()) {
            throw new MinException(Messages.formatMissingIndex(itemName, action));
        }

        try {
            int itemNumber = Integer.parseInt(indexText);
            if (itemCount == 0) {
                throw new MinException(Messages.formatNoItems(itemName, action));
            }
            if (itemNumber < 1 || itemNumber > itemCount) {
                throw new MinException(Messages.formatIndexOutOfRange(itemName, itemCount));
            }
            return itemNumber - 1;
        } catch (NumberFormatException e) {
            throw new MinException(Messages.formatIndexNotANumber(itemName));
        }
    }

    /**
     * Creates a note from the input.
     *
     * @param input The command entered by the user.
     * @return The created note.
     * @throws MinException If the note text is missing or contains the storage field separator.
     */
    public Note parseNote(String input) throws MinException {
        String text = extractArguments(input, Command.NOTE);
        if (text.isEmpty()) {
            throw new MinException(Messages.INVALID_NOTE);
        }
        if (text.contains(FILE_FIELD_SEPARATOR)) {
            throw new MinException(Messages.INVALID_NOTE_TEXT);
        }
        return new Note(text);
    }

    /**
     * Creates a todo task from the input.
     *
     * @param input The command entered by the user.
     * @return The created todo task.
     * @throws MinException If the todo description is missing or contains the storage field separator.
     */
    public Todo parseTodo(String input) throws MinException {
        String description = extractArguments(input, Command.TODO);
        if (description.isEmpty()) {
            throw new MinException(Messages.INVALID_TODO);
        }
        validateTaskText(description);
        return new Todo(description);
    }

    /**
     * Parses the keyword used to find tasks.
     *
     * @param input The command entered by the user.
     * @return The keyword used to match task descriptions.
     * @throws MinException If no find keyword is provided.
     */
    public String parseFindKeyword(String input) throws MinException {
        String keyword = extractArguments(input, Command.FIND);
        if (keyword.isEmpty()) {
            throw new MinException(Messages.INVALID_FIND);
        }
        return keyword;
    }

    /**
     * Creates a deadline task from the input.
     *
     * @param input The command entered by the user.
     * @return The created deadline task.
     * @throws MinException If the deadline details or date are invalid.
     */
    public Deadline parseDeadline(String input) throws MinException {
        String deadlineDetails = extractArguments(input, Command.DEADLINE);
        int byIndex = deadlineDetails.indexOf(BY_SEPARATOR);
        if (byIndex == -1) {
            throw new MinException(Messages.INVALID_DEADLINE);
        }

        String description = deadlineDetails.substring(0, byIndex).trim();
        String byText = deadlineDetails.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new MinException(Messages.INVALID_DEADLINE);
        }
        validateTaskText(description);
        return new Deadline(description, parseDeadlineDate(byText));
    }

    /**
     * Creates an event task from the input.
     *
     * @param input The command entered by the user.
     * @return The created event task.
     * @throws MinException If the event details are invalid.
     */
    public Event parseEvent(String input) throws MinException {
        String eventDetails = extractArguments(input, Command.EVENT);
        int fromIndex = eventDetails.indexOf(FROM_SEPARATOR);
        if (fromIndex == -1 || fromIndex != eventDetails.lastIndexOf(FROM_SEPARATOR)) {
            throw new MinException(Messages.INVALID_EVENT);
        }

        int toIndex = eventDetails.indexOf(TO_SEPARATOR);
        if (toIndex == -1 || toIndex != eventDetails.lastIndexOf(TO_SEPARATOR)
                || toIndex < fromIndex) {
            throw new MinException(Messages.INVALID_EVENT);
        }

        String description = eventDetails.substring(0, fromIndex).trim();
        String from = eventDetails.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = eventDetails.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new MinException(Messages.INVALID_EVENT);
        }
        validateTaskText(description);
        validateTaskText(from);
        validateTaskText(to);
        return new Event(description, from, to);
    }

    /**
     * Rejects text that would make a saved task record ambiguous.
     *
     * @param text The text to check.
     * @throws MinException If the text contains the storage field separator.
     */
    private static void validateTaskText(String text) throws MinException {
        if (text.contains(FILE_FIELD_SEPARATOR)) {
            throw new MinException(Messages.INVALID_TASK_TEXT);
        }
    }

    private String extractArguments(String input, Command command) {
        assert input != null : "Command input must not be null.";
        assert command != null : "Command must not be null.";
        assert command.matches(input)
                : "Input must match the command whose details are being parsed.";

        return input.substring(command.getWord().length()).trim();
    }

    /**
     * Parses a deadline date in the required ISO format.
     *
     * @param dateText The date text to parse.
     * @return The parsed deadline date.
     * @throws MinException If the date text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String dateText) throws MinException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MinException(Messages.INVALID_DEADLINE_DATE);
        }
    }
}
