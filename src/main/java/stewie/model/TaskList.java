package stewie.model;

import java.util.ArrayList;
import java.util.Arrays;

import stewie.storage.Storage;

/**
 * Manages ordered tasks and publishes changes only after a successful save.
 * Presentation and user-facing error messages belong to the calling interface.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final Storage storage;
    private long revision;

    /**
     * Creates a task list with data loaded from the default storage file.
     */
    public TaskList() {
        this(new Storage());
    }

    /**
     * Creates a task list using supplied storage.
     *
     * @param storage Storage instance used for loading and saving.
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        this.tasks = storage.loadFromDisk();
        assert this.tasks != null : "Storage must return a task list";
    }

    /**
     * Returns the revision of the current task list to detect outdated GUI controls.
     *
     * @return Revision incremented after every successfully saved change.
     */
    public long getRevision() {
        return revision;
    }

    /**
     * Returns startup storage warnings for display in either interface.
     *
     * @return Warning, or an empty string if loading succeeded.
     */
    public String getLoadWarning() {
        return storage.getLoadWarning();
    }

    /**
     * Adds a task of type todo to the task list.
     * Saves the task to the disk.
     *
     * @param description Description of the task.
     */
    public void addToDo(String description) {
        addTask(new ToDo(description));
    }

    /**
     * Adds a task of type event to the task list.
     * Saves the event to the disk.
     *
     * @param description Description of the event.
     * @param from Start date of the event in a supported format.
     * @param to End date of the event in a supported format.
     */
    public void addEvent(String description, String from, String to) {
        addTask(new Event(description, from, to));
    }

    /**
     * Adds a task of type deadline to the task list.
     * Saves the task with deadline to the disk.
     *
     * @param description Description of the deadline task.
     * @param deadline Deadline date of the task.
     */
    public void addDeadline(String description, String deadline) {
        addTask(new Deadline(description, deadline));
    }

    /**
     * Adds a task and persists the updated list before publishing the change.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        rejectDuplicate(task, -1);
        ArrayList<Task> proposed = new ArrayList<>(tasks);
        proposed.add(task);
        persist(proposed);
    }

    /**
     * Marks a task as done.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be marked as done.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public void markAsDone(int index) {
        changeStatus(index, true);
    }

    /**
     * Marks a task as not done.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be marked as not done.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public void markAsUndone(int index) {
        changeStatus(index, false);
    }

    /**
     * Deletes a task from the task list.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be deleted.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public void deleteTask(int index) {
        ArrayList<Task> proposed = new ArrayList<>(tasks);
        proposed.remove(index);
        persist(proposed);
    }

    /**
     * Updates a task description while preserving its type, metadata, and completion status.
     *
     * @param index Index of the task to update.
     * @param description Replacement description.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public void updateTask(int index, String description) {
        updateTask(index, description, null, null, null);
    }

    /**
     * Updates selected fields of a task while preserving all omitted fields.
     *
     * @param index Index of the task to update.
     * @param description Replacement description, or null to preserve it.
     * @param deadlineValue Replacement deadline, or null to preserve it.
     * @param from Replacement event start date, or null to preserve it.
     * @param to Replacement event end date, or null to preserve it.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public void updateTask(int index, String description, String deadlineValue, String from, String to) {
        Task updatedTask = TaskUpdater.update(tasks.get(index), description, deadlineValue, from, to);
        rejectDuplicate(updatedTask, index);
        replaceTask(index, updatedTask);
    }

    /**
     * Saves a replacement in a proposed list before publishing it to the live list.
     */
    private void replaceTask(int index, Task replacement) {
        ArrayList<Task> proposed = new ArrayList<>(tasks);
        proposed.set(index, replacement);
        persist(proposed);
    }

    /**
     * Publishes a proposed task list only after storage confirms a successful save.
     */
    private void persist(ArrayList<Task> proposed) {
        storage.saveToDisk(proposed);
        tasks.clear();
        tasks.addAll(proposed);
        revision++;
    }

    /**
     * Copies a task before changing its status so failed saves cannot mutate the live list.
     */
    private void changeStatus(int index, boolean isDone) {
        replaceTask(index, TaskUpdater.withStatus(tasks.get(index), isDone));
    }

    /**
     * Rejects duplicates while allowing an update to preserve its own details.
     */
    private void rejectDuplicate(Task candidate, int ignoredIndex) {
        for (int index = 0; index < tasks.size(); index++) {
            if (index != ignoredIndex && candidate.hasSameDetails(tasks.get(index))) {
                throw new IllegalArgumentException(
                        "A task with these details already exists (task " + (index + 1) + ").");
            }
        }
    }

    /**
     * Returns an ordered snapshot of the formatted tasks.
     *
     * @return Tasks as a string array.
     */
    public String[] produceTaskList() {
        String[] taskDescriptions = new String[this.tasks.size()];

        for (int i = 0; i < this.tasks.size(); i++) {
            assert this.tasks.get(i) != null : "A task list must not contain null tasks";
            taskDescriptions[i] = this.tasks.get(i).toString();
        }
        return taskDescriptions;
    }

    /**
     * Returns formatted tasks containing at least one supplied keyword.
     * Matches are case-sensitive and include task types, completion markers, descriptions, and dates.
     *
     * @param keywords Keywords that tasks must contain.
     * @return Array of task strings.
     */
    public String[] findTasks(String... keywords) {
        ArrayList<String> matchingTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (Arrays.stream(keywords).anyMatch(task.toString()::contains)) {
                matchingTasks.add(task.toString());
            }
        }
        return matchingTasks.toArray(String[]::new);
    }
}
