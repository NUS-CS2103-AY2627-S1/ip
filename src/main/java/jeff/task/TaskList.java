package jeff.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Wraps the in-memory list of tasks, providing the operations needed to
 * add, remove, and look up tasks in it.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list pre-populated with the given tasks, e.g. loaded from {@link jeff.storage.Storage}. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds each given task, in order, to the end of the list. */
    public void add(Task... tasksToAdd) {
        // A null element here would sit silently in the list until it NPEs much later
        // (e.g. when Storage or Ui tries to use it), far from where the real mistake was
        // made. Every current caller (Jeff.addTask) only ever passes freshly constructed,
        // non-null tasks, so a null getting through would indicate a bug in this code base,
        // not something a user could trigger.
        assert tasksToAdd != null : "tasksToAdd must not be null";
        assert Arrays.stream(tasksToAdd).noneMatch(Objects::isNull) : "tasksToAdd must not contain null tasks";
        Collections.addAll(tasks, tasksToAdd);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        // By the time this is called, Jeff has already turned the user's 1-based task
        // number into a 0-based index via Parser.parseTaskIndex, which throws
        // IndexOutOfBoundsException on anything out of range. So an out-of-range index
        // reaching here would mean a caller skipped that validation, which is a
        // programming error rather than something a user typed.
        assert index >= 0 && index < tasks.size() : "index should already be range-checked by the caller";
        return tasks.remove(index);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        // Same assumption as remove(int): the caller is expected to have range-checked
        // index already (see Parser.parseTaskIndex).
        assert index >= 0 && index < tasks.size() : "index should already be range-checked by the caller";
        return tasks.get(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying tasks, e.g. for {@link jeff.storage.Storage} to persist
     * or {@link jeff.ui.Ui} to display.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /** Returns the tasks whose description contains the given keyword, in list order. */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Sorts the tasks in place so that Deadlines and Events come first, ordered chronologically
     * by {@link Task#getChronologicalDate()}, followed by every task with no date (e.g. Todos),
     * which keep their original relative order since there's no date to sort them by.
     */
    public void sortByDate() {
        Comparator<Task> byDate = Comparator.comparing(TaskList::chronologicalKey, Comparator.nullsLast(
                Comparator.naturalOrder()));
        tasks.sort(byDate);
    }

    /** Returns the date {@code task} should be sorted by, or {@code null} if it has none. */
    private static LocalDate chronologicalKey(Task task) {
        return task.getChronologicalDate().orElse(null);
    }
}
