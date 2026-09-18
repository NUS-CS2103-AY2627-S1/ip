package alfred.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import alfred.AlfredException;

/**
 * In-memory list of tasks, with operations to add, find, delete, and update them.
 */
public class TaskList {
    /** Message used when a live or archive index is out of range. */
    private static final String MESSAGE_MISSING_TASK = "that task number does not exist, sir.";

    /** Message used when archive-all or archive-index has no live tasks. */
    private static final String MESSAGE_NOTHING_TO_ARCHIVE = "there are no tasks to archive, sir.";

    /** Message used when restore or delete-archive has no archived tasks. */
    private static final String MESSAGE_NOTHING_ARCHIVED = "there are no archived tasks, sir.";

    /** Tasks stored in file order, including archived tasks. */
    private final List<Task> tasks;

    /** Snapshot used to restore in-memory state if saving fails. */
    private Snapshot snapshot;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks, in the same order.
     *
     * @param tasks Tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        // Storage.load() always returns a concrete list, even when the file is missing.
        assert tasks != null : "Initial task list should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Appends {@code task} to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        // AddCommand is only created with a parsed ToDo, Deadline, or Event.
        assert task != null : "Cannot add a null task to the list";
        tasks.add(task);
    }

    /**
     * Removes and returns the live task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Live-list index of the task to remove.
     * @return Removed task.
     * @throws AlfredException If the index is out of range.
     */
    public Task delete(int zeroBasedIndex) throws AlfredException {
        int backingIndex = backingIndexForLive(zeroBasedIndex);
        Task removed = tasks.remove(backingIndex);
        assert removed != null : "TaskList should not store null tasks";
        return removed;
    }

    /**
     * Permanently removes and returns the archived task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Archive-list index of the task to remove.
     * @return Removed task.
     * @throws AlfredException If there is no archived task at that index.
     */
    public Task deleteArchived(int zeroBasedIndex) throws AlfredException {
        if (getArchivedTasks().isEmpty()) {
            throw new AlfredException(MESSAGE_NOTHING_ARCHIVED);
        }
        int backingIndex = backingIndexForArchived(zeroBasedIndex);
        Task removed = tasks.remove(backingIndex);
        assert removed != null : "TaskList should not store null tasks";
        return removed;
    }

    /**
     * Returns the live task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Live-list index of the task to retrieve.
     * @return Task at that index.
     * @throws AlfredException If the index is out of range.
     */
    public Task get(int zeroBasedIndex) throws AlfredException {
        Task task = tasks.get(backingIndexForLive(zeroBasedIndex));
        assert task != null : "TaskList should not store null tasks";
        return task;
    }

    /**
     * Marks the live task at {@code zeroBasedIndex} as done.
     *
     * @param zeroBasedIndex Live-list index of the task to mark.
     * @return Updated task.
     * @throws AlfredException If the index is out of range.
     */
    public Task mark(int zeroBasedIndex) throws AlfredException {
        Task task = get(zeroBasedIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the live task at {@code zeroBasedIndex} as not done.
     *
     * @param zeroBasedIndex Live-list index of the task to unmark.
     * @return Updated task.
     * @throws AlfredException If the index is out of range.
     */
    public Task unmark(int zeroBasedIndex) throws AlfredException {
        Task task = get(zeroBasedIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Archives the live task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Live-list index of the task to archive.
     * @return Archived task.
     * @throws AlfredException If the index is out of range.
     */
    public Task archive(int zeroBasedIndex) throws AlfredException {
        Task task = get(zeroBasedIndex);
        task.archive();
        return task;
    }

    /**
     * Archives every live task.
     *
     * @return Number of tasks archived.
     * @throws AlfredException If there are no live tasks.
     */
    public int archiveAll() throws AlfredException {
        List<Task> liveTasks = getLiveTasks();
        if (liveTasks.isEmpty()) {
            throw new AlfredException(MESSAGE_NOTHING_TO_ARCHIVE);
        }
        for (int i = 0; i < liveTasks.size(); i++) {
            liveTasks.get(i).archive();
        }
        return liveTasks.size();
    }

    /**
     * Restores the archived task at {@code zeroBasedIndex} to the end of the live list.
     *
     * @param zeroBasedIndex Archive-list index of the task to restore.
     * @return Restored task.
     * @throws AlfredException If there is no archived task at that index.
     */
    public Task restore(int zeroBasedIndex) throws AlfredException {
        if (getArchivedTasks().isEmpty()) {
            throw new AlfredException(MESSAGE_NOTHING_ARCHIVED);
        }
        int backingIndex = backingIndexForArchived(zeroBasedIndex);
        Task task = tasks.remove(backingIndex);
        task.unarchive();
        tasks.add(task);
        return task;
    }

    /**
     * Restores every archived task to the end of the live list, in archive order.
     *
     * @return Number of tasks restored.
     * @throws AlfredException If there are no archived tasks.
     */
    public int restoreAll() throws AlfredException {
        List<Task> archivedTasks = new ArrayList<>(getArchivedTasks());
        if (archivedTasks.isEmpty()) {
            throw new AlfredException(MESSAGE_NOTHING_ARCHIVED);
        }
        for (int i = 0; i < archivedTasks.size(); i++) {
            Task task = archivedTasks.get(i);
            tasks.remove(task);
            task.unarchive();
            tasks.add(task);
        }
        return archivedTasks.size();
    }

    /** Returns the number of live, unarchived tasks. */
    public int size() {
        return getLiveTasks().size();
    }

    /**
     * Returns an unmodifiable view of live tasks in insertion order.
     *
     * @return Live tasks currently stored.
     */
    public List<Task> getTasks() {
        return getLiveTasks();
    }

    /**
     * Returns an unmodifiable view of archived tasks in file order.
     *
     * @return Archived tasks currently stored.
     */
    public List<Task> getArchivedTasks() {
        return tasks.stream()
                .filter(Task::isArchived)
                .toList();
    }

    /**
     * Returns an unmodifiable view of every stored task, including archived ones.
     *
     * @return All tasks in file order.
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns live tasks whose description contains {@code keyword}, ignoring case.
     * Date and time text is not searched.
     *
     * @param keyword Substring to look for in each description.
     * @return Matching live tasks in insertion order.
     */
    public List<Task> find(String keyword) {
        // Parser rejects a missing or blank find keyword before this is called.
        assert keyword != null && !keyword.isBlank() : "Find keyword should already be validated";
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        return getLiveTasks().stream()
                .filter(task -> {
                    String description = task.getDescription().toLowerCase(Locale.ENGLISH);
                    return description.contains(normalizedKeyword);
                })
                .toList();
    }

    /** Remembers the current order, archive flags, and completion statuses. */
    public void saveSnapshot() {
        snapshot = Snapshot.capture(tasks);
    }

    /** Restores the last snapshot, if one was saved. */
    public void restoreSnapshot() {
        if (snapshot == null) {
            return;
        }
        snapshot.applyTo(tasks);
        snapshot = null;
    }

    /** Forgets the last snapshot after a successful save. */
    public void discardSnapshot() {
        snapshot = null;
    }

    private List<Task> getLiveTasks() {
        return tasks.stream()
                .filter(task -> !task.isArchived())
                .toList();
    }

    private int backingIndexForLive(int liveIndex) throws AlfredException {
        int seen = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).isArchived()) {
                if (seen == liveIndex) {
                    return i;
                }
                seen++;
            }
        }
        throw new AlfredException(MESSAGE_MISSING_TASK);
    }

    private int backingIndexForArchived(int archiveIndex) throws AlfredException {
        int seen = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isArchived()) {
                if (seen == archiveIndex) {
                    return i;
                }
                seen++;
            }
        }
        throw new AlfredException(MESSAGE_MISSING_TASK);
    }

    /**
     * Captured order and per-task flags so a failed save can roll back memory.
     */
    private static class Snapshot {
        private final List<Task> order;
        private final boolean[] isArchived;
        private final boolean[] isDone;

        private Snapshot(List<Task> order, boolean[] isArchived, boolean[] isDone) {
            this.order = order;
            this.isArchived = isArchived;
            this.isDone = isDone;
        }

        private static Snapshot capture(List<Task> tasks) {
            List<Task> order = new ArrayList<>(tasks);
            boolean[] isArchived = new boolean[tasks.size()];
            boolean[] isDone = new boolean[tasks.size()];
            for (int i = 0; i < tasks.size(); i++) {
                isArchived[i] = tasks.get(i).isArchived();
                isDone[i] = tasks.get(i).isDone();
            }
            return new Snapshot(order, isArchived, isDone);
        }

        private void applyTo(List<Task> tasks) {
            tasks.clear();
            tasks.addAll(order);
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (isArchived[i]) {
                    task.archive();
                } else {
                    task.unarchive();
                }
                if (isDone[i]) {
                    task.markAsDone();
                } else {
                    task.markAsNotDone();
                }
            }
        }
    }
}
