package kiki.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import kiki.exception.KikiException;
import kiki.task.Deadlines;
import kiki.task.Events;
import kiki.task.Priority;

/**
 * Parses raw command strings typed by the user into task fields, dates, and
 * task indices.
 */
public class Parser {
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter CHECK_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses the description out of a "todo ..." command.
     *
     * @param trimmedInput Full command line, starting with "todo".
     * @return The trimmed, non-empty description.
     * @throws KikiException If the description is empty.
     */
    public static String parseTodoDescription(String trimmedInput) throws KikiException {
        String description = trimmedInput.substring("todo".length()).trim();
        ensureNotEmpty(description, "The description of a todo cannot be empty.");

        return description;
    }

    /**
     * Parses a "deadline ... /by ..." command into a {@link Deadlines} task.
     *
     * @param trimmedInput Full command line, starting with "deadline".
     * @return The parsed deadline task.
     * @throws KikiException If the description or time is missing/empty, or the
     *         time is not in "yyyy-MM-dd HHmm" format.
     */
    public static Deadlines parseDeadline(String trimmedInput) throws KikiException {
        String deadlineInput = trimmedInput.substring("deadline".length()).trim();
        int byIndex = deadlineInput.indexOf(BY_MARKER);

        if (byIndex < 0) {
            throw new KikiException("Please use: deadline DESCRIPTION /by TIME");
        }
        if (hasRepeatedMarker(deadlineInput, BY_MARKER)) {
            throw new KikiException("Please use only one /by marker in a deadline command.");
        }

        String description = deadlineInput.substring(0, byIndex).trim();
        String by = deadlineInput.substring(byIndex + BY_MARKER.length()).trim();
        ensureNotEmpty(description, "The description of a deadline cannot be empty.");
        ensureNotEmpty(by, "The by time of a deadline cannot be empty.");

        try {
            LocalDateTime byInput = LocalDateTime.parse(by, DATE_TIME_FORMAT);
            return new Deadlines(description, byInput);
        } catch (DateTimeParseException e) {
            throw new KikiException("Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm");
        }
    }

    /**
     * Parses an "event ... /from ... /to ..." command into an {@link Events} task.
     *
     * @param trimmedInput Full command line, starting with "event".
     * @return The parsed event task.
     * @throws KikiException If the description or either time is missing/empty,
     *         or either time is not in "yyyy-MM-dd HHmm" format.
     */
    public static Events parseEvent(String trimmedInput) throws KikiException {
        String eventInput = trimmedInput.substring("event".length()).trim();
        int fromIndex = eventInput.indexOf(FROM_MARKER);

        if (fromIndex < 0) {
            throw new KikiException("Please use: event DESCRIPTION /from START /to END");
        }
        if (hasRepeatedMarker(eventInput, FROM_MARKER)) {
            throw new KikiException("Please use only one /from marker in an event command.");
        }

        String description = eventInput.substring(0, fromIndex).trim();
        String fromAndTo = eventInput.substring(fromIndex + FROM_MARKER.length()).trim();
        int toIndex = fromAndTo.indexOf(TO_MARKER);

        if (toIndex < 0) {
            throw new KikiException("Please use: event DESCRIPTION /from START /to END");
        }
        if (hasRepeatedMarker(eventInput, TO_MARKER)) {
            throw new KikiException("Please use only one /to marker in an event command.");
        }

        String from = fromAndTo.substring(0, toIndex).trim();
        String to = fromAndTo.substring(toIndex + TO_MARKER.length()).trim();
        ensureNotEmpty(description, "The description of an event cannot be empty.");
        ensureNotEmpty(from, "The start time of an event cannot be empty.");
        ensureNotEmpty(to, "The end time of an event cannot be empty.");

        try {
            LocalDateTime fromInput = LocalDateTime.parse(from, DATE_TIME_FORMAT);
            LocalDateTime toInput = LocalDateTime.parse(to, DATE_TIME_FORMAT);
            if (!fromInput.isBefore(toInput)) {
                throw new KikiException("The event start time must be earlier than its end time.");
            }
            return new Events(description, fromInput, toInput);
        } catch (KikiException e) {
            throw e;
        } catch (DateTimeParseException e) {
            throw new KikiException(
                    "Please use: event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        }
    }

    /**
     * Parses the keyword out of a "find ..." command.
     *
     * @param trimmedInput Full command line, starting with "find".
     * @return The trimmed, non-empty keyword.
     * @throws KikiException If the keyword is empty.
     */
    public static String parseFindKeyword(String trimmedInput) throws KikiException {
        String keyword = trimmedInput.substring("find".length()).trim();
        ensureNotEmpty(keyword, "Please tell me what keyword to search for.");

        return keyword;
    }

    /**
     * Parses the question from an {@code @ai ...} command.
     *
     * @param trimmedInput Full command line starting with {@code @ai}.
     * @return Trimmed, non-empty question.
     * @throws KikiException If the question is empty.
     */
    public static String parseAiQuestion(String trimmedInput) throws KikiException {
        String question = trimmedInput.substring("@ai".length()).trim();
        ensureNotEmpty(question, "Please tell me what you would like to ask Kiki's AI.");
        return question;
    }

    /**
     * Parses the 1-based task number out of a command like "mark 2" into a
     * 0-based index into the task list.
     *
     * @param input Full command line, starting with {@code command}.
     * @param command The command word (e.g. "mark"), used to strip the prefix
     *         and in error messages.
     * @param taskCount Current number of tasks, used to validate the range.
     * @return The 0-based task index.
     * @throws KikiException If the number is missing, not a whole number, or
     *         out of range.
     */
    public static int parseTaskIndex(String input, String command, int taskCount) throws KikiException {
        String numberText = input.substring(command.length()).trim();
        ensureNotEmpty(numberText, "Please tell me which task number to " + command + ".");

        try {
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new KikiException("That task number is not in your list.");
            }

            return taskIndex;
        } catch (NumberFormatException e) {
            throw new KikiException("Task number must be a whole number.");
        }
    }

    /**
     * Parses a priority name from a priority command.
     *
     * @param input Full priority command.
     * @return Parsed priority.
     * @throws KikiException If the priority name is unknown.
     */
    public static Priority parsePriority(String input) throws KikiException {
        String priorityText = input.substring("priority".length()).trim().toUpperCase(Locale.ENGLISH);
        try {
            return Priority.valueOf(priorityText);
        } catch (IllegalArgumentException e) {
            throw new KikiException("Priority must be low, normal, or high.");
        }
    }

    /**
     * Parses a date such as "21 August" or "21 August 2025" into a
     * {@link LocalDate}, defaulting to the current year when omitted.
     */
    public static LocalDate parseCheckDate(String dateText) throws KikiException {
        ensureNotEmpty(dateText, "Please provide a date, e.g. check day 21 August");

        try {
            return LocalDate.parse(dateText, CHECK_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateText + " " + LocalDate.now().getYear(), CHECK_DATE_FORMAT);
            } catch (DateTimeParseException e2) {
                throw new KikiException("Please use a date like: 21 August");
            }
        }
    }

    private static void ensureNotEmpty(String value, String errorMessage) throws KikiException {
        if (value.isEmpty()) {
            throw new KikiException(errorMessage);
        }
    }

    private static boolean hasRepeatedMarker(String input, String marker) {
        int firstIndex = input.indexOf(marker);
        return firstIndex >= 0 && input.indexOf(marker, firstIndex + marker.length()) >= 0;
    }
}
