package coco;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of raw user input: identifies the command and parses it into
 * the corresponding Task or task-index argument.
 */
public class Parser {
    /**
     * Identifies which command the given input line is asking for.
     *
     * @param input Raw user input line.
     * @return The matching Command, or Command.UNKNOWN if none match.
     */
    public static Command parseCommand(String input) {
        return Command.fromInput(input);
    }

    /**
     * Parses a "todo" command into a Todo task.
     *
     * @param input Raw user input line, e.g. "todo read book".
     * @return The parsed Todo.
     * @throws CocoException If the description is empty.
     */
    public static Task parseTodo(String input) throws CocoException {
        String description = argumentsOf(input, Command.TODO).trim();
        if (description.isEmpty()) {
            throw new CocoException("Whoa, gotta tell me what the todo actually is!");
        }
        return new Todo(description);
    }

    /**
     * Parses a "deadline" command into a Deadline task. The date may
     * optionally be followed by "/every daily|weekly|monthly" to make it a
     * recurring deadline.
     *
     * @param input Raw user input line, e.g.
     *              "deadline return book /by 2019-10-15" or
     *              "deadline standup /by 2019-10-15 /every daily".
     * @return The parsed Deadline.
     * @throws CocoException If the '/by' marker is missing, the description,
     *                       date, or recurrence (when '/every' is given) is
     *                       empty or invalid.
     */
    public static Task parseDeadline(String input) throws CocoException {
        String rest = argumentsOf(input, Command.DEADLINE);
        int byIndex = rest.indexOf("/by");
        if (byIndex == -1) {
            throw new CocoException(
                    "Easy now, a deadline needs a '/by' date! Try: deadline "
                            + "<description> /by <date> [/every daily|weekly|monthly]");
        }
        String description = rest.substring(0, byIndex).trim();
        DateAndRecurrence dateAndRecurrence = parseDateAndRecurrence(rest.substring(byIndex + "/by".length()));
        if (description.isEmpty()) {
            throw new CocoException("Hold up, the deadline needs an actual description!");
        }
        if (dateAndRecurrence.byText().isEmpty()) {
            throw new CocoException("Hey now, gotta give me a date for that deadline!");
        }
        LocalDate by;
        try {
            by = LocalDate.parse(dateAndRecurrence.byText());
        } catch (DateTimeParseException e) {
            throw new CocoException("Hmm, '" + dateAndRecurrence.byText()
                    + "' doesn't look like a date to me. Use yyyy-mm-dd, like 2019-10-15.");
        }
        return new Deadline(description, by, dateAndRecurrence.recurrence());
    }

    /**
     * A deadline's date text and optional recurrence, still unparsed - what
     * comes after "/by" in a deadline command, split on an optional trailing
     * "/every ...".
     */
    private record DateAndRecurrence(String byText, Recurrence recurrence) {
    }

    /**
     * Splits the text after "/by" into the date text and, if a "/every
     * <recurrence>" suffix is present, the recurrence it names.
     *
     * @param afterBy Everything after the "/by" marker.
     * @return The split-out date text and recurrence (null if none given).
     * @throws CocoException If "/every" is present but names no recurrence,
     *                       or names one Recurrence.fromText doesn't recognize.
     */
    private static DateAndRecurrence parseDateAndRecurrence(String afterBy) throws CocoException {
        int everyIndex = afterBy.indexOf("/every");
        if (everyIndex == -1) {
            return new DateAndRecurrence(afterBy.trim(), null);
        }
        String byText = afterBy.substring(0, everyIndex).trim();
        String recurrenceText = afterBy.substring(everyIndex + "/every".length()).trim();
        if (recurrenceText.isEmpty()) {
            throw new CocoException("How often's this thing happening? Give me daily, weekly, or monthly.");
        }
        return new DateAndRecurrence(byText, Recurrence.fromText(recurrenceText));
    }

    /**
     * Parses an "event" command into an Event task.
     *
     * @param input Raw user input line, e.g.
     *              "event meeting /from Mon 2pm /to 4pm".
     * @return The parsed Event.
     * @throws CocoException If the '/from' or '/to' marker is missing (or
     *                       out of order), or the description, start, or
     *                       end time is empty.
     */
    public static Task parseEvent(String input) throws CocoException {
        String rest = argumentsOf(input, Command.EVENT);
        int fromIndex = rest.indexOf("/from");
        int toIndex = rest.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new CocoException(
                    "Easy now, an event needs a '/from' and '/to'! Try: event "
                            + "<description> /from <start> /to <end>");
        }
        String description = rest.substring(0, fromIndex).trim();
        String from = rest.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = rest.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new CocoException("Hold up, the event needs an actual description!");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new CocoException("Gonna need both a start and end time for that one!");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses a "find" command into the keyword to search for.
     *
     * @param input Raw user input line, e.g. "find book".
     * @return The trimmed keyword.
     * @throws CocoException If the keyword is empty.
     */
    public static String parseFind(String input) throws CocoException {
        String keyword = argumentsOf(input, Command.FIND).trim();
        if (keyword.isEmpty()) {
            throw new CocoException("What am I even looking for? Give me a keyword!");
        }
        return keyword;
    }

    /**
     * Parses the task number out of a "mark"/"unmark"/"delete" command and
     * converts it to a zero-based index.
     *
     * @param input Raw user input line, e.g. "mark 2".
     * @param commandWord The command word the input starts with, e.g. "mark".
     * @param taskCount Current number of tasks, used to validate range.
     * @return Zero-based index of the referenced task.
     * @throws CocoException If the task number is missing, not a number, or
     *                       out of range.
     */
    public static int parseIndex(String input, String commandWord, int taskCount)
            throws CocoException {
        String arg = input.length() > commandWord.length()
                ? input.substring(commandWord.length()).trim()
                : "";
        if (arg.isEmpty()) {
            throw new CocoException("Which task number, chief? Gotta tell me that to "
                    + commandWord + " it!");
        }
        int index;
        try {
            index = Integer.parseInt(arg) - 1;
        } catch (NumberFormatException e) {
            throw new CocoException("'" + arg + "' ain't a number I recognize!");
        }
        if (index < 0 || index >= taskCount) {
            throw new CocoException("No task number " + (index + 1) + " around here!");
        }
        // Documents the guarantee this method makes to its callers (Coco,
        // TaskList.get/remove): any index returned is already in range.
        assert index >= 0 && index < taskCount : "range check above should have thrown otherwise";
        return index;
    }

    /**
     * Strips the leading command word off the input, returning whatever
     * follows it (or an empty string if there's nothing after it).
     *
     * @param input Raw user input line.
     * @param command Command whose keyword should be stripped.
     * @return The remainder of the input after the command word.
     */
    private static String argumentsOf(String input, Command command) {
        String keyword = command.name().toLowerCase();
        return input.equals(keyword) ? "" : input.substring(keyword.length() + 1);
    }
}
