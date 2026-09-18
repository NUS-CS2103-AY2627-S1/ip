package zhangwei.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zhangwei.ZhangWeiException;

/**
 * The list of tasks the chatbot is keeping, together with the operations that
 * change it.
 *
 * <p>Task numbers are 1-based everywhere the user sees them and 0-based inside
 * this class. Translating between the two, and refusing numbers that do not
 * refer to a task, is this class's job: the list knows how many tasks it holds,
 * so no other class has to guard the bounds itself.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, e.g. the ones just loaded.
     *
     * @param tasks the tasks to start with. They are copied, so later changes
     *     to the given list do not reach this one.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds the given task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task with the given 1-based number.
     * The number must already have been checked by {@link #requireTask}.
     *
     * @param taskNumber the 1-based number of the task to remove.
     * @return the task that was removed, so the caller can show it.
     */
    public Task delete(int taskNumber) {
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Task number " + taskNumber + " is out of range; requireTask should have rejected it.";
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task with the given 1-based number.
     * The number must already have been checked by {@link #requireTask}.
     *
     * @param taskNumber the 1-based number of the task wanted.
     * @return the task with that number.
     */
    public Task get(int taskNumber) {
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Task number " + taskNumber + " is out of range; requireTask should have rejected it.";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Reorders the tasks by the given criterion.
     *
     * <p>The sort is stable, so tasks the criterion cannot tell apart keep the
     * order they were already in, and sorting twice the same way changes
     * nothing.
     *
     * @param criterion the order to put the tasks into.
     */
    public void sort(SortCriterion criterion) {
        tasks.sort(criterion.getComparator());
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks held.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list holds no tasks.
     *
     * @return true if there are no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in order, for reading only. Callers cannot change the
     * list through it, so every change still goes through this class.
     *
     * @return an unmodifiable view of the tasks, in the order they were added.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the tasks whose description contains the given keyword, in the
     * order they appear in this list.
     *
     * <p>The match ignores case and does not have to fall on a word boundary,
     * so "book" finds "Booking" too. Only the description is searched: the
     * keyword is a word the user wrote, and dates are already searchable by
     * reading the list.
     *
     * @param keyword the word or phrase to look for.
     * @return an unmodifiable list of the matching tasks, empty if none match.
     */
    public List<Task> find(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }

    /**
     * Checks that the given 1-based number refers to a task in this list.
     *
     * @param taskNumber the 1-based number the user typed.
     * @throws ZhangWeiException if the list is empty or the number is out of
     *     range, explaining which numbers would have worked.
     */
    public void requireTask(int taskNumber) throws ZhangWeiException {
        if (tasks.isEmpty()) {
            throw new ZhangWeiException("You have no tasks yet, so there is no task "
                    + taskNumber + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            String noun = tasks.size() == 1 ? "task" : "tasks";
            throw new ZhangWeiException("There is no task " + taskNumber + ". "
                    + "You have " + tasks.size() + " " + noun + ".");
        }
    }
}
