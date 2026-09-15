package penny;

import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Parses input from user, identifying the command used and the arguments passed behind.
 */
public class Parser {
    /**
     * Processes a line of user input against the given task list.
     *
     * @param tasks tasklist to operate on.
     * @param input string entered by user.
     * @param isLoading boolean if loading in commands from previous sessions.
     * @return the response message and whether Penny should stop running.
     * @throws PennyException If the command arguments are wrongly formatted.
     */
    public static ParseResult handleInput(TaskList tasks, String input, boolean isLoading) throws PennyException {
        assert tasks != null : "tasks must not be null";
        if (input == null || input.isBlank()) {
            throw new PennyException("Sorry I didn't quite catch that.");
        }
        if (input.chars().anyMatch(c -> Character.isISOControl(c) && c != '\t')) {
            throw new PennyException("Enter one command on a single line, without control characters.");
        }

        String[] parts = input.strip().split("\\s+", 2);
        Command command = Command.parse(parts[0]);
        String arguments = parts.length > 1 ? parts[1].strip() : "";
        if ((command == Command.BYE || command == Command.LIST) && !arguments.isEmpty()) {
            throw new PennyException(parts[0] + " does not take any arguments.");
        }

        switch (command) {
            case BYE:
                return handleBye();
            case LIST:
                return handleList(tasks, isLoading);
            case DUE:
                return handleDue(tasks, arguments, isLoading);
            case FIND:
                return handleFind(tasks, arguments, isLoading);
            case MARK:
                return handleMark(tasks, arguments, isLoading);
            case UNMARK:
                return handleUnmark(tasks, arguments, isLoading);
            case DELETE:
                return handleDelete(tasks, arguments, isLoading);
            case TODO:
                return handleTodo(tasks, arguments, isLoading);
            case DEADLINE:
                return handleDeadline(tasks, arguments, isLoading);
            case EVENT:
                return handleEvent(tasks, arguments, isLoading);
            case UNKNOWN:
                throw new PennyException("I don't think I understand.");
            default:
                return new ParseResult("", false);
        }
    }

    private static ParseResult handleBye() {
        return new ParseResult("Bye! See you soon!", true);
    }

    private static ParseResult handleList(TaskList tasks, boolean isLoading) throws PennyException {
        if (isLoading) {
            return new ParseResult("", false);
        }

        if (tasks.isEmpty()) {
            throw new PennyException("There are no tasks on your list");
        }

        return new ParseResult(formatNumbered(tasks, task -> true), false);
    }

    private static ParseResult handleDue(TaskList tasks, String arguments, boolean isLoading) throws PennyException {
        if (isLoading) {
            return new ParseResult("", false);
        }

        if (arguments.isBlank()) {
            throw new PennyException("Due date cannot be empty");
        }

        LocalDateTime dueDate = DateTime.parse(arguments);

        if (tasks.isEmpty()) {
            throw new PennyException("There are no tasks on your list");
        }

        String dueTasks = formatNumbered(tasks, task -> task.isDueOn(dueDate));

        if (dueTasks.isEmpty()) {
            throw new PennyException("There are no tasks due on " + DateTime.format(dueDate));
        }

        return new ParseResult(dueTasks, false);
    }

    private static ParseResult handleFind(TaskList tasks, String arguments, boolean isLoading) throws PennyException {
        if (isLoading) {
            return new ParseResult("", false);
        }

        if (arguments.isEmpty()) {
            throw new PennyException("Find keyword cannot be empty");
        }

        if (tasks.isEmpty()) {
            throw new PennyException("There are no tasks on your list");
        }

        String matchingTasks = formatNumbered(tasks, task -> task.hasKeyword(arguments.trim()));

        if (matchingTasks.isEmpty()) {
            throw new PennyException("No matching tasks on your list");
        }

        return new ParseResult(matchingTasks, false);
    }

    private static ParseResult handleMark(TaskList tasks, String arguments, boolean isLoading) throws PennyException {
        if (!isInteger(arguments)) {
            throw new PennyException("Mark expects a number");
        }

        int markNumber = Integer.parseInt(arguments);
        if (markNumber < 1 || markNumber > tasks.size()) {
            throw new PennyException("Mark out of bounds");
        }
        int markIndex = markNumber - 1;

        Task markedTask = tasks.get(markIndex);
        markedTask.markAsDone();
        return new ParseResult(isLoading ? "" : "Marked as done: " + markedTask, false);
    }

    private static ParseResult handleUnmark(TaskList tasks, String arguments, boolean isLoading)
            throws PennyException {
        if (!isInteger(arguments)) {
            throw new PennyException("Unmark expects a number");
        }

        int unmarkNumber = Integer.parseInt(arguments);
        if (unmarkNumber < 1 || unmarkNumber > tasks.size()) {
            throw new PennyException("Unmark out of bounds");
        }
        int unmarkIndex = unmarkNumber - 1;

        Task unmarkedTask = tasks.get(unmarkIndex);
        unmarkedTask.unmarkAsDone();
        return new ParseResult(isLoading ? "" : "Marked as undone: " + unmarkedTask, false);
    }

    private static ParseResult handleDelete(TaskList tasks, String arguments, boolean isLoading)
            throws PennyException {
        if (!isInteger(arguments)) {
            throw new PennyException("Delete expects a number");
        }

        int deleteNumber = Integer.parseInt(arguments);
        if (deleteNumber < 1 || deleteNumber > tasks.size()) {
            throw new PennyException("Delete out of bounds");
        }
        int deleteIndex = deleteNumber - 1;

        Task deletedTask = tasks.remove(deleteIndex);
        return new ParseResult(isLoading ? "" : "Deleted: " + deletedTask, false);
    }

    private static ParseResult handleTodo(TaskList tasks, String arguments, boolean isLoading) throws PennyException {
        Task toDoTask = ToDoTask.create(arguments);
        ensureUnique(tasks, toDoTask);
        tasks.add(toDoTask);
        return new ParseResult(isLoading ? "" : "Added: " + toDoTask, false);
    }

    private static ParseResult handleDeadline(TaskList tasks, String arguments, boolean isLoading)
            throws PennyException {
        Task deadlineTask = DeadlineTask.create(arguments);
        ensureUnique(tasks, deadlineTask);
        tasks.add(deadlineTask);
        return new ParseResult(isLoading ? "" : "Added: " + deadlineTask, false);
    }

    private static ParseResult handleEvent(TaskList tasks, String arguments, boolean isLoading)
            throws PennyException {
        Task eventTask = EventTask.create(arguments);
        ensureUnique(tasks, eventTask);
        tasks.add(eventTask);
        return new ParseResult(isLoading ? "" : "Added: " + eventTask, false);
    }

    /**
     * Throws if a task with the same description as newTask is already on tasks, regardless of
     * newTask's type or any dates it carries.
     *
     * @param tasks tasklist to check against.
     * @param newTask task about to be added.
     * @throws PennyException if a task with the same description already exists.
     */
    private static void ensureUnique(TaskList tasks, Task newTask) throws PennyException {
        if (tasks.hasDescription(newTask.getDescription())) {
            throw new PennyException("This task is already on your list.");
        }
    }

    private static String formatNumbered(TaskList tasks, Predicate<Task> filter) {
        return IntStream.range(0, tasks.size())
                .filter(i -> filter.test(tasks.get(i)))
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns whether str can be parsed as an integer.
     *
     * @param str string to check.
     * @return true if str is a valid integer, false otherwise.
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
