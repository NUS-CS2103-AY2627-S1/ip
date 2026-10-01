package bo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import bo.command.CommandException;
import bo.storage.Storage;
import bo.task.Deadline;
import bo.task.Task;
import bo.task.TaskList;
import bo.ui.Ui;

/**
 * Stores and displays the tasks entered during one run of Bo.
 */
public class Tracker {
    private static final String UPCOMING_DEADLINES_HEADING = " Upcoming deadlines:";
    private static final String DEADLINES_DUE_HEADING_PREFIX = " Deadlines due on ";

    private final TaskList taskList;
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates an empty tracker backed by Bo's default save file.
     *
     * @param ui The user interface used to display task messages.
     */
    public Tracker(Ui ui) {
        this(ui, new Storage());
    }

    /**
     * Creates a tracker backed by a specific storage. Intended mainly for
     * tests, where storage pointed at a temporary file should be used
     * instead of Bo's real save file.
     *
     * @param ui The user interface used to display task messages.
     * @param storage The storage used to load and save tasks.
     */
    public Tracker(Ui ui, Storage storage) {
        this.storage = storage;
        this.ui = ui;
        taskList = loadTaskList();
    }

    /**
     * Adds a task to the tracker.
     *
     * @param task The task object to store.
     * @throws CommandException If the task list cannot be saved.
     */
    public void addTask(Task task) throws CommandException {
        taskList.add(task);
        try {
            saveTasks();
        } catch (CommandException exception) {
            taskList.remove(taskList.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, taskList.size());
    }

    /**
     * Prints all saved tasks as a numbered list.
     */
    public void printTasks() {
        ui.showTaskList(taskList.asList());
    }

    /**
     * Displays incomplete deadlines that are due today or later, ordered by date.
     *
     * @param currentDate The date used to decide which deadlines are upcoming.
     */
    public void printUpcomingDeadlines(LocalDate currentDate) {
        List<Integer> taskIndexes = getDeadlineIndexes();
        taskIndexes.removeIf(index -> {
            Deadline deadline = (Deadline) taskList.get(index);
            return deadline.isDone() || deadline.getDueDate().isBefore(currentDate);
        });
        printDeadlineIndexes(taskIndexes, UPCOMING_DEADLINES_HEADING);
    }

    /**
     * Displays all deadlines due on one specified date.
     *
     * @param dueDate The date to match.
     */
    public void printDeadlinesDueOn(LocalDate dueDate) {
        List<Integer> taskIndexes = getDeadlineIndexes();
        taskIndexes.removeIf(index -> !((Deadline) taskList.get(index)).getDueDate().equals(dueDate));
        String heading = DEADLINES_DUE_HEADING_PREFIX + Deadline.formatDate(dueDate) + ":";
        printDeadlineIndexes(taskIndexes, heading);
    }

    /**
     * Postpones a deadline by a positive number of days and saves the result.
     *
     * @param taskNumber The numbered deadline to postpone.
     * @param days The number of days to add.
     * @throws CommandException If the task is not a deadline or cannot be saved.
     */
    public void snoozeTask(int taskNumber, int days) throws CommandException {
        Deadline deadline = getDeadline(taskNumber);
        LocalDate oldDate = deadline.getDueDate();
        LocalDate newDate = oldDate.plusDays(days);
        ensureNotPast(newDate);
        updateDeadline(deadline, newDate);
        ui.showTaskSnoozed(deadline);
    }

    /**
     * Replaces a deadline date and saves the result.
     *
     * @param taskNumber The numbered deadline to reschedule.
     * @param newDueDate The replacement date.
     * @throws CommandException If the task is not a deadline or cannot be saved.
     */
    public void rescheduleTask(int taskNumber, LocalDate newDueDate) throws CommandException {
        Deadline deadline = getDeadline(taskNumber);
        ensureNotPast(newDueDate);
        updateDeadline(deadline, newDueDate);
        ui.showTaskRescheduled(deadline);
    }

    private Deadline getDeadline(int taskNumber) throws CommandException {
        assert taskList.isValidTaskNumber(taskNumber)
                : "Task number must be validated before retrieving a deadline";
        Task task = taskList.get(taskNumber - 1);
        if (!(task instanceof Deadline)) {
            throw new CommandException("Only deadline tasks can be snoozed or rescheduled.");
        }
        return (Deadline) task;
    }

    private void updateDeadline(Deadline deadline, LocalDate newDueDate) throws CommandException {
        LocalDate oldDate = deadline.getDueDate();
        deadline.reschedule(newDueDate);
        try {
            saveTasks();
        } catch (CommandException exception) {
            deadline.reschedule(oldDate);
            throw exception;
        }
    }

    private void ensureNotPast(LocalDate dueDate) throws CommandException {
        if (dueDate.isBefore(LocalDate.now())) {
            throw new CommandException("A deadline cannot be rescheduled to a past date.");
        }
    }

    /**
     * Returns the indexes of all deadline tasks, ordered by their due dates.
     *
     * @return The ordered indexes of deadline tasks.
     */
    private List<Integer> getDeadlineIndexes() {
        return IntStream.range(0, taskList.size())
                .filter(index -> taskList.get(index) instanceof Deadline)
                .boxed()
                .sorted(Comparator.comparing(index ->
                        ((Deadline) taskList.get(index)).getDueDate()))
                .collect(Collectors.toList());
    }

    /**
     * Prints deadline tasks using their original task numbers.
     *
     * @param taskIndexes Indexes of deadlines to display.
     * @param heading The heading to print before the deadlines.
     */
    private void printDeadlineIndexes(List<Integer> taskIndexes, String heading) {
        assert taskIndexes.stream().allMatch(index -> taskList.get(index) instanceof Deadline)
                : "Deadline indexes must refer only to deadline tasks";
        ui.showDeadlines(taskIndexes, taskList.asList(), heading);
    }

    /**
     * Checks whether a task number refers to a saved task.
     *
     * @param taskNumber The task number entered by the user.
     * @return True if the task number is valid.
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskList.isValidTaskNumber(taskNumber);
    }

    /**
     * Marks a numbered task as completed.
     *
     * @param taskNumber The task number displayed in the list.
     * @throws CommandException If the task list cannot be saved.
     */
    public void markTask(int taskNumber) throws CommandException {
        assert taskList.isValidTaskNumber(taskNumber)
                : "Task number must be validated before marking a task";
        Task task = taskList.get(taskNumber - 1);
        updateTaskStatus(task, Task::markAsDone, Task::markAsNotDone);
        ui.showTaskMarked(task);
    }

    /**
     * Marks a numbered task as not completed.
     *
     * @param taskNumber The task number displayed in the list.
     * @throws CommandException If the task list cannot be saved.
     */
    public void unmarkTask(int taskNumber) throws CommandException {
        assert taskList.isValidTaskNumber(taskNumber)
                : "Task number must be validated before unmarking a task";
        Task task = taskList.get(taskNumber - 1);
        updateTaskStatus(task, Task::markAsNotDone, Task::markAsDone);
        ui.showTaskUnmarked(task);
    }

    /**
     * Changes and persists a task status, restoring its previous status if saving fails.
     *
     * @param task The task whose status is changed.
     * @param applyStatus The requested status change.
     * @param restoreStatus The status change that reverses the requested change.
     * @throws CommandException If the updated task list cannot be saved.
     */
    private void updateTaskStatus(Task task, Consumer<Task> applyStatus, Consumer<Task> restoreStatus)
            throws CommandException {
        boolean wasDone = task.isDone();
        applyStatus.accept(task);
        try {
            saveTasks();
        } catch (CommandException exception) {
            if (task.isDone() != wasDone) {
                restoreStatus.accept(task);
            }
            throw exception;
        }
    }

    /**
     * Removes a numbered task and displays its confirmation message.
     *
     * @param taskNumber The task number displayed in the list.
     * @throws CommandException If the task list cannot be saved.
     */
    public void deleteTask(int taskNumber) throws CommandException {
        assert taskList.isValidTaskNumber(taskNumber)
                : "Task number must be validated before deleting a task";
        int taskIndex = taskNumber - 1;
        Task task = taskList.remove(taskIndex);
        try {
            saveTasks();
        } catch (CommandException exception) {
            taskList.add(taskIndex, task);
            throw exception;
        }
        ui.showTaskDeleted(task, taskList.size());
    }

    /**
     * Saves the current task list and reports a file error as a command error.
     *
     * @throws CommandException If the task list cannot be saved.
     */
    private void saveTasks() throws CommandException {
        try {
            storage.save(taskList.asList());
        } catch (IOException exception) {
            throw new CommandException("I could not save your tasks. Please try again.");
        }
    }

    /**
     * Loads saved tasks, or returns an empty list if loading fails.
     *
     * @return The loaded task list, or an empty list after a loading error.
     */
    private TaskList loadTaskList() {
        try {
            return new TaskList(storage.load());
        } catch (IOException | CommandException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /**
     * Displays tasks whose description contains a given keyword.
     *
     * @param keyword the text to search for
     */
    public void findTasks(String keyword) {
        List<Integer> taskIndexes = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).matchesKeyword(keyword)) {
                taskIndexes.add(i);
            }
        }
        ui.showMatchingTasks(taskIndexes, taskList.asList());
    }
}
