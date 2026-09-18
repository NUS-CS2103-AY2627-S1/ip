package rem.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import rem.exception.InvalidTaskNumberException;
import rem.exception.MissingNoteException;
import rem.exception.RemException;

/**
 * Stores Rem's tasks and provides operations for managing them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks to store.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "Initial task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task using its one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return The selected task.
     */
    public Task getTask(int taskNumber) {
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Task number must identify an existing task";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Removes a task using its one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task mark(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task unmark(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Adds or replaces a task's note.
     *
     * @param taskNumber One-based number of the task to update.
     * @param note Valid, trimmed note text.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task setNote(int taskNumber, String note) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        task.setNote(note);
        return task;
    }

    /**
     * Removes a task's note.
     *
     * @param taskNumber One-based number of the task to update.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     * @throws MissingNoteException If the selected task has no note.
     */
    public Task deleteNote(int taskNumber)
            throws InvalidTaskNumberException, MissingNoteException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        if (!task.hasNote()) {
            throw new MissingNoteException();
        }
        task.deleteNote();
        return task;
    }

    /**
     * Finds deadlines due and events occurring on a date.
     *
     * @param date Date to search for.
     * @return Scheduled tasks in their task-list order.
     */
    public List<Task> findTasksOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task instanceof Deadline deadline && deadline.occursOn(date)
                        || task instanceof Event event && event.occursOn(date))
                .toList();
    }

    /**
     * Finds tasks whose descriptions or notes contain a keyword, ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their task-list order.
     */
    public List<Task> findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                        || task.hasNote()
                        && task.getNote().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                .toList();
    }

    /**
     * Returns a read-only snapshot of all tasks.
     *
     * @return Current tasks in task-list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Checks that a one-based task number identifies a stored task.
     *
     * @param taskNumber One-based task number to validate.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException();
        }
    }

    /**
     * Copies tasks and their mutable state so failed commands can be discarded.
     *
     * @return An independent task list with the same contents.
     */
    public TaskList copy() {
        ArrayList<Task> copies = new ArrayList<>();
        for (Task task : tasks) {
            Task copy;
            if (task instanceof Deadline deadline) {
                copy = new Deadline(task.getDescription(), deadline.getBy());
            } else if (task instanceof Event event) {
                copy = new Event(task.getDescription(), event.getFrom(), event.getTo());
            } else {
                copy = new Todo(task.getDescription());
            }
            if (task.isDone()) {
                copy.markAsDone();
            }
            if (task.hasNote()) {
                copy.setNote(task.getNote());
            }
            copies.add(copy);
        }
        return new TaskList(copies);
    }

    /**
     * Rejects an addition matching an existing task, ignoring completion status.
     *
     * @param candidate Task being added.
     * @throws RemException If the task already exists.
     */
    public void validateUnique(Task candidate) throws RemException {
        for (int i = 0; i < tasks.size(); i++) {
            Task existing = tasks.get(i);
            boolean hasSameSchedule = true;
            if (candidate instanceof Deadline deadline && existing instanceof Deadline other) {
                hasSameSchedule = deadline.getBy().equals(other.getBy());
            } else if (candidate instanceof Event event && existing instanceof Event other) {
                hasSameSchedule = event.getFrom().equals(other.getFrom()) && event.getTo().equals(other.getTo());
            }
            if (candidate.getClass() == existing.getClass() && hasSameSchedule
                    && candidate.getDescription().equals(existing.getDescription())
                    && Objects.equals(candidate.getNote(), existing.getNote())) {
                throw new RemException("This task already exists as task " + (i + 1) + ". Use list to see it.");
            }
        }
    }
}
