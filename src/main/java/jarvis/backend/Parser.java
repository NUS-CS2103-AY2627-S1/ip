package jarvis.backend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.classes.ToDo;
import jarvis.exceptions.IncompleteCommandException;
import jarvis.exceptions.InvalidDateAndTimeException;
import jarvis.exceptions.InvalidStartAndEndTimeException;
import jarvis.exceptions.TooSimpleArgumentException;


/** Parses user commands and converts them into tasks or command parameters. */
public class Parser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final String FROM_COMMAND = "/from";
    private static final String TO_COMMAND = "/to";
    private static final String BY_COMMAND = "/by";
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final String FIND_COMMAND = "find";

    private static final String ERROR_PARAMETER_MISSING_MSG_HEADER =
            "Error : Your command is missing certain parameters.";
    private static final String ERROR_FORMAT_MSG_BODY = "Please re-enter your command in the format : ";
    private static final String ERROR_INVALID_DATE_TIME_MSG_HEADER = "Error : Invalid start/end date/date & time.";
    private static final String ERROR_FIND_KEYWORD_TOO_GENERAL_MSG_HEADER = "Error : Your <Key Word> is too general.";
    private static final String ERROR_INVALID_DATE_TIME_FORMAT_MSG_HEADER = "Error : Invalid date/ date & time format.";


    private static final String RE_ENTER_COMMAND_MSG_BODY = "Please re-enter your command in the format : ";
    private static final String MULTIPLE_DATE_FORMAT_MSG_BODY = "Please use either one of the format below : ";


    private static final String TODO_TASK_FORMAT = "todo <Task>";


    private static final String DEADLINE_TASK_DATE_ONLY_FORMAT =
            "For date only : deadline <Task> /by yyyy-MM-dd ";
    private static final String DEADLINE_TASK_DATE_TIME_FORMAT =
            "For date and time : deadline <Task> /by yyyy-MM-dd HH:mm";


    private static final String EVENT_TASK_DATE_TIME_FORMAT =
            "For date and time : event <Task> /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm";
    private static final String EVENT_TASK_DATE_ONLY_FORMAT =
            "For date only : event <Task> /from yyyy-MM-dd /to yyyy-MM-dd";
    private static final String EVENT_TASK_INVALID_DATE_TIME_MSG_BODY =
            "Ensure the start date is strictly before the end date for date only events\n"
            + "If the event includes time, the start time must also be strictly before the end time.";


    private static final String MARK_UNMARK_DELETE_COMMAND_FORMAT =
            "mark 1 / unmark 2 / delete 3, with a valid task number";


    private static final String FIND_COMMAND_FORMAT = "find <key word>";
    private static final String FIND_COMMAND_INPUT_MIN_2_CHARACTER_MSG_BODY =
            "Please re-enter your command with a key word of minimum 2 characters.";


    /**
     * Extracts the description of a todo task.
     *
     * @param userInput User's command.
     * @return A ToDo containing the task.
     * @throws IncompleteCommandException If the command is incomplete.
     */
    public static ToDo parseToDo(String userInput)
            throws IncompleteCommandException {
        assert userInput != null : "userInput should not be null";

        userInput = userInput.trim();

        if (userInput.length() < 5) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                            ERROR_PARAMETER_MISSING_MSG_HEADER,
                            ERROR_FORMAT_MSG_BODY, TODO_TASK_FORMAT
                    )
            );
        }

        String task = userInput.substring(TODO_COMMAND.length()).trim();

        if (task.isEmpty()) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                    ERROR_PARAMETER_MISSING_MSG_HEADER,
                            ERROR_FORMAT_MSG_BODY, TODO_TASK_FORMAT
                    )
            );
        }

        return new ToDo(task);
    }

    /**
     * Extracts the task and deadline from a deadline command.
     *
     * @param userInput User's command.
     * @return A Deadline containing the task and deadline.
     * @throws IncompleteCommandException If the command is incomplete.
     * @throws InvalidDateAndTimeException If the date/date and time is incomplete
     */
    public static Deadline parseDeadline(String userInput)
            throws IncompleteCommandException, InvalidDateAndTimeException {
        assert userInput != null : "userInput should not be null";

        userInput = userInput.trim();

        int positionOfBy = userInput.indexOf(BY_COMMAND);

        if (positionOfBy == -1) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                    ERROR_PARAMETER_MISSING_MSG_HEADER,
                            RE_ENTER_COMMAND_MSG_BODY,
                            DEADLINE_TASK_DATE_ONLY_FORMAT,
                            DEADLINE_TASK_DATE_TIME_FORMAT
                    )
            );
        }

        assert positionOfBy >= DEADLINE_COMMAND.length()
                : "The /by command must appear after the deadline task prefix";

        String task = userInput.substring(DEADLINE_COMMAND.length(), positionOfBy).trim();
        String deadline = userInput.substring(positionOfBy + BY_COMMAND.length()).trim();

        if (task.isEmpty() || deadline.isEmpty()) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                    ERROR_PARAMETER_MISSING_MSG_HEADER,
                            RE_ENTER_COMMAND_MSG_BODY,
                            DEADLINE_TASK_DATE_ONLY_FORMAT,
                            DEADLINE_TASK_DATE_TIME_FORMAT
                    )
            );
        }

        LocalDateTime deadlineDateAndTime;

        try {
            // Attempt parsing date and time first
            deadlineDateAndTime = LocalDateTime.parse(deadline, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException error1) {

            try {
                // Retry to see user only inputted date
                LocalDate deadlineDate = LocalDate.parse(deadline, DATE_FORMATTER);

                // Converts a date-only deadline to a date-time deadline.
                deadlineDateAndTime = deadlineDate.atStartOfDay();
            } catch (DateTimeParseException error2) {
                throw new InvalidDateAndTimeException(
                        buildErrorMessage(
                                ERROR_INVALID_DATE_TIME_FORMAT_MSG_HEADER,
                                MULTIPLE_DATE_FORMAT_MSG_BODY,
                                DEADLINE_TASK_DATE_ONLY_FORMAT,
                                DEADLINE_TASK_DATE_TIME_FORMAT
                        )
                );
            }
        }

        // In case of bug with try-catches above, the assert will catch the error
        assert deadlineDateAndTime != null
                : "A valid deadline command must produce a date and time";

        return new Deadline(task, deadlineDateAndTime);
    }

    /**
     * Extracts the task, start date/time and end date/time from an event command.
     *
     * @param userInput User's command.
     * @return An Event containing the task, start date or/and time and end date or/and time.
     * @throws IncompleteCommandException If the command is incomplete.
     * @throws InvalidDateAndTimeException If the date/time is incomplete
     * @throws InvalidStartAndEndTimeException If the end datetime is before the start date/time
     */
    public static Event parseEvent(String userInput)
            throws IncompleteCommandException,
            InvalidDateAndTimeException,
            InvalidStartAndEndTimeException {
        assert userInput != null : "userInput should not be null";

        userInput = userInput.trim();

        int positionOfFrom = userInput.indexOf(FROM_COMMAND);
        int positionOfTo = userInput.indexOf(TO_COMMAND);

        if (positionOfFrom == -1
                || positionOfTo == -1
                || positionOfFrom >= positionOfTo) {

            throw new IncompleteCommandException(
                    buildErrorMessage(
                            ERROR_PARAMETER_MISSING_MSG_HEADER,
                            MULTIPLE_DATE_FORMAT_MSG_BODY,
                            EVENT_TASK_DATE_ONLY_FORMAT,
                            EVENT_TASK_DATE_TIME_FORMAT
                    )
            );
        }

        // In case of bug with try-catches above, the assert will catch the error
        assert positionOfFrom < positionOfTo
                : "The /from command must occur before the /to command";

        String task = userInput.substring(EVENT_COMMAND.length(), positionOfFrom).trim();

        String startDateTime =
                userInput.substring(positionOfFrom + FROM_COMMAND.length(), positionOfTo).trim();

        String endDateTime =
                userInput.substring(positionOfTo + TO_COMMAND.length()).trim();

        if (task.isEmpty()
                || startDateTime.isEmpty()
                || endDateTime.isEmpty()) {

            throw new IncompleteCommandException(
                    buildErrorMessage(
                            ERROR_PARAMETER_MISSING_MSG_HEADER,
                            MULTIPLE_DATE_FORMAT_MSG_BODY,
                            EVENT_TASK_DATE_ONLY_FORMAT,
                            EVENT_TASK_DATE_TIME_FORMAT
                    )
            );
        }

        LocalDateTime eventStartDateAndTime;
        LocalDateTime eventEndDateAndTime;

        try {
            // Attempt parsing date and time first
            eventStartDateAndTime = LocalDateTime.parse(startDateTime, DATE_TIME_FORMATTER);
            eventEndDateAndTime = LocalDateTime.parse(endDateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException error1) {

            try {
                // Retry to see user only inputted date
                LocalDate eventStartDate;
                LocalDate eventEndDate;

                eventStartDate = LocalDate.parse(startDateTime, DATE_FORMATTER);
                eventEndDate = LocalDate.parse(endDateTime, DATE_FORMATTER);

                // Converts a date-only event to a date-time event.
                eventStartDateAndTime = eventStartDate.atStartOfDay();
                eventEndDateAndTime = eventEndDate.atStartOfDay();
            } catch (DateTimeParseException error2) {
                throw new InvalidDateAndTimeException(
                        buildErrorMessage(
                                ERROR_INVALID_DATE_TIME_FORMAT_MSG_HEADER,
                                MULTIPLE_DATE_FORMAT_MSG_BODY,
                                EVENT_TASK_DATE_ONLY_FORMAT,
                                EVENT_TASK_DATE_TIME_FORMAT
                        )
                );
            }
        }

        if (!eventEndDateAndTime.isAfter(eventStartDateAndTime)) {
            throw new InvalidStartAndEndTimeException(
                    buildErrorMessage(
                            ERROR_INVALID_DATE_TIME_MSG_HEADER,
                            EVENT_TASK_INVALID_DATE_TIME_MSG_BODY
                    )
            );
        }

        assert eventEndDateAndTime.isAfter(eventStartDateAndTime)
                : "An event's end time must not be before its start time";

        return new Event(task, eventStartDateAndTime, eventEndDateAndTime);
    }

    /**
     * Extracts the task number from a mark/unmark/delete command.
     *
     * @param userInput User's command.
     * @return The task number.
     * @throws IncompleteCommandException If no valid number is provided.
     */
    public static int parseTaskNumber(String userInput)
            throws IncompleteCommandException {
        assert userInput != null : "userInput should not be null";

        userInput = userInput.trim();

        String[] splitUserInput = userInput.trim().split("\\s+");

        try {
            return Integer.parseInt(splitUserInput[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException error) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                            ERROR_PARAMETER_MISSING_MSG_HEADER,
                            RE_ENTER_COMMAND_MSG_BODY,
                            MARK_UNMARK_DELETE_COMMAND_FORMAT
                    )
            );
        }
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param userInput User's command containing the keyword.
     * @return The keyword to filter tasks by.
     * @throws IncompleteCommandException If no key words are inputted by user
     * @throws TooSimpleArgumentException If the key words are too general
     */
    public static String parseTaskKeyWord(String userInput)
            throws IncompleteCommandException, TooSimpleArgumentException {
        assert userInput != null : "userInput should not be null";

        userInput = userInput.trim();

        String taskKeyWord = userInput.substring(FIND_COMMAND.length()).trim().toLowerCase();

        if (taskKeyWord.isEmpty()) {
            throw new IncompleteCommandException(
                    buildErrorMessage(
                            ERROR_PARAMETER_MISSING_MSG_HEADER,
                            RE_ENTER_COMMAND_MSG_BODY,
                            FIND_COMMAND_FORMAT
                    )

            );
        } else if (taskKeyWord.length() == 1) {
            throw new TooSimpleArgumentException(
                    buildErrorMessage(
                            ERROR_FIND_KEYWORD_TOO_GENERAL_MSG_HEADER,
                            FIND_COMMAND_INPUT_MIN_2_CHARACTER_MSG_BODY
                    )
            );
        }

        return taskKeyWord;
    }

    /**
     * Builds a formatted error message from multiple lines.
     *
     * @param messages The lines of the error message.
     * @return A formatted error message.
     */
    private static String buildErrorMessage(String... messages) {
        return String.join("\n", messages) + "\n";
    }

}
