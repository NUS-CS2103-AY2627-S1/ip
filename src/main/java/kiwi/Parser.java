package kiwi;

import java.time.LocalDate;
import java.util.List;

import kiwi.command.AddCommand;
import kiwi.command.Command;
import kiwi.command.DeleteCommand;
import kiwi.command.ExitCommand;
import kiwi.command.FindCommand;
import kiwi.command.FreeCommand;
import kiwi.command.ListCommand;
import kiwi.command.MarkCommand;
import kiwi.command.OnCommand;
import kiwi.command.UnmarkCommand;
import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.FreeSlot;
import kiwi.task.KiwiDate;
import kiwi.task.Task;
import kiwi.task.Todo;

/**
 * Makes sense of a raw user command line and returns a {@link Command} ready to run.
 * Does not mutate the task list or print messages.
 */
public class Parser {
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_ON = "on";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_FREE = "free";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";

    private static final String FLAG_BY = "/by";
    private static final String FLAG_FROM = "/from";
    private static final String FLAG_TO = "/to";

    private static final String DEADLINE_USAGE =
            "Deadlines need both a description and /by yyyy-MM-dd — "
                    + "e.g. deadline return book /by 2019-12-02";
    private static final String EVENT_USAGE =
            "Events need /from and /to as yyyy-MM-dd — "
                    + "e.g. event meeting /from 2019-10-04 /to 2019-10-11";
    private static final String FREE_USAGE =
            "Please give a number of hours, e.g. free 4";

    /**
     * Parses one full input line into a {@link Command}.
     * Leading, trailing, and repeated whitespace are treated as a single space.
     *
     * @param input full line typed by the user.
     * @return a command object that can be executed.
     * @throws KiwiException If the command is unknown or its arguments are invalid.
     */
    public static Command parse(String input) throws KiwiException {
        assert input != null : "Ui and GUI always pass a command line, never null";
        String commandLine = normalizeSpaces(input);
        if (commandLine.isEmpty()) {
            throw new KiwiException("Please type a command, e.g. todo borrow book");
        }

        String commandWord = firstWord(commandLine);
        String arguments = restAfterFirstWord(commandLine);

        if (commandWord.equals(COMMAND_BYE)) {
            requireNoArguments(arguments, COMMAND_BYE);
            return new ExitCommand();
        }
        if (commandWord.equals(COMMAND_LIST)) {
            requireNoArguments(arguments, COMMAND_LIST);
            return new ListCommand();
        }
        if (commandWord.equals(COMMAND_TODO)) {
            return new AddCommand(parseTodo(arguments));
        }
        if (commandWord.equals(COMMAND_DEADLINE)) {
            return new AddCommand(parseDeadline(arguments));
        }
        if (commandWord.equals(COMMAND_EVENT)) {
            return new AddCommand(parseEvent(arguments));
        }
        if (commandWord.equals(COMMAND_ON)) {
            return new OnCommand(parseOnDate(arguments));
        }
        if (commandWord.equals(COMMAND_FIND)) {
            return new FindCommand(parseFindKeyword(arguments));
        }
        if (commandWord.equals(COMMAND_FREE)) {
            return parseFree(arguments);
        }
        if (commandWord.equals(COMMAND_MARK)) {
            return new MarkCommand(parseTaskNumber(arguments, COMMAND_MARK));
        }
        if (commandWord.equals(COMMAND_UNMARK)) {
            return new UnmarkCommand(parseTaskNumber(arguments, COMMAND_UNMARK));
        }
        if (commandWord.equals(COMMAND_DELETE)) {
            return new DeleteCommand(parseTaskNumber(arguments, COMMAND_DELETE));
        }
        throw new KiwiException(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, free, list, "
                        + "mark, unmark, delete, or bye.");
    }

    /**
     * Trims the line and collapses internal whitespace so extra spaces are not treated as errors.
     */
    private static String normalizeSpaces(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    private static String firstWord(String commandLine) {
        int spaceIndex = commandLine.indexOf(' ');
        if (spaceIndex < 0) {
            return commandLine;
        }
        return commandLine.substring(0, spaceIndex);
    }

    private static String restAfterFirstWord(String commandLine) {
        int spaceIndex = commandLine.indexOf(' ');
        if (spaceIndex < 0) {
            return "";
        }
        return commandLine.substring(spaceIndex + 1);
    }

    private static void requireNoArguments(String arguments, String commandWord) throws KiwiException {
        if (!arguments.isEmpty()) {
            throw new KiwiException(commandWord + " doesn't take extra words. Just type: " + commandWord);
        }
    }

    /**
     * Builds a to-do from the description after {@code todo}.
     *
     * @param description task description.
     * @return a new {@link Todo}.
     * @throws KiwiException If the description is empty or contains the save-file separator.
     */
    private static Todo parseTodo(String description) throws KiwiException {
        if (description.isEmpty()) {
            throw new KiwiException("A todo needs a description — try: todo borrow book");
        }
        assert !description.isEmpty() : "Todo description is non-empty after the user-input check";
        Task.validateDescription(description);
        return new Todo(description);
    }

    /**
     * Parses {@code description /by yyyy-MM-dd} into a deadline task.
     *
     * @param body text after the {@code deadline} command.
     * @return a new {@link Deadline}.
     * @throws KiwiException If the description or {@code /by} date is missing or invalid.
     */
    private static Deadline parseDeadline(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "A deadline needs details — try: deadline return book /by 2019-12-02");
        }
        rejectUnknownAndDuplicateFlags(body, List.of(FLAG_BY), DEADLINE_USAGE);
        requireFlagPresent(body, FLAG_BY, DEADLINE_USAGE);
        String[] parts = splitOnFlag(body, FLAG_BY);
        boolean hasDescription = !parts[0].isEmpty();
        boolean hasByDate = parts.length == 2 && !parts[1].isEmpty();
        if (!hasDescription || !hasByDate) {
            throw new KiwiException(DEADLINE_USAGE);
        }
        String description = parts[0];
        String byText = parts[1];
        Task.validateDescription(description);
        LocalDate by = parseSingleDate(byText,
                "Please use only a yyyy-MM-dd date after /by — "
                        + "e.g. deadline return book /by 2019-12-02");
        return new Deadline(description, by);
    }

    /**
     * Parses {@code description /from yyyy-MM-dd /to yyyy-MM-dd} into an event.
     *
     * @param body text after the {@code event} command.
     * @return a new {@link Event}.
     * @throws KiwiException If the description or dates are missing or invalid.
     */
    private static Event parseEvent(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(
                    "An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11");
        }
        rejectUnknownAndDuplicateFlags(body, List.of(FLAG_FROM, FLAG_TO), EVENT_USAGE);
        requireFlagPresent(body, FLAG_FROM, EVENT_USAGE);
        requireFlagPresent(body, FLAG_TO, EVENT_USAGE);
        requireFlagOrder(body, FLAG_FROM, FLAG_TO, EVENT_USAGE);

        String[] fromSplit = splitOnFlag(body, FLAG_FROM);
        boolean hasDescription = !fromSplit[0].isEmpty();
        if (!hasDescription || fromSplit.length != 2) {
            throw new KiwiException(EVENT_USAGE);
        }
        String[] toSplit = splitOnFlag(fromSplit[1], FLAG_TO);
        boolean hasFromDate = !toSplit[0].isEmpty();
        boolean hasToDate = toSplit.length == 2 && !toSplit[1].isEmpty();
        if (!hasFromDate || !hasToDate) {
            throw new KiwiException(EVENT_USAGE);
        }
        String description = fromSplit[0];
        Task.validateDescription(description);
        LocalDate from = parseSingleDate(toSplit[0],
                "Please use only a yyyy-MM-dd date after /from — "
                        + "e.g. event meeting /from 2019-10-04 /to 2019-10-11");
        LocalDate to = parseSingleDate(toSplit[1],
                "Please use only a yyyy-MM-dd date after /to — "
                        + "e.g. event meeting /from 2019-10-04 /to 2019-10-11");
        return new Event(description, from, to);
    }

    /**
     * Checks that a {@code find} keyword is not empty.
     *
     * @param keyword search text after {@code find}
     * @return the keyword unchanged
     * @throws KiwiException if the keyword is missing
     */
    private static String parseFindKeyword(String keyword) throws KiwiException {
        if (keyword.isEmpty()) {
            throw new KiwiException("Please give a keyword to search for, e.g. find book");
        }
        assert !keyword.isEmpty() : "Find keyword is non-empty after the user-input check";
        return keyword;
    }

    /**
     * Parses {@code HOURS} or {@code HOURS /from yyyy-MM-dd} into a {@link FreeCommand}.
     * Omitting {@code /from} means the search starts from today when the command runs.
     *
     * @param body text after the {@code free} command.
     * @return a command that searches for a free slot of that length.
     * @throws KiwiException If the hours or optional start date are missing or invalid.
     */
    private static FreeCommand parseFree(String body) throws KiwiException {
        if (body.isEmpty()) {
            throw new KiwiException(FREE_USAGE);
        }
        rejectUnknownAndDuplicateFlags(body, List.of(FLAG_FROM),
                "free takes a number of hours and optional /from yyyy-MM-dd — e.g. free 4");
        if (body.endsWith(" " + FLAG_FROM) || body.equals(FLAG_FROM)) {
            throw new KiwiException("Please give a start date, e.g. free 4 /from 2019-12-01");
        }
        requireFlagSpacing(body, FLAG_FROM,
                "Please put spaces around /from — e.g. free 4 /from 2019-12-01");

        String[] fromSplit = splitOnFlag(body, FLAG_FROM);
        String hoursText = fromSplit[0];
        LocalDate startDate = null;
        if (fromSplit.length == 2) {
            String dateText = fromSplit[1];
            if (dateText.isEmpty()) {
                throw new KiwiException("Please give a start date, e.g. free 4 /from 2019-12-01");
            }
            startDate = parseSingleDate(dateText,
                    "Please use only a yyyy-MM-dd date after /from — e.g. free 4 /from 2019-12-01");
        }
        int hours = parseFreeHours(hoursText);
        return new FreeCommand(hours, startDate);
    }

    /**
     * Parses a positive hour count that fits in one work day.
     *
     * @param hoursText text immediately after {@code free}, before any {@code /from}.
     * @return the hour count.
     * @throws KiwiException If the text is not a valid slot length.
     */
    private static int parseFreeHours(String hoursText) throws KiwiException {
        if (hoursText.isEmpty() || hoursText.contains(" ")) {
            throw new KiwiException(FREE_USAGE);
        }
        final int hours;
        try {
            hours = Integer.parseInt(hoursText);
        } catch (NumberFormatException e) {
            throw new KiwiException("That duration doesn't look like a number: " + hoursText);
        }
        if (hours < 1) {
            throw new KiwiException("Please give a positive number of hours, e.g. free 4");
        }
        if (hours > FreeSlot.WORK_DAY_HOURS) {
            throw new KiwiException(
                    "A free slot must fit in one work day (08:00-18:00, "
                            + FreeSlot.WORK_DAY_HOURS + " hours).");
        }
        return hours;
    }

    /**
     * Parses the date argument of an {@code on} command.
     *
     * @param dateText {@code yyyy-MM-dd} date.
     * @return the parsed date.
     * @throws KiwiException If the date is missing, has extra words, or is invalid.
     */
    private static LocalDate parseOnDate(String dateText) throws KiwiException {
        if (dateText.isEmpty()) {
            throw new KiwiException("Please give a date, e.g. on 2019-12-02");
        }
        return parseSingleDate(dateText, "on takes only a yyyy-MM-dd date, e.g. on 2019-12-02");
    }

    /**
     * Reads the 1-based task number from a mark/unmark/delete command.
     * Does not check whether the index exists in the list — commands do that at execute time.
     *
     * @param arguments text after the command word.
     * @param command {@code mark}, {@code unmark}, or {@code delete}.
     * @return 0-based index.
     * @throws KiwiException If the number is missing, extra, not an integer, or less than 1.
     */
    private static int parseTaskNumber(String arguments, String command) throws KiwiException {
        if (arguments.isEmpty()) {
            throw new KiwiException("Please give a task number, e.g. " + command + " 1");
        }
        String[] parts = arguments.split(" ");
        if (parts.length > 1) {
            throw new KiwiException("Please give only one task number, e.g. " + command + " 1");
        }
        final int userNumber;
        try {
            userNumber = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new KiwiException("That task number doesn't look like a number: " + parts[0]);
        }
        if (userNumber < 1) {
            throw new KiwiException("Task numbers start from 1.");
        }
        return userNumber - 1;
    }

    /**
     * Rejects flags that this command does not use, and the same flag given twice.
     */
    private static void rejectUnknownAndDuplicateFlags(String body, List<String> allowedFlags, String usage)
            throws KiwiException {
        boolean[] seen = new boolean[allowedFlags.size()];
        for (String token : body.split(" ")) {
            if (!token.startsWith("/")) {
                continue;
            }
            int allowedIndex = allowedFlags.indexOf(token);
            if (allowedIndex < 0) {
                throw new KiwiException("Unexpected " + token + ". " + usage);
            }
            if (seen[allowedIndex]) {
                throw new KiwiException(token + " appears more than once. " + usage);
            }
            seen[allowedIndex] = true;
        }
    }

    /**
     * Ensures {@code flag} appears as its own token, or explains how to space it.
     */
    private static void requireFlagPresent(String body, String flag, String usage) throws KiwiException {
        if (containsFlag(body, flag)) {
            return;
        }
        requireFlagSpacing(body, flag, "Please put spaces around " + flag + ". " + usage);
        throw new KiwiException(usage);
    }

    /**
     * Rejects {@code flag} glued to neighboring text instead of standing as its own token.
     */
    private static void requireFlagSpacing(String body, String flag, String message) throws KiwiException {
        if (!containsFlag(body, flag) && body.contains(flag)) {
            throw new KiwiException(message);
        }
    }

    /**
     * Ensures {@code firstFlag} appears before {@code secondFlag} in {@code body}.
     */
    private static void requireFlagOrder(String body, String firstFlag, String secondFlag, String usage)
            throws KiwiException {
        int firstIndex = indexOfFlag(body, firstFlag);
        int secondIndex = indexOfFlag(body, secondFlag);
        if (firstIndex >= 0 && secondIndex >= 0 && secondIndex < firstIndex) {
            throw new KiwiException("Please put " + firstFlag + " before " + secondFlag + ". " + usage);
        }
    }

    private static boolean containsFlag(String body, String flag) {
        return indexOfFlag(body, flag) >= 0;
    }

    private static int indexOfFlag(String body, String flag) {
        String[] tokens = body.split(" ");
        int position = 0;
        for (String token : tokens) {
            if (token.equals(flag)) {
                return position;
            }
            position += token.length() + 1;
        }
        return -1;
    }

    /**
     * Splits {@code body} on the first {@code flag} token. Missing flags yield a one-element array.
     */
    private static String[] splitOnFlag(String body, String flag) {
        String delimiter = " " + flag + " ";
        if (body.startsWith(flag + " ")) {
            return new String[] {"", body.substring(flag.length() + 1)};
        }
        return body.split(delimiter, 2);
    }

    /**
     * Parses a single {@code yyyy-MM-dd} token, rejecting extra words after a valid date.
     */
    private static LocalDate parseSingleDate(String dateText, String extraWordsMessage) throws KiwiException {
        if (!dateText.contains(" ")) {
            return KiwiDate.parse(dateText);
        }
        String firstToken = firstWord(dateText);
        KiwiDate.parse(firstToken);
        throw new KiwiException(extraWordsMessage);
    }
}
