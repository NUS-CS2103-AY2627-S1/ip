package bob;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Owns the tasks currently managed by the application. */
public class TaskList {
    private static final int TODO_PREFIX_LENGTH = 4;
    private static final int FIND_PREFIX_LENGTH = 4;
    private static final int EDIT_PREFIX_LENGTH = 4;
    private static final int DELETE_PREFIX_LENGTH = 6;
    private static final int MARK_PREFIX_LENGTH = 4;
    private static final int UNMARK_PREFIX_LENGTH = 6;
    private static final int DEADLINE_PREFIX_LENGTH = 8;
    private static final int EVENT_PREFIX_LENGTH = 5;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks to copy.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task collection must exist";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the specified index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list must not contain null tasks");
        }
        for (Task existing : tasks) {
            if (existing.toStorageString().equals(task.toStorageString())) {
                throw new IllegalArgumentException("A task with those details already exists");
            }
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Validates a one-based task number.
     *
     * @param taskNumber One-based task number.
     * @throws BobException If the task number is invalid.
     */
    public void checkTaskNumber(int taskNumber) throws BobException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BobException("That task number does not exist.");
        }
        assert taskNumber - 1 >= 0 && taskNumber - 1 < tasks.size()
                : "A validated task number must map to a list index";
    }

    /**
     * Returns a copy suitable for persistence.
     *
     * @return Copy of the tasks.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the tasks for display in their current order.
     *
     * @return Tasks in their current order.
     */
    public List<Task> listTasks() {
        return asList();
    }

    /** Returns tasks whose descriptions contain the supplied keyword. */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getItem().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Removes a one-based task and returns it.
     *
     * @param taskNumber One-based task number.
     * @return Removed task.
     * @throws BobException If the task number is invalid.
     */
    public Task deleteTask(int taskNumber) throws BobException {
        checkTaskNumber(taskNumber);
        return remove(taskNumber - 1);
    }

    /**
     * Marks a one-based task as done and returns it.
     *
     * @param taskNumber One-based task number.
     * @return Marked task.
     * @throws BobException If the task number is invalid.
     */
    public Task markTask(int taskNumber) throws BobException {
        checkTaskNumber(taskNumber);
        Task task = get(taskNumber - 1);
        task.markDone();
        return task;
    }

    /**
     * Marks a one-based task as not done and returns it.
     *
     * @param taskNumber One-based task number.
     * @return Unmarked task.
     * @throws BobException If the task number is invalid.
     */
    public Task unmarkTask(int taskNumber) throws BobException {
        checkTaskNumber(taskNumber);
        Task task = get(taskNumber - 1);
        task.markUndone();
        return task;
    }

    /**
     * Replaces a task's details while retaining its task type and completion state.
     *
     * @param taskNumber One-based task number.
     * @param input New task details in the format for the existing task type.
     * @return Updated task.
     * @throws BobException If the task number or new details are invalid.
     */
    public Task editTask(int taskNumber, String input) throws BobException {
        checkTaskNumber(taskNumber);
        if (input == null || input.trim().isEmpty()) {
            throw new BobException("Please provide the new task details.");
        }

        Task original = get(taskNumber - 1);
        Task updated;
        if (original instanceof Deadline) {
            updated = createDeadline(input);
        } else if (original instanceof Event) {
            updated = createEvent(input);
        } else {
            String description = input.trim();
            if (description.isEmpty()) {
                throw new BobException("The description of a todo cannot be empty.");
            }
            updated = new Task(description);
        }

        if (original.isDone()) {
            updated.markDone();
        }
        tasks.set(taskNumber - 1, updated);
        return updated;
    }

    /**
     * Creates and adds a todo task.
     *
     * @param description Todo description.
     * @return Created task.
     * @throws BobException If the description is empty.
     */
    public Task addTodo(String description) throws BobException {
        if (description == null || description.trim().isEmpty()) {
            throw new BobException("The description of a todo cannot be empty.");
        }
        Task task = new Task(description.trim());
        addUnique(task);
        return task;
    }

    /**
     * Creates and adds a deadline from its description and date input.
     *
     * @param input Deadline description and date.
     * @return Created task.
     * @throws BobException If the input is invalid.
     */
    public Task addDeadline(String input) throws BobException {
        String[] parts = validateDeadlineInput(input);
        String description = parts[0];
        String end = parts[1];
        try {
            Task task = new Deadline(description, LocalDate.parse(end));
            addUnique(task);
            return task;
        } catch (DateTimeParseException e) {
            throw new BobException("Please enter the date as yyyy-MM-dd, "
                    + "for example 2019-10-15.");
        }
    }

    /**
     * Creates and adds an event from its description and date inputs.
     *
     * @param input Event description and dates.
     * @return Created task.
     * @throws BobException If the input is invalid.
     */
    public Task addEvent(String input) throws BobException {
        String[] parts = validateEventInput(input);
        String description = parts[0];
        String from = parts[1];
        String to = parts[2];
        try {
            Task task = new Event(description, LocalDate.parse(from), LocalDate.parse(to));
            addUnique(task);
            return task;
        } catch (DateTimeParseException e) {
            throw new BobException("Please enter dates as yyyy-MM-dd, "
                    + "for example 2019-10-15.");
        }
    }

    /**
     * Executes a non-exit command against this task list.
     *
     * @param command Complete command entered by the user.
     * @param type Parsed command type.
     * @param storage Storage used to persist changes.
     * @throws BobException If the command or task number is invalid.
     */
    public String execute(String command, CommandType type, Storage storage)
            throws BobException {
        if (command == null || command.trim().isEmpty()) {
            throw new BobException("Please enter a command.");
        }
        String normalizedCommand = command.trim();
        if (type == null || storage == null) {
            throw new BobException("The command could not be processed.");
        }
        switch (type) {
            case LIST:
                return formatTasks("Here are the tasks in your list:", listTasks());
            case FIND:
                return findAndFormatTasks(normalizedCommand);
            case EDIT:
                return editAndFormatTask(normalizedCommand, storage);
            case DELETE:
                return deleteAndFormatTask(normalizedCommand, storage);
            case MARK:
                return markAndFormatTask(normalizedCommand, storage);
            case UNMARK:
                return unmarkAndFormatTask(normalizedCommand, storage);
            case TODO:
                return showAdded(addTodo(normalizedCommand.substring(TODO_PREFIX_LENGTH).trim()), storage);
            case DEADLINE:
                return showAdded(addDeadline(normalizedCommand.substring(DEADLINE_PREFIX_LENGTH).trim()), storage);
            case EVENT:
                return showAdded(addEvent(normalizedCommand.substring(EVENT_PREFIX_LENGTH).trim()), storage);
            default:
                throw new BobException("I don't understand that command.");
        }
    }

    private String[] validateEventInput(String input) throws BobException {
        if (input == null) {
            throw new BobException("An event needs a description, /from, and /to.");
        }
        if (countOccurrences(input, " /from ") != 1 || countOccurrences(input, " /to ") != 1) {
            throw new BobException("An event needs exactly one /from and one /to.");
        }
        String[] parts = input.split("\\s+/from\\s+|\\s+/to\\s+", 3);
        if (parts.length < 3) {
            throw new BobException("An event needs a description, /from, and /to.");
        }
        String description = parts[0].trim();
        String from = parts[1].trim();
        String to = parts[2].trim();
        if (description.isEmpty()) {
            throw new BobException("The event description cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new BobException("The event start time cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new BobException("The event end time cannot be empty.");
        }
        return new String[] {description, from, to};
    }

    private String[] validateDeadlineInput(String input) throws BobException {
        if (input == null || countOccurrences(input, " /by ") != 1) {
            throw new BobException("A deadline needs exactly one /by date.");
        }
        String[] parts = input.split("\\s+/by\\s+", 2);
        if (parts.length < 2) {
            throw new BobException("A deadline needs a description and a /by date.");
        }
        String description = parts[0].trim();
        String end = parts[1].trim();
        if (description.isEmpty()) {
            throw new BobException("The deadline description cannot be empty.");
        }
        if (end.isEmpty()) {
            throw new BobException("The deadline date cannot be empty.");
        }
        return new String[] {description, end};
    }

    private String findAndFormatTasks(String command) throws BobException {
        String keyword = command.substring(FIND_PREFIX_LENGTH).trim();
        if (keyword.isEmpty()) {
            throw new BobException("Please provide a keyword to find.");
        }
        return formatTasks("Here are the matching tasks in your list:", findTasks(keyword));
    }

    private String formatTasks(String heading, List<Task> tasksToFormat) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasksToFormat.size(); i++) {
            response.append(System.lineSeparator()).append(i + 1).append(".").append(tasksToFormat.get(i));
        }
        return response.toString();
    }

    private String deleteAndFormatTask(String command, Storage storage) throws BobException {
        Task deleted = deleteTask(parseTaskNumber(command, DELETE_PREFIX_LENGTH));
        storage.save(asList());
        return "Noted. I've removed this task:" + System.lineSeparator()
                + "  " + deleted + System.lineSeparator()
                + "Now you have " + size() + " tasks in the list.";
    }

    private Task createDeadline(String input) throws BobException {
        String[] parts = validateDeadlineInput(input);
        try {
            return new Deadline(parts[0], LocalDate.parse(parts[1]));
        } catch (DateTimeParseException e) {
            throw new BobException("Please enter the date as yyyy-MM-dd, "
                    + "for example 2019-10-15.");
        }
    }

    private Task createEvent(String input) throws BobException {
        String[] parts = validateEventInput(input);
        try {
            return new Event(parts[0], LocalDate.parse(parts[1]), LocalDate.parse(parts[2]));
        } catch (DateTimeParseException e) {
            throw new BobException("Please enter dates as yyyy-MM-dd, "
                    + "for example 2019-10-15.");
        }
    }

    private String editAndFormatTask(String command, Storage storage) throws BobException {
        String[] parts = command.substring(EDIT_PREFIX_LENGTH).trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BobException("Usage: edit TASK_NUMBER NEW_DETAILS");
        }
        Task updated = editTask(parseTaskNumber(parts[0], 0), parts[1].trim());
        storage.save(asList());
        return "Got it. I've updated this task:" + System.lineSeparator()
                + "  " + updated;
    }

    private String markAndFormatTask(String command, Storage storage) throws BobException {
        Task marked = markTask(parseTaskNumber(command, MARK_PREFIX_LENGTH));
        storage.save(asList());
        return "Nice! I've marked this task as done:" + System.lineSeparator()
                + "  " + marked;
    }

    private String unmarkAndFormatTask(String command, Storage storage) throws BobException {
        Task unmarked = unmarkTask(parseTaskNumber(command, UNMARK_PREFIX_LENGTH));
        storage.save(asList());
        return "OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "  " + unmarked;
    }

    /**
     * Formats a newly added task and persists the task list.
     *
     * @param task Newly added task.
     * @param storage Storage used to persist changes.
     * @throws BobException If the task list cannot be saved.
     */
    private String showAdded(Task task, Storage storage) throws BobException {
        storage.save(asList());
        return "Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + size() + " tasks in the list.";
    }

    private void addUnique(Task task) throws BobException {
        try {
            add(task);
        } catch (IllegalArgumentException e) {
            throw new BobException("A task with those details already exists.");
        }
    }

    private int parseTaskNumber(String input, int prefixLength) throws BobException {
        String value = input.substring(prefixLength).trim();
        if (!value.matches("\\d+")) {
            throw new BobException("Please enter a valid task number.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BobException("Please enter a valid task number.");
        }
    }

    private int countOccurrences(String input, String marker) {
        int count = 0;
        int index = 0;
        while ((index = input.indexOf(marker, index)) >= 0) {
            count++;
            index += marker.length();
        }
        return count;
    }
}
