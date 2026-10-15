package tuesday.processor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Scanner;

import tuesday.exceptions.TuesdayExceptions;
import tuesday.task.Deadline;
import tuesday.task.Event;
import tuesday.task.Task;
import tuesday.task.TaskList;
import tuesday.task.Todo;

/**
 * Interprets and validates user input without changing the task list or accessing files.
 */
public final class UserInputParser {
    private UserInputParser() {
    }

    /**
     * Identifies the command at the start of an input line.
     *
     * @param input the complete line entered by the user.
     * @return the matching command, or UNKNOWN when it is not recognized.
     */
    public static Command getCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Command.UNKNOWN;
        }

        String trimmedInput = input.trim();
        String command = trimmedInput.split("\\s+", 2)[0];

        return switch (command) {
            case "todo" -> Command.TODO;
            case "deadline" -> Command.DEADLINE;
            case "event" -> Command.EVENT;
            case "list" -> Command.LIST;
            case "find" -> Command.FIND;
            case "mark" -> Command.MARK;
            case "unmark" -> Command.UNMARK;
            case "delete" -> Command.DELETE;
            case "new" -> Command.NEW;
            case "old" -> Command.OLD;
            case "bye" -> Command.BYE;
            default -> Command.UNKNOWN;
        };
    }

    /**
     * Reads the keyword using the existing find-command whitespace rules.
     */
    static String parseFindKeyword(String input) {
        return input.substring("find".length()).trim();
    }

    /**
     * Recognizes exact duplicate choices while allowing the usual bye command syntax.
     */
    static Command parseDuplicateChoice(String input) {
        String trimmedInput = input == null ? "" : input.trim();
        if (getCommand(trimmedInput) == Command.BYE) {
            return Command.BYE;
        }
        return switch (trimmedInput) {
            case "old" -> Command.OLD;
            case "new" -> Command.NEW;
            default -> Command.UNKNOWN;
        };
    }

    /**
     * Checks whether the input is a valid task-creation command.
     *
     * @param input the complete line entered by the user.
     * @return true if the input is a valid todo, deadline, or event command.
     * @throws TuesdayExceptions.NoDescriptionException if the description is missing.
     * @throws TuesdayExceptions.DeadlineMissingByDateException if the deadline is missing.
     * @throws TuesdayExceptions.EventMissingTimeException if an event time section is missing.
     */
    public static boolean isAvailableTaskCommand(String input)
            throws TuesdayExceptions.NoDescriptionException,
            TuesdayExceptions.DeadlineMissingByDateException,
            TuesdayExceptions.EventMissingTimeException {
        ValidationError error = parseTaskCreation(input, getCommand(input)).error();
        if (error == null) {
            return true;
        }
        return switch (error.code()) {
            case MISSING_DESCRIPTION -> throw new TuesdayExceptions.NoDescriptionException(error.detail());
            case MISSING_DEADLINE -> throw new TuesdayExceptions.DeadlineMissingByDateException(error.detail());
            case MISSING_EVENT_TIME -> throw new TuesdayExceptions.EventMissingTimeException(error.detail());
            default -> false;
        };
    }

    /**
     * Checks whether the input is a valid mark or unmark command.
     *
     * @param input the complete line entered by the user.
     * @param tasks the tasks currently stored.
     * @return true if the command refers to an existing task.
     * @throws TuesdayExceptions.MarkTaskNumberOutOfRangeException if the task number is outside the list.
     */
    public static boolean isAvailableMark(String input, TaskList tasks)
            throws TuesdayExceptions.MarkTaskNumberOutOfRangeException {
        Objects.requireNonNull(input);
        ValidationError error = validateTaskIndex(input, tasks.size(), Command.MARK).error();
        if (error != null && error.code() == ValidationCode.MARK_OUT_OF_RANGE) {
            throw new TuesdayExceptions.MarkTaskNumberOutOfRangeException(error.detail());
        }
        return error == null;
    }

    /**
     * Checks whether a delete command refers to an existing task.
     *
     * @param input the complete line entered by the user.
     * @param tasks the tasks currently stored.
     * @return true if the command refers to an existing task.
     * @throws TuesdayExceptions.DeleteTaskNumberOutOfRangeException if the task number is outside the list.
     */
    public static boolean isAvailableDelete(String input, TaskList tasks)
            throws TuesdayExceptions.DeleteTaskNumberOutOfRangeException {
        Objects.requireNonNull(input);
        ValidationError error = validateTaskIndex(input, tasks.size(), Command.DELETE).error();
        if (error != null && error.code() == ValidationCode.DELETE_OUT_OF_RANGE) {
            throw new TuesdayExceptions.DeleteTaskNumberOutOfRangeException(error.detail());
        }
        return error == null;
    }

    /**
     * Returns a validated zero-based index or an error before any task is accessed.
     *
     * @param input the complete command to validate.
     * @param taskCount the current list size used to check the task number.
     * @param command DELETE for deletion, or MARK/UNMARK for a status change.
     * @return a valid index or a validation error.
     */
    static IndexValidation validateTaskIndex(String input, int taskCount, Command command) {
        ValidationError syntaxError = new ValidationError(ValidationCode.INVALID_COMMAND_SYNTAX, input);
        try (Scanner scanner = new Scanner(input)) {
            if (!scanner.hasNext()) {
                return new IndexValidation(-1, syntaxError);
            }
            Command actualCommand = getCommand(scanner.next());
            boolean isMatchingCommand = command == Command.DELETE
                    ? actualCommand == Command.DELETE
                    : actualCommand == Command.MARK || actualCommand == Command.UNMARK;
            if (!isMatchingCommand || !scanner.hasNextInt()) {
                return new IndexValidation(-1, syntaxError);
            }
            int target = scanner.nextInt();
            if (scanner.hasNext()) {
                return new IndexValidation(-1, syntaxError);
            }
            if (target <= 0 || target > taskCount) {
                ValidationCode code = command == Command.DELETE
                        ? ValidationCode.DELETE_OUT_OF_RANGE : ValidationCode.MARK_OUT_OF_RANGE;
                return new IndexValidation(-1, new ValidationError(code, String.valueOf(target)));
            }
            return new IndexValidation(target - 1, null);
        }
    }

    /**
     * Parses a task once, returning either the task or its validation error.
     *
     * @param input the complete command to parse.
     * @param command the command already recognized from the input.
     * @return the new task or a validation error.
     */
    static TaskValidation parseTaskCreation(String input, Command command) {
        return switch (command) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> TaskValidation.invalid(ValidationCode.UNKNOWN_COMMAND, input);
        };
    }

    /**
     * Parses a todo or reports its missing description.
     */
    private static TaskValidation parseTodo(String input) {
        String description = input.trim().substring("todo".length()).trim();
        if (description.isEmpty()) {
            return TaskValidation.invalid(ValidationCode.MISSING_DESCRIPTION, "todo");
        }
        return new TaskValidation(new Todo(description), null);
    }

    /**
     * Checks description and marker syntax before validating the deadline value.
     */
    private static TaskValidation parseDeadline(String input) {
        String details = input.trim().substring("deadline".length()).trim();
        int byIndex = details.indexOf("/by");

        if (details.isEmpty() || (byIndex >= 0 && details.substring(0, byIndex).trim().isEmpty())) {
            return TaskValidation.invalid(ValidationCode.MISSING_DESCRIPTION, "deadline");
        }

        if (byIndex < 0) {
            return TaskValidation.invalid(ValidationCode.MISSING_DEADLINE, "", "/by");
        }
        String deadline = details.substring(byIndex + 3).trim();
        if (countOccurrences(details, "/by") != 1) {
            return TaskValidation.invalid(ValidationCode.INVALID_COMMAND_SYNTAX, input);
        }
        if (deadline.isEmpty()) {
            return TaskValidation.invalid(ValidationCode.MISSING_DEADLINE, "", "/by");
        }
        Event.DateTimeValue by = parseDateTime(deadline);
        if (by == null) {
            return TaskValidation.invalid(ValidationCode.INVALID_DATE_TIME, deadline, "/by");
        }
        String description = details.substring(0, byIndex).trim();
        return new TaskValidation(new Deadline(description, by.date(), by.time()), null);
    }

    /**
     * Parses an event while preserving the precedence of description, marker, and date-time errors.
     */
    private static TaskValidation parseEvent(String input) {
        String details = input.trim().substring("event".length()).trim();
        int fromIndex = details.indexOf("/from");
        int toIndex = details.indexOf("/to");

        if (details.isEmpty() || fromIndex == 0 || toIndex == 0) {
            return TaskValidation.invalid(ValidationCode.MISSING_DESCRIPTION, "event");
        }

        ValidationError markerError = validateEventMarkers(details, fromIndex, toIndex);
        if (markerError != null) {
            return new TaskValidation(null, markerError);
        }
        ValidationError timeError = validateEventTimeValues(details, fromIndex, toIndex);
        if (timeError != null) {
            return new TaskValidation(null, timeError);
        }

        String startTime = details.substring(fromIndex + 5, toIndex).trim();
        String endTime = details.substring(toIndex + 3).trim();
        Event.DateTimeValue from = parseDateTime(startTime);
        Event.DateTimeValue to = parseDateTime(endTime);
        if (from == null) {
            return TaskValidation.invalid(ValidationCode.INVALID_DATE_TIME, startTime, "/from");
        }
        if (to == null) {
            return TaskValidation.invalid(ValidationCode.INVALID_DATE_TIME, endTime, "/to");
        }
        String description = details.substring(0, fromIndex).trim();
        return new TaskValidation(new Event(description, from, to), null);
    }

    /**
     * Checks that each event marker appears once and that the start marker ends before the end marker.
     */
    private static ValidationError validateEventMarkers(String details, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < 0) {
            String field = fromIndex < 0 && toIndex < 0 ? "/from and /to" : fromIndex < 0 ? "/from" : "/to";
            return new ValidationError(ValidationCode.MISSING_EVENT_TIME, "event", field);
        }
        if (toIndex < fromIndex + "/from".length()
                || countOccurrences(details, "/from") != 1 || countOccurrences(details, "/to") != 1) {
            return new ValidationError(ValidationCode.INVALID_COMMAND_SYNTAX, details);
        }
        return null;
    }

    /**
     * Checks for empty time sections after marker presence, order, and uniqueness have been validated.
     */
    private static ValidationError validateEventTimeValues(String details, int fromIndex, int toIndex) {
        boolean isStartEmpty = details.substring(fromIndex + "/from".length(), toIndex).trim().isEmpty();
        boolean isEndEmpty = details.substring(toIndex + "/to".length()).trim().isEmpty();
        if (isStartEmpty || isEndEmpty) {
            String field = isStartEmpty && isEndEmpty ? "/from and /to" : isStartEmpty ? "/from" : "/to";
            return new ValidationError(ValidationCode.MISSING_EVENT_TIME, "event", field);
        }
        return null;
    }

    /**
     * Counts how many times a marker appears in a string.
     */
    private static int countOccurrences(String text, String marker) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(marker, index)) >= 0) {
            count++;
            index += marker.length();
        }

        return count;
    }

    /**
     * Checks whether a value is an ISO date.
     */
    private static boolean isDate(String value) {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        int year = Integer.parseInt(value.substring(0, 4));
        int month = Integer.parseInt(value.substring(5, 7));
        int day = Integer.parseInt(value.substring(8, 10));
        return month >= 1 && month <= 12 && YearMonth.of(year, month).isValidDay(day);
    }

    /**
     * Checks whether a value is an ISO time.
     */
    private static boolean isTime(String value) {
        if (!value.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        int hour = Integer.parseInt(value.substring(0, 2));
        int minute = Integer.parseInt(value.substring(3, 5));
        return hour < 24 && minute < 60;
    }

    /**
     * Parses an input date-time value. It accepts a date, a time, or both.
     *
     * @param value an ISO date, ISO time, or ISO date followed by a time.
     * @return the parsed date-time components, or null if invalid.
     */
    public static Event.DateTimeValue parseDateTime(String value) {
        if (isDate(value)) {
            return new Event.DateTimeValue(LocalDate.parse(value), null);
        }
        if (isTime(value)) {
            return new Event.DateTimeValue(null, LocalTime.parse(value));
        }

        String[] parts = value.split(" ", -1);
        if (parts.length == 2 && isDate(parts[0]) && isTime(parts[1])) {
            return new Event.DateTimeValue(
                    LocalDate.parse(parts[0]), LocalTime.parse(parts[1]));
        }
        return null;
    }

    /**
     * Identifies expected input failures without throwing during command processing.
     */
    enum ValidationCode {
        UNKNOWN_COMMAND,
        INVALID_DATE_TIME,
        INVALID_COMMAND_SYNTAX,
        MISSING_DESCRIPTION,
        MISSING_DEADLINE,
        MISSING_EVENT_TIME,
        MARK_OUT_OF_RANGE,
        DELETE_OUT_OF_RANGE
    }

    /**
     * Keeps the failure category and payload used by the public exception-based adapters.
     */
    record ValidationError(ValidationCode code, String detail, String field) {
        ValidationError(ValidationCode code, String detail) {
            this(code, detail, "");
        }
    }

    /**
     * Holds either a parsed task or its error so successful validation does not require parsing again.
     */
    record TaskValidation(Task task, ValidationError error) {
        /**
         * Creates a failed validation result without a task.
         */
        private static TaskValidation invalid(ValidationCode code, String detail) {
            return new TaskValidation(null, new ValidationError(code, detail));
        }

        /**
         * Creates a failed validation result identifying the affected date/time field.
         */
        private static TaskValidation invalid(ValidationCode code, String detail, String field) {
            return new TaskValidation(null, new ValidationError(code, detail, field));
        }
    }

    /**
     * Holds a valid zero-based index, or -1 and a non-null error on failure.
     */
    record IndexValidation(int index, ValidationError error) {
    }
}
