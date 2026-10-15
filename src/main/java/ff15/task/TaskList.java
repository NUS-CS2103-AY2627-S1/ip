package ff15.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ff15.FF15Exception;

/**
 * Holds the tasks the user is keeping track of, and the operations that add to,
 * remove from, and search that collection.
 *
 * <p>Task numbers are 1-based here, matching what the user types and what the
 * list prints, so the conversion to the ArrayList's 0-based index happens inside
 * this class instead of at every call site.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty list, used when there is nothing saved to start from. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a list holding {@code tasks}, typically the ones just read from disk. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Returns how many tasks the list holds. */
    public int size() {
        return tasks.size();
    }

    /** Adds {@code tasksToAdd} to the end of the list, keeping the order given. */
    public void add(Task... tasksToAdd) {
        // Varargs makes add() with no arguments legal, which would silently do nothing.
        assert tasksToAdd.length > 0 : "add was called with no tasks to add";
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Returns the task the user knows as {@code number}, counting from 1.
     *
     * @throws FF15Exception if there is no such task.
     */
    public Task get(int number) throws FF15Exception {
        checkNumber(number);
        int index = number - 1;
        assert index >= 0 && index < tasks.size()
                : "checkNumber let through an out-of-range task number: " + number;
        return tasks.get(index);
    }

    /**
     * Removes and returns the task the user knows as {@code number}, counting from 1.
     *
     * @throws FF15Exception if there is no such task.
     */
    public Task delete(int number) throws FF15Exception {
        checkNumber(number);
        int index = number - 1;
        assert index >= 0 && index < tasks.size()
                : "checkNumber let through an out-of-range task number: " + number;
        return tasks.remove(index);
    }

    /** Rejects a task number that does not name a task in this list. */
    private void checkNumber(int number) throws FF15Exception {
        if (number < 1 || number > tasks.size()) {
            throw new FF15Exception("Task " + number + "? There are " + tasks.size()
                    + ". I'm not a magician. Well, I'm a bit of a magician.");
        }
    }

    /**
     * Returns the number of the task in this list that is the same as
     * {@code task}, counting from 1, or nothing if there is no such task. This is
     * what lets an add refuse a duplicate and say which one it duplicates.
     */
    public OptionalInt findSame(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).isSameAs(task))
                .map(i -> i + 1)
                .findFirst();
    }

    /**
     * Returns the tasks falling within {@code range}, kept in list order.
     * Todos never match, since they have no date attached.
     */
    public List<Task> tasksIn(DateRange range) {
        // Collected into an ArrayList rather than with toList(), because callers are
        // handed a copy they are free to modify.
        return tasks.stream()
                .filter(task -> task.occursIn(range))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the tasks whose description contains {@code keyword}, kept in list
     * order. Only the description is searched, so a date is never matched.
     *
     * @param keyword the text the user is searching for.
     * @return the matching tasks, empty when nothing matches.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the tasks for reading only, e.g. to print them or write them to
     * disk. The view cannot be modified, so nothing outside this class can change
     * the list behind its back.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
