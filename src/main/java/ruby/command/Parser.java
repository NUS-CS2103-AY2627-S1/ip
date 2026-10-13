package ruby.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ruby.RubyException;
import ruby.task.Deadline;
import ruby.task.Event;

/**
 * Parses raw user input into executable commands.
 */
public class Parser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm",
            Locale.ENGLISH);
    private static final String DEADLINE_DELIMITER = "/by";
    private static final String EVENT_START_DELIMITER = "/from";
    private static final String EVENT_END_DELIMITER = "/to";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String LIST_COMMAND = "list";
    private static final String EXIT_COMMAND = "bye";
    private static final String CONTACT_COMMAND = "contact";
    private static final String CONTACT_ADD = "add";
    private static final String CONTACT_LIST = "list";
    private static final String CONTACT_DELETE = "delete";
    private static final String PHONE_TAG = "/phone";
    private static final String EMAIL_TAG = "/email";
    private static final String ADDRESS_TAG = "/address";
    private static final String FIELD_SEPARATOR = "|";
    private static final String PHONE_PATTERN = "\\+?\\d{3,}";
    private static final String EMAIL_PATTERN = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
    private static final List<String> CONTACT_TAGS = List.of(PHONE_TAG, EMAIL_TAG, ADDRESS_TAG);
    private static final String CONTACT_USAGE = "contact add NAME [/phone PHONE] [/email EMAIL]"
            + " [/address ADDRESS], contact list, or contact delete INDEX.";

    private Parser() {
    }

    /**
     * Parses one complete user command.
     *
     * @param userInput Raw text entered by the user.
     * @return The command represented by the input.
     * @throws RubyException If the command is unknown or malformed.
     */
    public static Command parse(String userInput) throws RubyException {
        if (userInput == null) {
            throw new RubyException("I can't read a missing command.");
        }
        String command = userInput;
        if (command.isEmpty()) {
            throw new RubyException("I can't work with silence. Type a command.");
        }
        if (!command.equals(command.strip()) || command.contains("  ") || containsNonSpaceWhitespace(command)) {
            throw new RubyException("Use one space between command parts, with no leading or trailing spaces.");
        }

        if (EXIT_COMMAND.equals(command)) {
            return new ExitCommand();
        }
        if (LIST_COMMAND.equals(command)) {
            return new ListCommand();
        }
        if (isCommand(command, MARK_COMMAND)) {
            return new MarkCommand(parseTaskIndex(command, MARK_COMMAND));
        }
        if (isCommand(command, UNMARK_COMMAND)) {
            return new UnmarkCommand(parseTaskIndex(command, UNMARK_COMMAND));
        }
        if (isCommand(command, DELETE_COMMAND)) {
            return new DeleteCommand(parseTaskIndex(command, DELETE_COMMAND));
        }
        if (isCommand(command, TODO_COMMAND)) {
            return new TodoCommand(parseTodo(command));
        }
        if (isCommand(command, DEADLINE_COMMAND)) {
            return new DeadlineCommand(parseDeadline(command));
        }
        if (isCommand(command, EVENT_COMMAND)) {
            return new EventCommand(parseEvent(command));
        }
        if (isCommand(command, FIND_COMMAND)) {
            return new FindCommand(parseFindKeyword(command));
        }
        if (isCommand(command, CONTACT_COMMAND)) {
            return parseContact(command);
        }
        throw new RubyException("That's not in my repertoire. Try a command I know.");
    }

    /**
     * Parses a todo command into its description.
     *
     * @param input Raw todo command.
     * @return The todo description.
     * @throws RubyException If the description is missing.
     */
    private static String parseTodo(String input) throws RubyException {
        String description = input.substring(TODO_COMMAND.length()).strip();
        if (description.isEmpty()) {
            throw new RubyException("A todo with no description? Give me something to work with after todo.");
        }
        rejectFieldSeparator(description);
        return description;
    }

    /**
     * Parses a one-based task number and converts it to a zero-based index.
     *
     * @param input       Raw command entered by the user.
     * @param commandWord Command word that precedes the task number.
     * @return Zero-based index of the task.
     * @throws RubyException If the task number is missing, invalid, or not
     *                       positive.
     */
    public static int parseTaskIndex(String input, String commandWord) throws RubyException {
        return parseIndex(input.substring(commandWord.length()).strip(), commandWord, "task");
    }

    /**
     * Parses a one-based number for the given noun and converts it to a
     * zero-based index.
     *
     * @param numberText  Text containing only the number.
     * @param commandWord Command that precedes the number, used in error
     *                    messages.
     * @param noun        Noun for the numbered items, e.g. "task" or "contact".
     * @return Zero-based index of the item.
     * @throws RubyException If the number is missing, invalid, or not positive.
     */
    private static int parseIndex(String numberText, String commandWord, String noun) throws RubyException {
        String number = numberText.strip();
        if (number.isEmpty()) {
            throw new RubyException("You forgot the number. Give me a " + noun + " number after " + commandWord + ".");
        }
        try {
            int oneBasedIndex = Integer.parseInt(number);
            if (oneBasedIndex <= 0) {
                throw new RubyException("Since when is " + oneBasedIndex + " a valid " + noun
                        + " number? Give me a positive whole number after " + commandWord + ".");
            }
            return oneBasedIndex - 1;
        } catch (NumberFormatException exception) {
            throw new RubyException("That's not a number. Give me a whole number after " + commandWord + ".");
        }
    }

    /**
     * Parses a deadline command into its description and deadline text.
     *
     * @param input Raw deadline command.
     * @return The parsed deadline.
     * @throws RubyException If the command is missing its description or deadline.
     */
    public static Deadline parseDeadline(String input) throws RubyException {
        String details = input.substring(DEADLINE_COMMAND.length()).strip();
        int byIndex = findDelimiter(details, DEADLINE_DELIMITER);
        if (byIndex < 0) {
            throw new RubyException("That's not how deadlines work. Use: deadline DESCRIPTION " + DEADLINE_DELIMITER
                    + " DATE_OR_TIME.");
        }

        String description = details.substring(0, byIndex).strip();
        String deadline = details.substring(byIndex + DEADLINE_DELIMITER.length()).strip();
        if (description.isEmpty()) {
            throw new RubyException("A deadline with no description? Give me something to call it.");
        }
        if (deadline.isEmpty()) {
            throw new RubyException("A deadline needs a date or time after " + DEADLINE_DELIMITER
                    + " — I can't read minds.");
        }
        rejectFieldSeparator(description);
        return new Deadline(description, parseDateTime(deadline));
    }

    /**
     * Parses an event command into its description, start, and end text.
     *
     * @param input Raw event command.
     * @return The parsed event.
     * @throws RubyException If the command is missing its description, start, or
     *                       end.
     */
    public static Event parseEvent(String input) throws RubyException {
        String details = input.substring(EVENT_COMMAND.length()).strip();
        int fromIndex = findDelimiter(details, EVENT_START_DELIMITER);
        if (fromIndex < 0) {
            throw new RubyException(
                    "That's not how events work. Use: event DESCRIPTION "
                            + EVENT_START_DELIMITER
                            + " START "
                            + EVENT_END_DELIMITER
                            + " END.");
        }

        String description = details.substring(0, fromIndex).strip();
        String dates = details.substring(fromIndex + EVENT_START_DELIMITER.length()).strip();
        int toIndex = findDelimiter(dates, EVENT_END_DELIMITER);
        if (description.isEmpty()) {
            throw new RubyException("An event with no description? Give me something to call it.");
        }
        if (toIndex < 0) {
            throw new RubyException("An event needs an end after " + EVENT_END_DELIMITER
                    + " — don't leave me guessing.");
        }

        String startDate = dates.substring(0, toIndex).strip();
        String endDate = dates.substring(toIndex + EVENT_END_DELIMITER.length()).strip();
        if (startDate.isEmpty()) {
            throw new RubyException("An event needs a start after " + EVENT_START_DELIMITER
                    + " — don't leave me guessing.");
        }
        if (endDate.isEmpty()) {
            throw new RubyException("An event needs an end after " + EVENT_END_DELIMITER
                    + " — don't leave me guessing.");
        }
        rejectFieldSeparator(description);
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);
        if (!start.isBefore(end)) {
            throw new RubyException("An event must end after it starts.");
        }
        return new Event(description, start, end);
    }

    /**
     * Parses a find command into its search keyword.
     *
     * @param input Raw find command.
     * @return The keyword to search for.
     * @throws RubyException If the keyword is missing.
     */
    private static String parseFindKeyword(String input) throws RubyException {
        String keyword = input.substring(FIND_COMMAND.length()).strip();
        if (keyword.isEmpty()) {
            throw new RubyException("Find what? Give me a keyword after find.");
        }
        return keyword;
    }

    /**
     * Parses a contact command into one of its subcommands.
     *
     * @param input Raw contact command.
     * @return The command represented by the contact subcommand.
     * @throws RubyException If the subcommand is missing, unknown, or malformed.
     */
    private static Command parseContact(String input) throws RubyException {
        String details = input.substring(CONTACT_COMMAND.length()).strip();
        if (details.isEmpty()) {
            throw new RubyException("Here's how contacts work: " + CONTACT_USAGE);
        }

        int spaceIndex = indexOfWhitespace(details);
        String subcommand = spaceIndex < 0 ? details : details.substring(0, spaceIndex);
        String arguments = spaceIndex < 0 ? "" : details.substring(spaceIndex).strip();

        switch (subcommand) {
            case CONTACT_ADD:
                return parseContactAdd(arguments);
            case CONTACT_LIST:
                if (!arguments.isEmpty()) {
                    throw new RubyException("Contact list doesn't take arguments. Just type contact list.");
                }
                return new ListContactsCommand();
            case CONTACT_DELETE:
                return new DeleteContactCommand(parseIndex(arguments, "contact delete", "contact"));
            default:
                throw new RubyException("That's not a contact command. Use contact add, contact list,"
                        + " or contact delete.");
        }
    }

    /**
     * Parses the name and optional fields of a contact add command.
     *
     * @param details Text after the add subcommand.
     * @return The command that adds the parsed contact.
     * @throws RubyException If the name, tags, or field values are invalid.
     */
    private static AddContactCommand parseContactAdd(String details) throws RubyException {
        StringBuilder name = new StringBuilder();
        Map<String, StringBuilder> fields = new LinkedHashMap<>();
        StringBuilder currentField = name;

        for (String token : details.split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            if (token.startsWith("/")) {
                if (!CONTACT_TAGS.contains(token)) {
                    throw new RubyException("I don't know the field " + token + ". Try " + PHONE_TAG + ", " + EMAIL_TAG
                            + ", or " + ADDRESS_TAG + ".");
                }
                if (fields.containsKey(token)) {
                    throw new RubyException("Once is enough — use " + token + " only once.");
                }
                currentField = new StringBuilder();
                fields.put(token, currentField);
            } else {
                appendToken(currentField, token);
            }
        }

        String contactName = name.toString();
        if (contactName.isEmpty()) {
            throw new RubyException("Contacts need a name. I can't remember someone with no name.");
        }

        String phone = requireFieldValue(fields, PHONE_TAG);
        String email = requireFieldValue(fields, EMAIL_TAG);
        String address = requireFieldValue(fields, ADDRESS_TAG);

        rejectFieldSeparator(contactName);
        rejectFieldSeparator(phone);
        rejectFieldSeparator(email);
        rejectFieldSeparator(address);

        if (!phone.isEmpty() && !phone.matches(PHONE_PATTERN)) {
            throw new RubyException("What's a phone number without digits? Use a plus sign and at least 3 digits,"
                    + " e.g. +123456789.");
        }
        if (!email.isEmpty() && !email.matches(EMAIL_PATTERN)) {
            throw new RubyException("That email won't reach anyone. Use name@example.com.");
        }

        return new AddContactCommand(contactName, phone, email, address);
    }

    /**
     * Appends a token to a field, separating tokens with a single space.
     *
     * @param field Field text being built.
     * @param token Token to append.
     */
    private static void appendToken(StringBuilder field, String token) {
        if (field.length() > 0) {
            field.append(' ');
        }
        field.append(token);
    }

    /**
     * Returns the value of a tagged field, or an empty string when the tag is
     * absent.
     *
     * @param fields Parsed fields keyed by tag.
     * @param tag    Tag whose value to return.
     * @return The field value, or an empty string when the tag is absent.
     * @throws RubyException If the tag is present but has no value.
     */
    private static String requireFieldValue(Map<String, StringBuilder> fields, String tag) throws RubyException {
        if (!fields.containsKey(tag)) {
            return "";
        }
        String value = fields.get(tag).toString();
        if (value.isEmpty()) {
            throw new RubyException("The field " + tag + " must have a value after it.");
        }
        return value;
    }

    /**
     * Rejects a field containing the data-file field separator.
     *
     * @param field Field value to check.
     * @throws RubyException If the value contains the separator.
     */
    private static void rejectFieldSeparator(String field) throws RubyException {
        if (field.contains(FIELD_SEPARATOR)) {
            throw new RubyException("The character " + FIELD_SEPARATOR + " is not allowed in contact details.");
        }
    }

    /**
     * Returns the index of the first whitespace character in the text.
     *
     * @param text Text to search.
     * @return Index of the first whitespace, or -1 when there is none.
     */
    private static int indexOfWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a delimiter only when it appears as a complete whitespace-separated
     * token.
     *
     * @param text      Text to search in.
     * @param delimiter Delimiter to search for.
     * @return Index of the delimiter, or -1 when it is not found as a complete
     *         token.
     */
    private static int findDelimiter(String text, String delimiter) {
        int searchFrom = 0;
        while (searchFrom < text.length()) {
            int index = text.indexOf(delimiter, searchFrom);
            if (index < 0) {
                return -1;
            }

            int afterDelimiter = index + delimiter.length();
            boolean hasLeftBoundary = index == 0 || Character.isWhitespace(text.charAt(index - 1));
            boolean hasRightBoundary = afterDelimiter == text.length()
                    || Character.isWhitespace(text.charAt(afterDelimiter));
            if (hasLeftBoundary && hasRightBoundary) {
                return index;
            }
            searchFrom = index + delimiter.length();
        }
        return -1;
    }

    /**
     * Returns whether the input is a command word, optionally followed by
     * arguments.
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord)
                || (input.startsWith(commandWord)
                        && Character.isWhitespace(input.charAt(commandWord.length())));
    }

    /**
     * Returns whether the command contains whitespace other than an ordinary
     * single space, which is not part of Ruby's command syntax.
     *
     * @param command Command text to inspect.
     * @return Whether the command contains a tab, newline, or similar whitespace.
     */
    private static boolean containsNonSpaceWhitespace(String command) {
        return command.chars().anyMatch(character -> Character.isWhitespace(character) && character != ' ');
    }

    /**
     * Parses a user-supplied date or date and time into a LocalDateTime.
     *
     * @param input Date text, e.g. "2019-10-15" or "2019-10-15 1800".
     * @return The parsed date and time; a date without a time becomes midnight.
     * @throws RubyException If the text is not in a recognised format.
     */
    public static LocalDateTime parseDateTime(String input) throws RubyException {
        String text = input.strip();
        if (text.isEmpty()) {
            throw new RubyException("A deadline needs a date or time after " + DEADLINE_DELIMITER
                    + " — I can't read minds.");
        }

        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException exception) {
            // Not a date with a time; try a date alone below.
        }

        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE
                    .withResolverStyle(ResolverStyle.STRICT)).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new RubyException(
                    "That date is a mystery even to me. Use yyyy-mm-dd (e.g. 2019-10-15)"
                            + " or yyyy-mm-dd HHmm (e.g. 2026-10-15 1800).");
        }
    }

    /**
     * Formats a date for display as "Oct 15 2019", appending the time only
     * when it was given (a midnight time means the user supplied no time).
     *
     * @param dateTime Date and time to format.
     * @return The formatted date, with a time when one is present.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        assert dateTime != null : "caller always passes a parsed date";
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DATE_FORMAT);
        }
        return dateTime.format(DATE_TIME_FORMAT);
    }
}
