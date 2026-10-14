package bingus.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import bingus.command.AddCommand;
import bingus.command.Command;
import bingus.command.DeleteCommand;
import bingus.command.ExitCommand;
import bingus.command.FindCommand;
import bingus.command.ListCommand;
import bingus.command.MarkCommand;
import bingus.command.UpdateCommand;
import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.task.Todo;

/**
 * Parses command arguments into validated tasks and values.
 */
public class Parser {

    /** Strict format accepted when filtering tasks by date. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Usage guidance for updating supported task details. */
    private static final String UPDATE_USAGE = "Try `update [TASK_NUMBER] /desc [DESCRIPTION]`, "
            + "`update [TASK_NUMBER] /by [DATETIME]`, `update [TASK_NUMBER] /from [DATETIME]`, "
            + "or `update [TASK_NUMBER] /to [DATETIME]`.";

    private final TaskParser taskParser = new TaskParser();

    /**
     * Converts one command line into the corresponding command object.
     *
     * @param userInput complete command line entered by the user.
     * @param tasks current task list, used to validate task numbers.
     * @return command represented by the input.
     * @throws BingusException if the command or its arguments are invalid.
     */
    public Command parse(String userInput, TaskList tasks) throws BingusException {
        String[] commandParts = splitCommand(userInput);
        String commandWord = commandParts[0];
        switch (commandWord) {
            case "bye":
                requireNoArguments(commandParts, "Bingus cannot use extra details with `bye`.\nTry `bye`.");
                return new ExitCommand();
            case "list":
                return commandParts.length == 1
                        ? new ListCommand()
                        : new ListCommand(parseSingleListDate(commandParts[1]));
            case "todo":
                return new AddCommand(taskParser.parseTodo(commandParts));
            case "deadline":
                return new AddCommand(taskParser.parseDeadline(commandParts));
            case "event":
                return new AddCommand(taskParser.parseEvent(commandParts));
            case "mark":
                return new MarkCommand(parseRequiredTaskId(
                        commandParts, tasks.size(), "Which task should I mark?\nTry `mark [TASK_NUMBER]`.",
                        "I can only mark one task at a time.\nTry `mark [TASK_NUMBER]`."), true);
            case "unmark":
                return new MarkCommand(parseRequiredTaskId(
                        commandParts, tasks.size(), "Which task should I unmark?\nTry `unmark [TASK_NUMBER]`.",
                        "I can only unmark one task at a time.\nTry `unmark [TASK_NUMBER]`."), false);
            case "delete":
                return new DeleteCommand(parseRequiredTaskId(
                        commandParts, tasks.size(), "Which task should I remove?\nTry `delete [TASK_NUMBER]`.",
                        "I can only remove one task at a time.\nTry `delete [TASK_NUMBER]`."));
            case "find":
                return new FindCommand(parseFindKeyword(commandParts));
            case "update":
                return parseUpdateCommand(commandParts, tasks);
            default:
                throw new BingusException("Hmm, I do not recognise that command.\n"
                        + "Try `todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`, "
                        + "`find`, `update`, or `bye`.");
        }
    }

    /**
     * Parses an update command that replaces a supported task detail.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @param tasks current task list, used to find the task being updated.
     * @return command containing the updated replacement task.
     * @throws BingusException if the command arguments are missing or invalid.
     */
    private UpdateCommand parseUpdateCommand(String[] commandParts, TaskList tasks) throws BingusException {
        if (commandParts.length < 2 || commandParts[1].trim().isEmpty()) {
            throw new BingusException("Tell me which task to update and what to change.\n" + UPDATE_USAGE);
        }

        String[] updateParts = commandParts[1].trim().split("\\s+", 3);
        int taskId = parseTaskId(updateParts[0], tasks.size());
        validateUpdateParts(updateParts);

        String updateField = updateParts[1];
        String updatedValue = updateParts[2].trim();
        Task originalTask = tasks.get(taskId - 1);
        Task updatedTask = createUpdatedTask(originalTask, updateField, updatedValue);
        return new UpdateCommand(taskId, updatedTask);
    }

    /**
     * Validates the field and replacement value supplied to an update command.
     *
     * @param updateParts task number, update field, and replacement value.
     * @throws BingusException if the update field or replacement value is invalid.
     */
    private void validateUpdateParts(String[] updateParts) throws BingusException {
        if (updateParts.length < 2) {
            throw new BingusException("Tell me what to change for that task.\n" + UPDATE_USAGE);
        }
        String updateField = updateParts[1];
        if (!isSupportedUpdateField(updateField)) {
            throw new BingusException("I cannot update that field.\n" + UPDATE_USAGE);
        }
        if (updateParts.length < 3 || updateParts[2].trim().isEmpty()) {
            if (updateField.equals("/desc")) {
                throw new BingusException("I need a new description.\n" + UPDATE_USAGE);
            }
            throw new BingusException("I need a new date and time.\n" + UPDATE_USAGE);
        }
    }

    private boolean isSupportedUpdateField(String updateField) {
        switch (updateField) {
            case "/desc":
            case "/by":
            case "/from":
            case "/to":
                return true;
            default:
                return false;
        }
    }

    /**
     * Creates a replacement task containing the requested update.
     *
     * @param originalTask task whose details should be updated.
     * @param updateField task detail to replace.
     * @param updatedValue replacement value for the selected detail.
     * @return replacement task containing the update.
     * @throws BingusException if the update is incompatible with the task.
     */
    private Task createUpdatedTask(Task originalTask, String updateField, String updatedValue)
            throws BingusException {
        switch (updateField) {
            case "/desc":
                return createTaskWithUpdatedDescription(originalTask, updatedValue);
            case "/by":
                return createDeadlineWithUpdatedDateTime(originalTask, updatedValue);
            case "/from":
            case "/to":
                return createEventWithUpdatedDateTime(originalTask, updateField, updatedValue);
            default:
                throw new IllegalStateException("Unsupported update field: " + updateField);
        }
    }

    /**
     * Creates a task of the same type with a new description and unchanged type-specific details.
     *
     * @param originalTask task whose type-specific details should be preserved.
     * @param updatedDescription replacement task description.
     * @return replacement task with the updated description.
     */
    private Task createTaskWithUpdatedDescription(Task originalTask, String updatedDescription) {
        switch (originalTask.getType()) {
            case TODO:
                return new Todo(updatedDescription);
            case DEADLINE:
                Deadline deadline = (Deadline) originalTask;
                return new Deadline(updatedDescription, deadline.getBy());
            case EVENT:
                Event event = (Event) originalTask;
                return new Event(
                        updatedDescription,
                        event.getFrom(),
                        event.getTo());
            default:
                throw new IllegalStateException(
                        "Unsupported task type: " + originalTask.getType());
        }
    }

    /**
     * Creates a deadline with a new due date and an unchanged description.
     *
     * @param originalTask deadline whose description should be preserved.
     * @param updatedDateTime replacement deadline date and time.
     * @return replacement deadline with the updated due date.
     * @throws BingusException if the task is not a deadline or the date and time is invalid.
     */
    private Deadline createDeadlineWithUpdatedDateTime(Task originalTask, String updatedDateTime)
            throws BingusException {
        if (!(originalTask instanceof Deadline)) {
            throw new BingusException("I can only use `/by` with deadlines.\n" + UPDATE_USAGE);
        }

        LocalDateTime updatedBy = taskParser.parseDeadlineDateTime(updatedDateTime);
        return new Deadline(originalTask.getDescription(), updatedBy);
    }

    /**
     * Creates an event with one updated endpoint and all other details unchanged.
     *
     * @param originalTask event whose existing details should be preserved.
     * @param updateField {@code /from} to update the start or {@code /to} to update the end.
     * @param updatedDateTime replacement event date and time.
     * @return replacement event with the updated endpoint.
     * @throws BingusException if the task is not an event, the date and time is invalid, or the range is invalid.
     */
    private Event createEventWithUpdatedDateTime(
            Task originalTask, String updateField, String updatedDateTime) throws BingusException {
        assert updateField.equals("/from") || updateField.equals("/to")
                : "Event update field must be /from or /to";
        if (!(originalTask instanceof Event)) {
            throw new BingusException("I can only use `/from` and `/to` with events.\n" + UPDATE_USAGE);
        }

        Event originalEvent = (Event) originalTask;
        LocalDateTime updatedDate = taskParser.parseEventDateTime(updatedDateTime);
        LocalDateTime updatedFrom = updateField.equals("/from") ? updatedDate : originalEvent.getFrom();
        LocalDateTime updatedTo = updateField.equals("/to") ? updatedDate : originalEvent.getTo();
        if (!updatedTo.isAfter(updatedFrom)) {
            throw new BingusException("The event must end after it starts.");
        }
        return new Event(originalEvent.getDescription(), updatedFrom, updatedTo);
    }

    /**
     * Parses the required search word for a find command.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @return validated search word.
     * @throws BingusException if the search word is missing.
     */
    private String parseFindKeyword(String[] commandParts) throws BingusException {
        if (commandParts.length < 2 || commandParts[1].trim().isEmpty()) {
            throw new BingusException("What should I search for?\nTry `find [KEYWORD]`.");
        }

        return commandParts[1].trim();
    }

    /**
     * Parses a required task number using the specified missing-argument message.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @param taskCount current number of tasks.
     * @param missingArgumentMessage message to show when the task number is missing.
     * @param extraArgumentMessage message to show when more than one task number is supplied.
     * @return validated one-based task number.
     * @throws BingusException if the number is missing or invalid.
     */
    private int parseRequiredTaskId(
            String[] commandParts, int taskCount, String missingArgumentMessage, String extraArgumentMessage)
            throws BingusException {
        if (commandParts.length < 2) {
            throw new BingusException(missingArgumentMessage);
        }
        String[] taskIds = commandParts[1].trim().split("\\s+");
        if (taskIds.length != 1) {
            throw new BingusException(extraArgumentMessage);
        }
        return parseTaskId(taskIds[0], taskCount);
    }

    /**
     * Rejects details supplied to a command that does not accept them.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @param errorMessage message to show when unused details are supplied.
     * @throws BingusException if the command has unused details.
     */
    private void requireNoArguments(String[] commandParts, String errorMessage) throws BingusException {
        if (commandParts.length > 1 && !commandParts[1].trim().isEmpty()) {
            throw new BingusException(errorMessage);
        }
    }

    /**
     * Parses the optional date argument for a list command.
     *
     * @param input date argument supplied to the list command.
     * @return validated calendar date.
     * @throws BingusException if more than one date is supplied or the date is invalid.
     */
    private LocalDate parseSingleListDate(String input) throws BingusException {
        String[] dates = input.trim().split("\\s+");
        if (dates.length != 1) {
            throw new BingusException("I can only use one date to filter the list.\n"
                    + "Try `list [yyyy-MM-dd]`.");
        }
        return parseListDate(dates[0]);
    }

    /**
     * Splits a user command into its command word and remaining arguments.
     *
     * @param userInput complete command line entered by the user.
     * @return command and arguments split into at most two parts.
     */
    private String[] splitCommand(String userInput) {
        return userInput.split("\\s+", 2);
    }

    /**
     * Parses a one-based task number and verifies that it refers to a task.
     *
     * @param input task number text.
     * @param taskCount current number of tasks.
     * @return validated one-based task number.
     * @throws BingusException if the number is not an integer or is out of range.
     */
    int parseTaskId(String input, int taskCount) throws BingusException {
        try {
            int taskId = Integer.parseInt(input.trim());
            if (taskId < 1 || taskId > taskCount) {
                throw new BingusException("I could not find that task number in your list.");
            }
            return taskId;
        } catch (NumberFormatException e) {
            throw new BingusException("The task number must be a whole number.");
        }
    }

    /**
     * Parses a date used to filter the task list.
     *
     * @param input date text in yyyy-MM-dd format.
     * @return validated calendar date.
     * @throws BingusException if the date is invalid.
     */
    LocalDate parseListDate(String input) throws BingusException {
        try {
            return LocalDate.parse(input.trim(), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BingusException("That list date is not valid.\n"
                    + "Use yyyy-MM-dd, for example 2019-12-02.");
        }
    }
}
