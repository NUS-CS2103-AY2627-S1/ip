package mouse.parser;

import mouse.MouseException;
import mouse.task.Deadline;
import mouse.task.Event;
import mouse.task.Priority;
import mouse.task.ToDo;

/**
 * Extracts command details from a raw user input line.
 */
public class Parser {
    /**
     * Parses {@code todo DESCRIPTION} into a {@link ToDo}.
     *
     * @param input Full command line starting with {@code todo}.
     * @return Parsed to-do task.
     * @throws MouseException If the description is missing or empty.
     */
    public static ToDo parseTodo(String input) throws MouseException {
        String description = restAfter(input, "todo");
        if (description.isEmpty()) {
            throw new MouseException("That crumb has no name GRR");
        }
        return new ToDo(description);
    }

    /**
     * Parses {@code find KEYWORD} into the search keyword.
     *
     * @param input Full command line starting with {@code find}.
     * @return Keyword to search for.
     * @throws MouseException If the keyword is missing or empty.
     */
    public static String parseFind(String input) throws MouseException {
        String keyword = restAfter(input, "find");
        if (keyword.isEmpty()) {
            throw new MouseException("Mouse needs a sniff-word to search GRR");
        }
        return keyword;
    }

    /**
     * Parses {@code deadline DESCRIPTION /by WHEN} into a {@link Deadline}.
     *
     * @param input Full command line starting with {@code deadline}.
     * @return Parsed deadline task.
     * @throws MouseException If {@code /by}, the description, or the time is missing.
     */
    public static Deadline parseDeadline(String input) throws MouseException {
        String rest = restAfter(input, "deadline");
        if (rest.isEmpty()) {
            throw new MouseException("A deadline crumb needs a name and a '/by' time GRR");
        }
        int byIndex = indexOfSingleFlag(rest, "/by", "A deadline crumb needs a '/by' time GRR");
        String description = requireSlice(rest, 0, byIndex, "That deadline crumb has no name GRR");
        String by = requireSlice(rest, byIndex + "/by".length(), rest.length(),
                "The '/by' time cannot be empty GRR");
        return new Deadline(description, by);
    }

    /**
     * Parses {@code event DESCRIPTION /from START /to END} into an {@link Event}.
     *
     * @param input Full command line starting with {@code event}.
     * @return Parsed event task.
     * @throws MouseException If delimiters, description, or times are invalid.
     */
    public static Event parseEvent(String input) throws MouseException {
        String rest = restAfter(input, "event");
        if (rest.isEmpty()) {
            throw new MouseException("An event crumb needs a name, '/from', and '/to' GRR");
        }
        String missingRange = "An event crumb needs both '/from' and '/to' times GRR";
        int fromIndex = indexOfSingleFlag(rest, "/from", missingRange);
        int toIndex = indexOfSingleFlag(rest, "/to", missingRange);
        if (fromIndex > toIndex) {
            throw new MouseException("The '/from' time must come before the '/to' time GRR");
        }
        String description = requireSlice(rest, 0, fromIndex, "That event crumb has no name GRR");
        String from = requireSlice(rest, fromIndex + "/from".length(), toIndex,
                "The '/from' time cannot be empty GRR");
        String to = requireSlice(rest, toIndex + "/to".length(), rest.length(),
                "The '/to' time cannot be empty GRR");
        return new Event(description, from, to);
    }

    /**
     * Parses {@code priority INDEX LEVEL} into an index and {@link Priority}.
     *
     * @param input Full command line starting with {@code priority}.
     * @return Parsed index and priority.
     * @throws MouseException If the index or level is missing or invalid.
     */
    public static PriorityCommand parsePriority(String input) throws MouseException {
        String rest = restAfter(input, "priority");
        String[] parts = rest.split("\\s+", 2);
        if (rest.isEmpty() || parts.length < 2 || parts[1].isEmpty()) {
            throw new MouseException("Priority needs a crumb number and high, low, or none GRR");
        }
        if (parts[1].trim().split("\\s+").length != 1) {
            throw new MouseException("Priority only wants high, low, or none GRR");
        }
        int index = parsePositiveIndex(parts[0]) - 1;
        return new PriorityCommand(index, Priority.fromString(parts[1]));
    }

    /**
     * Parses the 1-based task number after {@code commandWord} as a 0-based index.
     *
     * @param input Full command line, for example {@code mark 2}.
     * @param commandWord Command word without a trailing space, for example {@code mark}.
     * @return Zero-based task index.
     * @throws MouseException If the number is missing, extra, or not a positive integer.
     */
    public static int parseIndex(String input, String commandWord) throws MouseException {
        assert commandWord != null && !commandWord.isEmpty() : "Command word should be a non-empty string";
        String rest = restAfter(input, commandWord);
        if (rest.isEmpty()) {
            throw new MouseException("Mouse needs a crumb number after " + commandWord + " GRR");
        }
        String[] parts = rest.split("\\s+");
        if (parts.length != 1) {
            throw new MouseException("Extra crumbs after the number GRR");
        }
        return parsePositiveIndex(parts[0]) - 1;
    }

    /**
     * Parses a 1-based crumb number.
     *
     * @param raw Number text.
     * @return Positive integer.
     * @throws MouseException If {@code raw} is not an integer of 1 or more.
     */
    private static int parsePositiveIndex(String raw) throws MouseException {
        try {
            int number = Integer.parseInt(raw);
            if (number < 1) {
                throw new MouseException("Crumb numbers start at 1 GRR");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new MouseException("That's not a valid crumb number GRR");
        }
    }

    /**
     * Rejects leftover words on a command that takes no arguments.
     *
     * @param input Raw user input.
     * @param commandWord Expected bare command.
     * @throws MouseException If anything follows {@code commandWord}.
     */
    public static void assertBareCommand(String input, String commandWord) throws MouseException {
        if (!input.trim().equalsIgnoreCase(commandWord)) {
            throw new MouseException("`" + commandWord + "` does not take extra crumbs GRR");
        }
    }

    /**
     * Returns the trimmed text after {@code commandWord}.
     *
     * @param input Full command line.
     * @param commandWord Leading command word.
     * @return Remaining argument text, or an empty string.
     */
    private static String restAfter(String input, String commandWord) {
        String trimmed = input.trim();
        if (trimmed.length() == commandWord.length()) {
            return "";
        }
        return trimmed.substring(commandWord.length()).trim();
    }

    /**
     * Returns the index of {@code flag}, requiring it exactly once.
     *
     * @param text Argument text to scan.
     * @param flag Flag such as {@code /by}.
     * @param missingMessage Error if {@code flag} is absent.
     * @return Index of {@code flag} in {@code text}.
     * @throws MouseException If {@code flag} is missing or repeated.
     */
    private static int indexOfSingleFlag(String text, String flag, String missingMessage)
            throws MouseException {
        if (countOccurrences(text, flag) > 1) {
            throw new MouseException("Use " + flag + " only once GRR");
        }
        int index = text.indexOf(flag);
        if (index == -1) {
            throw new MouseException(missingMessage);
        }
        return index;
    }

    /**
     * Returns a trimmed slice, rejecting blanks.
     *
     * @param text Source text.
     * @param start Start index, inclusive.
     * @param end End index, exclusive.
     * @param emptyMessage Error if the slice is blank.
     * @return Trimmed slice.
     * @throws MouseException If the slice is blank.
     */
    private static String requireSlice(String text, int start, int end, String emptyMessage)
            throws MouseException {
        String value = text.substring(start, end).trim();
        if (value.isEmpty()) {
            throw new MouseException(emptyMessage);
        }
        return value;
    }

    /**
     * Returns how many times {@code token} appears in {@code text}.
     *
     * @param text Text to scan.
     * @param token Substring to count.
     * @return Number of non-overlapping matches.
     */
    private static int countOccurrences(String text, String token) {
        int count = 0;
        int from = 0;
        while (true) {
            int at = text.indexOf(token, from);
            if (at < 0) {
                return count;
            }
            count++;
            from = at + token.length();
        }
    }
}
