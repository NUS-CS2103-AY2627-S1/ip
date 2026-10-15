package stephen.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import stephen.command.AddCommand;
import stephen.command.Command;
import stephen.command.DeleteCommand;
import stephen.command.ExitCommand;
import stephen.command.FindCommand;
import stephen.command.ListCommand;
import stephen.command.MarkCommand;
import stephen.command.ScheduleCommand;
import stephen.command.SortCommand;
import stephen.command.UnmarkCommand;
import stephen.exception.ChatbotException;
import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.task.Todo;

/** Interprets user input and validates command arguments. */
public class Parser {
    /** Creates a parser for Orbit's supported commands. */
    public Parser() {
    }

    /**
     * Interprets one input line and creates the command that should handle it.
     *
     * @param input complete user input
     * @param tasks current task list, used to validate task numbers
     * @return command representing the input
     * @throws ChatbotException if the command or its arguments are invalid
     */
    public Command parse(String input, TaskList tasks) throws ChatbotException {
        String command = getCommand(input).toLowerCase(Locale.ROOT);
        String arguments = getArguments(input);
        if (command.isEmpty()) {
            throw new ChatbotException("Please enter a command. Try: list");
        }

        return createCommand(command, arguments, tasks);
    }

    /** Creates the command corresponding to a validated, non-empty command name. */
    private Command createCommand(String command, String arguments, TaskList tasks)
            throws ChatbotException {
        return switch (command) {
            case "bye" -> requireNoArguments(command, arguments, new ExitCommand());
            case "list" -> requireNoArguments(command, arguments, new ListCommand());
            case "sort" -> requireNoArguments(command, arguments, new SortCommand());
            case "schedule" -> new ScheduleCommand(parseScheduleDate(arguments));
            case "find" -> new FindCommand(parseFindKeyword(arguments));
            case "mark" -> new MarkCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "unmark" -> new UnmarkCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "delete" -> new DeleteCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "todo" -> new AddCommand(parseTodo(arguments));
            case "deadline" -> new AddCommand(parseDeadline(arguments));
            case "event" -> new AddCommand(parseEvent(arguments));
            default -> throw new ChatbotException("I don't recognise that command.");
        };
    }

    /** Returns a command only when the user supplied no unexpected arguments. */
    private Command requireNoArguments(String commandName, String arguments, Command command)
            throws ChatbotException {
        if (!arguments.isEmpty()) {
            throw unexpectedArguments(commandName);
        }
        return command;
    }

    /**
     * Returns the first word of the input as the command name.
     *
     * @param input complete user input
     * @return command name, or an empty string when the input is empty
     */
    public String getCommand(String input) {
        String normalizedInput = input.trim();
        int firstWhitespace = findFirstWhitespace(normalizedInput);
        return firstWhitespace < 0 ? normalizedInput : normalizedInput.substring(0, firstWhitespace);
    }

    /**
     * Returns the text following the command name, with surrounding whitespace removed.
     *
     * @param input complete user input
     * @return trimmed command arguments, or an empty string when none are present
     */
    public String getArguments(String input) {
        String normalizedInput = input.trim();
        int firstWhitespace = findFirstWhitespace(normalizedInput);
        return firstWhitespace < 0 ? "" : normalizedInput.substring(firstWhitespace + 1).trim();
    }

    /**
     * Parses a todo description.
     *
     * @param description todo description
     * @return todo containing the supplied description
     * @throws ChatbotException if the description is empty
     */
    public Todo parseTodo(String description) throws ChatbotException {
        if (description.isEmpty()) {
            throw new ChatbotException("A todo needs a description. Try: todo borrow book");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline while checking its description and {@code /by} value.
     *
     * @param details deadline description and date arguments
     * @return deadline represented by the arguments
     * @throws ChatbotException if required arguments are missing or the date is invalid
     */
    public Deadline parseDeadline(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "A deadline needs a description and '/by'. "
                            + "Try: deadline return book /by 2019-12-02");
        }
        if (details.equals("/by") || details.startsWith("/by ")) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        int byIndex = findMarker(details, "/by", 0);
        if (byIndex < 0) {
            throw new ChatbotException(
                    "A deadline needs '/by'. Try: deadline return book /by 2019-12-02");
        }
        if (findMarker(details, "/by", byIndex + 4) >= 0) {
            throw new ChatbotException("A deadline accepts only one '/by' date.");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new ChatbotException("A deadline needs a date after '/by'. Try: /by 2019-12-02");
        }
        LocalDate deadlineDate = parseDate(by,
                "Invalid deadline date. Please use yyyy-MM-dd, for example 2019-12-02.");
        return new Deadline(description, deadlineDate);
    }

    /**
     * Parses an event while checking its description, start, and end values.
     *
     * @param details event description, start date, and end date arguments
     * @return event represented by the arguments
     * @throws ChatbotException if required arguments are missing or a date is invalid
     */
    public Event parseEvent(String details) throws ChatbotException {
        validateEventOverview(details);
        int fromIndex = findEventFromIndex(details);
        int toIndex = findEventToIndex(details, fromIndex);
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 6, toIndex).trim();
        String to = details.substring(toIndex + 4).trim();
        validateEventValues(description, from, to);

        String invalidDateMessage = "Invalid event date. Please use yyyy-MM-dd for both dates, "
                + "for example /from 2019-12-02 /to 2019-12-03.";
        LocalDate fromDate = parseDate(from, invalidDateMessage);
        LocalDate toDate = parseDate(to, invalidDateMessage);
        if (toDate.isBefore(fromDate)) {
            throw new ChatbotException("An event's end date cannot be before its start date.");
        }
        return new Event(description, fromDate, toDate);
    }

    /** Checks the event argument as a whole before locating its date markers. */
    private void validateEventOverview(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "An event needs a description, '/from', and '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        if (details.equals("/from") || details.startsWith("/from ")) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
    }

    /** Returns the single valid {@code /from} marker position. */
    private int findEventFromIndex(String details) throws ChatbotException {
        int fromIndex = findMarker(details, "/from", 0);
        if (fromIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/from'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        int firstToIndex = findMarker(details, "/to", 0);
        if (firstToIndex >= 0 && firstToIndex < fromIndex) {
            throw new ChatbotException("An event needs '/from' before '/to'.");
        }
        if (findMarker(details, "/from", fromIndex + 6) >= 0) {
            throw new ChatbotException("An event accepts only one '/from' date.");
        }
        return fromIndex;
    }

    /** Returns the single valid {@code /to} marker position after {@code /from}. */
    private int findEventToIndex(String details, int fromIndex) throws ChatbotException {
        int toIndex = findMarker(details, "/to", fromIndex + 6);
        if (toIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        if (findMarker(details, "/to", toIndex + 4) >= 0) {
            throw new ChatbotException("An event accepts only one '/to' date.");
        }
        return toIndex;
    }

    /** Checks the description and date text extracted from the event arguments. */
    private void validateEventValues(String description, String from, String to)
            throws ChatbotException {
        if (description.isEmpty()) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
        if (from.isEmpty()) {
            throw new ChatbotException("An event needs a start date after '/from'.");
        }
        if (to.isEmpty()) {
            throw new ChatbotException("An event needs an end date after '/to'.");
        }
    }

    /**
     * Parses and validates a one-based task number, returning its zero-based index.
     *
     * @param numberText user-supplied task number
     * @param command command name used in validation guidance
     * @param taskCount number of tasks currently available
     * @return zero-based index corresponding to the task number
     * @throws ChatbotException if the number is missing, non-numeric, or out of range
     */
    public int parseTaskIndex(String numberText, String command, int taskCount)
            throws ChatbotException {
        if (numberText.isEmpty()) {
            throw new ChatbotException("Please provide a task number. Try: " + command + " 2");
        }
        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new ChatbotException("The task number must be a number. Try: " + command + " 2");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChatbotException("That task number does not exist.");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task number must map to an existing zero-based index";
        return taskIndex;
    }

    /**
     * Parses the date supplied to the schedule command.
     *
     * @param dateText date in {@code yyyy-MM-dd} format
     * @return parsed schedule date
     * @throws ChatbotException if the date is missing or invalid
     */
    public LocalDate parseScheduleDate(String dateText) throws ChatbotException {
        if (dateText.isEmpty()) {
            throw new ChatbotException(
                    "Please provide a schedule date. Try: schedule 2019-12-02");
        }
        return parseDate(dateText,
                "Invalid schedule date. Please use yyyy-MM-dd, for example 2019-12-02.");
    }

    /**
     * Parses and validates the phrase supplied to the find command.
     *
     * @param keyword search phrase supplied by the user
     * @return validated search phrase
     * @throws ChatbotException if the search phrase is empty
     */
    public String parseFindKeyword(String keyword) throws ChatbotException {
        if (keyword.isEmpty()) {
            throw new ChatbotException("Please provide a search keyword. Try: find book");
        }
        return keyword;
    }

    /**
     * Parses a strict ISO date and converts format failures into user-facing errors.
     *
     * @param dateText date in {@code yyyy-MM-dd} format
     * @param invalidDateMessage message to use when parsing fails
     * @return parsed date
     * @throws ChatbotException if the date is invalid
     */
    private LocalDate parseDate(String dateText, String invalidDateMessage)
            throws ChatbotException {
        try {
            return LocalDate.parse(dateText, Task.INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ChatbotException(invalidDateMessage);
        }
    }

    /** Returns a validation error for a command that should not receive arguments. */
    private ChatbotException unexpectedArguments(String command) {
        return new ChatbotException("The '" + command + "' command does not accept extra details.");
    }

    /** Returns the index of the first whitespace character, or {@code -1} when absent. */
    private int findFirstWhitespace(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isWhitespace(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a standalone command marker that is followed by whitespace or ends the input.
     *
     * @param details arguments to search
     * @param marker marker text, such as {@code /by}
     * @param fromIndex index at which to begin searching
     * @return index of the marker's leading whitespace, or {@code -1} if it is absent
     */
    private int findMarker(String details, String marker, int fromIndex) {
        int markerIndex = details.indexOf(marker, fromIndex);
        while (markerIndex >= 0) {
            int markerEnd = markerIndex + marker.length();
            boolean hasValidStart = markerIndex == 0
                    || Character.isWhitespace(details.charAt(markerIndex - 1));
            boolean hasValidEnd = markerEnd == details.length()
                    || Character.isWhitespace(details.charAt(markerEnd));
            if (hasValidStart && hasValidEnd) {
                return markerIndex == 0 ? 0 : markerIndex - 1;
            }
            markerIndex = details.indexOf(marker, markerIndex + 1);
        }
        return -1;
    }
}
