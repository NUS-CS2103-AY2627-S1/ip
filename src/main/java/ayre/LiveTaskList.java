package ayre;

import ayre.storage.Storage;
import ayre.tasks.Task;

/**
 * Intermediary class that handles mutation of TaskList and corresponding file IO.
 * This allows TaskList to stay separate from file read/write, maintaining the abstraction wall.
 * Constructed partially with Claude Sonnet 5 medium
 */
public class LiveTaskList {
    @FunctionalInterface
    interface TaskTransformer {
        /**
         * A method that performs an operation on the TaskList and returns the result as a String.
         *
         * @param tasks TaskList to be mutated.
         * @return String result message.
         */
        String transform(TaskList tasks);
    }

    private final TaskList tasks;
    private final Storage store;

    /**
     * Initializes the TaskList and Storage.
     *
     * @param tasks TaskList loaded by Storage.
     * @param store Storage.
     */
    public LiveTaskList(TaskList tasks, Storage store) {
        this.tasks = tasks;
        this.store = store;
    }

    /**
     * Transforms the TaskList with the given operation while updating the save file via Storage.
     *
     * @param mut The mutator operation.
     * @return The combined result message of the operation and updating the save file.
     */
    private String mutateList(TaskTransformer mut) {
        return mut.transform(this.tasks)
                + this.store.update(this.tasks);
    }

    /**
     * Adds a new Task to the TaskList while updating the save file.
     *
     * @param tsk Task to be added.
     * @return Context String of adding a Task.
     */
    public String add(Task tsk) {
        return this.mutateList(lst -> lst.addTask(tsk));
    }

    /**
     * Removes a Task from the TaskList while updating the save file.
     *
     * @param i Index of Task to be removed.
     * @return Context String of removing a Task.
     */
    public String del(int i) {
        return this.mutateList(lst -> lst.delTask(i));
    }

    /**
     * Marks a Task as complete while updating the save file.
     *
     * @param i Index of Task to be marked.
     * @return Context String of marking a Task.
     */
    public String mark(int i) {
        return this.mutateList(lst -> lst.markTask(i));
    }

    /**
     * Unmarks a Task as complete while updating the save file.
     *
     * @param i Index of Task to be unmarked.
     * @return Context String of unmarking a Task.
     */
    public String unmark(int i) {
        return this.mutateList(lst -> lst.unmarkTask(i));
    }

    /**
     * Getter for the number of Tasks.
     * Allows Commands to check the number of Tasks present for validation and printouts.
     *
     * @return Number of Tasks.
     */
    public int getNumTasks() {
        return this.tasks.getNumTasks();
    }

    /**
     * Finds all Tasks that contain the input substring leniently (without considering capitalization).
     *
     * @param name Substring to be tested.
     * @return All Tasks that contain the substring, formatted as a printout.
     */
    public String findTasks(String name) {
        return this.tasks.findTasks(name);
    }

    @Override
    public String toString() {
        return this.tasks.toString();
    }
}
