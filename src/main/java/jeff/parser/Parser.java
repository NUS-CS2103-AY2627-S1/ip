package jeff.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Todo;
import jeff.task.UpdateField;

/**
 * Parses user input into commands and task information.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Returns the command word from the specified user input.
     *
     * @param input Full user input.
     * @return Command word, or an empty string if the input is empty.
     */
    public static String getCommandWord(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return "";
        }

        return trimmedInput.split("\\s+", 2)[0];
    }

    /**
     * Returns the task number from the specified user input.
     *
     * @param input Full user input.
     * @return One-based task number supplied by the user.
     * @throws IllegalArgumentException If a valid task number was not supplied.
     */
    public static int parseTaskNumber(String input) {
        String[] parts = input.trim().split("\\s+", 2);

        if (parts.length < 2) {
            throw new IllegalArgumentException("Please enter a task number.");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter a valid task number.");
        }
    }

    /**
     * Returns a todo parsed from the specified user input.
     *
     * @param input Full user input.
     * @return Parsed todo.
     * @throws IllegalArgumentException If the todo description is missing.
     */
    public static Todo parseTodo(String input) {
        String description = input.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Missing description.");
        }

        return new Todo(description);
    }

    /**
     * Returns a deadline parsed from the specified user input.
     *
     * @param input Full user input.
     * @return Parsed deadline.
     * @throws IllegalArgumentException If the deadline details are invalid.
     */
    public static Deadline parseDeadline(String input) {
        String deadlineDetails = input.trim()
                .substring("deadline".length()).trim();
        int bySeparatorIndex = findUniqueSeparator(
                deadlineDetails, "/by");

        if (bySeparatorIndex == -1) {
            throw new IllegalArgumentException(
                    "A deadline must use: deadline <description> /by <date>");
        }

        String description = deadlineDetails.substring(0, bySeparatorIndex).trim();
        String dueDateText = deadlineDetails
                .substring(bySeparatorIndex + "/by".length()).trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException(
                    "A deadline needs a description.");
        }

        if (dueDateText.isEmpty()) {
            throw new IllegalArgumentException(
                    "A deadline needs a date.");
        }

        try {
            return new Deadline(description, LocalDate.parse(dueDateText));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Please enter the date in YYYY-MM-DD format.");
        }
    }

    /**
     * Returns an event parsed from the specified user input.
     *
     * @param input Full user input.
     * @return Parsed event.
     * @throws IllegalArgumentException If the event details are invalid.
     */
    public static Event parseEvent(String input) {
        String eventDetails = input.trim()
                .substring("event".length()).trim();

        int fromSeparatorIndex = findUniqueSeparator(
                eventDetails, "/from");
        int toSeparatorIndex = findUniqueSeparator(
                eventDetails, "/to");

        if (fromSeparatorIndex == -1
                || toSeparatorIndex == -1
                || fromSeparatorIndex >= toSeparatorIndex) {
            throw new IllegalArgumentException(
                    "An event must use: "
                            + "event <description> /from <time> /to <time>");
        }

        String description = eventDetails
                .substring(0, fromSeparatorIndex).trim();
        String startTime = eventDetails
                .substring(
                        fromSeparatorIndex + "/from".length(),
                        toSeparatorIndex)
                .trim();
        String endTime = eventDetails
                .substring(toSeparatorIndex + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException(
                    "An event needs a description.");
        }

        if (startTime.isEmpty()) {
            throw new IllegalArgumentException(
                    "An event needs a start time.");
        }

        if (endTime.isEmpty()) {
            throw new IllegalArgumentException(
                    "An event needs an end time.");
        }

        return new Event(description, startTime, endTime);
    }

    /**
     * Finds a separator appearing as a standalone token.
     *
     * @param details Command details to search.
     * @param separator Separator to locate.
     * @return Separator index, or -1 if absent.
     * @throws IllegalArgumentException If the separator appears repeatedly.
     */
    private static int findUniqueSeparator(
            String details, String separator) {
        Pattern pattern = Pattern.compile(
                "(?<!\\S)" + Pattern.quote(separator) + "(?=\\s|$)");
        Matcher matcher = pattern.matcher(details);

        if (!matcher.find()) {
            return -1;
        }

        int separatorIndex = matcher.start();

        if (matcher.find()) {
            throw new IllegalArgumentException(
                    "Please specify " + separator + " only once.");
        }

        return separatorIndex;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input Full user input.
     * @return Keyword to search for.
     * @throws IllegalArgumentException If no keyword was provided.
     */
    public static String parseFindKeyword(String input) {
        String keyword = input.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new IllegalArgumentException(
                    "A find command needs a keyword.");
        }

        return keyword;
    }

    /**
     * Returns the update details parsed from the specified user input.
     *
     * @param input Full user input.
     * @return Parsed update request.
     * @throws IllegalArgumentException If the update details are invalid.
     */
    public static UpdateRequest parseUpdate(String input) {
        String updateDetails = input.substring("update".length()).trim();

        if (updateDetails.isEmpty()) {
            throw new IllegalArgumentException("Please enter a task number.");
        }

        String[] taskNumberAndUpdate = updateDetails.split("\\s+", 2);
        int taskNumber = parseUpdateTaskNumber(taskNumberAndUpdate[0]);

        if (taskNumberAndUpdate.length < 2) {
            throw new IllegalArgumentException(
                    "An update command needs a field.");
        }

        String[] fieldAndValue =
                taskNumberAndUpdate[1].trim().split("\\s+", 2);
        UpdateField field =
                UpdateField.fromCommandFlag(fieldAndValue[0]);

        if (fieldAndValue.length < 2 || fieldAndValue[1].isBlank()) {
            throw new IllegalArgumentException(
                    "An update command needs a new value.");
        }

        String value = fieldAndValue[1].trim();

        if (containsUpdateField(value)) {
            throw new IllegalArgumentException(
                    "Please update one field at a time.");
        }

        return new UpdateRequest(taskNumber, field, value);
    }

    private static int parseUpdateTaskNumber(String taskNumberText) {
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter a valid task number.");
        }
    }

    private static boolean containsUpdateField(String value) {
        String[] words = value.split("\\s+");

        for (String word : words) {
            if (UpdateField.isCommandFlag(word)) {
                return true;
            }
        }

        return false;
    }
}
