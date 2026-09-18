package rem.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rem.command.AddCommand;
import rem.command.Command;
import rem.command.CommandType;
import rem.command.DeleteCommand;
import rem.command.DeleteNoteCommand;
import rem.command.ExitCommand;
import rem.command.FindCommand;
import rem.command.ListCommand;
import rem.command.MarkCommand;
import rem.command.NoteCommand;
import rem.command.OnCommand;
import rem.command.UnmarkCommand;
import rem.exception.EmptyDescriptionException;
import rem.exception.EmptyNoteException;
import rem.exception.InvalidDateException;
import rem.exception.InvalidDeadlineFormatException;
import rem.exception.InvalidEventFormatException;
import rem.exception.InvalidTaskNumberException;
import rem.exception.NoteTooLongException;
import rem.exception.RemException;
import rem.exception.UnknownCommandException;
import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.TaskDateTime;
import rem.task.Todo;

/**
 * Interprets user commands and converts their arguments into application values.
 */
public class Parser {
    /**
     * Converts user input into an executable command.
     *
     * @param input Full user input.
     * @return Command represented by the input.
     * @throws RemException If the command or any of its arguments are invalid.
     */
    public static Command parse(String input) throws RemException {
        if (input == null) {
            throw new UnknownCommandException();
        }
        validateText(input);
        String command = input.strip();
        CommandType commandType = getCommandType(command);
        if ((commandType == CommandType.LIST || commandType == CommandType.BYE)
                && command.split("\\s+").length != 1) {
            throw new RemException(commandType.name().toLowerCase(Locale.ROOT) + " does not take arguments.");
        }
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseKeyword(command));
            case ON -> new OnCommand(parseDate(command));
            case MARK -> new MarkCommand(parseTaskNumber(command, "mark"));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(command, "unmark"));
            case DELETE -> new DeleteCommand(parseTaskNumber(command, "delete"));
            case NOTE -> createNoteCommand(command);
            case DELETENOTE -> new DeleteNoteCommand(parseTaskNumber(command, "deletenote"));
            case TODO, DEADLINE, EVENT -> new AddCommand(createTask(command));
            case UNKNOWN -> throw new UnknownCommandException();
        };
    }

    /**
     * Identifies the command type from the first word of the input.
     *
     * @param input Trimmed user input.
     * @return The matching command type, or {@code UNKNOWN} if the command is not supported.
     */
    public static CommandType getCommandType(String input) {
        if (input.isBlank()) {
            return CommandType.UNKNOWN;
        }

        String commandWord = input.split("\\s+", 2)[0].toUpperCase(Locale.ROOT);
        try {
            return CommandType.valueOf(commandWord);
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Reads and validates a task number from a command.
     *
     * @param command Full user command.
     * @param commandWord Command word preceding the number.
     * @return The positive one-based task number.
     * @throws InvalidTaskNumberException If the argument is not a positive number.
     */
    public static int parseTaskNumber(String command, String commandWord)
            throws InvalidTaskNumberException {
        String numberText = command.substring(commandWord.length()).strip();
        if (!numberText.matches("[0-9]+")) {
            throw new InvalidTaskNumberException();
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1) {
                throw new InvalidTaskNumberException();
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException();
        }
    }

    /**
     * Reads the date supplied to an {@code on} command.
     *
     * @param command Full user command.
     * @return The parsed date.
     * @throws InvalidDateException If the date is missing or has an invalid format.
     */
    public static LocalDate parseDate(String command) throws InvalidDateException {
        String dateText = command.substring(2).trim();
        if (dateText.isEmpty() || dateText.matches(".*\\s+.*")) {
            throw new InvalidDateException();
        }
        try {
            return TaskDateTime.parse(dateText).toLocalDate();
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
    }

    /**
     * Reads the keyword supplied to a {@code find} command.
     *
     * @param command Full user command.
     * @return Keyword to search for.
     * @throws EmptyDescriptionException If no keyword is supplied.
     */
    public static String parseKeyword(String command) throws EmptyDescriptionException {
        String keyword = command.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new EmptyDescriptionException();
        }
        return keyword;
    }

    /**
     * Creates the task subtype requested by an add command.
     *
     * @param command Full user command.
     * @return The task described by the command.
     * @throws RemException If the task description or date-time arguments are invalid.
     */
    public static Task createTask(String command) throws RemException {
        validateText(command);
        command = command.strip();
        int noteIndex = findNoteIndex(command);
        String taskCommand = noteIndex < 0 ? command : command.substring(0, noteIndex).trim();
        String note = noteIndex < 0 ? null : command.substring(noteIndex + 6).strip();
        validateFields(taskCommand);
        Task task;
        if (isCommand(taskCommand, "todo")) {
            String description = taskCommand.substring(4).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            task = new Todo(description);
        } else if (isCommand(taskCommand, "deadline")) {
            task = createDeadline(taskCommand);
        } else {
            assert isCommand(taskCommand, "event")
                    : "Add command must be a todo, deadline, or event";
            task = createEvent(taskCommand);
        }

        if (note != null) {
            validateNote(note);
            task.setNote(note);
        }
        return task;
    }

    /**
     * Creates a command that adds or replaces a task's note.
     *
     * @param command Full note command.
     * @return Command containing the selected task number and validated note.
     * @throws RemException If the task number or note is invalid.
     */
    private static NoteCommand createNoteCommand(String command) throws RemException {
        String arguments = command.substring(4).strip();
        if (arguments.isEmpty()) {
            throw new InvalidTaskNumberException();
        }

        String[] argumentParts = arguments.split("\\s+", 2);
        int taskNumber = parseTaskNumber("note " + argumentParts[0], "note");
        if (argumentParts.length < 2) {
            throw new EmptyNoteException();
        }
        String note = argumentParts[1].strip();
        validateNote(note);
        return new NoteCommand(taskNumber, note);
    }

    /**
     * Finds the first standalone note field in a task-creation command.
     *
     * @param command Full task-creation command.
     * @return Index of the whitespace before {@code /note}, or {@code -1} if absent.
     */
    private static int findNoteIndex(String command) {
        String lowerCommand = command.toLowerCase(Locale.ROOT);
        int searchIndex = 0;
        while (searchIndex < lowerCommand.length()) {
            int slashIndex = lowerCommand.indexOf("/note", searchIndex);
            if (slashIndex < 0) {
                return -1;
            }
            int characterAfterNote = slashIndex + 5;
            boolean hasWhitespaceBefore = slashIndex > 0
                    && Character.isWhitespace(lowerCommand.charAt(slashIndex - 1));
            if (characterAfterNote == lowerCommand.length()
                    || Character.isWhitespace(lowerCommand.charAt(characterAfterNote))) {
                if (hasWhitespaceBefore) {
                    return slashIndex - 1;
                }
            }
            searchIndex = characterAfterNote;
        }
        return -1;
    }

    /**
     * Validates a trimmed, single-line note.
     *
     * @param note Note to validate.
     * @throws EmptyNoteException If the note is blank.
     * @throws NoteTooLongException If the note exceeds the maximum length.
     */
    private static void validateNote(String note)
            throws EmptyNoteException, NoteTooLongException {
        if (note.isBlank()) {
            throw new EmptyNoteException();
        }
        if (note.codePointCount(0, note.length()) > Task.MAX_NOTE_LENGTH) {
            throw new NoteTooLongException();
        }
    }

    /**
     * Checks whether the input consists of a given command word, optionally followed by arguments.
     *
     * @param input User input to inspect.
     * @param commandWord Command word to match.
     * @return True if the input starts with the complete command word.
     */
    private static boolean isCommand(String input, String commandWord) {
        String lowerInput = input.toLowerCase(Locale.ROOT);
        return lowerInput.equals(commandWord)
                || lowerInput.matches(commandWord + "\\s+.*");
    }

    /**
     * Creates a deadline from its description and due-date arguments.
     *
     * @param command Original user command, preserving the description's capitalization.
     * @return Deadline described by the command.
     * @throws RemException If the description or due-date arguments are invalid.
     */
    private static Deadline createDeadline(String command) throws RemException {
        String details = command.substring(8).trim();
        if (details.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        int byIndex = findField(command, "by");
        if (byIndex < 0) {
            throw new InvalidDeadlineFormatException();
        }

        String description = command.substring(8, byIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        String by = command.substring(byIndex + 4).trim();
        if (by.isEmpty()) {
            throw new InvalidDeadlineFormatException();
        }
        try {
            return new Deadline(description, TaskDateTime.parse(by.replaceAll("\\s+", " ")));
        } catch (DateTimeParseException e) {
            throw new InvalidDeadlineFormatException();
        }
    }

    /**
     * Creates an event from its description, start, and end arguments.
     *
     * @param command Original user command, preserving the description's capitalization.
     * @return Event described by the command.
     * @throws RemException If the description or date-time arguments are invalid.
     */
    private static Event createEvent(String command) throws RemException {
        String details = command.substring(5).trim();
        if (details.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        int fromIndex = findField(command, "from");
        int toIndex = findField(command, "to");
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new InvalidEventFormatException();
        }

        if (fromIndex <= 5) {
            throw new EmptyDescriptionException();
        }
        String description = command.substring(6, fromIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        String from = command.substring(fromIndex + 6, toIndex).trim();
        String to = command.substring(toIndex + 4).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidEventFormatException();
        }
        try {
            LocalDateTime startDateTime = TaskDateTime.parse(from.replaceAll("\\s+", " "));
            LocalDateTime endDateTime = TaskDateTime.parse(to.replaceAll("\\s+", " "));
            if (!endDateTime.isAfter(startDateTime)) {
                throw new InvalidEventFormatException();
            }
            return new Event(description, startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            throw new InvalidEventFormatException();
        }
    }

    /** Rejects multiline and control input while allowing tabs as separators or text. */
    private static void validateText(String text) throws RemException {
        if (text.codePoints().anyMatch(value -> Character.isISOControl(value) && value != '\t'
                || value == 0x2028 || value == 0x2029)) {
            throw new RemException("Use a single line without control characters.");
        }
    }

    /** Locates a complete scheduling field, including its preceding whitespace. */
    private static int findField(String command, String field) {
        Matcher matcher = Pattern.compile("(?i)\\s/" + field + "(?=\\s|$)").matcher(command);
        return matcher.find() ? matcher.start() : -1;
    }

    /** Requires each scheduling field exactly once and in the documented order. */
    private static void validateFields(String command) throws RemException {
        Matcher matcher = Pattern.compile("(?i)(?<=\\s)/(by|from|to)(?=\\s|$)").matcher(command);
        StringBuilder fields = new StringBuilder();
        while (matcher.find()) {
            fields.append(matcher.group(1).toLowerCase(Locale.ROOT)).append(' ');
        }
        if (isCommand(command, "deadline") && !fields.toString().equals("by ")
                && !command.equalsIgnoreCase("deadline")) {
            throw new InvalidDeadlineFormatException();
        }
        if (isCommand(command, "event") && !fields.toString().equals("from to ")
                && !command.equalsIgnoreCase("event")) {
            throw new InvalidEventFormatException();
        }
        if (isCommand(command, "todo") && !fields.isEmpty()) {
            throw new RemException("A todo has no scheduling fields. Use deadline or event instead.");
        }
    }
}
