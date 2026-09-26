package dulio.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

import dulio.storage.Storage;

/**
 * Manages the task collection and persists changes through Storage.
 */
public class TaskList {
    private ArrayList<Task> tasks;
    private Storage storage;

    /**
     * Creates a task list backed by the default data file.
     */
    public TaskList() {
        this(new Storage(Path.of("data", "dulio.txt")));
    }

    /**
     * Creates a task list using the supplied persistence service.
     *
     * @param storage The persistence service to use.
     */
    public TaskList(Storage storage) {
        assert storage != null : "Task list storage must not be null";
        this.storage = storage;
        tasks = storage.load();
        assert tasks != null : "Storage must return a task list";
        assert !tasks.contains(null) : "Loaded task list must not contain null tasks";
    }

    /**
     * Adds and persists a task.
     *
     * @param task The task to add.
     * @throws IOException If the task list cannot be saved.
     */
    public void add(Task task) throws IOException {
        assert task != null : "Task to add must not be null";
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Adding a task must increase the list size by one";
        storage.save(tasks);
    }

    /**
     * Returns whether a task with the same type and details already exists.
     *
     * @param task The task to compare against existing tasks.
     * @return True if an equivalent task exists.
     */
    public boolean containsEquivalent(Task task) {
        assert task != null : "Task to compare must not be null";
        for (Task existingTask : tasks) {
            if (existingTask.getTypeIcon().equals(task.getTypeIcon())
                    && existingTask.toString().equals(task.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of tasks.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks as numbered display lines.
     *
     * @return The formatted task list.
     */
    public String list() {
        return formatTasks(tasks);
    }

    /**
     * Returns all tasks matching a keyword as numbered display lines (case-insensitive).
     *
     * @param keyword The search keyword.
     * @return The formatted matching tasks.
     */
    public String find(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        ArrayList<Task> matchingTasks = new ArrayList<>();
        ArrayList<Integer> matchingNumbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase(Locale.ENGLISH).contains(normalizedKeyword)) {
                matchingTasks.add(tasks.get(i));
                matchingNumbers.add(i + 1);
            }
        }
        return formatTasks(matchingTasks, matchingNumbers);
    }

    private String formatTasks(ArrayList<Task> tasksToFormat) {
        ArrayList<Integer> taskNumbers = new ArrayList<>();
        for (int i = 0; i < tasksToFormat.size(); i++) {
            taskNumbers.add(i + 1);
        }
        return formatTasks(tasksToFormat, taskNumbers);
    }

    private String formatTasks(ArrayList<Task> tasksToFormat, ArrayList<Integer> taskNumbers) {
        assert tasksToFormat.size() == taskNumbers.size()
                : "Each formatted task must have exactly one display number";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasksToFormat.size(); i++) {
            Task task = tasksToFormat.get(i);
            sb.append(" ").append(taskNumbers.get(i)).append(".[").append(task.getTypeIcon()).append("][")
                .append(task.getStatusIcon()).append("] ").append(task.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Marks a numbered task as completed and persists the change.
     *
     * @param taskNumber The one-based task number.
     * @return The marked task, or null if the number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    public Task mark(int taskNumber) throws IOException {
        int idx = taskNumber - 1;
        if (idx < 0 || idx >= tasks.size()) {
            return null;
        }
        tasks.get(idx).markAsDone();
        assert tasks.get(idx).isDone() : "Marked task must be completed";
        storage.save(tasks);
        return tasks.get(idx);
    }

    /**
     * Marks a numbered task as incomplete and persists the change.
     *
     * @param taskNumber The one-based task number.
     * @return The unmarked task, or null if the number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    public Task unmark(int taskNumber) throws IOException {
        int idx = taskNumber - 1;
        if (idx < 0 || idx >= tasks.size()) {
            return null;
        }
        tasks.get(idx).markAsNotDone();
        assert !tasks.get(idx).isDone() : "Unmarked task must be incomplete";
        storage.save(tasks);
        return tasks.get(idx);
    }

    /**
     * Deletes and returns a numbered task, or null for an invalid number.
     *
     * @param taskNumber The one-based task number.
     * @return The deleted task, or null if the number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    public Task delete(int taskNumber) throws IOException {
        int idx = taskNumber - 1;
        if (idx < 0 || idx >= tasks.size()) {
            return null;
        }
        int previousSize = tasks.size();
        Task deletedTask = tasks.remove(idx);
        assert deletedTask != null : "Deleted task must not be null";
        assert tasks.size() == previousSize - 1 : "Deleting a task must decrease the list size by one";
        storage.save(tasks);
        return deletedTask;
    }
}
