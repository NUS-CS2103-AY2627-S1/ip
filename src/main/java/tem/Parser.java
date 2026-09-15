package tem;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user commands and turns them into tasks or task indexes.
 */
public class Parser {

    /**
     * Creates a task from a task-creation command.
     *
     * @param command command entered by the user.
     * @return the task described by the command
     * @throws TemException if the command is unknown or required details are missing
     */
    public static Task parseTask(String command) throws TemException {
        String normalizedCommand = normalizeSpaces(command);
        if (normalizedCommand.equals("todo") || normalizedCommand.startsWith("todo ")) {
            String description = normalizedCommand.substring("todo".length()).trim();
            ensurePresent(description, "A todo needs a description. Try: todo read a book");
            return new Todo(description);
        }
        if (normalizedCommand.equals("deadline") || normalizedCommand.startsWith("deadline ")) {
            return createDeadline(normalizedCommand.substring("deadline".length()).trim());
        }
        if (normalizedCommand.equals("event") || normalizedCommand.startsWith("event ")) {
            return createEvent(normalizedCommand.substring("event".length()).trim());
        }
        if (normalizedCommand.isEmpty()) {
            throw new TemException("Please enter a command.");
        }
        throw new TemException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark,"
                        + " delete, find, sort, or bye.");
    }

    /**
     * Finds the zero-based list position referenced by a numbered task command.
     *
     * @param command command containing a one-based task number.
     * @param tasks tasks currently stored by Tem.
     * @param action action described in a missing-number error message.
     * @return zero-based position of the selected task
     * @throws TemException if the task number is missing, malformed, or out of range
     */
    public static int parseTaskIndex(String command, TaskList tasks, String action) throws TemException {
        assert command != null : "Command text should not be null";
        assert tasks != null : "Task list should not be null";
        assert action != null : "Action label should not be null";
        String normalizedCommand = normalizeSpaces(command);
        int firstSpaceIndex = normalizedCommand.indexOf(' ');
        String taskNumberText = firstSpaceIndex < 0 ? "" : normalizedCommand.substring(firstSpaceIndex + 1).trim();
        if (taskNumberText.isEmpty()) {
            throw new TemException("Please provide the task number to " + action + ".");
        }
        if (taskNumberText.contains(" ")) {
            throw new TemException("Please provide only one task number to " + action + ".");
        }
        if (tasks.size() == 0) {
            throw new TemException("There are no tasks to " + action + ".");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TemException("Choose a task number from 1 to " + tasks.size() + ".");
            }
            int zeroBasedIndex = taskNumber - 1;
            assert zeroBasedIndex >= 0 && zeroBasedIndex < tasks.size()
                    : "Parsed task index should be within list bounds";
            return zeroBasedIndex;
        } catch (NumberFormatException exception) {
            throw new TemException("The task number must be a whole number.");
        }
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command find command entered by the user.
     * @return keyword to search for
     * @throws TemException if the keyword is missing
     */
    public static String parseFindKeyword(String command) throws TemException {
        String normalizedCommand = normalizeSpaces(command);
        String keyword = normalizedCommand.substring("find".length()).trim();
        ensurePresent(keyword, "A find command needs a keyword. Try: find book");
        return keyword;
    }

    /**
     * Creates a deadline from the text that follows the {@code deadline} keyword.
     *
     * @param details description and {@code /by} date text.
     * @return parsed deadline task
     * @throws TemException if required deadline fields are missing or invalid
     */
    private static Task createDeadline(String details) throws TemException {
        List<Integer> byIndices = findMarkerPositions(details, "/by");
        if (byIndices.isEmpty()) {
            throw new TemException("A deadline needs a due date. Try: deadline return book /by 2019-10-15");
        }
        if (byIndices.size() > 1) {
            throw new TemException("Use /by only once in a deadline.");
        }
        int byIndex = byIndices.get(0);
        String description = details.substring(0, byIndex).trim();
        String byText = details.substring(byIndex + "/by".length()).trim();
        ensurePresent(description, "A deadline needs a description before /by.");
        ensurePresent(byText, "A deadline needs a due date after /by.");
        return new Deadline(description, parseDate(byText));
    }

    /**
     * Parses a deadline date in {@code yyyy-MM-dd} format.
     *
     * @param dateText date text supplied after {@code /by}.
     * @return parsed date
     * @throws TemException if the date text is not in the expected format
     */
    private static LocalDate parseDate(String dateText) throws TemException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new TemException("Use a date like 2019-10-15 after /by.");
        }
    }

    /**
     * Creates an event from the text that follows the {@code event} keyword.
     *
     * @param details description, {@code /from}, and {@code /to} text.
     * @return parsed event task
     * @throws TemException if required event fields are missing
     */
    private static Task createEvent(String details) throws TemException {
        List<Integer> fromIndices = findMarkerPositions(details, "/from");
        List<Integer> toIndices = findMarkerPositions(details, "/to");
        if (fromIndices.isEmpty() || toIndices.isEmpty()) {
            throw new TemException("An event needs /from and /to times. Try: event meeting /from Mon 2pm /to 4pm");
        }
        if (fromIndices.size() > 1 || toIndices.size() > 1) {
            throw new TemException("Use /from and /to only once each in an event.");
        }
        int fromIndex = fromIndices.get(0);
        int toIndex = toIndices.get(0);
        if (toIndex < fromIndex) {
            throw new TemException("Place /from before /to in an event.");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = details.substring(toIndex + "/to".length()).trim();
        ensurePresent(description, "An event needs a description before /from.");
        ensurePresent(from, "An event needs a start time after /from.");
        ensurePresent(to, "An event needs an end time after /to.");
        return new Event(description, from, to);
    }

    /**
     * Ensures that a required text field is not blank.
     *
     * @param value text to validate.
     * @param message error message shown when the text is blank.
     * @throws TemException if the text is blank
     */
    private static void ensurePresent(String value, String message) throws TemException {
        if (value.isEmpty()) {
            throw new TemException(message);
        }
    }

    /**
     * Returns the positions of a command marker when it appears as its own token.
     *
     * @param details text containing command details.
     * @param marker marker to find, for example {@code /by}.
     * @return positions of standalone marker tokens
     */
    private static List<Integer> findMarkerPositions(String details, String marker) {
        Pattern markerPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?=\\s|$)");
        Matcher matcher = markerPattern.matcher(details);
        List<Integer> positions = new ArrayList<>();
        while (matcher.find()) {
            positions.add(matcher.start());
        }
        return positions;
    }

    /**
     * Returns command text with leading, trailing, and repeated whitespace removed.
     *
     * @param command command text supplied by the user.
     * @return command with individual words separated by one space
     */
    private static String normalizeSpaces(String command) {
        return command.trim().replaceAll("\\s+", " ");
    }
}
