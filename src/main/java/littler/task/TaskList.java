package littler.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;
import littler.task.attribute.PriorityLevel;

/**
 * Manages an encapsulated list of tasks and handles internal task operations,
 * including bounds checking and querying tasks by date.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private ArrayList<Task> undoSnapshot; // null means nothing to undo

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks the initial list of tasks to manage
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Captures an independent snapshot of the current task list, suitable for restoring later.
     * Since tasks can be mutated in place (mark, tag, priority), this clones each task rather
     * than just copying the list itself.
     *
     * @return a new ArrayList of cloned tasks representing the current state
     */
    public ArrayList<Task> snapshotTasks() {
        ArrayList<Task> snapshot = new ArrayList<>();
        for (Task task : tasks) {
            snapshot.add(cloneTask(task));
        }
        return snapshot;
    }

    /**
     * Restores the task list to the most recently stored undo snapshot, then clears it,
     * so undo only ever reverts a single step.
     *
     * @throws LittleRException if there is no snapshot to restore
     */
    public void undo() throws LittleRException {
        if (undoSnapshot == null) {
            throw new LittleRException("There is nothing to undo.");
        }
        tasks.clear();
        tasks.addAll(undoSnapshot);
        undoSnapshot = null;
    }

    /**
     * Sets the given snapshot as the current undo point, replacing whatever was there before.
     *
     * @param snapshot the snapshot to store as the new undo point
     */
    public void setUndoSnapshot(ArrayList<Task> snapshot) {
        this.undoSnapshot = snapshot;
    }

    /**
     * Computes a snapshot of statistics across the current task list.
     *
     * @return the computed Statistics
     */
    public Statistics getStatistics() {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isMarked).count();
        int pending = total - completed;

        int todoCount = (int) tasks.stream().filter(t -> t instanceof Todo).count();
        int deadlineCount = (int) tasks.stream().filter(t -> t instanceof Deadline).count();
        int eventCount = (int) tasks.stream().filter(t -> t instanceof Event).count();

        int highCount = (int) tasks.stream().filter(t -> t.getPriority() == PriorityLevel.HIGH).count();
        int mediumCount = (int) tasks.stream().filter(t -> t.getPriority() == PriorityLevel.MEDIUM).count();
        int lowCount = (int) tasks.stream().filter(t -> t.getPriority() == PriorityLevel.LOW).count();
        int noPriorityCount = total - highCount - mediumCount - lowCount;

        int taggedCount = (int) tasks.stream().filter(t -> !t.getTags().isEmpty()).count();
        int untaggedCount = total - taggedCount;

        return new Statistics(total, completed, pending, todoCount, deadlineCount, eventCount,
            highCount, mediumCount, lowCount, noPriorityCount, taggedCount, untaggedCount);
    }

    /**
     * Replaces the task at the specified 0-based index with a new task instance.
     *
     * @param index the index of the task to replace
     * @param updatedTask the new task to put at that index
     * @return the updated task
     * @throws LittleRException if the index is out of bounds
     */
    public Task update(int index, Task updatedTask) throws LittleRException {
        checkIndex(index);
        tasks.set(index, updatedTask);
        return updatedTask;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to be added
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes all tasks from the list.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Removes and returns the task at the specified 0-based index.
     *
     * @param index the index of the task to be removed
     * @return the removed task
     * @throws LittleRException if the index is out of bounds
     */
    public Task delete(int index) throws LittleRException {
        checkIndex(index);
        assert index >= 0 && index < tasks.size() : "checkIndex should have already validated this index";
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified 0-based index as completed.
     *
     * @param index the index of the task to be marked
     * @return the marked task
     * @throws LittleRException if the index is out of bounds
     */
    public Task mark(int index) throws LittleRException {
        checkIndex(index);
        assert index >= 0 && index < tasks.size() : "checkIndex should have already validated this index";
        Task task = tasks.get(index);
        task.mark();
        return task;
    }

    /**
     * Unmarks the task at the specified 0-based index, setting it to incomplete.
     *
     * @param index the index of the task to be unmarked
     * @return the unmarked task
     * @throws LittleRException if the index is out of bounds
     */
    public Task unmark(int index) throws LittleRException {
        checkIndex(index);
        assert index >= 0 && index < tasks.size() : "checkIndex should have already validated this index";
        Task task = tasks.get(index);
        task.unmark();
        return task;
    }

    /**
     * Retrieves the task at the specified 0-based index.
     *
     * @param index the index of the task to retrieve
     * @return the task at the specified index
     * @throws LittleRException if the index is out of bounds
     */
    public Task get(int index) throws LittleRException {
        checkIndex(index);
        assert index >= 0 && index < tasks.size() : "checkIndex should have already validated this index";
        return tasks.get(index);
    }

    /**
     * Returns the most recently added task in the list.
     *
     * @return the last task in the task list
     */
    public Task getLast() {
        assert !tasks.isEmpty() : "getLast() should not be called on an empty task list";
        return tasks.get(tasks.size() - 1);
    }

    /**
     * Returns the total number of tasks currently in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the task list is completely empty.
     *
     * @return true if there are no tasks in the list; false otherwise
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Filters and returns all schedulable tasks that occur on or match the given date.
     *
     * @param date the parsed date to check against task occurrences
     * @return an ArrayList containing all matching Schedulable tasks
     */
    public ArrayList<Task> getTasksOn(ParsedDateTime date) {
        return tasks.stream()
            .filter(task -> task instanceof Schedulable schedulable && schedulable.isOccurringOn(date))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all tasks whose description contains the given keyword,
     * matched case-insensitively as a substring (partial matches included).
     *
     * @param keyword the search term to match against task descriptions
     * @return a list of tasks whose description contains the keyword
     */
    public ArrayList<Task> findByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
            .filter(task -> task.getName().toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the underlying list of tasks for storage or UI display operations.
     *
     * @return the internal ArrayList of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Validates whether the given index falls within the valid bounds of the task list.
     *
     * @param index the 0-based index to validate
     * @throws LittleRException if the index is negative or greater than or equal to the list size
     */
    private void checkIndex(int index) throws LittleRException {
        if (index < 0 || index >= tasks.size()) {
            throw new LittleRException("That task number doesn't exist.");
        }
    }

    /**
     * Checks whether a task with the same type, description, and date(s) (if any)
     * already exists in the list.
     *
     * @param task the task to check for duplicates against
     * @return true if an equivalent task already exists; false otherwise
     */
    public boolean containsDuplicate(Task task) {
        return tasks.stream().anyMatch(existing -> existing.equals(task));
    }

    /**
     * Returns a new list of all tasks sorted alphabetically by their first (earliest-added) tag.
     * Untagged tasks are placed after all tagged tasks in ascending order (this flips, along with
     * everything else, when descending is true). The underlying task list is left unchanged.
     *
     * @param descending if true, reverses the sort order
     * @return a new sorted ArrayList of tasks
     */
    public ArrayList<Task> getSortedByTag(boolean descending) {
        Comparator<Task> comparator = TaskList::compareByTag;
        if (descending) {
            comparator = comparator.reversed();
        }
        return tasks.stream().sorted(comparator).collect(Collectors.toCollection(ArrayList::new));
    }

    private static int compareByTag(Task a, Task b) {
        Optional<String> aTag = a.getFirstTag();
        Optional<String> bTag = b.getFirstTag();
        if (aTag.isPresent() != bTag.isPresent()) {
            return aTag.isPresent() ? -1 : 1;
        }
        if (aTag.isEmpty()) {
            return 0;
        }
        return aTag.get().compareToIgnoreCase(bTag.get());
    }

    /**
     * Returns a new list of all tasks sorted by their priority levels.
     * Tasks without a priority are considered lowest and are placed after all prioritized tasks.
     * The underlying task list is left unchanged.
     *
     * @param isDescending if true, sorts from highest to lowest priority; if false, lowest to highest
     * @return a new sorted ArrayList of tasks
     */
    public ArrayList<Task> getSortedByPriority(boolean isDescending) {
        Comparator<Task> byOrdinal = Comparator.comparingInt(task -> task.getPriority().ordinal());
        if (isDescending) {
            byOrdinal = byOrdinal.reversed();
        }
        Comparator<Task> nullsLast = Comparator.comparing(task -> task.getPriority() == null);
        return tasks.stream()
            .sorted(nullsLast.thenComparing(byOrdinal))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a new list of all tasks sorted chronologically by their relevant date.
     * Tasks without a date (Todos) are placed after all dated tasks, sorted alphabetically
     * among themselves. The underlying task list is left unchanged.
     *
     * @param descending if true, sorts dated tasks latest-first; if false, earliest-first
     * @return a new sorted ArrayList of tasks
     */
    public ArrayList<Task> getSortedByDate(boolean descending) {
        Comparator<Task> comparator = TaskList::compareByDate;
        if (descending) {
            comparator = comparator.reversed();
        }
        return tasks.stream()
            .sorted(comparator)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a new list of all tasks sorted alphabetically by description.
     * The underlying task list is left unchanged.
     *
     * @param descending if true, sorts Z to A; if false, A to Z
     * @return a new sorted ArrayList of tasks
     */
    public ArrayList<Task> getSortedByName(boolean descending) {
        Comparator<Task> comparator = Comparator.comparing(task -> task.getName().toLowerCase());
        if (descending) {
            comparator = comparator.reversed();
        }
        return tasks.stream()
            .sorted(comparator)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Compares two tasks for date-based ordering: tasks with a date (Deadline, Event) sort
     * before tasks without one (Todo). Dated tasks are compared by their date; undated tasks
     * are compared alphabetically among themselves.
     */
    private static int compareByDate(Task a, Task b) {
        boolean isDateOfTaskA = a instanceof Schedulable;
        boolean isDateOfTaskB = b instanceof Schedulable;

        if (isDateOfTaskA != isDateOfTaskB) {
            return isDateOfTaskA ? -1 : 1;
        }
        if (isDateOfTaskA) {
            ParsedDateTime aDate = ((Schedulable) a).getSortDate();
            ParsedDateTime bDate = ((Schedulable) b).getSortDate();
            return aDate.compareTo(bDate);
        }
        return a.getName().compareToIgnoreCase(b.getName());
    }

    /**
     * Creates a deep copy of the given task. This is necessary because tasks can be mutated
     * in place (e.g., marking, tagging, changing priority), and we want to ensure that
     * snapshots for undo functionality are independent of the current state.
     *
     * @param task the task to clone
     * @return a new Task instance that is a copy of the original
     */
    private static Task cloneTask(Task task) {
        try {
            // withUpdates() with no field overrides still produces a fully independent
            // copy, since marked status, priority, and tags are always carried over
            return task.withUpdates(Collections.emptyMap());
        } catch (LittleRException e) {
            throw new AssertionError("Cloning a task with no field overrides should never fail", e);
        }
    }
}
