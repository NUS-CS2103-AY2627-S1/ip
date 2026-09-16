package downtowngurl.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Contains the tasks tracked by the chatbot and common operations on that list.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from tasks loaded from storage.
     *
     * @param tasks Saved tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Loaded task collection should not be null.";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Adds a task at the given zero-based index.
     *
     * @param index Position where the task should be inserted.
     * @param task Task to add.
     */
    public void add(int index, Task task) {
        this.tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index Position of the task to remove.
     * @return Removed task.
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Removes the last task in the list.
     */
    public void removeLast() {
        this.tasks.removeLast();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Position of the task to return.
     * @return Task at the given index.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index Position of the task to mark.
     * @return Task that was marked.
     */
    public Task markAsDone(int index) {
        Task task = this.tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index Position of the task to unmark.
     * @return Task that was unmarked.
     */
    public Task markAsNotDone(int index) {
        Task task = this.tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Task list containing matching tasks in their current order.
     */
    public TaskList findByKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matches = this.tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }

    /**
     * Sorts dated tasks before undated tasks, ordered by their date and time.
     */
    public void sortByDate() {
        this.tasks.sort(Comparator.comparing(Task::getSortDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        assert isSortedByDate() : "Tasks should be sorted by date after sorting.";
    }

    /**
     * Returns whether the current task order is nondecreasing by sort date, with undated tasks last.
     *
     * @return true if tasks are sorted by sort date.
     */
    private boolean isSortedByDate() {
        Comparator<Task> taskDateComparator =
                Comparator.comparing(Task::getSortDateTime, Comparator.nullsLast(Comparator.naturalOrder()));
        for (int i = 1; i < this.tasks.size(); i++) {
            if (taskDateComparator.compare(this.tasks.get(i - 1), this.tasks.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an iterator over the tasks in their current list order.
     *
     * @return Iterator over the stored tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }
}
