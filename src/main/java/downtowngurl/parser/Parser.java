package downtowngurl.parser;

import java.time.LocalDateTime;
import java.util.Locale;

import downtowngurl.command.AddCommand;
import downtowngurl.command.ByeCommand;
import downtowngurl.command.Command;
import downtowngurl.command.DeleteCommand;
import downtowngurl.command.FindCommand;
import downtowngurl.command.ListCommand;
import downtowngurl.command.MarkCommand;
import downtowngurl.command.RepeatCommand;
import downtowngurl.command.UnmarkCommand;
import downtowngurl.command.UnrepeatCommand;
import downtowngurl.exception.DowntownGurlException;
import downtowngurl.task.Deadline;
import downtowngurl.task.Event;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskDateTime;
import downtowngurl.task.Todo;

/**
 * Makes sense of raw user commands.
 */
public class Parser {
    private static final String ARGUMENT_SEPARATOR = " ";
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String REPEAT_COMMAND = "repeat";
    private static final String UNREPEAT_COMMAND = "unrepeat";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";
    private static final String REPEAT_SEPARATOR = " /repeat ";
    private static final String WEEKLY_REPEAT_SEPARATOR = " /weekly";
    private static final String EMPTY_TASK_MESSAGE = "Soz queen you gotta at least give me SOMETHING to work with.";
    private static final String FIND_FORMAT_HINT = createFormatHint("find <keyword>");
    private static final String MARK_FORMAT_HINT = createFormatHint("mark <task number>");
    private static final String UNMARK_FORMAT_HINT = createFormatHint("unmark <task number>");
    private static final String DELETE_FORMAT_HINT = createFormatHint("delete <task number>");
    private static final String REPEAT_FORMAT_HINT = createFormatHint("repeat <task number> /weekly");
    private static final String UNREPEAT_FORMAT_HINT = createFormatHint("unrepeat <task number>");
    private static final String TODO_FORMAT_HINT = createFormatHint("todo <task name>");
    private static final String DEADLINE_FORMAT_HINT = createFormatHint("deadline <task name> /by dd/mm/yyyy time");
    private static final String REPEATING_DEADLINE_FORMAT_HINT =
            createFormatHint("deadline <task name> /by dd/mm/yyyy time /repeat weekly");
    private static final String EVENT_FORMAT_HINT =
            createFormatHint("event <event name> /from dd/mm/yyyy time /to dd/mm/yyyy time");
    private static final String REPEATING_EVENT_FORMAT_HINT =
            createFormatHint("event <event name> /from dd/mm/yyyy time /to dd/mm/yyyy time /repeat weekly");
    private static final String UNKNOWN_COMMAND_MESSAGE = "Girl you need some sleep... "
            + "I don't understand whatchu talking about.";

    /**
     * Parses a full user command into a command object the application can execute.
     *
     * @param command Full user command.
     * @return Parsed command.
     * @throws DowntownGurlException If the command is invalid.
     */
    public static Command parse(String command) throws DowntownGurlException {
        if (command == null || command.isBlank()) {
            throw new DowntownGurlException(UNKNOWN_COMMAND_MESSAGE);
        }
        command = command.trim();

        if (isCommand(command, BYE_COMMAND)) {
            return new ByeCommand();
        }

        if (isCommand(command, LIST_COMMAND)) {
            return new ListCommand();
        }

        if (isCommandOrArgumentStart(command, FIND_COMMAND)) {
            return new FindCommand(getFindKeyword(command));
        }

        if (isCommandOrArgumentStart(command, MARK_COMMAND)) {
            return new MarkCommand(getTaskIndexFromCommand(command, getArgumentStartIndex(MARK_COMMAND),
                    MARK_FORMAT_HINT));
        }

        if (isCommandOrArgumentStart(command, UNMARK_COMMAND)) {
            return new UnmarkCommand(getTaskIndexFromCommand(command, getArgumentStartIndex(UNMARK_COMMAND),
                    UNMARK_FORMAT_HINT));
        }

        if (isCommandOrArgumentStart(command, DELETE_COMMAND)) {
            return new DeleteCommand(getTaskIndexFromCommand(command, getArgumentStartIndex(DELETE_COMMAND),
                    DELETE_FORMAT_HINT));
        }

        if (startsWithCommandArgument(command, REPEAT_COMMAND)) {
            return createRepeatCommand(command);
        }

        if (isCommandOrArgumentStart(command, UNREPEAT_COMMAND)) {
            return new UnrepeatCommand(getTaskIndexFromCommand(command, getArgumentStartIndex(UNREPEAT_COMMAND),
                    UNREPEAT_FORMAT_HINT));
        }

        if (isCommandOrArgumentStart(command, TODO_COMMAND)) {
            return new AddCommand(createTodo(command));
        }

        if (isCommandOrArgumentStart(command, DEADLINE_COMMAND)) {
            return new AddCommand(createDeadline(command));
        }

        if (isCommandOrArgumentStart(command, EVENT_COMMAND)) {
            return new AddCommand(createEvent(command));
        }

        throw new DowntownGurlException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Creates a two-line hint that shows the expected command shape before the empty-task reminder.
     *
     * @param commandFormat Expected command format.
     * @return Format hint message.
     */
    private static String createFormatHint(String commandFormat) {
        return "Maybe you could try formatting it as: " + commandFormat + "\n" + EMPTY_TASK_MESSAGE;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command Full user command.
     * @return Keyword to search for.
     * @throws DowntownGurlException If the keyword is missing.
     */
    private static String getFindKeyword(String command) throws DowntownGurlException {
        String keyword = getCommandArgument(command, FIND_COMMAND);
        if (keyword.isBlank()) {
            throw new DowntownGurlException(FIND_FORMAT_HINT);
        }
        return keyword.trim();
    }

    /**
     * Creates a todo task from a command in this form: todo DESCRIPTION.
     *
     * @param command Full user command.
     * @return New todo task.
     * @throws DowntownGurlException If the todo description is missing.
     */
    private static Todo createTodo(String command) throws DowntownGurlException {
        String description = getCommandArgument(command, TODO_COMMAND);
        if (description.isBlank()) {
            throw new DowntownGurlException(TODO_FORMAT_HINT);
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from a command in this form: deadline DESCRIPTION /by TIME.
     *
     * @param command Full user command.
     * @return New deadline task.
     * @throws DowntownGurlException If the description or deadline time is missing.
     */
    private static Deadline createDeadline(String command) throws DowntownGurlException {
        String taskCommand = removeRepeatClause(command);
        int separatorIndex = taskCommand.toLowerCase(Locale.ROOT).indexOf(DEADLINE_SEPARATOR);
        if (separatorIndex == -1) {
            throw new DowntownGurlException(DEADLINE_FORMAT_HINT);
        }
        String description = taskCommand.substring(getArgumentStartIndex(DEADLINE_COMMAND), separatorIndex);
        assert separatorIndex >= 9 : "Deadline separator should appear after the command word.";
        String by = taskCommand.substring(separatorIndex + DEADLINE_SEPARATOR.length());
        if (description.isBlank() || by.isBlank()) {
            throw new DowntownGurlException(DEADLINE_FORMAT_HINT);
        }
        LocalDateTime byDateTime;
        try {
            byDateTime = TaskDateTime.parse(by);
        } catch (DowntownGurlException e) {
            throw new DowntownGurlException(DEADLINE_FORMAT_HINT);
        }
        Deadline deadline = new Deadline(description, byDateTime);
        setRepeatFrequencyIfPresent(command, deadline);
        return deadline;
    }

    /**
     * Creates an event task from a command in this form: event DESCRIPTION /from START /to END.
     *
     * @param command Full user command.
     * @return New event task.
     * @throws DowntownGurlException If the description, start, or end is missing.
     */
    private static Event createEvent(String command) throws DowntownGurlException {
        String taskCommand = removeRepeatClause(command);
        String normalizedTaskCommand = taskCommand.toLowerCase(Locale.ROOT);
        int fromIndex = normalizedTaskCommand.indexOf(EVENT_FROM_SEPARATOR);
        int toIndex = normalizedTaskCommand.indexOf(EVENT_TO_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            throw new DowntownGurlException(EVENT_FORMAT_HINT);
        }
        String description = taskCommand.substring(getArgumentStartIndex(EVENT_COMMAND), fromIndex);
        assert fromIndex >= 6 : "Event start separator should appear after the command word.";
        assert toIndex > fromIndex : "Event end separator should appear after the start separator.";
        String from = taskCommand.substring(fromIndex + EVENT_FROM_SEPARATOR.length(), toIndex);
        String to = taskCommand.substring(toIndex + EVENT_TO_SEPARATOR.length());
        if (description.isBlank() || from.isBlank() || to.isBlank()) {
            throw new DowntownGurlException(EVENT_FORMAT_HINT);
        }
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime;
        try {
            fromDateTime = TaskDateTime.parse(from);
            toDateTime = TaskDateTime.parse(to);
        } catch (DowntownGurlException e) {
            throw new DowntownGurlException(EVENT_FORMAT_HINT);
        }
        Event event = new Event(description, fromDateTime, toDateTime);
        setRepeatFrequencyIfPresent(command, event);
        return event;
    }

    /**
     * Creates a command that makes an existing task repeat weekly.
     *
     * @param command Full user command.
     * @return Command for setting weekly recurrence.
     * @throws DowntownGurlException If the command is not in the expected format.
     */
    private static RepeatCommand createRepeatCommand(String command) throws DowntownGurlException {
        String normalizedCommand = command.toLowerCase(Locale.ROOT);
        int separatorIndex = normalizedCommand.indexOf(WEEKLY_REPEAT_SEPARATOR);
        int taskNumberStartIndex = getArgumentStartIndex(REPEAT_COMMAND);
        if (separatorIndex < taskNumberStartIndex
                || separatorIndex + WEEKLY_REPEAT_SEPARATOR.length() != command.length()) {
            throw new DowntownGurlException(REPEAT_FORMAT_HINT);
        }
        String taskNumber = command.substring(taskNumberStartIndex, separatorIndex);
        if (taskNumber.isBlank()) {
            throw new DowntownGurlException(REPEAT_FORMAT_HINT);
        }
        return new RepeatCommand(getTaskIndexFromCommand(taskNumber.trim(), 0, REPEAT_FORMAT_HINT),
                RecurrenceFrequency.WEEKLY);
    }

    /**
     * Removes the optional repeat clause from a dated task command.
     *
     * @param command Full task command.
     * @return Command without the repeat clause.
     */
    private static String removeRepeatClause(String command) {
        int repeatIndex = command.toLowerCase(Locale.ROOT).indexOf(REPEAT_SEPARATOR);
        if (repeatIndex == -1) {
            return command;
        }
        return command.substring(0, repeatIndex);
    }

    /**
     * Sets weekly recurrence on a task if the command contains a repeat clause.
     *
     * @param command Full task command.
     * @param task Task to update.
     * @throws DowntownGurlException If the repeat clause has an unsupported frequency.
     */
    private static void setRepeatFrequencyIfPresent(String command, Task task) throws DowntownGurlException {
        int repeatIndex = command.toLowerCase(Locale.ROOT).indexOf(REPEAT_SEPARATOR);
        if (repeatIndex == -1) {
            return;
        }
        String frequency = command.substring(repeatIndex + REPEAT_SEPARATOR.length());
        try {
            task.setRecurrenceFrequency(RecurrenceFrequency.parse(frequency));
        } catch (DowntownGurlException e) {
            throw new DowntownGurlException(getRepeatingTaskFormatHint(task));
        }
    }

    /**
     * Returns the expected command shape for a dated task with recurrence.
     *
     * @param task Dated task being made recurring.
     * @return Format hint for the task type.
     */
    private static String getRepeatingTaskFormatHint(Task task) {
        if (task instanceof Deadline) {
            return REPEATING_DEADLINE_FORMAT_HINT;
        }
        return REPEATING_EVENT_FORMAT_HINT;
    }

    /**
     * Finds the zero-based task index in a command containing a one-based task number.
     *
     * @param command Full user command.
     * @param taskNumberStartIndex Index where the task number starts.
     * @return Zero-based index of the task selected by the user.
     * @throws DowntownGurlException If the task number is not valid.
     */
    private static int getTaskIndexFromCommand(String command, int taskNumberStartIndex, String formatHint)
            throws DowntownGurlException {
        if (taskNumberStartIndex >= command.length()) {
            throw new DowntownGurlException(formatHint);
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(command.substring(taskNumberStartIndex).trim());
        } catch (NumberFormatException e) {
            throw new DowntownGurlException(formatHint);
        }
        if (taskNumber < 1) {
            throw new DowntownGurlException(formatHint);
        }
        return taskNumber - 1;
    }

    /**
     * Returns whether the command is exactly the command word or begins with its argument separator.
     *
     * @param command Full user command.
     * @param commandWord Command word to match.
     * @return true if the input matches the command word or its argument form.
     */
    private static boolean isCommandOrArgumentStart(String command, String commandWord) {
        return isCommand(command, commandWord) || startsWithCommandArgument(command, commandWord);
    }

    /**
     * Returns whether the command exactly matches the command word, ignoring letter case.
     *
     * @param command Full user command.
     * @param commandWord Command word to match.
     * @return true if the input matches the command word.
     */
    private static boolean isCommand(String command, String commandWord) {
        return command.equalsIgnoreCase(commandWord);
    }

    /**
     * Returns whether the command begins with the command word followed by an argument separator.
     *
     * @param command Full user command.
     * @param commandWord Command word to match.
     * @return true if the input starts with the command word and an argument separator.
     */
    private static boolean startsWithCommandArgument(String command, String commandWord) {
        return command.toLowerCase(Locale.ROOT).startsWith(commandWord + ARGUMENT_SEPARATOR);
    }

    /**
     * Extracts the argument portion after the command word and its separator.
     *
     * @param command Full user command.
     * @param commandWord Command word at the start of the input.
     * @return Argument text, or an empty string if there is no argument separator.
     */
    private static String getCommandArgument(String command, String commandWord) {
        if (isCommand(command, commandWord)) {
            return "";
        }
        assert startsWithCommandArgument(command, commandWord) : "Command should start with the expected word.";
        return command.substring(getArgumentStartIndex(commandWord));
    }

    /**
     * Returns the index where a command's argument starts.
     *
     * @param commandWord Command word before the argument.
     * @return Zero-based argument start index.
     */
    private static int getArgumentStartIndex(String commandWord) {
        return commandWord.length() + ARGUMENT_SEPARATOR.length();
    }
}
