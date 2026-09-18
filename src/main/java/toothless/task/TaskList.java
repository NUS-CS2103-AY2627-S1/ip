package toothless.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import toothless.exception.ToothlessException;

/**
 * Stores tasks and provides focused operations for changing the task list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list backed by an {@link ArrayList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Adds a task only when no task of the same type and details already exists.
     *
     * @param task proposed task.
     * @throws ToothlessException if an equivalent task already exists.
     */
    public void addUniqueTask(Task task) throws ToothlessException {
        ensureUnique(task, -1);
        addTask(task);
    }

    /**
     * Returns a separate list with copied tasks for a proposed persistent change.
     *
     * @return independent task list with the same contents and completion states.
     */
    public TaskList copy() {
        TaskList copy = new TaskList();
        for (Task task : tasks) {
            Task clonedTask = copyTask(task);
            if (task.isDone()) {
                clonedTask.markAsDone();
            }
            copy.addTask(clonedTask);
        }
        return copy;
    }

    /**
     * Publishes a fully saved proposal without changing this list's identity.
     *
     * @param savedTasks list successfully persisted to storage.
     */
    public void replaceWith(TaskList savedTasks) {
        tasks.clear();
        tasks.addAll(savedTasks.tasks);
    }

    /**
     * Copies a task's details before its completion state is copied separately.
     */
    private Task copyTask(Task task) {
        if (task instanceof Todo) {
            return new Todo(task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return new Deadline(task.getDescription(), deadline.getBy());
        }
        if (task instanceof Event event) {
            return new Event(task.getDescription(), event.getFrom(), event.getTo());
        }
        return new Task(task.getDescription());
    }

    /**
     * Checks a candidate against all tasks except the one being edited.
     */
    private void ensureUnique(Task candidate, int excludedIndex) throws ToothlessException {
        for (int i = 0; i < tasks.size(); i++) {
            if (i != excludedIndex && sameDetails(tasks.get(i), candidate)) {
                throw new ToothlessException("Toothless already remembers that exact quest.\n"
                        + "Change its details or edit the existing task instead.");
            }
        }
    }

    /**
     * Compares only the fields that identify a task, not its completion state.
     */
    private boolean sameDetails(Task first, Task second) {
        if (first.getClass() != second.getClass()
                || !normalize(first.getDescription()).equals(normalize(second.getDescription()))) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return normalize(firstEvent.getFrom()).equals(normalize(secondEvent.getFrom()))
                    && normalize(firstEvent.getTo()).equals(normalize(secondEvent.getTo()));
        }
        return true;
    }

    /**
     * Reduces insignificant whitespace while preserving meaningful punctuation and case.
     */
    private String normalize(String value) {
        return value.strip().replaceAll("\\s+", " ");
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return selected task
     * @throws IndexOutOfBoundsException if the index is outside the task list
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Finds tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to look for within task descriptions
     * @return matching tasks in their original list order
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Marks the task with the given one-based number as completed.
     *
     * @param taskNumber one-based task number.
     * @return marked task
     * @throws ToothlessException if the task number is outside the task list
     */
    public Task markTask(int taskNumber) throws ToothlessException {
        Task task = getTaskByNumber(taskNumber, "mark");
        task.markAsDone();
        return task;
    }

    /**
     * Checks whether the selected task is already complete without changing it.
     *
     * @param taskNumber one-based task number.
     * @return whether the selected task is complete.
     * @throws ToothlessException if the task does not exist.
     */
    public boolean isTaskDone(int taskNumber) throws ToothlessException {
        return getTaskByNumber(taskNumber, "mark").isDone();
    }

    /**
     * Unmarks the task with the given one-based number when it is completed.
     *
     * @param taskNumber one-based task number.
     * @return the selected task and whether its completion state changed
     * @throws ToothlessException if the task number is outside the task list
     */
    public UnmarkResult unmarkTask(int taskNumber) throws ToothlessException {
        Task task = getTaskByNumber(taskNumber, "unmark");
        if (!task.isDone()) {
            return new UnmarkResult(task, false);
        }
        task.unmarkAsDone();
        return new UnmarkResult(task, true);
    }

    /**
     * Removes and returns the task with the given one-based number.
     *
     * @param taskNumber one-based task number.
     * @return removed task
     * @throws ToothlessException if the task number is outside the task list
     */
    public Task deleteTask(int taskNumber) throws ToothlessException {
        Task task = getTaskByNumber(taskNumber, "delete");
        tasks.remove(task);
        return task;
    }

    /**
     * Replaces selected fields of one task without changing its type, status, or list position.
     *
     * @param taskNumber one-based task number.
     * @param update replacement fields to apply.
     * @return updated task
     * @throws ToothlessException if the task number or replacement fields are invalid
     */
    public Task editTask(int taskNumber, TaskUpdate update) throws ToothlessException {
        Task originalTask = getTaskByNumber(taskNumber, "edit");
        validateUpdate(originalTask, update);

        Task updatedTask = createUpdatedTask(originalTask, update);
        if (updatedTask instanceof Event event) {
            EventTime.validate(event.getFrom(), event.getTo());
        }
        ensureUnique(updatedTask, taskNumber - 1);
        if (originalTask.isDone()) {
            updatedTask.markAsDone();
        }
        tasks.set(taskNumber - 1, updatedTask);
        return updatedTask;
    }

    /**
     * Validates that an update contains fields supported by the selected task type.
     *
     * @param task task selected for editing.
     * @param update replacement fields requested by the user.
     * @throws ToothlessException if no field is present or a field does not belong to the task type
     */
    private void validateUpdate(Task task, TaskUpdate update) throws ToothlessException {
        if (!update.hasAnyField()) {
            throw new ToothlessException("Toothless needs at least one field to edit.\n"
                    + "Try: edit 1 /description read the new textbook");
        }
        if (update.hasBy() && !(task instanceof Deadline)) {
            throw new ToothlessException("Only deadline tasks have a '/by' date to edit.");
        }
        if (update.hasEventTime() && !(task instanceof Event)) {
            throw new ToothlessException("Only event tasks have '/from' or '/to' times to edit.");
        }
    }

    /**
     * Creates a same-type task containing the requested replacement fields.
     *
     * @param task original task selected for editing.
     * @param update validated replacement fields.
     * @return updated task with the same concrete type
     */
    private Task createUpdatedTask(Task task, TaskUpdate update) {
        String description = update.getDescriptionOr(task.getDescription());
        if (task instanceof Todo) {
            return new Todo(description);
        }
        if (task instanceof Deadline deadline) {
            return new Deadline(description, update.getByOr(deadline.getBy()));
        }
        if (task instanceof Event event) {
            return new Event(description, update.getFromOr(event.getFrom()),
                    update.getToOr(event.getTo()));
        }
        return new Task(description);
    }

    /**
     * Returns the task identified by a one-based number after validating the selection.
     *
     * @param taskNumber one-based task number.
     * @param action verb describing the operation attempted on the task.
     * @return selected task
     * @throws ToothlessException if the task number is outside the task list
     */
    private Task getTaskByNumber(int taskNumber, String action) throws ToothlessException {
        if (tasks.isEmpty()) {
            throw new ToothlessException("Toothless's cave is empty, so there is no task to "
                    + action + ".\nAdd a task first, then try again.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ToothlessException(
                    "Toothless can’t find task " + taskNumber + " in the cave.\n"
                            + "Please choose a number from 1 to " + tasks.size() + ".");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return true when no tasks are stored
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Describes the outcome of attempting to unmark a task.
     */
    public static class UnmarkResult {
        private final Task task;
        private final boolean wasChanged;

        /**
         * Creates an unmark result for a selected task.
         *
         * @param task selected task.
         * @param wasChanged whether the task changed from marked to unmarked.
         */
        private UnmarkResult(Task task, boolean wasChanged) {
            this.task = task;
            this.wasChanged = wasChanged;
        }

        /**
         * Returns the task selected by the unmark operation.
         *
         * @return selected task
         */
        public Task getTask() {
            return task;
        }

        /**
         * Returns whether unmarking changed the task's completion state.
         *
         * @return true when a marked task became unmarked
         */
        public boolean wasChanged() {
            return wasChanged;
        }
    }
}
