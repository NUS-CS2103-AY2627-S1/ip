package zinc.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import zinc.ui.Ui;

/**
 * Stores up to 100 tasks and presents them as a numbered list.
 */
public class TaskList {
    /** The maximum number of tasks that can be stored. */
    private static final int MAX_TASKS = 100;

    /** The message displayed when the user has no stored tasks. */
    private static final String EMPTY_TASK_LIST_MESSAGE = "You have no tasks and is free to jam!";

    /** The format used when displaying a date supplied to the list command. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    /** The stored tasks. */
    private final List<Task> tasks = new ArrayList<>();

    /** The component responsible for persisting the task list. */
    private final TaskStorage taskStorage = new TaskStorage();

    /** Whether malformed task data was found while restoring saved tasks. */
    private final boolean wasStoredDataCorrupted;

    /** The UI used to display task-list messages. */
    private final Ui ui;

    /**
     * Creates a task list and restores tasks saved by a previous run.
     */
    public TaskList() {
        this(new Ui());
    }

    /**
     * Creates a task list that displays messages through the supplied UI.
     *
     * @param ui The UI used to display task-list messages.
     */
    public TaskList(Ui ui) {
        assert ui != null : "Task-list UI must not be null";
        this.ui = ui;
        List<Task> storedTasks = taskStorage.loadTasks();
        wasStoredDataCorrupted = taskStorage.wasDataCorrupted();
        for (Task task : storedTasks) {
            if (tasks.size() == MAX_TASKS) {
                break;
            }
            tasks.add(task);
        }
    }

    /**
     * Stores a task when there is remaining space in the list.
     *
     * @param task The task to store.
     */
    public void addTask(Task task) {
        assert task != null : "Task to add must not be null";
        if (tasks.size() >= MAX_TASKS) {
            ui.printTaskListFull();
            return;
        }
        if (tasks.stream().anyMatch(existingTask -> isDuplicate(existingTask, task))) {
            ui.printDuplicateTask();
            return;
        }

        tasks.add(task);
        saveTasks();
        ui.printTaskAdded(formatTask(task), tasks.size());
    }

    /**
     * Prints every stored task with its list number.
     */
    public void printTasks() {
        String heading = tasks.isEmpty()
                ? EMPTY_TASK_LIST_MESSAGE
                : "Here are your current tasks:";
        printMatchingTasks(heading, task -> true);
    }

    /**
     * Prints deadlines due, and events ending, on the specified calendar date.
     * Todo tasks are excluded because they do not have an end date.
     *
     * @param date The date on which matching tasks end.
     */
    public void printTasksEndingOn(LocalDate date) {
        assert date != null : "List date must not be null";
        String heading = "Here are your tasks ending on " + date.format(DISPLAY_DATE_FORMAT) + ":";
        printMatchingTasks(heading, task -> endsOn(task, date));
    }

    /**
     * Prints tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword The exact, case-sensitive text to search for.
     */
    public void printTasksContaining(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        String heading = "Here are your tasks containing \"" + keyword + "\":";
        printMatchingTasks(heading, task -> task.getDescription().contains(keyword));
    }

    /** Prints tasks accepted by the supplied filter while preserving their original list numbers. */
    private void printMatchingTasks(String heading, Predicate<Task> taskFilter) {
        List<String> taskEntries = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (taskFilter.test(task)) {
                taskEntries.add((i + 1) + ". " + formatTask(task));
            }
        }
        ui.printTaskList(heading, taskEntries);
    }

    /** Formats a task with its type identifier for list output. */
    private String formatTask(Task task) {
        return task.getTaskType().getDisplayIdentifier() + task;
    }

    /** Returns whether two tasks have identical descriptions and time parameters. */
    private boolean isDuplicate(Task existingTask, Task newTask) {
        if (!existingTask.getDescription().equals(newTask.getDescription())
                || existingTask.getTaskType() != newTask.getTaskType()) {
            return false;
        }
        if (existingTask instanceof Deadline existingDeadline && newTask instanceof Deadline newDeadline) {
            return existingDeadline.getDeadline().equals(newDeadline.getDeadline());
        }
        if (existingTask instanceof Event existingEvent && newTask instanceof Event newEvent) {
            return existingEvent.getStart().equals(newEvent.getStart())
                    && existingEvent.getEnd().equals(newEvent.getEnd());
        }
        return true;
    }

    /** Returns whether a deadline or event ends on the specified date. */
    private boolean endsOn(Task task, LocalDate date) {
        assert task != null && date != null : "Task and date must not be null";
        if (task instanceof Deadline deadline) {
            return deadline.getDeadline().toLocalDate().equals(date);
        }
        if (task instanceof Event event) {
            return event.getEnd().toLocalDate().equals(date);
        }
        return false;
    }

    /**
     * Changes task to a completed state.
     *
     * @param taskNumber The number of the task in the 1-indexed list.
     */
    public void markTaskAsCompleted(int taskNumber) {
        updateTaskCompletion(taskNumber, true);
    }

    /**
     * Changes a task to an incomplete state.
     *
     * @param taskNumber The number of the task in the 1-indexed list.
     */
    public void markTaskAsIncomplete(int taskNumber) {
        updateTaskCompletion(taskNumber, false);
    }

    /** Updates and displays the completion state of a user-selected task. */
    private void updateTaskCompletion(int taskNumber, boolean shouldMarkAsCompleted) {
        if (tasks.size() < taskNumber || taskNumber <= 0) {
            ui.printTaskNotFound();
            return;
        }

        Task task = tasks.get(taskNumber - 1);
        if (shouldMarkAsCompleted) {
            task.markAsCompleted();
        } else {
            task.markAsIncomplete();
        }
        saveTasks();
        String actionDescription = shouldMarkAsCompleted ? "marked as done" : "unmarked as done";
        ui.printTaskCompletionChanged(actionDescription, task.toString());
    }

    /**
     * Deletes the task at the user-facing list number.
     *
     * @param taskNumber The task number shown to the user, starting from 1.
     */
    public void deleteTask(int taskNumber) {
        int listIndex = taskNumber - 1;
        if (listIndex < 0 || listIndex >= tasks.size()) {
            ui.printTaskNotFound();
            return;
        }

        Task deletedTask = tasks.remove(listIndex);
        saveTasks();

        ui.printTaskDeleted(deletedTask.getTaskType().getDisplayIdentifier() + deletedTask, tasks.size());
    }

    /**
     * Returns the list of tasks stored.
     *
     * @return The stored task list.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks stored.
     *
     * @return The number of items currently in the stored list.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns whether malformed task data was found during construction.
     *
     * @return Whether the saved task data was corrupted.
     */
    public boolean wasStoredDataCorrupted() {
        return wasStoredDataCorrupted;
    }

    /** Saves the current list after a task has been changed. */
    private void saveTasks() {
        assert tasks.size() <= MAX_TASKS : "Task count must be within list bounds";
        taskStorage.saveTasks(tasks);
    }
}
