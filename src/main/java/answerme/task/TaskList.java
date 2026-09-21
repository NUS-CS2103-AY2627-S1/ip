package answerme.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

import answerme.exception.AnswerMeException;

/**
 * Represents a list of tasks.
 */
public class TaskList extends ArrayList<Task> {
    /**
     * Represents a list of tasks.
     */
    public TaskList() {
        super();
    }

    /**
     * Constructs a task list from a collection of tasks.
     *
     * @param tasks The collection of tasks to be added.
     */
    public TaskList(Collection<Task> tasks) {
        super(tasks);
    }

    /**
     * Retrieves the specified task based on its index.
     *
     * @param taskIndex The index of the task to return.
     * @return The target task.
     * @throws AnswerMeException If the list is empty or the index is invalid.
     */
    public Task getTask(int taskIndex)
            throws AnswerMeException {
        if (this.size() == 0) {
            throw new AnswerMeException("The task list is empty so cannot perform operation.");
        }

        if (taskIndex < 0 || taskIndex >= size()) {
            throw new AnswerMeException("The task does not exist in the list.");
        }
        return this.get(taskIndex);
    }

    /**
     * Returns whether an equivalent task already exists in
     * the current task list.
     *
     * @param targetTask The task to check.
     * @return {@code true} if the tasks exists and {@code false} if not.
     */
    public boolean containsDuplicateTask(Task targetTask) {
        return stream().anyMatch(task -> task.isDuplicateOf(targetTask));
    }

    /**
     * Removes duplicate tasks and retains the first occurrence of each.
     *
     * @return The list of duplicate tasks.
     */
    public TaskList removeDuplicateTasks() {
        TaskList uniqueTasks = new TaskList();
        TaskList duplicateTasks = new TaskList();

        for (Task task : this) {
            if (uniqueTasks.containsDuplicateTask(task)) {
                duplicateTasks.add(task);
            } else {
                uniqueTasks.add(task);
            }
        }

        clear();
        addAll(uniqueTasks);
        return duplicateTasks;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the tasks as a printable numbered list.
     */
    @Override
    public String toString() {
        return IntStream.range(0, size())
                .mapToObj(index -> "\t" + (index + 1) + ". " + get(index) + "\n")
                .reduce("", String::concat);
    }
}
