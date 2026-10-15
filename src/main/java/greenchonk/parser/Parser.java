package greenchonk.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greenchonk.command.AddCommand;
import greenchonk.command.Command;
import greenchonk.command.DeleteCommand;
import greenchonk.command.EditCommand;
import greenchonk.command.ExitCommand;
import greenchonk.command.FindCommand;
import greenchonk.command.HelpCommand;
import greenchonk.command.ListCommand;
import greenchonk.command.ScheduleCommand;
import greenchonk.command.UpdateStatusCommand;
import greenchonk.exception.GreenChonkException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.TaskDate;
import greenchonk.task.TaskStatus;
import greenchonk.task.Todo;

/**
 * Interprets user commands and validates their arguments.
 */
public final class Parser {
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DELETE_COMMAND = "delete";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EDIT_COMMAND = "edit";
    private static final String EDIT_DESCRIPTION_FIELD = "/description";
    private static final String EDIT_DUE_DATE_FIELD = "/by";
    private static final String EDIT_END_DATE_FIELD = "/to";
    private static final String EDIT_START_DATE_FIELD = "/from";
    private static final String EVENT_COMMAND = "event";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String SCHEDULE_COMMAND = "schedule";
    private static final String TODO_COMMAND = "todo";
    private static final String UNMARK_COMMAND = "unmark";

    /**
     * Prevents instantiation of this command-parsing utility class.
     */
    private Parser() {
    }

    /**
     * Creates the command represented by the user's input.
     *
     * @param input the trimmed user input.
     * @return the parsed command.
     * @throws GreenChonkException if the command or its arguments are invalid.
     */
    public static Command parse(String input) throws GreenChonkException {
        assert input != null : "Command input must not be null";
        assert input.equals(input.strip()) : "Command input must be stripped before parsing";

        if (input.isEmpty()) {
            throw new GreenChonkException("Please enter a command. Try: todo buy milk");
        }
        if (input.equalsIgnoreCase("bye")) {
            return new ExitCommand();
        }
        if (input.equalsIgnoreCase("list")) {
            return new ListCommand();
        }
        if (input.equalsIgnoreCase("help")) {
            return new HelpCommand();
        }
        if (isCommand(input, FIND_COMMAND)) {
            return new FindCommand(parseFindKeyword(input));
        }
        if (isCommand(input, SCHEDULE_COMMAND)) {
            return new ScheduleCommand(parseScheduleDate(input));
        }
        if (isCommand(input, EDIT_COMMAND)) {
            return parseEdit(input);
        }
        if (isCommand(input, MARK_COMMAND)) {
            return new UpdateStatusCommand(parseTaskNumber(input, MARK_COMMAND),
                    TaskStatus.DONE, MARK_COMMAND);
        }
        if (isCommand(input, UNMARK_COMMAND)) {
            return new UpdateStatusCommand(parseTaskNumber(input, UNMARK_COMMAND),
                    TaskStatus.NOT_DONE, UNMARK_COMMAND);
        }
        if (isCommand(input, DELETE_COMMAND)) {
            return new DeleteCommand(parseTaskNumber(input, DELETE_COMMAND));
        }
        if (isCommand(input, TODO_COMMAND)) {
            return new AddCommand(parseTodo(input));
        }
        if (isCommand(input, DEADLINE_COMMAND)) {
            return new AddCommand(parseDeadline(input));
        }
        if (isCommand(input, EVENT_COMMAND)) {
            return new AddCommand(parseEvent(input));
        }

        throw new GreenChonkException("I don't recognize \"" + input
                + "\". Try todo, deadline, event, list, find, schedule, edit, mark, unmark, delete, help, or bye.");
    }

    /**
     * Creates an edit command after validating its task number, field, and value.
     *
     * @param command the complete edit command.
     * @return the parsed edit command.
     * @throws GreenChonkException if the task number, field, or replacement value is invalid.
     */
    private static EditCommand parseEdit(String command) throws GreenChonkException {
        String arguments = getArguments(command);
        if (arguments.isEmpty()) {
            throw new GreenChonkException("An edit command needs a task number and field. "
                    + "Try: edit 1 /description buy milk");
        }

        int fieldPosition = arguments.indexOf(' ');
        String numberText = fieldPosition < 0
                ? arguments
                : arguments.substring(0, fieldPosition);
        int taskNumber = parseTaskNumberText(numberText);
        if (fieldPosition < 0) {
            throw new GreenChonkException("Please choose a field to edit. "
                    + "Use /description, /by, /from, or /to.");
        }

        String fieldAndValue = arguments.substring(fieldPosition + 1).trim();
        int valuePosition = fieldAndValue.indexOf(' ');
        String field = valuePosition < 0
                ? fieldAndValue
                : fieldAndValue.substring(0, valuePosition);
        String value = valuePosition < 0
                ? ""
                : fieldAndValue.substring(valuePosition + 1).trim();

        switch (field) {
            case EDIT_DESCRIPTION_FIELD:
                if (value.isEmpty()) {
                    throw new GreenChonkException("Please provide a new description. "
                            + "Try: edit 1 /description buy milk");
                }
                return EditCommand.editDescription(taskNumber, value);
            case EDIT_DUE_DATE_FIELD:
                return EditCommand.editDueDate(taskNumber,
                        parseEditDate(value, "deadline", "2026-08-28"));
            case EDIT_START_DATE_FIELD:
                return EditCommand.editStartDate(taskNumber,
                        parseEditDate(value, "event start", "2026-08-28"));
            case EDIT_END_DATE_FIELD:
                return EditCommand.editEndDate(taskNumber,
                        parseEditDate(value, "event end", "2026-08-29"));
            default:
                throw new GreenChonkException("I don't recognize the edit field \"" + field
                        + "\". Use /description, /by, /from, or /to.");
        }
    }

    /**
     * Parses a replacement date after checking that the edit value is present.
     *
     * @param dateText the replacement date text.
     * @param dateLabel the field name used in validation feedback.
     * @param exampleDate a valid example used in validation feedback.
     * @return the parsed replacement date.
     * @throws GreenChonkException if the date is missing or invalid.
     */
    private static LocalDate parseEditDate(String dateText, String dateLabel,
            String exampleDate) throws GreenChonkException {
        if (dateText.isEmpty()) {
            throw new GreenChonkException("Please provide a new " + dateLabel
                    + " date. Try: " + exampleDate);
        }
        return parseDate(dateText, "new " + dateLabel, exampleDate);
    }

    /**
     * Returns the keyword supplied to a find command.
     *
     * @param command the complete find command.
     * @return the keyword to search for.
     * @throws GreenChonkException if the keyword is missing.
     */
    private static String parseFindKeyword(String command) throws GreenChonkException {
        String keyword = getArguments(command);
        if (keyword.isEmpty()) {
            throw new GreenChonkException("A find command needs a keyword. Try: find book");
        }
        return keyword;
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command the complete todo command.
     * @return the parsed todo.
     * @throws GreenChonkException if the description is empty.
     */
    private static Todo parseTodo(String command) throws GreenChonkException {
        String description = getArguments(command);
        if (description.isEmpty()) {
            throw new GreenChonkException("A todo needs a description. Try: todo buy milk");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and due date.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline.
     * @throws GreenChonkException if a required deadline field is missing or invalid.
     */
    private static Deadline parseDeadline(String command) throws GreenChonkException {
        String details = getArguments(command);
        List<Integer> separatorPositions = findParameterPositions(details, DEADLINE_SEPARATOR);
        if (separatorPositions.isEmpty()) {
            throw new GreenChonkException("A deadline needs /by followed by a date. "
                    + "Try: deadline submit report /by 2026-08-28");
        }
        if (separatorPositions.size() > 1) {
            throw new GreenChonkException("A deadline accepts /by only once.");
        }

        int separatorPosition = separatorPositions.get(0);
        String description = details.substring(0, separatorPosition).trim();
        String dueDateText = details.substring(separatorPosition + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new GreenChonkException("A deadline needs a description before /by.");
        }
        if (dueDateText.isEmpty()) {
            throw new GreenChonkException("A deadline needs a date after /by.");
        }
        LocalDate dueDate = parseDate(dueDateText, "deadline", "2026-08-28");
        return new Deadline(description, dueDate);
    }

    /**
     * Creates an event after validating its description and date range.
     *
     * @param command the complete event command.
     * @return the parsed event.
     * @throws GreenChonkException if a required event field is missing or invalid.
     */
    private static Event parseEvent(String command) throws GreenChonkException {
        String details = getArguments(command);
        List<Integer> fromPositions = findParameterPositions(details, EVENT_FROM_SEPARATOR);
        List<Integer> toPositions = findParameterPositions(details, EVENT_TO_SEPARATOR);
        if (fromPositions.isEmpty()) {
            throw new GreenChonkException("An event needs /from and /to. "
                    + "Try: event meeting /from 2026-08-28 /to 2026-08-29");
        }
        if (fromPositions.size() > 1) {
            throw new GreenChonkException("An event accepts /from only once.");
        }
        if (toPositions.isEmpty()) {
            throw new GreenChonkException("An event needs /to followed by an ending date.");
        }
        if (toPositions.size() > 1) {
            throw new GreenChonkException("An event accepts /to only once.");
        }

        int fromPosition = fromPositions.get(0);
        int toPosition = toPositions.get(0);
        if (toPosition < fromPosition) {
            throw new GreenChonkException("Put /from before /to in an event command.");
        }
        String description = details.substring(0, fromPosition).trim();
        if (description.isEmpty()) {
            throw new GreenChonkException("An event needs a description before /from.");
        }

        String startDateText = details.substring(
                fromPosition + EVENT_FROM_SEPARATOR.length(), toPosition).trim();
        String endDateText = details.substring(toPosition + EVENT_TO_SEPARATOR.length()).trim();
        if (startDateText.isEmpty()) {
            throw new GreenChonkException("An event needs a starting date after /from.");
        }
        if (endDateText.isEmpty()) {
            throw new GreenChonkException("An event needs an ending date after /to.");
        }
        LocalDate startDate = parseDate(startDateText, "event start", "2026-08-28");
        LocalDate endDate = parseDate(endDateText, "event end", "2026-08-29");
        try {
            return new Event(description, startDate, endDate);
        } catch (IllegalArgumentException exception) {
            throw new GreenChonkException("An event's end date cannot be before its start date. "
                    + "Try /to " + startDate + " or later.");
        }
    }

    /**
     * Parses the requested date from a schedule command.
     *
     * @param command the complete schedule command.
     * @return the requested schedule date.
     * @throws GreenChonkException if the schedule date is missing or invalid.
     */
    private static LocalDate parseScheduleDate(String command) throws GreenChonkException {
        String dateText = getArguments(command);
        if (dateText.isEmpty()) {
            throw new GreenChonkException("Please provide a schedule date. Try: schedule 2026-08-28");
        }
        return parseDate(dateText, "schedule", "2026-08-28");
    }

    /**
     * Parses the one-based task number supplied to a mutation command.
     *
     * @param command the complete task mutation command
     * @param commandName the command word used in validation feedback
     * @return the parsed task number
     * @throws GreenChonkException if the task number is missing or not an integer
     */
    private static int parseTaskNumber(String command, String commandName)
            throws GreenChonkException {
        String numberText = getArguments(command);
        if (numberText.isEmpty()) {
            throw new GreenChonkException("Please provide a task number. Try: " + commandName + " 1");
        }

        return parseTaskNumberText(numberText);
    }

    /**
     * Parses a one-based task number from its isolated text representation.
     *
     * @param numberText the text expected to contain a task number.
     * @return the parsed task number.
     * @throws GreenChonkException if the text is not an integer.
     */
    private static int parseTaskNumberText(String numberText) throws GreenChonkException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new GreenChonkException("\"" + numberText
                    + "\" is not a valid task number. Use a whole number such as 1.");
        }

        return taskNumber;
    }

    /**
     * Returns whether the input contains the specified command word.
     * A command matches only when followed by the end of input or a space.
     *
     * @param input the complete user input
     * @param command the command word to match
     * @return true if the input starts with the complete command word
     */
    private static boolean isCommand(String input, String command) {
        int commandEnd = findFirstWhitespace(input);
        String commandWord = commandEnd < 0 ? input : input.substring(0, commandEnd);
        return commandWord.equalsIgnoreCase(command);
    }

    /**
     * Parses a supported calendar date and translates parsing failures for the user.
     *
     * @param dateText the date text to parse
     * @param dateLabel the field name used in validation feedback
     * @param exampleDate a valid example used in validation feedback
     * @return the parsed date
     * @throws GreenChonkException if the date is invalid or uses an unsupported format
     */
    private static LocalDate parseDate(String dateText, String dateLabel, String exampleDate)
            throws GreenChonkException {
        try {
            return TaskDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new GreenChonkException("The " + dateLabel
                    + " date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: "
                    + exampleDate);
        }
    }

    /**
     * Returns the trimmed text following the first command word.
     *
     * @param command the complete user command
     * @return the command arguments, or an empty string when none were supplied
     */
    private static String getArguments(String command) {
        int commandEnd = findFirstWhitespace(command);
        if (commandEnd < 0) {
            return "";
        }
        return command.substring(commandEnd + 1).strip();
    }

    /**
     * Returns the position of the first whitespace character in a command.
     *
     * @param text the text to inspect.
     * @return the first whitespace position, or -1 when none exists.
     */
    private static int findFirstWhitespace(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (Character.isWhitespace(text.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns every position at which a standalone parameter marker begins.
     *
     * @param text the command details to inspect.
     * @param parameter the parameter marker to find.
     * @return the parameter's start positions in encounter order.
     */
    private static List<Integer> findParameterPositions(String text, String parameter) {
        Pattern parameterPattern = Pattern.compile(
                "(?<!\\S)" + Pattern.quote(parameter) + "(?=\\s|$)");
        Matcher matcher = parameterPattern.matcher(text);
        List<Integer> positions = new ArrayList<>();
        while (matcher.find()) {
            positions.add(matcher.start());
        }
        return positions;
    }
}
