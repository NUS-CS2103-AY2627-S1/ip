package carlo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import carlo.exception.CarloException;
import carlo.storage.Storage;
import carlo.task.CarloDateTime;
import carlo.task.Deadline;
import carlo.task.Event;
import carlo.task.Task;
import carlo.task.Todo;
import carlo.ui.Ui;



/**
 * Provides a command-line task list that stores, displays, and marks tasks,
 * saving them to disk so that they persist between runs.
 */
public class Carlo {
    private static final String FILE_PATH = "./data/carlo.txt";
    /** How many changes can be undone. Older snapshots are forgotten. */
    private static final int MAX_UNDO_STEPS = 20;

    private final Storage storage;
    private final List<Task> tasks;
    /**
     * Snapshots of the task list taken just before each change, newest
     * first. A {@link Deque} works as a stack here: {@code push} adds to
     * the front and {@code pop} takes the most recent snapshot back off.
     */
    private final Deque<Snapshot> undoHistory = new ArrayDeque<>();

    /**
     * A saved copy of the task list, as lines in the save-file format,
     * together with a description of the change that was about to happen.
     *
     * @param action what was about to be done, e.g. {@code deleting "essay"}
     * @param lines the task list in save-file format
     */
    private record Snapshot(String action, List<String> lines) {
    }

    /**
     * Creates a Carlo task manager and loads saved tasks from disk.
     */
    public Carlo() {
        this(FILE_PATH);
    }

    /**
     * Creates a Carlo task manager that saves to the given file. Tests use
     * this to work on a temporary file instead of the user's real tasks.
     *
     * @param filePath the path of the save file
     */
    public Carlo(String filePath) {
        storage = new Storage(filePath);
        tasks = storage.load();
        assert storage != null : "Storage must be initialised";
        assert tasks != null : "Loading tasks must return a list";
    }

    /**
     * Adds a todo task and saves the updated task list.
     *
     * @param description the description of the todo task
     * @throws CarloException if the description is empty
     */
    public void addTodo(String description) throws CarloException {
        if (description == null || description.isBlank()) {
            throw new CarloException("The todo description cannot be empty.");
        }

        rememberState("adding \"" + description + "\"");
        tasks.add(new Todo(description));
        storage.save(tasks);
    }

    /**
     * Adds a deadline task and saves the updated task list.
     *
     * @param description the description of the deadline
     * @param dueTime the deadline date or time
     * @throws CarloException if the description or due time is empty
     */
    public void addDeadline(String description, String dueTime)
            throws CarloException {
        if (description == null || description.isBlank()) {
            throw new CarloException(
                    "Hmm... I need to know what the deadline is for..."
            );
        }

        if (dueTime == null || dueTime.isBlank()) {
            throw new CarloException("When is this deadline due?");
        }

        Deadline deadline = new Deadline(description, dueTime);
        rememberState("adding \"" + description + "\"");
        tasks.add(deadline);
        storage.save(tasks);
    }

    /**
     * Adds an event task and saves the updated task list.
     *
     * @param description the description of the event
     * @param from the event start date or time
     * @param to the event end date or time
     * @throws CarloException if any input is empty
     */
    public void addEvent(String description, String from, String to)
            throws CarloException {
        if (description == null || description.isBlank()) {
            throw new CarloException(
                    "Hmm... I need to know what the event is..."
            );
        }

        if (from == null || from.isBlank()) {
            throw new CarloException("When does this event start?");
        }

        if (to == null || to.isBlank()) {
            throw new CarloException("When does this event end?");
        }

        Event event = new Event(description, from, to);
        rememberState("adding \"" + description + "\"");
        tasks.add(event);
        storage.save(tasks);
    }

    /**
     * Adds a task described by a text command, such as
     * {@code deadline essay /by 2026-10-15}, and saves the updated task list.
     * This lets the GUI's command bar reuse the same parsing as the
     * command-line version.
     *
     * @param command a {@code todo}, {@code deadline} or {@code event} command
     * @return the task that was added
     * @throws CarloException if the command is not an add command, or its details are invalid
     */
    public Task addTaskFromCommand(String command) throws CarloException {
        Task task = parseTaskCommand(command);
        rememberState("adding \"" + task.getDescription() + "\"");
        tasks.add(task);
        storage.save(tasks);
        return task;
    }

    /**
     * Replaces a task with a new version described by a text command,
     * keeping it in the same position and keeping whether it was done.
     *
     * @param index the zero-based index of the task to edit
     * @param command a {@code todo}, {@code deadline} or {@code event} command describing the new version
     * @return the edited task
     * @throws CarloException if the index is invalid, or the command is not valid
     */
    public Task editTask(int index, String command) throws CarloException {
        validateTaskIndex(index);
        Task oldTask = tasks.get(index);
        Task newTask = parseTaskCommand(command);
        if (oldTask.isDone()) {
            newTask.markAsDone();
        }

        rememberState("editing \"" + oldTask.getDescription() + "\"");
        tasks.set(index, newTask);
        storage.save(tasks);
        return newTask;
    }

    /**
     * Returns whether there is a change that can be undone.
     *
     * @return {@code true} if {@link #undo()} would do something
     */
    public boolean canUndo() {
        return !undoHistory.isEmpty();
    }

    /**
     * Undoes the most recent change by restoring the task list as it was
     * just before that change, then saves it.
     *
     * @return a description of the change that was undone, e.g. {@code deleting "essay"}
     * @throws CarloException if there is nothing to undo
     */
    public String undo() throws CarloException {
        if (undoHistory.isEmpty()) {
            throw new CarloException("There's nothing to undo!");
        }
        Snapshot snapshot = undoHistory.pop();

        tasks.clear();
        for (String line : snapshot.lines()) {
            // These lines were written by toFileFormat a moment ago, so they always parse.
            tasks.add(Storage.parseLine(line));
        }
        storage.save(tasks);
        return snapshot.action();
    }

    /**
     * Saves a snapshot of the task list so the change about to be made can
     * be undone. Call this after checking the change is valid, so that a
     * failed command does not leave a pointless snapshot behind.
     *
     * @param action a description of the change about to be made
     */
    private void rememberState(String action) {
        List<String> lines = tasks.stream().map(Task::toFileFormat).toList();
        undoHistory.push(new Snapshot(action, lines));
        if (undoHistory.size() > MAX_UNDO_STEPS) {
            undoHistory.removeLast();
        }
    }

    /**
     * Turns a {@code todo}, {@code deadline} or {@code event} command into a
     * task, without adding it to the list.
     */
    private static Task parseTaskCommand(String command) throws CarloException {
        String trimmed = command.trim();
        String keyword = trimmed.split("\\s+", 2)[0];

        return switch (keyword) {
            case "todo" -> {
                String description = trimmed.substring("todo".length()).trim();
                if (description.isEmpty()) {
                    throw new CarloException("hmm... there's nothing to do...");
                }
                yield new Todo(description);
            }
            case "deadline" -> createDeadline(trimmed);
            case "event" -> createEvent(trimmed);
            default -> throw new CarloException("Start with todo, deadline or event, like "
                    + "'deadline essay /by 2026-10-15' or 'event retreat /from 2026-10-20 /to 2026-10-22'.");
        };
    }

    /**
     * Marks a task as completed and saves the updated task list.
     *
     * @param index the zero-based index of the task
     * @throws CarloException if the index is invalid
     */
    public void markTask(int index) throws CarloException {
        validateTaskIndex(index);
        rememberState("marking \"" + tasks.get(index).getDescription() + "\" as done");
        tasks.get(index).markAsDone();
        storage.save(tasks);
    }

    /**
     * Marks a task as incomplete and saves the updated task list.
     *
     * @param index the zero-based index of the task
     * @throws CarloException if the index is invalid
     */
    public void unmarkTask(int index) throws CarloException {
        validateTaskIndex(index);
        rememberState("marking \"" + tasks.get(index).getDescription() + "\" as not done");
        tasks.get(index).markAsNotDone();
        storage.save(tasks);
    }

    /**
     * Deletes a task and saves the updated task list.
     *
     * @param index the zero-based index of the task
     * @throws CarloException if the index is invalid
     */
    public void deleteTask(int index) throws CarloException {
        validateTaskIndex(index);
        rememberState("deleting \"" + tasks.get(index).getDescription() + "\"");
        tasks.remove(index);
        storage.save(tasks);
    }

    /**
     * Checks whether a zero-based task index is valid.
     *
     * @param index the task index to check
     * @throws CarloException if the index does not identify a task
     */
    private void validateTaskIndex(int index) throws CarloException {
        if (index < 0 || index >= tasks.size()) {
            throw new CarloException("That task does not exist.");
        }
        assert index >= 0 && index < tasks.size()
                : "A validated task index must refer to an existing task";
    }

    /**
     * Returns the tasks currently stored by Carlo.
     *
     * @return the mutable list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Starts the Carlo command-line application.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Carlo carlo = new Carlo();
        Ui ui = new Ui();

        ui.showGreeting();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            ui.showLine();

            try {
                handleCommand(command, carlo, ui);
            } catch (CarloException e) {
                ui.showError(e.getMessage());
            }

            ui.showLine();

            if (command.equals("bye")) {
                break;
            }
        }
    }

    /**
     * Extracts and validates the one-based task number supplied in a command.
     *
     * @param command the complete command entered by the user
     * @param commandName the command prefix, such as {@code mark}
     * @param taskCount the number of tasks currently stored
     * @return the corresponding zero-based index in the task list
     * @throws CarloException if the task number is missing, not a whole number,
     *         or does not identify a stored task
     */
    static int getTaskIndex(String command, String commandName, int taskCount)
            throws CarloException {
        String numberText = command.substring(commandName.length()).trim();

        if (numberText.isEmpty()) {
            throw new CarloException(
                    "I don't know which task you are referring too... Could you specify the number? Thanku!"
            );
        }

        try {
            int taskIndex = Integer.parseInt(numberText) - 1;

            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new CarloException("I think that task number does not exist!!! haha");
            }

            return taskIndex;
        } catch (NumberFormatException e) {
            throw new CarloException("whole numbers only please!");
        }
    }

    /**
     * Prints the deadlines and events that fall on the date given in an
     * {@code on} command.
     *
     * @param command the complete on command
     * @param tasks the tasks to search through
     * @param ui the ui to print through
     * @throws CarloException if no date was given or it could not be understood
     */
    private static void printTasksOnDate(String command, List<Task> tasks, Ui ui) throws CarloException {
        String dateText = command.substring("on".length()).trim();

        if (dateText.isEmpty()) {
            throw new CarloException("which date do you mean? try something like 'on 2019-12-02'!");
        }

        LocalDate date;
        try {
            date = CarloDateTime.parseDate(dateText);
        } catch (DateTimeParseException e) {
            throw new CarloException("I couldn't understand that date! try yyyy-mm-dd, like 2019-12-02");
        }

        boolean[] matches = new boolean[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            matches[i] = occursOnDate(tasks.get(i), date);
        }

        ui.showTasksOnDate(date, tasks, matches);
    }

    /**
     * Returns whether the given task falls on the given date.
     *
     * @param task the task to check
     * @param date the date to check against
     * @return {@code true} if {@code task} is a deadline due on {@code date}
     *         or an event occurring on {@code date}
     */
    private static boolean occursOnDate(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getDueDateTime().isOnDate(date);
        } else if (task instanceof Event event) {
            return event.occursOn(date);
        }
        return false;
    }

    /**
     * Creates a deadline from a command using the {@code /by} separator.
     *
     * @param command the complete deadline command
     * @return the deadline described by the command
     * @throws CarloException if the deadline description is empty
     */
    private static Deadline createDeadline(String command) throws CarloException {
        String details = command.substring("deadline".length()).trim();
        int byIndex = details.indexOf("/by");

        String description;
        String by;

        if (byIndex == -1) {
            description = details.trim();
            by = "today";
        } else {
            description = details.substring(0, byIndex).trim();
            by = details.substring(byIndex + "/by".length()).trim();

            if (by.isEmpty()) {
                by = "today";
            }
        }

        if (description.isEmpty()) {
            throw new CarloException("hmm... I need to know what the deadline is for...");
        }

        return new Deadline(description, by);
    }

    /**
     * Creates an event from a command using the {@code /from} and {@code /to} separators.
     *
     * @param command the complete event command
     * @return the event described by the command
     * @throws CarloException if the event description is empty
     */
    private static Event createEvent(String command) throws CarloException {
        String details = command.substring("event".length()).trim();
        int fromIndex = details.indexOf("/from");

        if (fromIndex == -1) {
            if (details.isEmpty()) {
                throw new CarloException("hmm... I need to know what the event is...");
            }
            return new Event(details.trim(), "today", "today");
        }

        String description = details.substring(0, fromIndex).trim();
        String timeDetails = details.substring(fromIndex + "/from".length()).trim();
        int toIndex = timeDetails.indexOf("/to");

        String from;
        String to;

        if (toIndex == -1) {
            from = timeDetails.isEmpty() ? "today" : timeDetails;
            to = "today";
        } else {
            from = timeDetails.substring(0, toIndex).trim();
            to = timeDetails.substring(toIndex + "/to".length()).trim();

            if (from.isEmpty()) {
                from = "today";
            }
            if (to.isEmpty()) {
                to = "today";
            }
        }

        if (description.isEmpty()) {
            throw new CarloException("hmm... I need to know what the event is...");
        }

        return new Event(description, from, to);
    }

    /**
     * Displays tasks whose descriptions contain the keyword from a CLI command.
     *
     * @param command the complete find command
     * @param tasks the tasks to search through
     * @param ui the command-line user interface
     * @throws CarloException if no keyword was provided
     */
    private static void findTasks(
            String command,
            List<Task> tasks,
            Ui ui
    ) throws CarloException {
        String keyword = command.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new CarloException(
                    "what should I look for?\nTry 'find book'!"
            );
        }

        ui.showMatchingTasks(tasks, findMatches(tasks, keyword));
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword the keyword to search for
     * @return a list of matching tasks
     * @throws CarloException if the keyword is empty
     */
    public List<Task> findTasks(String keyword) throws CarloException {
        if (keyword == null || keyword.isBlank()) {
            throw new CarloException(
                    "What should I look for?\nTry searching for a keyword!"
            );
        }

        String lowerKeyword = keyword.toLowerCase();

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks that occur on a given date.
     *
     * @param date the date to search for
     * @return tasks occurring on the date
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> occursOnDate(task, date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns which of the given tasks have a description containing the
     * given keyword, using a case-insensitive substring match.
     *
     * @param tasks the tasks to search through
     * @param keyword the keyword to search for
     * @return a boolean for each task in {@code tasks}, in the same order,
     *         indicating whether that task's description contains {@code keyword}
     */
    static boolean[] findMatches(List<Task> tasks, String keyword) {
        boolean[] matches = new boolean[tasks.size()];
        String lowerKeyword = keyword.toLowerCase();

        for (int i = 0; i < tasks.size(); i++) {
            matches[i] = tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword);
        }

        return matches;
    }

    /**
     * Dispatches a command to its handler and displays the result.
     *
     * <p>The bye command displays a farewell; the caller handles
     * terminating the command loop.
     *
     * @param command the complete command entered by the user
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the command is unknown or its arguments are invalid
     */
    private static void handleCommand(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {

        if (command.equals("bye")) {
            ui.showGoodbye();
            return;
        }

        if (command.equals("list")) {
            ui.showTaskList(carlo.tasks);
            return;
        }

        if (command.equals("on") || command.startsWith("on ")) {
            printTasksOnDate(command, carlo.tasks, ui);
            return;
        }

        if (command.equals("find") || command.startsWith("find ")) {
            findTasks(command, carlo.tasks, ui);
            return;
        }

        if (command.equals("mark") || command.startsWith("mark ")) {
            handleMark(command, carlo, ui);
            return;
        }

        if (command.equals("unmark") || command.startsWith("unmark ")) {
            handleUnmark(command, carlo, ui);
            return;
        }

        if (command.equals("delete") || command.startsWith("delete ")) {
            handleDelete(command, carlo, ui);
            return;
        }

        if (command.equals("todo") || command.startsWith("todo ")) {
            handleTodo(command, carlo, ui);
            return;
        }

        if (command.equals("deadline") || command.startsWith("deadline ")) {
            handleDeadline(command, carlo, ui);
            return;
        }

        if (command.equals("event") || command.startsWith("event ")) {
            handleEvent(command, carlo, ui);
            return;
        }

        throw new CarloException("I'm not too sure what you mean actually...");
    }

    /**
     * Marks the specified task as completed and displays confirmation.
     *
     * @param command the complete mark command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the task number is missing, not a whole number,
     *         or does not identify a stored task
     */
    private static void handleMark(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {

        int taskIndex = getTaskIndex(
                command,
                "mark",
                carlo.tasks.size()
        );

        carlo.markTask(taskIndex);
        ui.showTaskMarked(carlo.tasks.get(taskIndex));
    }

    /**
     * Marks the specified task as incomplete and displays confirmation.
     *
     * @param command the complete unmark command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the task number is missing, not a whole number,
     *         or does not identify a stored task
     */
    private static void handleUnmark(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {
        int taskIndex = getTaskIndex(
                command,
                "unmark",
                carlo.tasks.size()
        );

        carlo.unmarkTask(taskIndex);
        ui.showTaskUnmarked(carlo.tasks.get(taskIndex));
    }

    /**
     * Deletes the specified task and displays the remaining task count.
     *
     * @param command the complete delete command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the task number is missing, not a whole number,
     *         or does not identify a stored task
     */
    private static void handleDelete(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {
        int taskIndex = getTaskIndex(
                command,
                "delete",
                carlo.tasks.size()
        );

        Task deletedTask = carlo.tasks.get(taskIndex);
        carlo.deleteTask(taskIndex);
        ui.showTaskDeleted(deletedTask, carlo.tasks.size());
    }

    /**
     * Parses a todo command, adds the task, and displays confirmation.
     *
     * @param command the complete todo command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the task description is empty
     */
    private static void handleTodo(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {
        String description = command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new CarloException("hmm... there's nothing to do...");
        }

        carlo.addTodo(description);
        ui.showTaskAdded(
                carlo.tasks.getLast(),
                carlo.tasks.size()
        );
    }

    /**
     * Parses a deadline command, adds the task, saves the task list,
     * and displays confirmation.
     *
     * @param command the complete deadline command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the deadline description is empty
     */
    private static void handleDeadline(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {
        Deadline deadline = createDeadline(command);

        carlo.tasks.add(deadline);
        carlo.storage.save(carlo.tasks);

        ui.showTaskAdded(
                carlo.tasks.getLast(),
                carlo.tasks.size()
        );
    }

    /**
     * Parses an event command, adds the task, saves the task list,
     * and displays confirmation.
     *
     * @param command the complete event command
     * @param carlo the task manager
     * @param ui the command-line user interface
     * @throws CarloException if the event description is empty
     */
    private static void handleEvent(
            String command,
            Carlo carlo,
            Ui ui
    ) throws CarloException {
        Event event = createEvent(command);

        carlo.tasks.add(event);
        carlo.storage.save(carlo.tasks);

        ui.showTaskAdded(
                carlo.tasks.getLast(),
                carlo.tasks.size()
        );
    }
}
