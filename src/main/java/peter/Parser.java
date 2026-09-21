package peter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import peter.command.AddCommand;
import peter.command.Command;
import peter.command.DeleteCommand;
import peter.command.ExitCommand;
import peter.command.FindCommand;
import peter.command.FindOnDateCommand;
import peter.command.ListCommand;
import peter.command.MarkCommand;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.ScheduleDateTime;
import peter.task.Task;
import peter.task.Todo;

/**
 * Interprets user commands and converts their arguments into application data.
 */
public final class Parser {
    /** Command words the chatbot recognizes, as the user types them. */
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_ON = "on";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";

    /** Separates a command word from the arguments that follow it. */
    private static final String ARGUMENT_SEPARATOR = " ";

    /**
     * Markers that introduce a scheduled task's dates, each with the spaces
     * that surround it when a value follows.
     */
    private static final String MARKER_BY = " /by ";
    private static final String MARKER_FROM = " /from ";
    private static final String MARKER_TO = " /to ";

    private static final String MESSAGE_UNKNOWN_COMMAND =
            "My spider-sense doesn't recognize that command. Please try again.";

    private Parser() {
    }

    /**
     * Turns a line of user input into the command it asks for.
     *
     * @param command complete command entered by the user.
     * @return command ready to be executed.
     * @throws PeterException if the command is unrecognized or its arguments are invalid.
     */
    public static Command parse(String command) throws PeterException {
        return switch (getCommandWord(command)) {
            case COMMAND_BYE -> new ExitCommand();
            case COMMAND_LIST -> new ListCommand();
            case COMMAND_ON -> new FindOnDateCommand(parseQueryDate(command));
            case COMMAND_FIND -> new FindCommand(parseKeywords(command));
            case COMMAND_TODO, COMMAND_DEADLINE, COMMAND_EVENT -> new AddCommand(parseTask(command));
            case COMMAND_MARK -> new MarkCommand(command, true);
            case COMMAND_UNMARK -> new MarkCommand(command, false);
            case COMMAND_DELETE -> new DeleteCommand(command);
            default -> throw new AssertionError("Unhandled command word");
        };
    }

    /**
     * Identifies the command word while rejecting unsupported command shapes.
     *
     * @param command complete command entered by the user.
     * @return recognized command word.
     * @throws PeterException if the command is not recognized.
     */
    private static String getCommandWord(String command) throws PeterException {
        if (command.equals(COMMAND_BYE) || command.equals(COMMAND_LIST)) {
            return command;
        }

        return matchCommandWord(command, COMMAND_ON, COMMAND_FIND, COMMAND_TODO,
                COMMAND_DEADLINE, COMMAND_EVENT, COMMAND_MARK, COMMAND_UNMARK, COMMAND_DELETE)
                .orElseThrow(() -> new PeterException(MESSAGE_UNKNOWN_COMMAND));
    }

    /**
     * Returns the first of the given command words that the command starts
     * with, if any of them matches.
     *
     * <p>The candidates are var-args so the caller can list them inline,
     * rather than building an array only for this method to read.
     *
     * <p>A stream over those candidates expresses the search directly: keep the
     * ones the command starts with, then take the first. Streams preserve the
     * order of their source, so {@code findFirst} still respects the caller's
     * order of preference. Returning an {@link Optional} rather than
     * {@code null} makes "no match" part of the method's type, so the caller
     * cannot forget to handle it.
     *
     * @param command complete command entered by the user.
     * @param commandWords command words to test, in order of preference.
     * @return matching command word, or an empty optional if there is none.
     */
    private static Optional<String> matchCommandWord(String command, String... commandWords) {
        return Arrays.stream(commandWords)
                .filter(commandWord -> command.equals(commandWord)
                        || command.startsWith(commandWord + ARGUMENT_SEPARATOR))
                .findFirst();
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param command complete task-creation command.
     * @return task described by the command.
     * @throws PeterException if a required field is missing or invalid.
     */
    private static Task parseTask(String command) throws PeterException {
        return switch (getCommandWord(command)) {
            case COMMAND_TODO -> parseTodo(command);
            case COMMAND_DEADLINE -> parseDeadline(command);
            case COMMAND_EVENT -> parseEvent(command);
            default -> throw new PeterException(MESSAGE_UNKNOWN_COMMAND);
        };
    }

    /**
     * Extracts the date requested by an {@code on} command.
     *
     * @param command complete on command.
     * @return requested date.
     * @throws PeterException if the date is missing or invalid.
     */
    private static LocalDate parseQueryDate(String command) throws PeterException {
        // The substring below drops a fixed number of characters, which is
        // only the command word if parse() dispatched here on "on".
        assert command.startsWith("on") : "parseQueryDate is only reached for an on command";

        String dateText = getArguments(command, COMMAND_ON).trim();
        if (dateText.isEmpty()) {
            throw new PeterException("Use 'on <date>' (e.g., on 2019-12-02).");
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new PeterException(
                    "Please enter the date in yyyy-MM-dd format (e.g., 2019-12-02).", e);
        }
    }

    /**
     * Extracts the keywords requested by a {@code find} command.
     *
     * <p>The arguments are split on runs of whitespace, so leading, trailing
     * and repeated spaces are insignificant and each word becomes its own
     * keyword. A blank argument is rejected here, which is what guarantees the
     * returned list is never empty.
     *
     * <p>The keywords are not checked against the storage delimiter, since a
     * search term is never written to the data file.
     *
     * @param command complete find command.
     * @return keywords to search descriptions for, in the order typed.
     * @throws PeterException if no keyword is given.
     */
    private static List<String> parseKeywords(String command) throws PeterException {
        assert command.startsWith("find") : "parseKeywords is only reached for a find command";

        String keywords = getArguments(command, COMMAND_FIND).trim();
        if (keywords.isEmpty()) {
            throw new PeterException("Use 'find <keywords>' (e.g., find read book).");
        }
        // Splitting a trimmed, non-empty string never yields an empty element,
        // so no keyword can be blank.
        return List.of(keywords.split("\\s+"));
    }

    /**
     * Converts a one-based task number in a command into a list index.
     *
     * <p>Called by the mark, unmark, and delete commands while they run, since
     * the checks below need the current task count.
     *
     * @param command complete mark, unmark, or delete command.
     * @param taskCount current number of tasks.
     * @return zero-based task index.
     * @throws PeterException if the task number is missing or invalid.
     */
    public static int parseTaskIndex(String command, int taskCount) throws PeterException {
        // A task count is a size, so a negative one could only come from a
        // broken task list rather than from anything the user typed.
        assert taskCount >= 0 : "task count must not be negative";
        String action = getCommandWord(command);
        // getCommandWord matches on a prefix, so the command must begin with
        // the word whose length is skipped here.
        assert command.startsWith(action) : "command must start with its own command word";

        String taskNumberText = getArguments(command, action).trim();
        if (taskNumberText.isEmpty()) {
            throw new PeterException("Oh dear! Please provide a task number to " + action + ".");
        }
        if (taskCount == 0) {
            throw new PeterException("Oh dear! There are no tasks to " + action + ".");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new PeterException(
                        "Oh dear! Task number must be between 1 and " + taskCount + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new PeterException(
                    "Oh dear! Please enter an integer task number to " + action + ".");
        }
    }

    /**
     * Returns the text following a command word.
     *
     * <p>Only the single space that separates the command word from its
     * arguments is dropped; any further spacing the user typed is left alone,
     * so callers that care about it can decide for themselves whether to trim.
     *
     * @param command complete command entered by the user.
     * @param commandWord command word the command starts with.
     * @return argument text, which is empty when the command has no arguments.
     */
    private static String getArguments(String command, String commandWord) {
        String arguments = command.substring(commandWord.length());
        return arguments.startsWith(ARGUMENT_SEPARATOR) ? arguments.substring(1) : arguments;
    }

    private static Todo parseTodo(String command) throws PeterException {
        assert command.startsWith("todo") : "parseTodo is only reached for a todo command";

        String description = getArguments(command, COMMAND_TODO);
        if (description.isBlank()) {
            throw new PeterException("Please include a description after 'todo'.");
        }
        validateStorageFields(description);
        return new Todo(description);
    }

    /**
     * Returns whether the command ends with a marker that has no value after
     * it, such as a deadline ending in {@code /by}.
     *
     * <p>The trailing space is stripped from the marker, since a marker at the
     * very end of the command is not followed by one.
     *
     * @param command complete command entered by the user.
     * @param marker marker to look for, with its surrounding spaces.
     * @return whether the command ends with the marker and nothing else.
     */
    private static boolean endsWithValuelessMarker(String command, String marker) {
        return command.endsWith(marker.stripTrailing());
    }

    private static Deadline parseDeadline(String command) throws PeterException {
        assert command.startsWith("deadline") : "parseDeadline is only reached for a deadline command";

        int byMarkerIndex = command.indexOf(MARKER_BY);
        if (byMarkerIndex == -1) {
            if (endsWithValuelessMarker(command, MARKER_BY)) {
                throw new PeterException("Please include a due date after '/by'.");
            }
            throw new PeterException("Use 'deadline <description> /by <date>'.");
        }
        if (byMarkerIndex <= COMMAND_DEADLINE.length()) {
            throw new PeterException("Please include a description before '/by'.");
        }

        String description = getArguments(command.substring(0, byMarkerIndex), COMMAND_DEADLINE);
        String dueDate = command.substring(byMarkerIndex + MARKER_BY.length());
        if (description.isBlank()) {
            throw new PeterException("Please include a description before '/by'.");
        }
        if (dueDate.isBlank()) {
            throw new PeterException("Please include a due date after '/by'.");
        }
        validateStorageFields(description, dueDate);
        return new Deadline(description, parseDate(dueDate, "due"));
    }

    private static Event parseEvent(String command) throws PeterException {
        assert command.startsWith("event") : "parseEvent is only reached for an event command";

        int fromMarkerIndex = command.indexOf(MARKER_FROM);
        int toMarkerIndex = command.indexOf(MARKER_TO);
        if (fromMarkerIndex == -1 || toMarkerIndex == -1 || fromMarkerIndex >= toMarkerIndex) {
            if (endsWithValuelessMarker(command, MARKER_FROM)) {
                throw new PeterException("Please include a start date after '/from'.");
            }
            if (endsWithValuelessMarker(command, MARKER_TO)) {
                throw new PeterException("Please include an end date after '/to'.");
            }
            throw new PeterException(
                    "Use 'event <description> /from <start-date> /to <end-date>'.");
        }
        if (fromMarkerIndex <= COMMAND_EVENT.length()) {
            throw new PeterException("Please include a description before '/from'.");
        }
        if (toMarkerIndex <= fromMarkerIndex + MARKER_FROM.length()) {
            throw new PeterException("Please include a start date after '/from'.");
        }

        String description = getArguments(command.substring(0, fromMarkerIndex), COMMAND_EVENT);
        String startDate = command.substring(fromMarkerIndex + MARKER_FROM.length(), toMarkerIndex);
        String endDate = command.substring(toMarkerIndex + MARKER_TO.length());
        if (description.isBlank()) {
            throw new PeterException("Please include a description before '/from'.");
        }
        if (startDate.isBlank()) {
            throw new PeterException("Please include a start date after '/from'.");
        }
        if (endDate.isBlank()) {
            throw new PeterException("Please include an end date after '/to'.");
        }
        validateStorageFields(description, startDate, endDate);

        LocalDateTime start = parseDate(startDate, "start");
        LocalDateTime end = parseDate(endDate, "end");
        // An event whose end precedes its start covers no dates at all, so it
        // would never appear under 'on'. Reject it here rather than storing a
        // task the user can never see again.
        if (end.isBefore(start)) {
            throw new PeterException(
                    "Please make sure the end date is not before the start date.");
        }
        return new Event(description, start, end);
    }

    private static LocalDateTime parseDate(String dateText, String dateName) throws PeterException {
        try {
            return ScheduleDateTime.parseUserInput(dateText);
        } catch (DateTimeParseException e) {
            throw new PeterException("Please enter the " + dateName + " date as yyyy-MM-dd"
                    + " or d/M/yyyy HHmm (e.g., 2019-10-15 or 2/12/2019 1800).", e);
        }
    }

    /**
     * Rejects the delimiter used to separate fields in the data file.
     *
     * <p>{@code anyMatch} states the check as the question being asked, and
     * stops at the first offending field rather than testing the rest.
     */
    private static void validateStorageFields(String... fields) throws PeterException {
        if (Arrays.stream(fields).anyMatch(field -> field.contains(Task.FIELD_DELIMITER))) {
            throw new PeterException(
                    "Oh dear! Task details cannot contain '" + Task.FIELD_DELIMITER + "'.");
        }
    }
}
