package koko;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles parsing and validation of koko.Koko commands.
 */
public class Parser {
    /** The format accepted for dates and times entered by the user. */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Checks whether a command is the {@code bye} command.
     *
     * @param command the user's command
     * @return true if the command is bye
     */
    public static boolean isBye(String command) {
        return command.equals("bye");
    }

    /**
     * Checks whether a command is the {@code list} command.
     *
     * @param command the user's command
     * @return true if the command is list
     */
    public static boolean isList(String command) {
        return command.equals("list");
    }

    /**
     * Checks whether a command starts with the given action.
     *
     * @param command the user's command
     * @param action the command action
     * @return true if the command is the action or starts with the action followed by a space
     */
    public static boolean isCommand(String command, String action) {
        return command.equals(action) || command.startsWith(action + " ");
    }

    /**
     * Extracts a task number from a command.
     *
     * @param command the user's command
     * @param action the action named in the command
     * @param numberOfTasks how many tasks are currently stored
     * @return the zero-based task index
     * @throws KokoException if the task number is missing, invalid, or unavailable
     */
    public static int getTaskIndex(String command, String action, int numberOfTasks)
            throws KokoException {
        assert isCommand(command, action) : "Task-index parsing requires the expected command action.";
        assert numberOfTasks >= 0 : "The number of stored tasks cannot be negative.";
        String taskNumberText = command.substring(action.length()).trim();

        return parseTaskIndex(taskNumberText, action, numberOfTasks);
    }

    /**
     * Parses an update command into its task index and optional replacement fields.
     *
     * @param command the user's update command
     * @param numberOfTasks how many tasks are currently stored
     * @return the parsed update request
     * @throws KokoException if the task number or update fields are invalid
     */
    public static UpdateRequest parseUpdate(String command, int numberOfTasks)
            throws KokoException {
        assert isCommand(command, "update") : "Update parsing requires an update command.";
        String details = command.substring("update".length()).trim();
        int firstSpace = details.indexOf(' ');
        String taskNumberText = firstSpace == -1 ? details : details.substring(0, firstSpace);
        int taskIndex = parseTaskIndex(taskNumberText, "update", numberOfTasks);
        String fields = firstSpace == -1 ? "" : details.substring(firstSpace).trim();

        if (fields.isEmpty()) {
            throw new KokoException(
                    "I need at least one field to update. Try: update 2 /desc new description.");
        }

        return parseUpdateFields(taskIndex, fields);
    }

    private static int parseTaskIndex(String taskNumberText, String action, int numberOfTasks)
            throws KokoException {

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            int taskIndex = taskNumber - 1;

            if (numberOfTasks == 0) {
                throw new KokoException(
                        "There are no tasks to " + action + " yet. Add one first.");
            }

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                throw new KokoException(
                        "Choose a task number from 1 to " + numberOfTasks + ".");
            }

            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new KokoException(
                    "I need a task number to " + action + ". Try: " + action + " 2.");
        }
    }

    private static UpdateRequest parseUpdateFields(int taskIndex, String fields)
            throws KokoException {
        String description = null;
        LocalDateTime by = null;
        LocalDateTime from = null;
        LocalDateTime to = null;
        String remainingFields = fields;

        while (!remainingFields.isEmpty()) {
            if (!remainingFields.startsWith("/")) {
                throw invalidUpdateFields();
            }

            int markerEnd = remainingFields.indexOf(' ');
            if (markerEnd == -1) {
                throw new KokoException("Each update field needs a value.");
            }

            String marker = remainingFields.substring(0, markerEnd);
            String valueAndRemainingFields = remainingFields.substring(markerEnd).trim();
            int nextMarker = valueAndRemainingFields.indexOf(" /");
            String value = nextMarker == -1
                    ? valueAndRemainingFields
                    : valueAndRemainingFields.substring(0, nextMarker).trim();
            remainingFields = nextMarker == -1
                    ? ""
                    : valueAndRemainingFields.substring(nextMarker + 1).trim();

            if (value.isEmpty()) {
                throw new KokoException("Each update field needs a value.");
            }

            switch (marker) {
                case "/desc":
                    if (description != null) {
                        throw duplicateUpdateField();
                    }
                    description = value;
                    break;
                case "/by":
                    if (by != null) {
                        throw duplicateUpdateField();
                    }
                    by = parseUpdateDateTime(value);
                    break;
                case "/from":
                    if (from != null) {
                        throw duplicateUpdateField();
                    }
                    from = parseUpdateDateTime(value);
                    break;
                case "/to":
                    if (to != null) {
                        throw duplicateUpdateField();
                    }
                    to = parseUpdateDateTime(value);
                    break;
                default:
                    throw invalidUpdateFields();
            }
        }

        return new UpdateRequest(taskIndex, description, by, from, to);
    }

    private static LocalDateTime parseUpdateDateTime(String dateTimeText) throws KokoException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new KokoException(
                    "I couldn't understand that date and time. "
                            + "Use yyyy-MM-dd HHmm, e.g. 2026-09-20 1800.");
        }
    }

    private static KokoException duplicateUpdateField() {
        return new KokoException("Each update field can be specified only once.");
    }

    private static KokoException invalidUpdateFields() {
        return new KokoException(
                "I couldn't understand the update fields. "
                        + "Use /desc, /by, /from, or /to as appropriate.");
    }

    /**
     * Extracts the description from a todo command.
     *
     * @param command the user's todo command
     * @return the task description
     * @throws KokoException if the description is missing
     */
    public static String parseTodo(String command) throws KokoException {
        assert isCommand(command, "todo") : "To-do parsing requires a to-do command.";
        String description = command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new KokoException(
                    "A to-do needs a description. Try: todo borrow book.");
        }

        return description;
    }

    /**
     * Parses a deadline command and creates a koko.Deadline.
     *
     * @param command the user's deadline command
     * @return the parsed koko.Deadline
     * @throws KokoException if the command or date/time is invalid
     */
    public static Deadline parseDeadline(String command) throws KokoException {
        assert isCommand(command, "deadline") : "Deadline parsing requires a deadline command.";
        String details = command.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");

        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            throw new KokoException(
                    "A deadline needs a description and a /by date and time. "
                            + "Try: deadline return book /by 2019-12-02 1800.");
        }

        String description = details.substring(0, byMarker).trim();
        String byText = details.substring(byMarker + " /by ".length()).trim();

        if (description.isEmpty() || byText.isEmpty()) {
            throw new KokoException(
                    "A deadline needs a description and a /by date and time. "
                            + "Try: deadline return book /by 2019-12-02 1800.");
        }

        try {
            LocalDateTime by = LocalDateTime.parse(byText, INPUT_FORMAT);
            return new Deadline(description, by);
        } catch (DateTimeParseException exception) {
            throw new KokoException(
                    "I couldn't understand that date and time. "
                            + "Use yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.");
        }
    }

    /**
     * Parses an event command and creates an koko.Event.
     *
     * @param command the user's event command
     * @return the parsed koko.Event
     * @throws KokoException if the command or date/time is invalid
     */
    public static Event parseEvent(String command) throws KokoException {
        assert isCommand(command, "event") : "Event parsing requires an event command.";
        String details = command.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = details.indexOf(" /to ");

        if (fromMarker < 1
                || toMarker < fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length()) {
            throw new KokoException(
                    "An event needs a description, /from date and time, and /to date and time. "
                            + "Try: event lecture /from 2019-12-02 1400 /to 2019-12-02 1600.");
        }

        String description = details.substring(0, fromMarker).trim();
        String fromText = details.substring(
                fromMarker + " /from ".length(), toMarker).trim();
        String toText = details.substring(toMarker + " /to ".length()).trim();

        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new KokoException(
                    "An event needs a description, /from date and time, and /to date and time. "
                            + "Try: event lecture /from 2019-12-02 1400 /to 2019-12-02 1600.");
        }

        try {
            LocalDateTime from = LocalDateTime.parse(fromText, INPUT_FORMAT);
            LocalDateTime to = LocalDateTime.parse(toText, INPUT_FORMAT);

            if (to.isBefore(from)) {
                throw new KokoException(
                        "An event cannot end before it starts.");
            }

            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new KokoException(
                    "I couldn't understand the event date and time. "
                            + "Use yyyy-MM-dd HHmm, e.g. 2019-12-02 1400.");
        }
    }
}
