package botzilla.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import botzilla.BotzillaException;
import botzilla.task.DateTimeUtil;
import botzilla.task.DeadlineTask;
import botzilla.task.EventTask;
import botzilla.task.Task;
import botzilla.task.ToDoTask;

/**
 * Interprets raw user input: identifies which command was issued and
 * builds the corresponding Task, task index, or date from the input text.
 */
public class Parser {

    /**
     * The recognised kinds of user commands.
     */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, ON, FIND, TODO, DEADLINE, EVENT, TAG, UNTAG, UNKNOWN
    }

    // Command keywords, defined once and reused for both command detection
    // (parseCommandType) and argument extraction (the parseXxx methods
    // below), so the two never drift out of sync. MARK and UNMARK include
    // their trailing space because they require an argument and are never
    // matched bare; the rest are matched both bare and with a trailing
    // space, so the space is stripped separately via trim().
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_DELETE = "delete";
    private static final String CMD_ON = "on";
    private static final String CMD_TODO = "todo";
    private static final String CMD_DEADLINE = "deadline";
    private static final String CMD_EVENT = "event";
    private static final String CMD_FIND = "find";
    private static final String CMD_TAG = "tag ";
    private static final String CMD_UNTAG = "untag ";

    // Matches inline "#tag" tokens embedded in a task description, e.g.
    // "read book #fun". Used to let users tag a task at creation time,
    // as an alternative to the dedicated "tag"/"untag" commands.
    private static final Pattern INLINE_TAG_PATTERN = Pattern.compile("#(\\w+)");

    // A valid tag/untag command argument: letters, digits, and underscores,
    // with an optional leading '#'. Rejects anything that would corrupt the
    // comma-delimited tags field in the save file (e.g. a literal ',' or
    // '|'), and rejects a bare/empty tag (e.g. a lone "#").
    private static final Pattern VALID_TAG_TOKEN = Pattern.compile("#?\\w+");

    /**
     * Determines which command the given input represents, based on its
     * leading keyword.
     *
     * @param input the raw user input
     * @return the matching CommandType, or UNKNOWN if unrecognized
     */
    public static CommandType parseCommandType(String input) {
        // Leading/trailing whitespace around the whole command (e.g. a
        // stray space from copy-pasting) shouldn't stop it from being
        // recognized, so match against a trimmed copy throughout.
        String trimmed = input.trim();
        if (trimmed.equals("bye")) {
            return CommandType.BYE;
        } else if (trimmed.equals("list")) {
            return CommandType.LIST;
        } else if (trimmed.equals(CMD_MARK.trim()) || trimmed.startsWith(CMD_MARK)) {
            return CommandType.MARK;
        } else if (trimmed.equals(CMD_UNMARK.trim()) || trimmed.startsWith(CMD_UNMARK)) {
            return CommandType.UNMARK;
        } else if (trimmed.equals(CMD_DELETE) || trimmed.startsWith(CMD_DELETE + " ")) {
            return CommandType.DELETE;
        } else if (trimmed.equals(CMD_ON) || trimmed.startsWith(CMD_ON + " ")) {
            return CommandType.ON;
        } else if (trimmed.equals(CMD_TODO) || trimmed.startsWith(CMD_TODO + " ")) {
            return CommandType.TODO;
        } else if (trimmed.equals(CMD_DEADLINE) || trimmed.startsWith(CMD_DEADLINE + " ")) {
            return CommandType.DEADLINE;
        } else if (trimmed.equals(CMD_EVENT) || trimmed.startsWith(CMD_EVENT + " ")) {
            return CommandType.EVENT;
        } else if (trimmed.equals(CMD_FIND) || trimmed.startsWith(CMD_FIND + " ")) {
            return CommandType.FIND;
        } else if (trimmed.equals(CMD_TAG.trim()) || trimmed.startsWith(CMD_TAG)) {
            return CommandType.TAG;
        } else if (trimmed.equals(CMD_UNTAG.trim()) || trimmed.startsWith(CMD_UNTAG)) {
            return CommandType.UNTAG;
        } else {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Returns the length of the "mark " keyword, for callers that need to
     * strip it from raw input before extracting the task number argument.
     *
     * @return the length of the "mark " command keyword, including its trailing space
     */
    public static int markKeywordLength() {
        return CMD_MARK.length();
    }

    /**
     * Returns the length of the "unmark " keyword, for callers that need to
     * strip it from raw input before extracting the task number argument.
     *
     * @return the length of the "unmark " command keyword, including its trailing space
     */
    public static int unmarkKeywordLength() {
        return CMD_UNMARK.length();
    }

    /**
     * Returns the length of the "delete" keyword, for callers that need to
     * strip it from raw input before extracting the task number argument.
     *
     * @return the length of the "delete" command keyword
     */
    public static int deleteKeywordLength() {
        return CMD_DELETE.length();
    }

    /**
     * Returns the length of the "tag " keyword, for callers that need to
     * strip it from raw input before extracting the task number/tag arguments.
     *
     * @return the length of the "tag " command keyword, including its trailing space
     */
    public static int tagKeywordLength() {
        return CMD_TAG.length();
    }

    /**
     * Returns the length of the "untag " keyword, for callers that need to
     * strip it from raw input before extracting the task number/tag arguments.
     *
     * @return the length of the "untag " command keyword, including its trailing space
     */
    public static int untagKeywordLength() {
        return CMD_UNTAG.length();
    }

    /**
     * Parses a 1-based task number from user input, validating that it
     * is numeric and refers to an existing task.
     *
     * @param numberText the raw number text, e.g. "1"
     * @param taskCount  the current number of tasks, for bounds checking
     * @return the corresponding 0-based task index
     * @throws BotzillaException if the text isn't a valid number or is
     *                           out of range
     */
    public static int parseTaskNumber(String numberText, int taskCount) throws BotzillaException {
        int index;
        try {
            index = Integer.parseInt(numberText.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new BotzillaException("Please give me a valid task number, e.g. mark 1");
        }
        if (index < 0 || index >= taskCount) {
            throw new BotzillaException("I fear I don't have a task numbered " + numberText.trim() + ".");
        }
        return index;
    }

    /**
     * Parses a "todo" command into a ToDoTask.
     *
     * @param input the raw user input, e.g. "todo read book"
     * @return the constructed ToDoTask
     * @throws BotzillaException if the description is empty or contains a
     *                           character reserved by the save file format
     */
    public static Task parseTodo(String input) throws BotzillaException {
        String rest = input.length() > CMD_TODO.length() ? input.substring(CMD_TODO.length()).trim() : "";
        InlineTags extracted = extractInlineTags(rest);
        if (extracted.text().isEmpty()) {
            throw new BotzillaException("Please give the todo a name! Description cannot be empty");
        }
        requireNoReservedChars(extracted.text(), "a task description");
        Task task = new ToDoTask(extracted.text());
        extracted.tags().forEach(task::addTag);
        return task;
    }

    /**
     * Parses a "deadline" command into a DeadlineTask.
     *
     * @param input Raw user input, e.g. "deadline return book /by 2/12/2019 1800".
     * @return The constructed DeadlineTask.
     * @throws BotzillaException If the description or "/by" date is missing,
     *                           "/by" is given more than once, the text
     *                           contains a character reserved by the save
     *                           file format, or the date given is invalid
     *                           (see {@link botzilla.task.DateTimeUtil#parse}).
     */
    public static Task parseDeadline(String input) throws BotzillaException {
        String rest = input.length() > CMD_DEADLINE.length() ? input.substring(CMD_DEADLINE.length()).trim() : "";
        InlineTags extracted = extractInlineTags(rest);
        requireNoReservedChars(extracted.text(), "a task description or date");

        String[] parts = extracted.text().split("\\s+/by\\s+");
        if (parts.length > 2) {
            throw new BotzillaException("Please give me only one '/by' date for a deadline.");
        }
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new BotzillaException("ADD A NAME, ADD A DATE! A deadline needs a description and a '/by' "
                    + "date, e.g. deadline return book /by 2/12/2019 1800");
        }
        Task task = new DeadlineTask(parts[0].trim(), parts[1].trim());
        extracted.tags().forEach(task::addTag);
        return task;
    }

    /**
     * Parses an "event" command into an EventTask.
     *
     * @param input Raw user input, e.g. "event meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
     * @return The constructed EventTask.
     * @throws BotzillaException If the description, "/from", or "/to" is
     *                           missing, either is given more than once, the
     *                           text contains a character reserved by the
     *                           save file format, either date given is
     *                           invalid (see {@link botzilla.task.DateTimeUtil#parse}),
     *                           or the start isn't before the end.
     */
    public static Task parseEvent(String input) throws BotzillaException {
        String rest = input.length() > CMD_EVENT.length() ? input.substring(CMD_EVENT.length()).trim() : "";
        InlineTags extracted = extractInlineTags(rest);
        requireNoReservedChars(extracted.text(), "a task description or date");

        String[] fromSplit = extracted.text().split("\\s+/from\\s+");
        if (fromSplit.length > 2) {
            throw new BotzillaException("Please give me only one '/from' time for an event.");
        }
        if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
            throw new BotzillaException("ERROR ALERT! An event needs a description and '/from' and '/to' "
                    + "times, e.g. event meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        String[] toSplit = fromSplit[1].split("\\s+/to\\s+");
        if (toSplit.length > 2) {
            throw new BotzillaException("Please give me only one '/to' time for an event.");
        }
        if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
            throw new BotzillaException("ERROR ALERT! An event needs a description and '/from' and '/to' "
                    + "times, e.g. event meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        EventTask task = new EventTask(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim());
        if (task.getStart() != null && task.getEnd() != null && !task.getStart().isBefore(task.getEnd())) {
            throw new BotzillaException("An event's start time must be before its end time!");
        }
        extracted.tags().forEach(task::addTag);
        return task;
    }

    /**
     * Parses an "on" command into a target date to query.
     *
     * @param input the raw user input, e.g. "on 2/12/2019"
     * @return the parsed LocalDate
     * @throws BotzillaException if no date is given or it can't be parsed
     */
    public static LocalDate parseOnDate(String input) throws BotzillaException {
        String dateText = input.length() > CMD_ON.length() ? input.substring(CMD_ON.length()).trim() : "";
        if (dateText.isEmpty()) {
            throw new BotzillaException("Please give me a date, e.g. on 2/12/2019");
        }

        Optional<LocalDateTime> parsed = DateTimeUtil.parse(dateText);
        if (parsed.isEmpty()) {
            throw new BotzillaException("I couldn't understand that date. Try a format like 2/12/2019.");
        }
        return parsed.get().toLocalDate();
    }

    /**
     * Parses a "find" command into a search keyword.
     *
     * @param input Raw user input, e.g. "find book".
     * @return The trimmed keyword to search for.
     * @throws BotzillaException If no keyword is given.
     */
    public static String parseFindKeyword(String input) throws BotzillaException {
        String keyword = input.length() > CMD_FIND.length() ? input.substring(CMD_FIND.length()).trim() : "";
        if (keyword.isEmpty()) {
            throw new BotzillaException("Please give me a keyword to search for, e.g. find book");
        }
        return keyword;
    }

    /**
     * Parses a "tag"/"untag" command into the target task index and the
     * tag name(s) to add/remove, e.g. "2 fun urgent" targets task 2 with
     * tags "fun" and "urgent". Tags may be written with or without a
     * leading '#'.
     *
     * @param input         raw user input, starting with the "tag "/"untag " keyword
     * @param keywordLength length of the leading keyword, to strip before parsing
     * @param taskCount     current number of tasks, for bounds checking
     * @return the parsed task index and tag names
     * @throws BotzillaException if the task number is missing/invalid, no
     *                           tag name is given, or a tag name contains a
     *                           character other than letters/digits/underscore
     *                           (an optional leading '#' is allowed)
     */
    public static TagCommand parseTagCommand(String input, int keywordLength, int taskCount)
            throws BotzillaException {
        String rest = input.length() > keywordLength ? input.substring(keywordLength).trim() : "";
        String[] tokens = rest.split("\\s+", 2);
        if (tokens.length < 2 || tokens[1].isBlank()) {
            throw new BotzillaException("Please give me a task number and at least one tag, e.g. tag 2 fun");
        }
        int index = parseTaskNumber(tokens[0], taskCount);
        List<String> tagNames = Arrays.asList(tokens[1].trim().split("\\s+"));
        for (String tagName : tagNames) {
            if (!VALID_TAG_TOKEN.matcher(tagName).matches()) {
                throw new BotzillaException("\"" + tagName + "\" isn't a valid tag — tags can only contain "
                        + "letters, numbers, and underscores, with an optional leading '#', e.g. tag 2 fun_times");
            }
        }
        return new TagCommand(index, tagNames);
    }

    /**
     * Rejects text containing a character reserved by the save file's
     * pipe-delimited format (a literal '|') or a line break, either of
     * which would corrupt the file (splitting the line into extra fields,
     * or into extra lines) the next time tasks are saved and reloaded.
     *
     * @param text     the text to check
     * @param whatItIs a short description of the text, for the error message
     * @throws BotzillaException if the text contains '|', '\n', or '\r'
     */
    private static void requireNoReservedChars(String text, String whatItIs) throws BotzillaException {
        if (text.contains("|") || text.contains("\n") || text.contains("\r")) {
            throw new BotzillaException("Sorry, " + whatItIs + " can't contain a '|' character or line breaks — "
                    + "that would mess up how I save your tasks.");
        }
    }

    /**
     * The parsed result of a "tag"/"untag" command: the target task index
     * and the tag name(s) to apply.
     *
     * @param index    zero-based index of the target task
     * @param tagNames the tag name(s) to add/remove, in the order given
     */
    public record TagCommand(int index, List<String> tagNames) {
    }

    /**
     * The result of extracting inline "#tag" tokens from free text: the
     * text with those tokens removed, and the tag names found, in order.
     *
     * @param text the tag-stripped text
     * @param tags the tag names found, in order
     */
    private record InlineTags(String text, List<String> tags) {
    }

    /**
     * Scans free-form text for inline "#tag" tokens (e.g. "read book
     * #fun #easy") and removes them, so task descriptions can carry tags
     * without a separate "tag" command.
     *
     * @param text the raw text to scan for inline tags
     * @return the tag-stripped text and the tag names found, in order
     */
    private static InlineTags extractInlineTags(String text) {
        Matcher matcher = INLINE_TAG_PATTERN.matcher(text);
        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }
        String stripped = matcher.replaceAll("").trim().replaceAll("\\s+", " ");
        return new InlineTags(stripped, tags);
    }
}
