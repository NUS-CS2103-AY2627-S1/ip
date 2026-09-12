package minweeder.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A simple ordered collection of {@link Task}s.
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task.
     * @return the task at that index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "index out of range: " + index;
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based index of the task to remove.
     * @return the removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "index out of range: " + index;
        return tasks.remove(index);
    }

    /**
     * Finds the positions of tasks whose description contains the given keyword.
     * Indices, rather than the tasks themselves, are returned so that callers can
     * refer to each match by its number in the full list.
     *
     * @param keyword the keyword to search for, matched case-insensitively.
     * @return the zero-based indices of the matching tasks, in list order.
     */
    public List<Integer> findIndices(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword))
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Returns a sequential stream of the tasks in this list, in order.
     *
     * @return a stream over the tasks.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }
}
