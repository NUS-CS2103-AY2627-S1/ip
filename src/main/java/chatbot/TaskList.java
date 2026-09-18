package chatbot;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages an ordered list of tasks.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes specified task from list.
     *
     * @param index Task index of task to be removed.
     */
    public void remove(int index) {
        tasks.remove(index);
    }

    /**
     * Retrieve task from list at specified index.
     *
     * @param index Index of task to be retrieved.
     * @return Task to be retrieved.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns number of tasks in list.
     *
     * @return Number of tasks in list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an iterator over the tasks in their current order.
     *
     * @return Iterator over this task list.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /**
     * Takes in an inputTask and compares against all Tasks in the TaskList to check whether it is a duplicate.
     *
     * @param inputTask The input task to check.
     * @return True if it is a duplicate task (of one of the existing tasks in list),
     *     False, otherwise.
     */
    public boolean isDuplicate(Task inputTask) {
        boolean result = false;
        for (Task task : tasks) {
            if (result) {
                break;
            }
            if (task.getClass() != inputTask.getClass()) {
                continue;
            }
            if (task instanceof Deadline) {
                Deadline d = (Deadline) task;
                Deadline taskToBeChecked = (Deadline) inputTask;
                result = result || d.isDuplicate(taskToBeChecked);
            } else if (task instanceof Event) {
                Event e = (Event) task;
                Event taskToBeChecked = (Event) inputTask;
                result = result || e.isDuplicate(taskToBeChecked);
            } else if (task instanceof Todo) {
                Todo t = (Todo) task;
                Todo taskToBeChecked = (Todo) inputTask;
                result = result || t.isDuplicate(taskToBeChecked);
            }
        }
        return result;
    }
}
