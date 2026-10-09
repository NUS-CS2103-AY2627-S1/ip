package sumo.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sumo.command.Command;
import sumo.command.ExitCommand;
import sumo.command.FindCommand;
import sumo.command.ListCommand;
import sumo.command.OnCommand;
import sumo.command.SortCommand;
import sumo.exception.SumoException;
import sumo.storage.Storage;
import sumo.task.Deadline;
import sumo.task.Event;
import sumo.task.Task;
import sumo.task.TaskList;
import sumo.task.Todo;
import sumo.ui.Ui;

/** Interprets user input and converts it into structured command data. */
public class Parser {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DAY_MONTH_DATE = DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern PARAMETER = Pattern.compile("(?<!\\S)/\\S+");

    /** Identifies the action requested by a parsed command. */
    public enum CommandType {
        /** Marks a task as complete. */
        MARK,
        /** Marks a task as incomplete. */
        UNMARK,
        /** Deletes a task. */
        DELETE,
        /** Adds a task. */
        ADD
    }

    /** Creates a parser for Sumo commands. */
    public Parser() {
    }

    /** Holds the values Sumo needs to carry out one parsed command. */
    public static class ParsedCommand extends Command {
        private final CommandType type;
        private final Task task;
        private final int taskIndex;

        private ParsedCommand(CommandType type, Task task, int taskIndex) {
            assert type != null : "A parsed command must have a command type.";
            assert (type == CommandType.ADD) == (task != null)
                    : "Only add commands carry a task.";
            assert type == CommandType.ADD ? taskIndex == -1 : taskIndex >= 0
                    : "Parsed command index does not match its command type.";
            this.type = type;
            this.task = task;
            this.taskIndex = taskIndex;
        }

        /**
         * Returns the action represented by this command.
         *
         * @return action represented by this command.
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the task to add.
         *
         * @return task to add, or {@code null} for commands that target an existing task.
         */
        public Task getTask() {
            return task;
        }

        /**
         * Returns the zero-based target index.
         *
         * @return zero-based target index, or {@code -1} when adding a task.
         */
        public int getTaskIndex() {
            return taskIndex;
        }

        /** Executes a mutation that has not yet been extracted into its own command class. */
        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SumoException {
            switch (type) {
                case MARK:
                    updateTaskStatus(tasks, ui, storage, true);
                    break;
                case UNMARK:
                    updateTaskStatus(tasks, ui, storage, false);
                    break;
                case DELETE:
                    deleteTask(tasks, ui, storage);
                    break;
                case ADD:
                    addTask(tasks, ui, storage);
                    break;
                default:
                    throw new IllegalStateException("Unsupported task command.");
            }
        }

        /** Updates a task's status and restores it if saving fails. */
        private void updateTaskStatus(TaskList tasks, Ui ui, Storage storage, boolean shouldMarkDone)
                throws IOException {
            Task selectedTask = tasks.get(taskIndex);
            boolean wasDone = selectedTask.isDone();
            tasks.setDone(taskIndex, shouldMarkDone);
            try {
                storage.save(tasks.getTasks());
            } catch (IOException exception) {
                tasks.setDone(taskIndex, wasDone);
                throw exception;
            }
            if (shouldMarkDone) {
                ui.showTaskMarked(selectedTask);
            } else {
                ui.showTaskUnmarked(selectedTask);
            }
        }

        /** Deletes a task and restores it at its original position if saving fails. */
        private void deleteTask(TaskList tasks, Ui ui, Storage storage) throws IOException {
            Task removedTask = tasks.delete(taskIndex);
            try {
                storage.save(tasks.getTasks());
            } catch (IOException exception) {
                tasks.insert(taskIndex, removedTask);
                throw exception;
            }
            ui.showTaskDeleted(removedTask, tasks.size());
        }

        /** Adds a task and removes it again if saving fails. */
        private void addTask(TaskList tasks, Ui ui, Storage storage) throws IOException, SumoException {
            tasks.add(task);
            try {
                storage.save(tasks.getTasks());
            } catch (IOException exception) {
                tasks.delete(tasks.size() - 1);
                throw exception;
            }
            ui.showTaskAdded(task, tasks.size());
        }
    }

    /**
     * Parses and validates one user command.
     *
     * @param command the user's input.
     * @param taskCount the current number of tasks, used to validate task numbers.
     * @return structured command data.
     * @throws SumoException if the command or any argument is invalid.
     */
    public Command parse(String command, int taskCount) throws SumoException {
        assert taskCount >= 0 : "The task count cannot be negative.";
        ensureNotBlank(command, "Please enter a command.");
        if (command.codePoints().anyMatch(character -> (Character.isISOControl(character) && character != '\t')
                || character == '\u2028' || character == '\u2029')) {
            throw new SumoException("Commands must be a single line without control characters.");
        }
        command = command.replaceAll("\\h+", " ").strip();
        ensureNotBlank(command, "Please enter a command.");
        if (isCommand(command, "bye")) {
            ensureNoArgument(command, "bye");
            return new ExitCommand();
        }
        if (isCommand(command, "list")) {
            ensureNoArgument(command, "list");
            return new ListCommand();
        }
        if (isCommand(command, "sort")) {
            ensureNoArgument(command, "sort");
            return new SortCommand();
        }
        return parseCommandWithArgument(command, taskCount);
    }

    /** Parses commands that either query or mutate tasks through an argument. */
    private Command parseCommandWithArgument(String command, int taskCount) throws SumoException {
        if (isCommand(command, "find")) {
            String keyword = getCommandArgument(command, "find");
            ensureNotBlank(keyword, "Please add a keyword after 'find'.");
            return new FindCommand(keyword);
        }
        if (isCommand(command, "on")) {
            return parseOn(getCommandArgument(command, "on"));
        }
        return parseIndexedOrAddCommand(command, taskCount);
    }

    /** Parses commands that target an existing task or add a new task. */
    private Command parseIndexedOrAddCommand(String command, int taskCount) throws SumoException {
        if (isCommand(command, "mark")) {
            return parseIndexedCommand(CommandType.MARK, getCommandArgument(command, "mark"), taskCount);
        }
        if (isCommand(command, "unmark")) {
            return parseIndexedCommand(CommandType.UNMARK, getCommandArgument(command, "unmark"), taskCount);
        }
        if (isCommand(command, "delete")) {
            return parseIndexedCommand(CommandType.DELETE, getCommandArgument(command, "delete"), taskCount);
        }
        return parseTaskCreationCommand(command);
    }

    /** Parses commands that create todo, deadline, or event tasks. */
    private Command parseTaskCreationCommand(String command) throws SumoException {
        if (isCommand(command, "todo")) {
            String description = getCommandArgument(command, "todo");
            ensureNotBlank(description, "Please add a description after 'todo'.");
            ensurePersistable(description);
            return addCommand(new Todo(description));
        }
        if (isCommand(command, "deadline")) {
            return parseDeadline(getCommandArgument(command, "deadline"));
        }
        if (isCommand(command, "event")) {
            return parseEvent(getCommandArgument(command, "event"));
        }
        throw new SumoException("I do not recognise that command. "
                + "Try todo, deadline, event, list, sort, find, on, mark, unmark, or delete.");
    }

    /** Returns whether input is the command itself or starts with its argument separator. */
    private boolean isCommand(String input, String command) {
        return command.equals(input) || input.startsWith(command + " ");
    }

    /** Returns the trimmed argument portion after a command keyword. */
    private String getCommandArgument(String input, String command) {
        return input.substring(command.length()).trim();
    }

    /** Rejects accidental arguments to commands that take none. */
    private void ensureNoArgument(String input, String command) throws SumoException {
        if (!getCommandArgument(input, command).isEmpty()) {
            throw new SumoException("Use: " + command + ". This command takes no arguments.");
        }
    }

    /** Parses a deadline command and preserves whether its date included a time. */
    private Command parseDeadline(String taskText) throws SumoException {
        String[] parts = splitCommand(taskText, "Use: deadline <description> /by <date>.", "/by");
        ParsedDateTime deadline = parseDateTime(parts[1], "Use: deadline <description> /by <date> [HHmm].");
        return addCommand(new Deadline(parts[0], deadline.value, deadline.hasTime));
    }

    /** Parses an event command and preserves each endpoint's input precision. */
    private Command parseEvent(String taskText) throws SumoException {
        String splitMessage = "Use: event <description> /from <start> /to <end>.";
        String[] parts = splitCommand(taskText, splitMessage, "/from", "/to");
        String message = "Use: event <description> /from <date> [HHmm] /to <date> [HHmm].";
        ParsedDateTime from = parseDateTime(parts[1], message);
        ParsedDateTime to = parseDateTime(parts[2], message);
        if (!from.value.isBefore(to.value)) {
            throw new SumoException("An event's end must be after its start.");
        }
        return addCommand(new Event(parts[0], from.value, to.value, from.hasTime, to.hasTime));
    }

    /** Parses a date-filter command using either supported date format. */
    private Command parseOn(String dateText) throws SumoException {
        if (dateText.isBlank()) {
            throw new SumoException("Use: on <date>.");
        }
        try {
            return new OnCommand(parseDate(dateText));
        } catch (DateTimeParseException exception) {
            throw new SumoException("Use: on <date>. Dates must use yyyy-MM-dd or d/M/yyyy.");
        }
    }

    /** Converts and validates a user-facing one-based task number. */
    private ParsedCommand parseIndexedCommand(CommandType type, String text, int taskCount) throws SumoException {
        assert type != null && type != CommandType.ADD
                : "An indexed command must update an existing task.";
        assert taskCount >= 0 : "The task count cannot be negative.";
        if (text.isBlank()) {
            throw new SumoException("Please specify the number of the task to update.");
        }
        if (!text.matches("[0-9]+")) {
            throw new SumoException("Task numbers must be whole numbers.");
        }
        try {
            int number = Integer.parseInt(text);
            if (number < 1 || number > taskCount) {
                throw new SumoException("That task number is not in your list.");
            }
            int index = number - 1;
            assert index >= 0 && index < taskCount : "Validated task number must be in the task list.";
            return new ParsedCommand(type, null, index);
        } catch (NumberFormatException exception) {
            throw new SumoException("That task number is not in your list.");
        }
    }

    private ParsedCommand addCommand(Task task) {
        assert task != null : "An add command must carry a task.";
        return new ParsedCommand(CommandType.ADD, task, -1);
    }

    /** Splits a command by ordered markers and validates all resulting fields. */
    private String[] splitCommand(String text, String message, String... markers) throws SumoException {
        String[] parts = new String[markers.length + 1];
        Matcher parameters = PARAMETER.matcher(text);
        int partStart = 0;
        for (int i = 0; i < markers.length; i++) {
            if (!parameters.find() || !parameters.group().equals(markers[i])) {
                throw new SumoException(message);
            }
            parts[i] = text.substring(partStart, parameters.start()).trim();
            partStart = parameters.end();
        }
        if (parameters.find()) {
            throw new SumoException(message + " Specify each parameter exactly once, in the shown order.");
        }
        parts[parts.length - 1] = text.substring(partStart).trim();

        for (String part : parts) {
            ensureNotBlank(part, message);
        }
        for (String part : parts) {
            ensurePersistable(part);
        }
        return parts;
    }

    /** Parses a date with an optional 24-hour time. */
    private ParsedDateTime parseDateTime(String text, String message) throws SumoException {
        try {
            String[] parts = text.trim().split("\\s+");
            if (parts.length == 1) {
                return new ParsedDateTime(parseDate(parts[0]).atStartOfDay(), false);
            }
            if (parts.length == 2) {
                return new ParsedDateTime(
                        LocalDateTime.of(parseDate(parts[0]), LocalTime.parse(parts[1], TIME)), true);
            }
        } catch (DateTimeParseException exception) {
            // Use the consistent, user-friendly error below.
        }
        throw new SumoException(message + " Dates must use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.");
    }

    /** Parses either of the date formats accepted by Sumo. */
    private LocalDate parseDate(String text) throws DateTimeParseException {
        if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}|[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}")) {
            throw new DateTimeParseException("Unsupported date format.", text, 0);
        }
        LocalDate date;
        try {
            date = LocalDate.parse(text, ISO_DATE);
        } catch (DateTimeParseException exception) {
            date = LocalDate.parse(text, DAY_MONTH_DATE);
        }
        if (date.getYear() < 1) {
            throw new DateTimeParseException("Years must be between 0001 and 9999.", text, 0);
        }
        return date;
    }

    private void ensureNotBlank(String text, String message) throws SumoException {
        if (text == null || text.isBlank()) {
            throw new SumoException(message);
        }
    }

    private void ensurePersistable(String text) throws SumoException {
        if (text.contains("|")) {
            throw new SumoException("Task text cannot contain '|'.");
        }
    }

    /** Retains the date value and whether the user supplied a time. */
    private static class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        private ParsedDateTime(LocalDateTime value, boolean hasTime) {
            this.value = value;
            this.hasTime = hasTime;
        }
    }
}
