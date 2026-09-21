package thaisbot.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import thaisbot.ThaisBotException;
import thaisbot.command.commands.AddDeadlineCommand;
import thaisbot.command.commands.AddEventCommand;
import thaisbot.command.commands.AddTodoCommand;
import thaisbot.command.commands.DeleteCommand;
import thaisbot.command.commands.ExitCommand;
import thaisbot.command.commands.FindCommand;
import thaisbot.command.commands.FindOnDateCommand;
import thaisbot.command.commands.ListCommand;
import thaisbot.command.commands.MarkCommand;
import thaisbot.command.commands.UnmarkCommand;

/**
 * Parses user input into Command objects. Exposes helper parsing methods for the
 * different argument types used by commands.
 */
public class Parser {
    // STRICT resolving rejects non-existent dates (e.g., 2026-02-30) instead of silently changing
    // them to the last valid day. STRICT needs "uuuu" (year) instead of "yyyy" (year-of-era).
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_COLON_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final String TAGS_HELP_MESSAGE =
            "Tags must use # followed by letters or numbers only.";
    private static final String TODO_USAGE = "Use: todo <description> [#tag ...]";
    /** Separator used between fields in the data file, so it cannot appear inside a description. */
    private static final String DATA_SEPARATOR = "|";

    /**
     * Parses a raw user input line and returns the corresponding Command.
     * @param userInput raw input from the user
     * @return parsed Command
     * @throws ThaisBotException if the command or its arguments are invalid
     */
    public Command parse(String userInput) throws ThaisBotException {
        String[] parts = userInput.trim().split("\\s+", 2);
        String commandWord = parts[0];

        switch (commandWord) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(parseTaskNumberArgument(parts,
                        "Please provide a task number to mark."));
            case "unmark":
                return new UnmarkCommand(parseTaskNumberArgument(parts,
                        "Please provide a task number to unmark."));
            case "todo":
                if (parts.length < 2) {
                    throw new ThaisBotException(TODO_USAGE);
                }
                ParsedTaggedText todo = parseTaggedText(parts[1], TODO_USAGE);
                return new AddTodoCommand(checkDescription(todo.getText()), todo.getTags());
            case "deadline":
                return parseDeadlineCommand(parts);
            case "event":
                return parseEventCommand(parts);
            case "delete":
                return new DeleteCommand(parseTaskNumberArgument(parts,
                        "Please provide a task number to delete."));
            case "on":
                return new FindOnDateCommand(parseQueryDate(parts));
            case "find":
                return new FindCommand(parseFindQuery(parts));
            default:
                throw new ThaisBotException(
                        "I'm sorry, but I don't know what that means :( . Please try again!");
        }
    }

    /**
     * Parses a task number text into an integer.
     * @param taskNumberText the string containing the task number
     * @return parsed integer task number
     * @throws ThaisBotException if the text is not a valid integer
     */
    public int parseTaskNumber(String taskNumberText) throws ThaisBotException {
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new ThaisBotException("Task number must be a valid integer.");
        }
    }

    /**
     * Parses a date string in yyyy-MM-dd format.
     * @param input date string
     * @return LocalDate parsed from input
     * @throws ThaisBotException if parsing fails
     */
    public LocalDate parseDate(String input) throws ThaisBotException {
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ThaisBotException("Date must use yyyy-MM-dd format.");
        }
    }

    /**
     * Attempts to parse a date/time string using several supported formats. Returns a
     * ParsedDateTime which contains the parsed LocalDateTime and whether the original
     * input had a time component.
     * @param input input string
     * @param errorMessage message to include in exception on failure
     * @return ParsedDateTime with parsed value
     * @throws ThaisBotException if none of the supported formats apply
     */
    public ParsedDateTime parseDateTime(String input, String errorMessage)
            throws ThaisBotException {
        try {
            LocalDateTime value = LocalDateTime.parse(input, DATE_TIME_FORMATTER);
            return new ParsedDateTime(value, true);
        } catch (DateTimeParseException ignored) {
            // Try next format.
        }

        try {
            LocalDateTime value = LocalDateTime.parse(input, DATE_TIME_COLON_FORMATTER);
            return new ParsedDateTime(value, true);
        } catch (DateTimeParseException ignored) {
            // Try date-only format.
        }

        try {
            LocalDate date = LocalDate.parse(input, DATE_FORMATTER);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new ThaisBotException(errorMessage);
        }
    }

    /**
     * Parses the two parts of a deadline command separated by " /by ".
     */
    public String[] parseDeadlineParts(String input, String usageMessage) throws ThaisBotException {
        String[] parts = input.split(" /by ", 2);
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new ThaisBotException(usageMessage);
        }
        return parts;
    }

    /**
     * Parses an event command into description, from and to parts.
     */
    public String[] parseEventParts(String input, String usageMessage) throws ThaisBotException {
        // Splitting alone accepts "/to" before "/from", which would swap the start and end.
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < fromIndex) {
            throw new ThaisBotException(usageMessage);
        }
        String[] parts = input.split(" /from | /to ", 3);
        if (parts.length != 3
                || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty()
                || parts[2].trim().isEmpty()) {
            throw new ThaisBotException(usageMessage);
        }
        return parts;
    }

    /**
     * Helper to parse an integer task number argument from the command parts.
     */
    private int parseTaskNumberArgument(String[] parts, String missingNumberMessage)
            throws ThaisBotException {
        if (parts.length < 2) {
            throw new ThaisBotException(missingNumberMessage);
        }
        return parseTaskNumber(parts[1]);
    }

    private Command parseDeadlineCommand(String[] parts) throws ThaisBotException {
        if (parts.length < 2) {
            throw new ThaisBotException(
                    "Use: deadline <description> /by <yyyy-MM-dd or yyyy-MM-dd HHmm> [#tag ...]");
        }
        String[] deadlineParts = parseDeadlineParts(parts[1],
                "Use: deadline <description> /by <yyyy-MM-dd or yyyy-MM-dd HHmm> [#tag ...]");
        ParsedDateTimeAndTags by = parseDateTimeAndTags(deadlineParts[1].trim(),
                "Deadline date/time must be yyyy-MM-dd or yyyy-MM-dd HHmm.",
                "Use: deadline <description> /by <yyyy-MM-dd or yyyy-MM-dd HHmm> [#tag ...]");
        return new AddDeadlineCommand(checkDescription(deadlineParts[0].trim()),
                by.getDateTime(), by.getTags());
    }

    private Command parseEventCommand(String[] parts) throws ThaisBotException {
        if (parts.length < 2) {
            throw new ThaisBotException("Use: event <description> /from <start> /to <end> [#tag ...].");
        }
        String[] eventParts = parseEventParts(parts[1],
                "Use: event <description> /from <start> /to <end> [#tag ...].");
        ParsedDateTime from = parseDateTime(eventParts[1].trim(),
                "Event start must be yyyy-MM-dd or yyyy-MM-dd HHmm.");
        ParsedDateTimeAndTags to = parseDateTimeAndTags(eventParts[2].trim(),
                "Event end must be yyyy-MM-dd or yyyy-MM-dd HHmm.",
                "Use: event <description> /from <start> /to <end> [#tag ...].");
        if (to.getDateTime().getValue().isBefore(from.getValue())) {
            throw new ThaisBotException("Event end cannot be before event start.");
        }
        return new AddEventCommand(checkDescription(eventParts[0].trim()), from,
                to.getDateTime(), to.getTags());
    }

    /**
     * Rejects descriptions containing the data file separator. Such descriptions would be
     * saved in a format that cannot be read back the next time the app starts.
     * @param description task description
     * @return the same description, if it is valid
     * @throws ThaisBotException if the description contains the separator
     */
    private String checkDescription(String description) throws ThaisBotException {
        if (description.contains(DATA_SEPARATOR)) {
            throw new ThaisBotException(
                    "Descriptions cannot contain the " + DATA_SEPARATOR + " character.");
        }
        return description;
    }

    private LocalDate parseQueryDate(String[] parts) throws ThaisBotException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new ThaisBotException("Use: on <yyyy-MM-dd>");
        }
        return parseDate(parts[1].trim());
    }

    private String parseFindQuery(String[] parts) throws ThaisBotException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new ThaisBotException("Use: find <keyword>");
        }
        return parts[1].trim();
    }

    private ParsedTaggedText parseTaggedText(String input, String usageMessage)
            throws ThaisBotException {
        String[] tokens = input.trim().split("\\s+");
        int tagStart = findFirstTagIndex(tokens);
        if (tagStart == 0) {
            throw new ThaisBotException(usageMessage);
        }
        String text = joinTokens(tokens, 0, tagStart);
        List<String> tags = parseTags(tokens, tagStart, usageMessage);
        if (text.isEmpty()) {
            throw new ThaisBotException(usageMessage);
        }
        return new ParsedTaggedText(text, tags);
    }

    private ParsedDateTimeAndTags parseDateTimeAndTags(String input, String errorMessage,
                                                       String usageMessage)
            throws ThaisBotException {
        String[] tokens = input.trim().split("\\s+");
        int tagStart = findFirstTagIndex(tokens);
        if (tagStart == 0) {
            throw new ThaisBotException(usageMessage);
        }
        String dateText = joinTokens(tokens, 0, tagStart);
        List<String> tags = parseTags(tokens, tagStart, usageMessage);
        if (dateText.isEmpty()) {
            throw new ThaisBotException(usageMessage);
        }
        return new ParsedDateTimeAndTags(parseDateTime(dateText, errorMessage), tags);
    }

    private int findFirstTagIndex(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("#")) {
                return i;
            }
        }
        return tokens.length;
    }

    private List<String> parseTags(String[] tokens, int startIndex, String usageMessage)
            throws ThaisBotException {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        for (int i = startIndex; i < tokens.length; i++) {
            String token = tokens[i];
            if (!token.startsWith("#") || token.length() == 1) {
                throw new ThaisBotException(TAGS_HELP_MESSAGE + " " + usageMessage);
            }
            String tag = token.substring(1);
            if (!tag.matches("[A-Za-z0-9]+")) {
                throw new ThaisBotException(TAGS_HELP_MESSAGE + " " + usageMessage);
            }
            tags.add(tag);
        }
        return new ArrayList<>(tags);
    }

    private String joinTokens(String[] tokens, int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = startInclusive; i < endExclusive; i++) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(tokens[i]);
        }
        return builder.toString();
    }

    /**
     * Holder for a parsed date/time and whether the original input included a time component.
     */
    public static class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        /**
         * Creates a parsed date/time value.
         * @param value parsed LocalDateTime
         * @param hasTime true if the original input had a time component
         */
        public ParsedDateTime(LocalDateTime value, boolean hasTime) {
            this.value = value;
            this.hasTime = hasTime;
        }

        /**
         * Returns the parsed date/time.
         * @return parsed date/time
         */
        public LocalDateTime getValue() {
            return value;
        }

        /**
         * Returns whether the original input included a time component.
         * @return true if the original input included a time component
         */
        public boolean hasTime() {
            return hasTime;
        }
    }

    /**
     * Holder for parsed text and its tags.
     */
    public static class ParsedTaggedText {
        private final String text;
        private final List<String> tags;

        /**
         * Creates parsed text and tags.
         * @param text text value
         * @param tags tags
         */
        public ParsedTaggedText(String text, List<String> tags) {
            this.text = text;
            this.tags = List.copyOf(tags);
        }

        /**
         * Returns the text portion.
         * @return text
         */
        public String getText() {
            return text;
        }

        /**
         * Returns the parsed tags.
         * @return tags
         */
        public List<String> getTags() {
            return tags;
        }
    }

    /**
     * Holder for a parsed date/time and its tags.
     */
    public static class ParsedDateTimeAndTags {
        private final ParsedDateTime dateTime;
        private final List<String> tags;

        /**
         * Creates parsed date/time and tags.
         * @param dateTime parsed date/time
         * @param tags tags
         */
        public ParsedDateTimeAndTags(ParsedDateTime dateTime, List<String> tags) {
            this.dateTime = dateTime;
            this.tags = List.copyOf(tags);
        }

        /**
         * Returns the parsed date/time.
         * @return parsed date/time
         */
        public ParsedDateTime getDateTime() {
            return dateTime;
        }

        /**
         * Returns the parsed tags.
         * @return tags
         */
        public List<String> getTags() {
            return tags;
        }
    }
}
