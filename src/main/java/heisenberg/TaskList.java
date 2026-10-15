package heisenberg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains an ordered task collection with one-based task numbers.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Task getTask(int taskNumber) {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes the task with the given task number from the list.
     *
     * @param taskNumber One-based task number as displayed to the user.
     * @return Task that was removed.
     * @throws InvalidTaskNumberException If no task has the given task number.
     */
    public Task deleteTask(int taskNumber) {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks the task with the given task number as done.
     *
     * @param taskNumber One-based task number as displayed to the user.
     * @return Task that was marked.
     * @throws InvalidTaskNumberException If no task has the given task number.
     */
    public Task markTask(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Finds tasks whose descriptions contain the case-sensitive keyword.
     *
     * @param keyword Text to match within each description.
     * @return Matching tasks in their original order.
     */
    public TaskList findTasks(String keyword) {
        TaskList matches = new TaskList();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matches.addTask(task);
            }
        }
        return matches;
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Sorts deadlines from earliest to latest while keeping other task types in their original order. */
    public void sortByDeadline() {
        tasks.sort(TaskList::compareByDeadline);
    }

    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }

    private int toIndex(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException("This task does not exist!");
        }
        return taskNumber - 1;
    }

    private static int compareByDeadline(Task firstTask, Task secondTask) {
        if (firstTask instanceof Deadline firstDeadline) {
            if (secondTask instanceof Deadline secondDeadline) {
                return firstDeadline.getDeadlineDateTime()
                        .compareTo(secondDeadline.getDeadlineDateTime());
            }
            return -1;
        }
        if (secondTask instanceof Deadline) {
            return 1;
        }
        return 0;
    }
}
