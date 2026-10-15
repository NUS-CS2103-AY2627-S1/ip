package esther.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import esther.exception.EstherException;
import esther.task.Deadline;
import esther.task.Event;
import esther.task.Task;
import esther.task.Todo;

/**
 * Interprets user commands and extracts their arguments.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Creates a task from an add-task command.
     *
     * @param command Full command entered by the user.
     * @return Task described by the command.
     * @throws EstherException If the command or its arguments are invalid.
     */
    public static Task parseTask(String command) throws EstherException {
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseTodo(command);
        } else if (command.equals("deadline")
                || command.startsWith("deadline ")) {
            return parseDeadline(command);
        } else if (command.equals("event")
                || command.startsWith("event ")) {
            return parseEvent(command);
        }

        throw new EstherException(
                "Bestie, you're not speaking my language.");
    }

    /**
     * Extracts and validates a task number from a command.
     *
     * @param command Full command entered by the user.
     * @param commandWord Command word being processed.
     * @param taskCount Number of tasks currently stored.
     * @return Valid zero-based task index.
     * @throws EstherException If the task number is missing or invalid.
     * @throws AssertionError If the task count is negative and assertions are enabled.
     */
    public static int parseTaskIndex(String command, String commandWord,
            int taskCount) throws EstherException {
        assert taskCount >= 0 : "Task count must not be negative";

        String numberText = command.substring(commandWord.length()).trim();

        if (numberText.isEmpty()) {
            throw new EstherException(
                    "STOP PLAYING! THERES NO NUMBER AT ALL!!");
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new EstherException(
                    "... NOT A WHOLE NUMBER, ARE YOU KIDDING ME!");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new EstherException(
                    "STOP PLAYING! THIS NUMBER DOES NOT EXIST!");
        }

        return taskNumber - 1;
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param command Find command entered by the user.
     * @return Keyword to search for.
     * @throws EstherException If no keyword was provided.
     */
    public static String parseFindKeyword(String command)
            throws EstherException {
        String keyword = command.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new EstherException(
                    "Bestie, what am I supposed to search for? Give me a keyword!");
        }

        return keyword;
    }

    /**
     * Creates a todo from its command.
     *
     * @param command Todo command entered by the user.
     * @return Todo described by the command.
     * @throws EstherException If the description is empty.
     */
    private static Todo parseTodo(String command) throws EstherException {
        String description = command.substring(4).trim();

        if (description.isEmpty()) {
            throw new EstherException(
                    "... You want to add a task but you're not "
                            + "giving me anything???");
        }

        return new Todo(description);
    }

    /**
     * Creates a deadline from its command.
     *
     * @param command Deadline command entered by the user.
     * @return Deadline described by the command.
     * @throws EstherException If the description or date is invalid.
     */
    private static Deadline parseDeadline(String command)
            throws EstherException {
        int dueDateMarkerIndex = command.indexOf(" /by ");

        if (hasRepeatedMarker(command, "/by")) {
            throw new EstherException(
                    "One deadline, one /by. You gave me too many!");
        }

        if (command.endsWith(" /by")) {
            throw new EstherException("HELLO I NEED A TIME OR DATE!");
        }

        if (dueDateMarkerIndex == -1) {
            throw new EstherException(
                    "PLEASEEEEE use this format: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        String description = command.substring(8, dueDateMarkerIndex).trim();
        String dueDateText = command.substring(dueDateMarkerIndex + 5).trim();

        if (description.isEmpty()) {
            throw new EstherException(
                    "... You want to add a task but you're not "
                            + "giving me anything???");
        }

        if (dueDateText.isEmpty()) {
            throw new EstherException("HELLO I NEED A TIME OR DATE!");
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateText);
            return new Deadline(description, dueDate);
        } catch (DateTimeParseException exception) {
            throw new EstherException(
                    "That date is confusing me. Use yyyy-MM-dd, like 2026-08-30!");
        }
    }

    /**
     * Creates an event from its command.
     *
     * @param command Event command entered by the user.
     * @return Event described by the command.
     * @throws EstherException If the description, start, or end is empty.
     */
    private static Event parseEvent(String command) throws EstherException {
        String startMarker = "/from";
        String endMarker = "/to";

        int startIndex = findMarkerIndex(command, startMarker);
        int endIndex = findMarkerIndex(command, endMarker);

        if (hasRepeatedMarker(command, "/from")) {
            throw new EstherException(
                    "I only need one /from. I cant time-travel bestie!");
        }

        if (hasRepeatedMarker(command, "/to")) {
            throw new EstherException("One /to is enough, promise!");
        }

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            throw new EstherException(
                    "PLEASEEEEEEE use this format: event DESCRIPTION /from START /to END");
        }

        String description = command.substring(5, startIndex).trim();
        String startTime = command.substring(
                startIndex + startMarker.length(), endIndex).trim();
        String endTime = command.substring(endIndex + endMarker.length()).trim();

        if (description.isEmpty()) {
            throw new EstherException(
                    "... You want to add a task but you're not "
                            + "giving me anything???");
        }

        if (startTime.isEmpty()) {
            throw new EstherException(
                    "HELLO I NEED A STARTING TIME OR DATE!");
        }

        if (endTime.isEmpty()) {
            throw new EstherException(
                    "HELLO I NEED AN ENDING TIME OR DATE!");
        }

        validateEventDateOrder(startTime, endTime);

        return new Event(description, startTime, endTime);
    }

    /**
     * Returns whether a command contains a parameter marker more than once.
     *
     * @param command Command to inspect.
     * @param marker Parameter marker to find.
     * @return True if the marker occurs more than once.
     */
    private static boolean hasRepeatedMarker(String command, String marker) {
        int markerCount = 0;
        int searchStartIndex = 0;
        int markerIndex;

        while ((markerIndex = command.indexOf(marker, searchStartIndex)) != -1) {
            int markerEndIndex = markerIndex + marker.length();
            boolean hasLeftBoundary = markerIndex == 0
                    || Character.isWhitespace(command.charAt(markerIndex - 1));
            boolean hasRightBoundary = markerEndIndex == command.length()
                    || Character.isWhitespace(command.charAt(markerEndIndex));

            if (hasLeftBoundary && hasRightBoundary && ++markerCount > 1) {
                return true;
            }

            searchStartIndex = markerEndIndex;
        }

        return false;
    }

    /**
     * Finds the first standalone occurrence of a parameter marker.
     *
     * @param command Command to inspect.
     * @param marker Parameter marker to find.
     * @return Marker index, or -1 if no standalone marker exists.
     */
    private static int findMarkerIndex(String command, String marker) {
        int searchStartIndex = 0;
        int markerIndex = command.indexOf(marker, searchStartIndex);

        while (markerIndex != -1) {
            int markerEndIndex = markerIndex + marker.length();
            boolean hasLeftBoundary = markerIndex == 0
                    || Character.isWhitespace(command.charAt(markerIndex - 1));
            boolean hasRightBoundary = markerEndIndex == command.length()
                    || Character.isWhitespace(command.charAt(markerEndIndex));

            if (hasLeftBoundary && hasRightBoundary) {
                return markerIndex;
            }

            searchStartIndex = markerEndIndex;
            markerIndex = command.indexOf(marker, searchStartIndex);
        }

        return -1;
    }

    /**
     * Validates the order when both event times use ISO dates.
     * Non-date event times remain supported.
     *
     * @param startTime Event start value.
     * @param endTime Event end value.
     * @throws EstherException If the end date is not after the start date.
     */
    private static void validateEventDateOrder(String startTime, String endTime)
            throws EstherException {
        try {
            LocalDate startDate = LocalDate.parse(startTime);
            LocalDate endDate = LocalDate.parse(endTime);

            if (!startDate.isBefore(endDate)) {
                throw new EstherException(
                        "Bestie, this event must end after it starts. "
                                + "I'm organised, not a time traveller!");
            }
        } catch (DateTimeParseException exception) {
            // Event times can be free-form text, such as "2pm".
        }
    }
}
