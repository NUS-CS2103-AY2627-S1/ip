package quu.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import quu.exception.QuuException;
import quu.exception.TaskNotFoundException;

/**
 * The collection of tasks the user is working with.
 *
 * <p>Indices accepted by {@link #removeTask(int)}, {@link #markTask(int)},
 * {@link #unmarkTask(int)} and {@link #editTask(int, Map)} are <em>one-based</em>, matching the numbering the user
 * sees when listing tasks. {@link #getTaskAt(int)} is zero-based because it is used
 * for iteration rather than driven by user input.
 */
public class TaskList {
    private final List<Task> todoList;

    /** Creates an empty task list. */
    public TaskList() {
        todoList = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks.
     *
     * <p>The tasks are copied into a list of this object's own, so later changes to the
     * list passed in do not reach into this one.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "a task list must be built from a real list, not null";
        todoList = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at a zero-based position.
     *
     * @param index zero-based position of the task
     * @return the task at that position
     */
    public Task getTaskAt(int index) {
        assert index >= 0 : "a caller iterating over the list never passes a negative index";
        assert index < todoList.size() : "a caller iterating over the list stays below getSize()";
        return todoList.get(index);
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks
     */
    public int getSize() {
        return todoList.size();
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        todoList.add(task);
    }

    /**
     * Returns a new list holding every task whose description contains the given
     * keyword, ignoring case.
     *
     * <p>The returned list is a separate {@code TaskList} that shares the same
     * {@link Task} objects, so marking a task through either list is visible in both,
     * but adding or removing tasks affects only the list acted on. Its numbering
     * restarts at one and therefore does not map back to positions in this list.
     *
     * @param keyword the text to search for in each task's description
     * @return a task list holding the matching tasks, empty if none match
     */
    public TaskList buildFoundList(String keyword) {
        TaskList foundList = new TaskList();
        String lowerCaseKeyword = keyword.toLowerCase();
        for (Task task : todoList) {
            if (task.getDescription().toLowerCase().contains(lowerCaseKeyword)) {
                foundList.addTask(task);
            }
        }
        return foundList;
    }

    /**
     * Removes and returns the task at a one-based position.
     *
     * @param taskNumber one-based position of the task to remove
     * @return the task that was removed
     * @throws TaskNotFoundException if no task sits at that position
     */
    public Task removeTask(int taskNumber) throws TaskNotFoundException {
        int taskIndex = convertToZeroBasedIndex(taskNumber);
        return todoList.remove(taskIndex);
    }

    /**
     * Marks the task at a one-based position as done.
     *
     * @param taskNumber one-based position of the task to mark
     * @return the task that was marked
     * @throws TaskNotFoundException if no task sits at that position
     */
    public Task markTask(int taskNumber) throws TaskNotFoundException {
        int taskIndex = convertToZeroBasedIndex(taskNumber);
        Task task = todoList.get(taskIndex);
        task.mark();
        assert task.isDone() : "marking a task must leave it done before it is reported to the user";
        return task;
    }

    /**
     * Marks the task at a one-based position as not done.
     *
     * @param taskNumber one-based position of the task to unmark
     * @return the task that was unmarked
     * @throws TaskNotFoundException if no task sits at that position
     */
    public Task unmarkTask(int taskNumber) throws TaskNotFoundException {
        int taskIndex = convertToZeroBasedIndex(taskNumber);
        Task task = todoList.get(taskIndex);
        task.unmark();
        assert !task.isDone() : "unmarking a task must leave it not done before it is reported to the user";
        return task;
    }

    /**
     * Applies edits to the task at a one-based position, leaving it where it is.
     *
     * <p>The task rebuilds itself through {@link Task#withEdits(Map)}, so the edited task is a
     * new object rather than the old one altered. Putting it back in the same slot keeps the
     * numbering the user sees unchanged, and because the replacement is built before anything
     * is overwritten, an edit that turns out to be invalid leaves the list exactly as it was.
     *
     * <p>A rebuilt task starts out not done, so a task that was already done is marked again
     * here. Doing it in this one place keeps every subclass of {@link Task} out of it.
     *
     * @param taskNumber one-based position of the task to edit
     * @param edits the requested edits, as returned by {@code Parser.parseEdits}
     * @return the edited task now held at that position
     * @throws QuuException if no task sits at that position, or the edits name a detail the
     *     task does not have
     */
    public Task editTask(int taskNumber, Map<String, String> edits) throws QuuException {
        int taskIndex = convertToZeroBasedIndex(taskNumber);
        Task oldTask = todoList.get(taskIndex);

        Task editedTask = oldTask.withEdits(edits);
        if (oldTask.isDone()) {
            editedTask.mark();
        }

        todoList.set(taskIndex, editedTask);
        assert editedTask.isDone() == oldTask.isDone() : "editing a task must not change whether it is done";
        return editedTask;
    }

    /**
     * Converts a task number as the user types it into a position in the backing list.
     *
     * <p>This is the only place the one-based numbering the user sees is translated into
     * the zero-based indexing the list uses, so the two never drift apart.
     *
     * @param taskNumber one-based position of the task, as shown when listing tasks
     * @return the matching zero-based index into the backing list
     * @throws TaskNotFoundException if no task sits at that position
     */
    private int convertToZeroBasedIndex(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > todoList.size()) {
            throw new TaskNotFoundException(taskNumber, todoList.size());
        }

        return taskNumber - 1;
    }
}
