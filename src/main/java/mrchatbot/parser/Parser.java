package mrchatbot.parser;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrchatbot.command.AddCommand;
import mrchatbot.command.Command;
import mrchatbot.command.DeleteCommand;
import mrchatbot.command.ExitCommand;
import mrchatbot.command.FindCommand;
import mrchatbot.command.HelpCommand;
import mrchatbot.command.ListCommand;
import mrchatbot.command.MarkCommand;
import mrchatbot.command.UnmarkCommand;
import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Deadline;
import mrchatbot.task.Event;
import mrchatbot.task.Recurrence;
import mrchatbot.task.RecurringTask;
import mrchatbot.task.Task;
import mrchatbot.task.Todo;

/**
 * Makes sense of user commands and converts task commands into task objects.
 */
public class Parser {
    private static final String RECURRING_FORMAT_MESSAGE =
            "Please use: recurring <description> /every day OR /every week [/on <weekday>].";
    private static final Pattern RECURRING_PATTERN = Pattern.compile(
            "^recurring\\s+(.+?)\\s+/every\\s+(day|week)(?:\\s+/on\\s+([a-z]+))?\\s*$",
            Pattern.CASE_INSENSITIVE);

    private static final String INVALID_TASK_FORMAT_MESSAGE = "Sorry, I don't understand that task format.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Sorry, I don't understand that command. Please type \"help\".";
    private static final String TODO_FORMAT_MESSAGE =
            "Todo description cannot be empty. Please use the format: todo <description>";
    private static final String DEADLINE_FORMAT_MESSAGE =
            "Please use the format: deadline <description> /by <yyyy-mm-dd>";
    private static final String DEADLINE_DESCRIPTION_MISSING_MESSAGE =
            "Deadline description cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_BY_MISSING_MESSAGE =
            "Deadline /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE =
            "Deadline description and /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String EVENT_FORMAT_MESSAGE =
            "Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>";
    private static final String FIND_FORMAT_MESSAGE =
            "Find keyword cannot be empty. Please use the format: find <keyword>";
    private static final String DEADLINE_DATE_FORMAT_MESSAGE =
            "Deadline date must be in yyyy-mm-dd format. " + DEADLINE_FORMAT_MESSAGE;
    private static final String EVENT_DATE_FORMAT_MESSAGE =
            "Event dates must be in yyyy-mm-dd format. " + EVENT_FORMAT_MESSAGE;

    private final Clock clock;

    /**
     * Creates a parser using the local calendar date.
     */
    public Parser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser with a controllable clock for recurrence scheduling.
     */
    public Parser(Clock clock) {
        this.clock = clock;
    }

    /**
     * Identifies the command word used by the user.
     */
    public CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Parses exact commands that have already been extracted as Command classes.
     */
    public Command parseCommand(String input) throws MrChatbotException {
        CommandType commandType = parseCommandType(input);
        if (commandType == CommandType.BYE) {
            return new ExitCommand();
        }
        if (commandType == CommandType.HELP) {
            return new HelpCommand();
        }
        if (commandType == CommandType.LIST) {
            return new ListCommand();
        }
        if (commandType == CommandType.MARK) {
            return new MarkCommand(parseTaskNumber(commandArgument(input, CommandType.MARK)));
        }
        if (commandType == CommandType.UNMARK) {
            return new UnmarkCommand(parseTaskNumber(commandArgument(input, CommandType.UNMARK)));
        }
        if (commandType == CommandType.DELETE) {
            return new DeleteCommand(parseTaskNumber(commandArgument(input, CommandType.DELETE)));
        }
        if (commandType == CommandType.FIND) {
            if (input.toLowerCase().equals(CommandType.FIND.word)) {
                throw new MrChatbotException(FIND_FORMAT_MESSAGE);
            }
            return new FindCommand(parseFindKeyword(commandArgument(input, CommandType.FIND)));
        }
        return new AddCommand(createTask(input));
    }

    /**
     * Creates a task from the user's command.
     */
    public Task createTask(String input) throws MrChatbotException {
        CommandType commandType = CommandType.from(input);
        if (commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                || commandType == CommandType.FIND) {
            throw new MrChatbotException(INVALID_TASK_FORMAT_MESSAGE);
        }

        if (commandType == CommandType.RECURRING) {
            return parseRecurringTask(input);
        }
        if (commandType == CommandType.TODO) {
            return parseTodo(input);
        }
        if (commandType == CommandType.DEADLINE) {
            return parseDeadline(input);
        }
        if (commandType == CommandType.EVENT) {
            return parseEvent(input);
        }
        throw new MrChatbotException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Parses daily or weekly recurrence and chooses the first reset strictly after today.
     */
    private RecurringTask parseRecurringTask(String input) throws MrChatbotException {
        Matcher matcher = RECURRING_PATTERN.matcher(input);
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            throw new MrChatbotException(RECURRING_FORMAT_MESSAGE);
        }
        String description = matcher.group(1).trim();
        Recurrence recurrence = Recurrence.valueOf(matcher.group(2).toUpperCase(Locale.ROOT));
        String weekday = matcher.group(3);
        LocalDate today = LocalDate.now(clock);
        if (recurrence == Recurrence.DAY) {
            if (weekday != null) {
                throw new MrChatbotException("Daily tasks cannot use /on. " + RECURRING_FORMAT_MESSAGE);
            }
            return new RecurringTask(description, recurrence, today.plusDays(1));
        }
        DayOfWeek resetDay = weekday == null ? today.getDayOfWeek() : parseWeekday(weekday);
        return new RecurringTask(description, recurrence, today.with(TemporalAdjusters.next(resetDay)));
    }

    /**
     * Reads a full weekday name without depending on the system language.
     */
    private DayOfWeek parseWeekday(String weekday) throws MrChatbotException {
        try {
            return DayOfWeek.valueOf(weekday.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new MrChatbotException("Use a weekday from monday to sunday. " + RECURRING_FORMAT_MESSAGE);
        }
    }

    /**
     * Parses a todo command and checks that its description is present.
     */
    private Todo parseTodo(String input) throws MrChatbotException {
        if (input.equalsIgnoreCase(CommandType.TODO.word)) {
            throw new MrChatbotException(TODO_FORMAT_MESSAGE);
        }
        String description = input.substring(CommandType.TODO.withTrailingSpace().length());
        if (description.isBlank()) {
            throw new MrChatbotException(TODO_FORMAT_MESSAGE);
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command, preserving specific errors for missing fields.
     */
    private Deadline parseDeadline(String input) throws MrChatbotException {
        if (input.equalsIgnoreCase(CommandType.DEADLINE.word)) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
        }
        String lowerCaseInput = input.toLowerCase();
        int byIndex = lowerCaseInput.indexOf(" /by ");
        if (byIndex == -1) {
            String description = input.substring(CommandType.DEADLINE.withTrailingSpace().length());
            if (description.isBlank()) {
                throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
            }
            throw new MrChatbotException(DEADLINE_BY_MISSING_MESSAGE);
        }
        if (byIndex <= CommandType.DEADLINE.withTrailingSpace().length()) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_MISSING_MESSAGE);
        }
        String description = input.substring(CommandType.DEADLINE.withTrailingSpace().length(), byIndex);
        String by = input.substring(byIndex + " /by ".length());
        if (description.isBlank() && by.isBlank()) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
        }
        if (description.isBlank()) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_MISSING_MESSAGE);
        }
        if (by.isBlank()) {
            throw new MrChatbotException(DEADLINE_BY_MISSING_MESSAGE);
        }
        return new Deadline(description, parseDate(by, DEADLINE_DATE_FORMAT_MESSAGE));
    }

    /**
     * Parses an event command with its date delimiters in either order.
     */
    private Event parseEvent(String input) throws MrChatbotException {
        if (input.equalsIgnoreCase(CommandType.EVENT.word)) {
            throw new MrChatbotException(eventMissingMessage(true, true, true));
        }
        String lowerCaseInput = input.toLowerCase();
        int fromIndex = lowerCaseInput.indexOf(" /from ");
        int toIndex = lowerCaseInput.indexOf(" /to ");
        String description = eventDescription(input, fromIndex, toIndex);
        boolean isDescriptionMissing = description.isBlank();
        boolean hasFrom = fromIndex != -1;
        boolean hasTo = toIndex != -1;
        boolean isFromMissing = !hasFrom;
        boolean isToMissing = !hasTo;
        String from = "";
        String to = "";

        if (hasFrom) {
            from = eventValue(input, fromIndex, " /from ".length(), toIndex);
            isFromMissing = from.isBlank();
        }
        if (hasTo) {
            to = eventValue(input, toIndex, " /to ".length(), fromIndex);
            isToMissing = to.isBlank();
        }

        if (isDescriptionMissing || isFromMissing || isToMissing) {
            throw new MrChatbotException(eventMissingMessage(isDescriptionMissing, isFromMissing, isToMissing));
        }
        LocalDate startDate = parseDate(from, EVENT_DATE_FORMAT_MESSAGE);
        LocalDate endDate = parseDate(to, EVENT_DATE_FORMAT_MESSAGE);
        if (!endDate.isAfter(startDate)) {
            throw new MrChatbotException("Event end date must be after its start date. " + EVENT_FORMAT_MESSAGE);
        }
        return new Event(description, startDate, endDate);
    }

    /**
     * Reads a task number from a command argument.
     */
    public int parseTaskNumber(String taskNumberText) throws MrChatbotException {
        if (taskNumberText.isBlank()) {
            throw new MrChatbotException("This task doesn't exist...");
        }
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new MrChatbotException("This task doesn't exist...");
        }
    }

    /**
     * Extracts the text after the command word.
     */
    public String commandArgument(String input, CommandType commandType) throws MrChatbotException {
        if (input.toLowerCase().equals(commandType.word)) {
            throw new MrChatbotException(INVALID_TASK_FORMAT_MESSAGE);
        }
        return input.substring(commandType.withTrailingSpace().length());
    }

    /**
     * Parses and validates the keyword used for finding tasks.
     */
    private String parseFindKeyword(String keyword) throws MrChatbotException {
        if (keyword.isBlank()) {
            throw new MrChatbotException(FIND_FORMAT_MESSAGE);
        }
        return keyword.trim();
    }

    /**
     * Parses a date in the command format accepted by the chatbot.
     */
    private LocalDate parseDate(String dateText, String errorMessage) throws MrChatbotException {
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException e) {
            throw new MrChatbotException(errorMessage);
        }
    }

    /**
     * Extracts the event description based on the first available event delimiter.
     */
    private String eventDescription(String input, int fromIndex, int toIndex) {
        // CASE 1: if fromIndex comes first, check if there's description between COMMAND and fromIndex
        if (fromIndex != -1 && (toIndex == -1 || fromIndex < toIndex)) {
            if (fromIndex <= CommandType.EVENT.withTrailingSpace().length()) {
                return "";
            }
            return input.substring(CommandType.EVENT.withTrailingSpace().length(), fromIndex);
        }

        // CASE 2: if toIndex comes first, check if there's description between COMMAND and toIndex
        if (toIndex != -1) {
            if (toIndex <= CommandType.EVENT.withTrailingSpace().length()) {
                return "";
            }
            return input.substring(CommandType.EVENT.withTrailingSpace().length(), toIndex);
        }

        return input.substring(CommandType.EVENT.withTrailingSpace().length());
    }

    /**
     * Extracts an event value until the other event delimiter, if the other delimiter comes later.
     * Suppose we have a delimiter, a value, then the other delimiter.
     * This function extracts the value.
     *
     * The valueIndex parameter is the starting index of the delimiter.
     */
    private String eventValue(String input, int valueIndex, int delimiterLength, int otherIndex) {
        // Callers only extract values after finding a complete delimiter in the input.
        assert valueIndex >= 0 && delimiterLength > 0 && valueIndex <= input.length() - delimiterLength
                : "Event value extraction requires a complete delimiter";
        int valueStartIndex = valueIndex + delimiterLength;
        int valueEndIndex = otherIndex != -1 && otherIndex > valueIndex ? otherIndex : input.length();
        // handles the edge case where, after adding the delimiter, valueStartIndex exceeds valueEndIndex.
        if (valueStartIndex > valueEndIndex) {
            return "";
        }
        return input.substring(valueStartIndex, valueEndIndex);
    }

    /**
     * Creates a message that says which event fields are missing.
     */
    private String eventMissingMessage(boolean isDescriptionMissing, boolean isFromMissing, boolean isToMissing) {
        // This helper is only used after detecting at least one missing event field.
        assert isDescriptionMissing || isFromMissing || isToMissing
                : "A missing-field message requires at least one missing field";
        String missingParts = "";
        if (isDescriptionMissing) {
            missingParts = "description";
        }
        if (isFromMissing) {
            missingParts = appendMissingPart(missingParts, "/from");
        }
        if (isToMissing) {
            missingParts = appendMissingPart(missingParts, "/to");
        }
        return "Event " + missingParts + " cannot be empty. " + EVENT_FORMAT_MESSAGE;
    }

    /**
     * Adds a missing part to a human-readable list.
     */
    private String appendMissingPart(String missingParts, String newPart) {
        if (missingParts.isEmpty()) {
            return newPart;
        }
        if (missingParts.contains(" and ")) {
            return missingParts.replace(" and ", ", ") + ", and " + newPart;
        }
        return missingParts + " and " + newPart;
    }
}
