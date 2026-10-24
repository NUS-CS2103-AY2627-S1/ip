package echo.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import echo.EchoException;
import echo.task.Deadline;
import echo.task.Event;
import echo.task.Task;
import echo.task.Todo;

/**
 * Makes sense of a raw user command line: splitting it into a command word
 * and arguments, and turning each command's arguments into the Task (or
 * other value) Echo needs to act on. Throws EchoException with a
 * user-facing message on anything malformed, exactly as Echo's inline
 * parsing used to.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Returns the first word of the input line (the command).
     */
    public static String getCommandWord(String input) {
        return input.split(" ", 2)[0];
    }

    /**
     * Returns everything after the first word (empty string if there is
     * no space, i.e. the command has no arguments).
     */
    public static String getArguments(String input) {
        String[] parts = input.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    /**
     * Parses a {@code todo} command's arguments into a Todo task.
     *
     * @param rest Text after the "todo" command word.
     * @return The parsed Todo task.
     * @throws EchoException If the description is empty.
     */
    public static Task parseTodo(String rest) throws EchoException {
        String desc = rest.trim();
        if (desc.isEmpty()) {
            throw new EchoException("The description of a todo cannot be empty.");
        }
        validateDescription(desc);
        return new Todo(desc);
    }

    /**
     * Parses a {@code deadline} command's arguments into a Deadline task.
     *
     * @param rest Text after the "deadline" command word, expected to
     *     contain a "/by &lt;date&gt;" clause.
     * @return The parsed Deadline task.
     * @throws EchoException If the "/by" clause is missing, either field
     *     is empty, or the date is not in yyyy-mm-dd format.
     */
    public static Task parseDeadline(String rest) throws EchoException {
        // Split at the first "/by" only (limit 2), so any later "/by" stays in the date part.
        // The \s+ on each side allows extra or irregular spaces around the "/by" marker.
        String[] parts = rest.split("\\s+/by\\s+", 2);
        if (parts.length < 2) {
            throw new EchoException("A deadline needs a '/by <when>' clause. "
                    + "Example: deadline return book /by Sunday");
        }
        String desc = parts[0].trim();
        String by = parts[1].trim();
        if (desc.isEmpty()) {
            throw new EchoException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new EchoException("The '/by' time of a deadline cannot be empty.");
        }
        validateDescription(desc);
        LocalDate byDate = parseDate(by, "Invalid date for '/by'. Use yyyy-mm-dd, e.g. 2019-10-15.");
        return new Deadline(desc, byDate);
    }

    /**
     * Parses an {@code event} command's arguments into an Event task.
     *
     * @param rest Text after the "event" command word, expected to
     *     contain "/from &lt;date&gt;" and "/to &lt;date&gt;" clauses.
     * @return The parsed Event task.
     * @throws EchoException If either clause is missing, any field is
     *     empty, either date is not in yyyy-mm-dd format, or the "/to"
     *     date is before the "/from" date.
     */
    public static Task parseEvent(String rest) throws EchoException {
        // \s+ tolerates extra/irregular whitespace around the "/from"/"/to" markers
        String[] fromParts = rest.split("\\s+/from\\s+", 2);
        if (fromParts.length < 2) {
            throw new EchoException("An event needs a '/from <when>' clause. "
                    + "Example: event meeting /from Mon 2pm /to 4pm");
        }
        String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
        if (toParts.length < 2) {
            throw new EchoException("An event needs a '/to <when>' clause after '/from'.");
        }
        String desc = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (desc.isEmpty()) {
            throw new EchoException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new EchoException("The '/from' time cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new EchoException("The '/to' time cannot be empty.");
        }
        validateDescription(desc);
        LocalDate fromDate = parseDate(from, "Invalid date for '/from'. Use yyyy-mm-dd, e.g. 2019-10-15.");
        LocalDate toDate = parseDate(to, "Invalid date for '/to'. Use yyyy-mm-dd, e.g. 2019-10-15.");
        if (toDate.isBefore(fromDate)) {
            throw new EchoException("An event's '/to' date cannot be before its '/from' date.");
        }
        return new Event(desc, fromDate, toDate);
    }

    /**
     * Parses an {@code on} command's argument into the date to query.
     *
     * @param rest Text after the "on" command word, expected to be a date.
     * @return The parsed date.
     * @throws EchoException If no date is given or it is not in
     *     yyyy-mm-dd format.
     */
    public static LocalDate parseOnDate(String rest) throws EchoException {
        String dateStr = rest.trim();
        if (dateStr.isEmpty()) {
            throw new EchoException("Please give a date. Example: on 2019-10-15");
        }
        return parseDate(dateStr, "Invalid date. Use yyyy-mm-dd, e.g. 2019-10-15.");
    }

    /**
     * Parses a {@code find} command's argument into the keyword to
     * search task descriptions for.
     *
     * @param rest Text after the "find" command word, expected to be a
     *     keyword.
     * @return The keyword to search for.
     * @throws EchoException If no keyword is given.
     */
    public static String parseFindKeyword(String rest) throws EchoException {
        String keyword = rest.trim();
        if (keyword.isEmpty()) {
            throw new EchoException("Please give a keyword to search for. Example: find book");
        }
        return keyword;
    }

    /**
     * Parses a task-number argument (e.g. for {@code mark}/{@code delete})
     * into a zero-based list index.
     *
     * @param rest Text after the command word, expected to be an integer.
     * @param count Current number of tasks, used to validate the index
     *     is in range.
     * @return The zero-based index of the referenced task.
     * @throws EchoException If no number is given, it isn't an integer,
     *     or it is out of range.
     */
    public static int parseTaskIndex(String rest, int count) throws EchoException {
        String s = rest.trim();
        if (s.isEmpty()) {
            throw new EchoException("Please give a task number. Example: mark 2");
        }
        int n;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new EchoException("Task number must be an integer, not '" + s + "'.");
        }
        if (n < 1 || n > count) {
            throw new EchoException("Task " + n + " does not exist. You have " + count + " task(s).");
        }
        int index = n - 1;
        // Postcondition: the range check above should guarantee this. Asserting
        // it here catches a future edit to that check silently breaking the
        // promise this method makes to callers like TaskList.get/remove.
        assert index >= 0 && index < count : "returned index should be within [0, count)";
        return index;
    }

    /**
     * Rejects a description that contains the save file's own field
     * separator, since that would corrupt the field count when the task
     * is saved and later reloaded (see {@link Task#FILE_FORMAT_SEPARATOR}).
     *
     * @param desc The already-trimmed description to check.
     * @throws EchoException If the description contains the separator.
     */
    private static void validateDescription(String desc) throws EchoException {
        if (desc.contains(Task.FILE_FORMAT_SEPARATOR)) {
            throw new EchoException("A task's description cannot contain '"
                    + Task.FILE_FORMAT_SEPARATOR + "', since that's used internally to save your tasks.");
        }
    }

    /**
     * Parses a yyyy-mm-dd date string, converting any parse failure into
     * the given user-facing error message.
     */
    private static LocalDate parseDate(String s, String errorMessage) throws EchoException {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new EchoException(errorMessage);
        }
    }
}
