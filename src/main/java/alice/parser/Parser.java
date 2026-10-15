package alice.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Recurrence;
import alice.task.Task;
import alice.task.ToDo;

/**
 * Interprets Alice commands and converts their arguments into domain objects.
 */
public class Parser {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final int MAX_COMMAND_SECTIONS = 2;
    private static final String DEADLINE_BY_PREFIX = " /by ";
    private static final String EVENT_FROM_PREFIX = " /from ";
    private static final String EVENT_TO_PREFIX = " /to ";
    private static final String RECURRENCE_PREFIX = " /r ";
    private static final String RECURRENCE_ERROR =
            "A recurrence interval must be daily, weekly, monthly, or yearly.";
    private static final String EVENT_DETAILS_ERROR =
            "An event needs a description, a /from date time, and a /to date time.";

    /**
     * Returns the first word of a user command without changing the original input.
     *
     * @param userInput The complete command entered by the user.
     * @return The command word, or an empty string when the input is empty.
     */
    public String getCommandWord(String userInput) {
        int firstSpace = userInput.indexOf(' ');
        return firstSpace == -1 ? userInput : userInput.substring(0, firstSpace);
    }

    /**
     * Returns the portion of a command after its command word.
     *
     * @param userInput The complete command entered by the user.
     * @param command The command to remove from the input.
     * @return The trimmed command arguments.
     */
    private String getCommandArguments(String userInput, Command command) {
        return userInput.substring(command.getCommandWord().length()).trim();
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param userInput The complete task-creation command.
     * @return The task described by the command.
     * @throws AliceException If the task command is incomplete or invalid.
     */
    public Task parseTask(String userInput) throws AliceException {
        return switch (Command.fromCommandWord(getCommandWord(userInput))) {
            case TODO -> parseToDo(userInput);
            case DEADLINE -> parseDeadline(userInput);
            case EVENT -> parseEvent(userInput);
            default -> throw new AliceException("I don't understand that command.");
        };
    }

    /**
     * Parses a task number and checks that it identifies an existing task.
     *
     * @param userInput The complete command entered by the user.
     * @param command The command being parsed.
     * @param taskCount The number of tasks currently in the list.
     * @return The valid one-based task number.
     * @throws AliceException If no valid task number was supplied.
     */
    public int parseTaskNumber(String userInput, Command command, int taskCount) throws AliceException {
        String commandWord = command.getCommandWord();
        String taskNumberText = getCommandArguments(userInput, command);

        if (taskNumberText.isEmpty()) {
            throw new AliceException("Please provide a task number to " + commandWord + ".");
        }

        return parseTaskNumber(taskNumberText, taskCount);
    }

    /** Parses a task-number value and checks that it identifies an existing task. */
    private int parseTaskNumber(String taskNumberText, int taskCount) throws AliceException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AliceException("There is no task numbered " + taskNumber + ".");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new AliceException("The task number must be a positive whole number.");
        }
    }

    /**
     * Parses a repeat command's task number and recurrence interval.
     *
     * @param userInput The complete repeat command.
     * @param taskCount The number of tasks currently in the list.
     * @return The requested task number and recurrence interval.
     * @throws AliceException If the repeat command is incomplete or invalid.
     */
    public RepeatDetails parseRepeatCommand(String userInput, int taskCount) throws AliceException {
        String[] sections = getCommandArguments(userInput, Command.REPEAT).split("\\s+");
        if (sections.length != MAX_COMMAND_SECTIONS) {
            throw new AliceException("A repeat command needs a task number and an interval.");
        }

        int taskNumber = parseTaskNumber(sections[0], taskCount);

        if (sections[1].equalsIgnoreCase("none")) {
            return new RepeatDetails(taskNumber, null);
        }
        Recurrence recurrence = Recurrence.fromIntervalName(sections[1]);
        if (recurrence == null) {
            throw new AliceException("Repeat interval must be daily, weekly, monthly, yearly, or none.");
        }
        return new RepeatDetails(taskNumber, recurrence);
    }

    /** Represents the details supplied by a valid repeat command. */
    public record RepeatDetails(int taskNumber, Recurrence recurrence) {
    }

    /**
     * Parses the date supplied to a date command.
     *
     * @param userInput The complete date command.
     * @return The requested date.
     * @throws AliceException If the date is not in yyyy-MM-dd format.
     */
    public LocalDate parseDate(String userInput) throws AliceException {
        String dateText = getCommandArguments(userInput, Command.DATE);
        try {
            return LocalDate.parse(dateText, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param userInput The complete find command.
     * @return The keyword to search for.
     * @throws AliceException If the keyword is empty.
     */
    public String parseKeyword(String userInput) throws AliceException {
        String keyword = getCommandArguments(userInput, Command.FIND);
        if (keyword.isEmpty()) {
            throw new AliceException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Creates a todo task from its command.
     *
     * @param userInput The complete todo command.
     * @return The requested todo task.
     * @throws AliceException If the description is empty.
     */
    private Task parseToDo(String userInput) throws AliceException {
        String description = getCommandArguments(userInput, Command.TODO);
        if (description.contains(RECURRENCE_PREFIX)) {
            throw new AliceException("Todos cannot recur.");
        }
        if (description.isEmpty()) {
            throw new AliceException("The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Creates a deadline task from its command.
     *
     * @param userInput The complete deadline command.
     * @return The requested deadline task.
     * @throws AliceException If the description or date is invalid.
     */
    private Task parseDeadline(String userInput) throws AliceException {
        String[] sections = getCommandArguments(userInput, Command.DEADLINE)
                .split(DEADLINE_BY_PREFIX, MAX_COMMAND_SECTIONS);
        if (sections.length != MAX_COMMAND_SECTIONS
                || sections[0].isBlank() || sections[1].isBlank()) {
            throw new AliceException("A deadline needs a description and a /by date.");
        }

        OptionalRecurrence optionalRecurrence = parseOptionalRecurrence(sections[1]);
        try {
            LocalDate by = LocalDate.parse(optionalRecurrence.scheduledDetails(), DEADLINE_INPUT_FORMAT);
            Deadline deadline = new Deadline(sections[0], by);
            setOptionalRecurrence(deadline, optionalRecurrence.recurrence());
            return deadline;
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event task from its command.
     *
     * @param userInput The complete event command.
     * @return The requested event task.
     * @throws AliceException If the description or date and time values are invalid.
     */
    private Task parseEvent(String userInput) throws AliceException {
        String[] fromSections = getCommandArguments(userInput, Command.EVENT)
                .split(EVENT_FROM_PREFIX, MAX_COMMAND_SECTIONS);
        if (fromSections.length != MAX_COMMAND_SECTIONS || fromSections[0].isBlank()) {
            throw new AliceException(EVENT_DETAILS_ERROR);
        }

        String[] toSections = fromSections[1].split(EVENT_TO_PREFIX, MAX_COMMAND_SECTIONS);
        if (toSections.length != MAX_COMMAND_SECTIONS
                || toSections[0].isBlank() || toSections[1].isBlank()) {
            throw new AliceException(EVENT_DETAILS_ERROR);
        }

        OptionalRecurrence optionalRecurrence = parseOptionalRecurrence(toSections[1]);
        try {
            LocalDateTime fromDateTime = LocalDateTime.parse(toSections[0], EVENT_INPUT_FORMAT);
            LocalDateTime toDateTime = LocalDateTime.parse(
                    optionalRecurrence.scheduledDetails(), EVENT_INPUT_FORMAT);
            if (toDateTime.isBefore(fromDateTime)) {
                throw new AliceException("An event cannot end before it starts.");
            }
            Event event = new Event(fromSections[0], fromDateTime, toDateTime);
            setOptionalRecurrence(event, optionalRecurrence.recurrence());
            return event;
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the event date time format yyyy-MM-dd HHmm.");
        }
    }

    /** Parses an optional recurrence option at the end of scheduled task details. */
    private OptionalRecurrence parseOptionalRecurrence(String details) throws AliceException {
        String[] sections = details.split(RECURRENCE_PREFIX, MAX_COMMAND_SECTIONS);
        if (sections.length == 1) {
            return new OptionalRecurrence(details, null);
        }
        if (sections[0].isBlank() || sections[1].isBlank()) {
            throw new AliceException(RECURRENCE_ERROR);
        }
        Recurrence recurrence = Recurrence.fromIntervalName(sections[1]);
        if (recurrence == null) {
            throw new AliceException(RECURRENCE_ERROR);
        }
        return new OptionalRecurrence(sections[0], recurrence);
    }

    /** Applies an optional recurrence interval to a scheduled task. */
    private void setOptionalRecurrence(Task task, Recurrence recurrence) {
        if (recurrence != null) {
            task.setRecurrence(recurrence);
        }
    }

    /** Represents scheduled task details and an optional recurrence interval. */
    private record OptionalRecurrence(String scheduledDetails, Recurrence recurrence) {
    }
}
