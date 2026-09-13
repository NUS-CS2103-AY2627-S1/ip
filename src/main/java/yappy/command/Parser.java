package yappy.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yappy.exception.YappyException;
import yappy.task.Deadline;
import yappy.task.Event;
import yappy.task.Todo;

/**
 * Converts raw user input into commands, task data, and validated task indexes.
 */
public class Parser {
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    private Parser() {
    }

    /**
     * Identifies the command word at the start of the user's input.
     */
    public static Command getCommand(String input) {
        return input.isBlank() ? Command.UNKNOWN : Command.fromInput(input);
    }

    /**
     * Parses a todo command into a validated todo task.
     */
    public static Todo parseTodo(String input) throws YappyException {
        String description = getTextAfterCommand(input, Command.TODO);
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command into a validated deadline task.
     */
    public static Deadline parseDeadline(String input) throws YappyException {
        String taskDetails = getTextAfterCommand(input, Command.DEADLINE);
        Matcher byMatcher = getMarkerMatcher(taskDetails, BY_MARKER);

        if (!byMatcher.find()) {
            throw new YappyException("OOPS!!! Please use: deadline DESCRIPTION /by WHEN");
        }
        int byIndex = byMatcher.start();
        int byEndIndex = byMatcher.end();
        if (byMatcher.find()) {
            throw new YappyException("OOPS!!! Please specify /by only once.");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(byEndIndex).trim();
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new YappyException("OOPS!!! The /by value of a deadline cannot be empty.");
        }

        return new Deadline(description, parseDate(by, BY_MARKER));
    }

    /**
     * Parses an event command into a validated event task.
     */
    public static Event parseEvent(String input) throws YappyException {
        String taskDetails = getTextAfterCommand(input, Command.EVENT);
        Matcher fromMatcher = getMarkerMatcher(taskDetails, FROM_MARKER);
        Matcher toMatcher = getMarkerMatcher(taskDetails, TO_MARKER);

        if (!fromMatcher.find() || !toMatcher.find()) {
            throw new YappyException("OOPS!!! Please use: event DESCRIPTION /from START /to END");
        }
        int fromIndex = fromMatcher.start();
        int fromEndIndex = fromMatcher.end();
        int toIndex = toMatcher.start();
        int toEndIndex = toMatcher.end();
        if (fromMatcher.find()) {
            throw new YappyException("OOPS!!! Please specify /from only once.");
        }
        if (toMatcher.find()) {
            throw new YappyException("OOPS!!! Please specify /to only once.");
        }
        if (toIndex < fromIndex) {
            throw new YappyException("OOPS!!! Please put /from before /to in an event.");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(fromEndIndex, toIndex).trim();
        String to = taskDetails.substring(toEndIndex).trim();
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new YappyException("OOPS!!! The /from value of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new YappyException("OOPS!!! The /to value of an event cannot be empty.");
        }

        LocalDate fromDate = parseDate(from, FROM_MARKER);
        LocalDate toDate = parseDate(to, TO_MARKER);
        if (toDate.isBefore(fromDate)) {
            throw new YappyException("OOPS!!! An event's /to date cannot be before its /from date.");
        }
        return new Event(description, fromDate, toDate);
    }

    /**
     * Parses and validates the keyword of a find command.
     */
    public static String parseFindKeyword(String input) throws YappyException {
        String keyword = getTextAfterCommand(input, Command.FIND);
        if (keyword.isEmpty()) {
            throw new YappyException("OOPS!!! The keyword of a find command cannot be empty.");
        }
        return keyword;
    }

    /**
     * Parses the non-negative number of days for a reminder query.
     */
    public static int parseReminderDays(String input) throws YappyException {
        String daysText = getTextAfterCommand(input, Command.REMIND);
        if (daysText.isEmpty()) {
            throw new YappyException("OOPS!!! Please tell me how many days ahead to check, e.g. remind 7.");
        }
        try {
            int days = Integer.parseInt(daysText);
            if (days < 0) {
                throw new YappyException("OOPS!!! Reminder days cannot be negative.");
            }
            return days;
        } catch (NumberFormatException e) {
            throw new YappyException("OOPS!!! Reminder days must be a whole number.");
        }
    }

    /**
     * Converts the user's 1-based task number into a valid zero-based index.
     */
    public static int parseTaskIndex(String input, Command command, int taskCount) throws YappyException {
        String numberText = getTextAfterCommand(input, command);
        if (numberText.isEmpty()) {
            throw new YappyException("OOPS!!! Please tell me which task to "
                    + command.getWord() + ", e.g. " + command.getWord() + " 1.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new YappyException("OOPS!!! Task numbers must be whole numbers.");
        }

        if (taskCount == 0) {
            throw new YappyException("OOPS!!! There are no tasks in the list yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new YappyException("OOPS!!! Task number must be between 1 and " + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Verifies that a command which takes no parameters has no trailing text.
     */
    public static void parseNoArguments(String input, Command command) throws YappyException {
        if (!getTextAfterCommand(input, command).isEmpty()) {
            throw new YappyException("OOPS!!! The " + command.getWord()
                    + " command does not take extra arguments.");
        }
    }

    /**
     * Returns the user's text after the command word.
     */
    private static String getTextAfterCommand(String input, Command command) {
        String trimmedInput = input.trim();
        return trimmedInput.substring(command.getWord().length()).trim();
    }

    /**
     * Finds a command marker only when it appears as a complete whitespace-delimited token.
     * This avoids treating text such as "/bypass" as the deadline marker.
     */
    private static Matcher getMarkerMatcher(String input, String marker) {
        Pattern markerPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        return markerPattern.matcher(input);
    }

    /**
     * Parses a user-entered ISO date and reports a command-specific error when invalid.
     */
    private static LocalDate parseDate(String dateText, String marker) throws YappyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new YappyException("OOPS!!! Please enter the " + marker
                    + " date as yyyy-MM-dd, e.g. 2019-10-15.");
        }
    }
}
