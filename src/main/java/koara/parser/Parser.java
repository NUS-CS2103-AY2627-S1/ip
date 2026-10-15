package koara.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import koara.client.Client;
import koara.exception.KoaraException;
import koara.task.Deadline;
import koara.task.Event;
import koara.task.Task;
import koara.task.Todo;

/**
 * Parses and validates commands entered by the user.
 */
public class Parser {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String CLIENT_ADD_COMMAND = "client add";
    private static final String CLIENT_EDIT_COMMAND = "client edit";
    private static final String CLIENT_FIND_COMMAND = "client find";
    private static final String CLIENT_DELETE_COMMAND = "client delete";
    private static final String NAME_SEPARATOR = " /name ";
    private static final String PHONE_SEPARATOR = " /phone ";
    private static final String GOAL_SEPARATOR = " /goal ";
    private static final String NOTES_SEPARATOR = " /notes ";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";
    private static final String STORAGE_SEPARATOR = "|";
    private static final String DATE_FORMAT_ERROR =
            "Alamak, that date is not it. Use yyyy-MM-dd, for example 2026-09-13.";

    private Parser() {
    }

    /**
     * Removes surrounding whitespace and collapses internal whitespace between command parts.
     *
     * @param command Raw command entered by the user.
     * @return Normalized command.
     */
    public static String normalizeCommand(String command) {
        assert command != null : "Command must not be null";
        return command.strip().replaceAll("\\s+", " ");
    }

    /**
     * Returns whether a command uses the specified keyword.
     *
     * @param command Command entered by the user.
     * @param keyword Expected command keyword.
     * @return True if the command uses the keyword.
     */
    public static boolean matchesCommand(String command, String keyword) {
        assert command != null : "Command must not be null";
        assert keyword != null && !keyword.isBlank() : "Command keyword must not be blank";
        String normalizedCommand = normalizeCommand(command);
        return normalizedCommand.equals(keyword) || normalizedCommand.startsWith(keyword + " ");
    }

    /**
     * Parses and validates a command that creates a task.
     *
     * @param command Command entered by the user.
     * @return Task represented by the command.
     * @throws KoaraException If the command is invalid.
     */
    public static Task parseTask(String command) throws KoaraException {
        assert command != null : "Command must not be null";
        String normalizedCommand = normalizeCommand(command);
        if (matchesCommand(normalizedCommand, TODO_COMMAND)) {
            return parseTodo(normalizedCommand);
        }
        if (matchesCommand(normalizedCommand, DEADLINE_COMMAND)) {
            return parseDeadline(normalizedCommand);
        }
        if (matchesCommand(normalizedCommand, EVENT_COMMAND)) {
            return parseEvent(normalizedCommand);
        }
        throw new KoaraException("Walao, Koara catch no ball. Try todo, deadline, event, list, find, "
                + "or a client command.");
    }

    private static Todo parseTodo(String command) throws KoaraException {
        String description = extractTaskDetails(command, TODO_COMMAND);
        if (description.isEmpty()) {
            throw new KoaraException("Alamak, a todo cannot be blank lah. Add a description after todo.");
        }
        validateTaskDescription(description);
        return new Todo(description);
    }

    private static Deadline parseDeadline(String command) throws KoaraException {
        String taskDetails = extractTaskDetails(command, DEADLINE_COMMAND);
        if (taskDetails.isEmpty() || taskDetails.startsWith("/by")) {
            throw new KoaraException("Alamak, a deadline needs a description. Add it after deadline.");
        }

        int bySeparatorIndex = taskDetails.indexOf(BY_SEPARATOR);
        if (countOccurrences(taskDetails, BY_SEPARATOR) != 1) {
            throw new KoaraException("Almost there! Add /by and a date to that deadline.");
        }

        String description = taskDetails.substring(0, bySeparatorIndex).trim();
        String dueDateText = taskDetails.substring(bySeparatorIndex + BY_SEPARATOR.length()).trim();
        if (dueDateText.isEmpty()) {
            throw new KoaraException("Almost there! Put a date after /by for that deadline.");
        }
        validateTaskDescription(description);
        return new Deadline(description, parseDate(dueDateText));
    }

    private static Event parseEvent(String command) throws KoaraException {
        String taskDetails = extractTaskDetails(command, EVENT_COMMAND);
        validateEventDescription(taskDetails);

        int fromSeparatorIndex = taskDetails.indexOf(FROM_SEPARATOR);
        if (countOccurrences(taskDetails, FROM_SEPARATOR) != 1) {
            throw new KoaraException("Almost there! An event needs both /from and /to dates.");
        }

        int toSeparatorIndex = taskDetails.indexOf(TO_SEPARATOR,
                fromSeparatorIndex + FROM_SEPARATOR.length());
        if (countOccurrences(taskDetails, TO_SEPARATOR) != 1 || toSeparatorIndex < fromSeparatorIndex) {
            throw new KoaraException("Almost there! Add /to and an end date for that event.");
        }

        String description = taskDetails.substring(0, fromSeparatorIndex).trim();
        String startDateText = taskDetails.substring(
                fromSeparatorIndex + FROM_SEPARATOR.length(), toSeparatorIndex).trim();
        String endDateText = taskDetails.substring(toSeparatorIndex + TO_SEPARATOR.length()).trim();
        if (startDateText.isEmpty() || endDateText.isEmpty()) {
            throw new KoaraException("Alamak, both event dates must be filled in. Try again—you got this.");
        }
        validateTaskDescription(description);
        LocalDate startDate = parseDate(startDateText);
        LocalDate endDate = parseDate(endDateText);
        if (startDate.isAfter(endDate)) {
            throw new KoaraException("Alamak, an event cannot end before it starts.");
        }
        return new Event(description, startDate, endDate);
    }

    private static void validateEventDescription(String taskDetails) throws KoaraException {
        boolean hasNoDescription = taskDetails.isEmpty()
                || taskDetails.startsWith("/from")
                || taskDetails.startsWith("/to");
        if (hasNoDescription) {
            throw new KoaraException("Alamak, an event needs a description before its dates.");
        }
    }

    private static String extractTaskDetails(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    /**
     * Validates a displayed task number and converts it to a zero-based index.
     *
     * @param command Command containing the task number.
     * @param action Action being performed on the task.
     * @param taskCount Number of tasks in the list.
     * @return Zero-based index of the selected task.
     * @throws KoaraException If the task number is missing or invalid.
     */
    public static int parseTaskIndex(String command, String action, int taskCount) throws KoaraException {
        assert action != null && !action.isBlank() : "Task action must not be blank";
        assert matchesCommand(command, action) : "Command must match the task action";
        assert taskCount >= 0 : "Task count must not be negative";
        String normalizedCommand = normalizeCommand(command);
        String taskNumberText = normalizedCommand.substring(action.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new KoaraException("Almost there! Tell Koara which task number to " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new KoaraException("Alamak, the task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KoaraException("That task number is not on the board yet. Check list and try again.");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param command Find command entered by the user.
     * @return Keyword to search for.
     * @throws KoaraException If the keyword is empty.
     */
    public static String parseFindKeyword(String command) throws KoaraException {
        assert matchesCommand(command, FIND_COMMAND) : "Command must be a find command";
        String normalizedCommand = normalizeCommand(command);
        String keyword = normalizedCommand.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new KoaraException("Koara needs a keyword to search. Add one after find.");
        }
        return keyword;
    }

    /**
     * Parses a command that creates a client.
     *
     * @param command Client-add command entered by the user.
     * @return Client represented by the command.
     * @throws KoaraException If any required client field is missing.
     */
    public static Client parseClient(String command) throws KoaraException {
        assert matchesCommand(command, CLIENT_ADD_COMMAND) : "Command must add a client";
        String normalizedCommand = normalizeCommand(command);
        String details = normalizedCommand.substring(CLIENT_ADD_COMMAND.length()).trim();
        return parseClientDetails(details, false);
    }

    /**
     * Parses a command that replaces a client's details.
     *
     * @param command Client-edit command entered by the user.
     * @param clientCount Number of clients currently stored.
     * @return Parsed client index and replacement details.
     * @throws KoaraException If the index or client details are invalid.
     */
    public static ClientEdit parseClientEdit(String command, int clientCount) throws KoaraException {
        assert matchesCommand(command, CLIENT_EDIT_COMMAND) : "Command must edit a client";
        String normalizedCommand = normalizeCommand(command);
        int nameSeparatorIndex = normalizedCommand.indexOf(NAME_SEPARATOR);
        if (countOccurrences(normalizedCommand, NAME_SEPARATOR) != 1) {
            throw clientFormatException(CLIENT_EDIT_COMMAND + " INDEX /name");
        }
        String indexCommand = normalizedCommand.substring(0, nameSeparatorIndex);
        int clientIndex = parseClientIndex(indexCommand, CLIENT_EDIT_COMMAND, clientCount);
        String details = normalizedCommand.substring(nameSeparatorIndex + 1);
        return new ClientEdit(clientIndex, parseClientDetails(details, true));
    }

    /**
     * Parses the keyword from a client-find command.
     *
     * @param command Client-find command entered by the user.
     * @return Keyword to search for.
     * @throws KoaraException If the keyword is empty.
     */
    public static String parseClientKeyword(String command) throws KoaraException {
        assert matchesCommand(command, CLIENT_FIND_COMMAND) : "Command must find clients";
        String normalizedCommand = normalizeCommand(command);
        String keyword = normalizedCommand.substring(CLIENT_FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new KoaraException("Koara needs some client info to search. Add it after client find.");
        }
        return keyword;
    }

    /**
     * Parses and validates a displayed client number.
     *
     * @param command Command containing the client number.
     * @param action Client action being performed.
     * @param clientCount Number of clients currently stored.
     * @return Zero-based index of the selected client.
     * @throws KoaraException If the client number is missing or invalid.
     */
    public static int parseClientIndex(String command, String action, int clientCount) throws KoaraException {
        assert matchesCommand(command, action) : "Command must match the client action";
        assert clientCount >= 0 : "Client count must not be negative";
        String normalizedCommand = normalizeCommand(command);
        String clientNumberText = normalizedCommand.substring(action.length()).trim();
        if (clientNumberText.isEmpty()) {
            throw new KoaraException("Almost there! Tell Koara which client number to "
                    + action.substring("client ".length()) + ".");
        }

        int clientNumber;
        try {
            clientNumber = Integer.parseInt(clientNumberText);
        } catch (NumberFormatException exception) {
            throw new KoaraException("Alamak, the client number must be a whole number.");
        }
        if (clientNumber < 1 || clientNumber > clientCount) {
            throw new KoaraException("That client number is not on the board yet. Check client list and try again.");
        }
        return clientNumber - 1;
    }

    private static Client parseClientDetails(String details, boolean includesNameLabel) throws KoaraException {
        String normalizedDetails = details;
        int phoneIndex = normalizedDetails.indexOf(PHONE_SEPARATOR);
        int goalIndex = normalizedDetails.indexOf(GOAL_SEPARATOR);
        int notesIndex = normalizedDetails.indexOf(NOTES_SEPARATOR);
        boolean hasRepeatedField = countOccurrences(normalizedDetails, PHONE_SEPARATOR) != 1
                || countOccurrences(normalizedDetails, GOAL_SEPARATOR) != 1
                || countOccurrences(normalizedDetails, NOTES_SEPARATOR) != 1;
        boolean hasInvalidOrder = phoneIndex < 0 || goalIndex < phoneIndex || notesIndex < goalIndex;
        if (hasRepeatedField || hasInvalidOrder) {
            throw clientFormatException(CLIENT_ADD_COMMAND);
        }

        int nameStartIndex = includesNameLabel ? "/name ".length() : 0;
        String name = normalizedDetails.substring(nameStartIndex, phoneIndex).trim();
        String phone = normalizedDetails.substring(phoneIndex + PHONE_SEPARATOR.length(), goalIndex).trim();
        String goal = normalizedDetails.substring(goalIndex + GOAL_SEPARATOR.length(), notesIndex).trim();
        String notes = normalizedDetails.substring(notesIndex + NOTES_SEPARATOR.length()).trim();
        if (name.isEmpty() || phone.isEmpty() || goal.isEmpty() || notes.isEmpty()) {
            throw clientFormatException(CLIENT_ADD_COMMAND);
        }
        if (!Client.isValidPhone(phone)) {
            throw new KoaraException("That phone number does not look valid. Use 7–16 digits with an optional +.");
        }
        if (containsUnsupportedCharacter(name, phone, goal, notes)) {
            throw new KoaraException("Keep each client detail on one line—tabs and line breaks are not supported.");
        }
        return new Client(name, phone, goal, notes);
    }

    private static boolean containsUnsupportedCharacter(String... fields) {
        for (String field : fields) {
            if (field.contains("\t") || field.contains("\n") || field.contains("\r")) {
                return true;
            }
        }
        return false;
    }

    private static int countOccurrences(String text, String token) {
        int count = 0;
        int searchIndex = 0;
        while ((searchIndex = text.indexOf(token, searchIndex)) >= 0) {
            count++;
            searchIndex += token.length();
        }
        return count;
    }

    private static void validateTaskDescription(String description) throws KoaraException {
        if (description.contains(STORAGE_SEPARATOR)) {
            throw new KoaraException("Task descriptions cannot contain the | character.");
        }
    }

    private static KoaraException clientFormatException(String command) {
        return new KoaraException("Almost there! Use: " + command
                + " NAME /phone PHONE /goal GOAL /notes NOTES");
    }

    /**
     * Contains a parsed client-edit target and its replacement details.
     *
     * @param clientIndex Zero-based client index.
     * @param client Replacement client details.
     */
    public record ClientEdit(int clientIndex, Client client) {
    }

    /**
     * Parses a date in the required ISO local-date format.
     *
     * @param dateText Date text to parse.
     * @return Parsed date.
     * @throws KoaraException If the text is not a valid date in yyyy-MM-dd format.
     */
    private static LocalDate parseDate(String dateText) throws KoaraException {
        assert dateText != null && !dateText.isBlank() : "Date text must not be blank";
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new KoaraException(DATE_FORMAT_ERROR);
        }
    }
}
