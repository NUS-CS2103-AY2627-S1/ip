package bucket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Holds the tasks the user has added, in the order they were added.
 *
 * The central invariant is that the list never holds a null task. Several callers
 * dereference what they get back without checking, so the assertions below guard
 * both ends of that: nothing null goes in, and nothing null comes out.
 */
public class TaskList {
    /**
     * Orders tasks by the date each one reports, earliest first.
     * Undated todos stand in as LocalDate.MAX so they fall to the end, which keeps
     * the comparator a single expression with no null or empty case to handle.
     */
    private static final Comparator<Task> BY_DATE =
            Comparator.comparing(task -> task.getSortDate().orElse(LocalDate.MAX));

    private ArrayList<Task> items = new ArrayList<>();

    /**
     * Adds a task to the end of the list.
     *
     * @param item Task to add.
     */
    public void addItem(Task item) {
        // Keeping nulls out here is what lets get() and toString() dereference freely.
        assert item != null : "the task list must never hold a null task";
        items.add(item);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index Zero-based position of the task to remove.
     */
    public void removeItem(int index) {
        items.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index Zero-based position, so "mark 2" means index 1.
     * @return Task at that position.
     */
    public Task get(int index) {
        // No assertion on index. A number the user invented is checked by the list
        // itself, and Bucket turns the IndexOutOfBoundsException into a message.
        Task task = items.get(index);

        // This is the other half of the addItem invariant: if nothing null went in,
        // nothing null can come out, so callers are safe to use it directly.
        assert task != null : "the task list must never hold a null task";
        return task;
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return Number of tasks in the list.
     */
    public int size() {
        return items.size();
    }

    /**
     * Returns whether there are no tasks.
     *
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     * Matching ignores case, so "BOOK" finds "read book".
     *
     * @param keyword Text to look for in each task description.
     * @return New list holding only the matching tasks, in their original order.
     */
    public TaskList find(String keyword) {
        assert keyword != null : "find() needs a keyword, not null";

        TaskList matches = new TaskList();
        String needle = keyword.toLowerCase();
        for (Task task : items) {
            if (task.getName().toLowerCase().contains(needle)) {
                matches.addItem(task);
            }
        }

        // Filtering can only ever remove tasks. A longer result would mean the loop
        // added something twice, which the user would see as duplicated search hits.
        assert matches.size() <= items.size() : "find() must not invent tasks";
        return matches;
    }

    /**
     * Orders the tasks by date, earliest first, with undated todos left at the end.
     *
     * Tasks sharing a date keep the order they were added in, because List.sort is
     * a stable sort. That also means the todos, which all compare equal, stay in
     * insertion order among themselves rather than being shuffled.
     */
    public void sort() {
        int sizeBefore = items.size();

        items.sort(BY_DATE);

        // Sorting rearranges the list and must never add or drop a task, which the
        // user would see as a task vanishing simply because they typed "list".
        assert items.size() == sizeBefore : "sorting must not change how many tasks there are";
    }

    /**
     * Returns the tasks numbered from 1, one per line.
     * Headings and dividers are left to Ui, so the same text suits the console
     * and the GUI without either front end having to strip anything out.
     *
     * @return Display form of the list.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(String.format("%d.%s", i + 1, items.get(i)));
        }
        return sb.toString();
    }
}
