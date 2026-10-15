package hu9o.parser;

import java.time.format.DateTimeParseException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu9o.errors.EventEndsBeforeStartException;
import hu9o.errors.Hu9oException;
import hu9o.errors.InvalidCommandException;
import hu9o.errors.InvalidDeadlineFormatException;
import hu9o.errors.InvalidEventFormatException;
import hu9o.errors.InvalidFindFormatException;
import hu9o.errors.InvalidTodoFormatException;
import hu9o.task.DeadlineTask;
import hu9o.task.EventTask;
import hu9o.task.Task;
import hu9o.task.TaskList;
import hu9o.task.ToDoTask;
import hu9o.ui.Ui;

/**
 * Turns text into actions for the rest of the program.
 *
 * <p>{@code Parser} handles two kinds of input: the pipe-delimited records read
 * from the save file ({@link #parseTask}), and the free-form commands typed by
 * the user ({@link #handleCommand}). It holds a {@link TaskList} to act on and a
 * {@link Ui} to report results through, so all console output stays in {@code Ui}.
 */
public class Parser {
    /** Matches a todo command and captures its description. */
    private static final Pattern TODO_PATTERN =
            Pattern.compile("^todo\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches a deadline command and captures its description and deadline. */
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^deadline\\s+(.+?)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches an event command and captures its description, start, and end times. */
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches a find command and captures its search keyword. */
    private static final Pattern FIND_PATTERN =
            Pattern.compile("^find\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    /** Every command name Hu9o understands. */
    private enum CommandType {
        TODO, DEADLINE, EVENT, LIST, FIND, MARK, UNMARK, DELETE, PROGRESS, BYE
    }

    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a parser that acts on the given task list and reports through the given UI.
     *
     * @param tasks the task list to read and modify.
     * @param ui    the UI used to display results and errors.
     */
    public Parser(TaskList tasks, Ui ui) {
        // Both collaborators are wired up by Hu9o, never supplied from outside;
        // a null here is a construction bug, not a user error.
        assert tasks != null : "Parser needs a task list to act on";
        assert ui != null : "Parser needs a UI to report through";
        this.tasks = tasks;
        this.ui = ui;
    }

    /**
     * Recreates one task from its pipe-delimited persistence record.
     *
     * <p>Example record: {@code T|X|read book|}. Storage decides what to do with
     * the returned task; the parser only rebuilds it.
     *
     * @param record the compressed task record read from the save file.
     * @return the reconstructed task, or {@code null} if the record's type is unknown.
     */
    public Task parseTask(String record) {
        String[] parts = record.split("\\|");
        Task task = switch (parts[0]) {
            case "T" -> new ToDoTask(parts[2]);
            case "D" -> new DeadlineTask(parts[2], parts[3]);
            case "E" -> new EventTask(parts[2], parts[3], parts[4]);
            default -> null;
        };
        if (task == null) {
            return null;
        }
        if (parts[1].equals("X")) {
            task.mark();
        }
        return task;
    }

    /**
     * Identifies the requested operation and delegates it to the matching
     * {@code handle...} method.
     *
     * <p>Each handler validates its own arguments by throwing a specific
     * {@link Hu9oException}; every user-facing error message is produced by the
     * single catch block below, which relies on all of those exceptions sharing
     * the {@code Hu9oException} base type.
     *
     * @param command the complete command entered by the user.
     */
    public void handleCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        // trim() + split on whitespace always yields at least one element:
        // an all-blank command trims to "", which split returns as [""].
        assert parts.length >= 1 : "splitting a trimmed string on whitespace should yield at least one element";
        ui.showBlockStart();
        try {
            switch (parseCommand(parts[0])) {
                case LIST -> handleList();
                case FIND -> handleFind(command);
                case MARK -> handleMark(parts);
                case UNMARK -> handleUnmark(parts);
                case TODO -> handleTodo(command);
                case DEADLINE -> handleDeadline(command);
                case EVENT -> handleEvent(command);
                case DELETE -> handleDelete(parts);
                case PROGRESS -> handleProgress();
                default -> throw new InvalidCommandException();
            }
        } catch (Hu9oException exception) {
            ui.showError(exception.getMessage());
        } catch (DateTimeParseException exception) {
            ui.showDateError();
        }
        ui.showBlockEnd();
    }

    /** Shows every task, in list order, for a {@code list} command. */
    private void handleList() {
        ui.showTaskList(tasks);
    }

    /**
     * Shows the tasks whose description contains the keyword of a {@code find} command.
     *
     * @param command the full command text, matched against {@link #FIND_PATTERN}.
     * @throws Hu9oException if the command carries no search keyword.
     */
    private void handleFind(String command) throws Hu9oException {
        Matcher matcher = requireMatch(FIND_PATTERN, command, InvalidFindFormatException::new);
        ui.showMatchingTasks(tasks.findTasks(matcher.group(1)));
    }

    /**
     * Marks the task addressed by a {@code mark} command as done.
     *
     * @param parts the whitespace-split command, expected as {@code ["mark", INDEX]}.
     * @throws Hu9oException if the command does not name an existing task.
     */
    private void handleMark(String[] parts) throws Hu9oException {
        Task task = tasks.getTask(parseIndex(parts), true);
        task.mark();
        ui.showTaskMarked(task);
    }

    /**
     * Marks the task addressed by an {@code unmark} command as not done.
     *
     * @param parts the whitespace-split command, expected as {@code ["unmark", INDEX]}.
     * @throws Hu9oException if the command does not name an existing task.
     */
    private void handleUnmark(String[] parts) throws Hu9oException {
        Task task = tasks.getTask(parseIndex(parts), false);
        task.unmark();
        ui.showTaskUnmarked(task);
    }

    /**
     * Adds the todo described by a {@code todo} command.
     *
     * @param command the full command text, matched against {@link #TODO_PATTERN}.
     * @throws Hu9oException if the command carries no description.
     */
    private void handleTodo(String command) throws Hu9oException {
        Matcher matcher = requireMatch(TODO_PATTERN, command, InvalidTodoFormatException::new);
        addAndConfirm(new ToDoTask(matcher.group(1)));
    }

    /**
     * Adds the deadline task described by a {@code deadline} command.
     *
     * @param command the full command text, matched against {@link #DEADLINE_PATTERN}.
     * @throws Hu9oException if the command is missing its description or {@code /by} date.
     */
    private void handleDeadline(String command) throws Hu9oException {
        Matcher matcher = requireMatch(DEADLINE_PATTERN, command, InvalidDeadlineFormatException::new);
        addAndConfirm(new DeadlineTask(matcher.group(1), matcher.group(2)));
    }

    /**
     * Adds the event task described by an {@code event} command.
     *
     * @param command the full command text, matched against {@link #EVENT_PATTERN}.
     * @throws Hu9oException if the command is missing its description, {@code /from}, or {@code /to},
     *                       or if the event would end before it starts.
     */
    private void handleEvent(String command) throws Hu9oException {
        Matcher matcher = requireMatch(EVENT_PATTERN, command, InvalidEventFormatException::new);
        EventTask event = new EventTask(matcher.group(1), matcher.group(2), matcher.group(3));
        if (event.endsBeforeStart()) {
            throw new EventEndsBeforeStartException();
        }
        addAndConfirm(event);
    }

    /**
     * Removes the task addressed by a {@code delete} command.
     *
     * @param parts the whitespace-split command, expected as {@code ["delete", INDEX]}.
     * @throws Hu9oException if the command does not name an existing task.
     */
    private void handleDelete(String[] parts) throws Hu9oException {
        ui.showTaskDeleted(tasks.deleteTask(parseIndex(parts)));
    }

    /** Shows a progress bar of marked-done tasks out of the total, for a {@code progress} command. */
    private void handleProgress() {
        ui.showProgress(tasks);
    }

    /** Adds a task to the list and asks the UI to confirm it. */
    private void addAndConfirm(Task task) {
        tasks.addTask(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Returns the successful match of {@code command} against {@code pattern}, or
     * throws if the command does not fit that format.
     *
     * @param pattern    the pattern the whole command must match.
     * @param command    the raw command text.
     * @param onMismatch supplies the exception to throw when the match fails.
     * @return the matcher, positioned so its captured argument groups can be read.
     * @throws Hu9oException the supplied exception when {@code command} does not match.
     */
    private Matcher requireMatch(Pattern pattern, String command,
            Supplier<? extends Hu9oException> onMismatch) throws Hu9oException {
        Matcher matcher = pattern.matcher(command);
        if (!matcher.matches()) {
            throw onMismatch.get();
        }
        return matcher;
    }

    /**
     * Converts the first word of a command into a {@link CommandType}.
     *
     * @param word the command name typed by the user.
     * @return the matching command type.
     * @throws InvalidCommandException if the word is not a known command.
     */
    private CommandType parseCommand(String word) throws InvalidCommandException {
        try {
            return CommandType.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidCommandException();
        }
    }

    /**
     * Reads the task number from a two-word command such as {@code mark 3}.
     *
     * @param parts the whitespace-split command.
     * @return the task number, or {@code 0} if the command is malformed (the
     *         task list treats 0 as out of range).
     */
    private int parseIndex(String[] parts) {
        boolean isValid = parts.length == 2 && parts[1].matches("[1-9][0-9]{0,8}");
        int index = isValid ? Integer.parseInt(parts[1]) : 0;
        // TaskList treats "0 or negative" as out of range; the regex above only
        // admits positive numbers, so a negative result would be a parsing bug.
        assert index >= 0 : "parseIndex should never return a negative number";
        return index;
    }
}
