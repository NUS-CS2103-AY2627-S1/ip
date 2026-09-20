package larper.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import larper.exception.EmptyDeletionException;
import larper.exception.InvalidNumberDeleteException;
import larper.exception.NoFindException;
import larper.exception.TaskAlreadyMarkedException;
import larper.exception.TaskAlreadyUnmarkedException;

/**
 * Stores the tasks and provides operations for changing the task list.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list from existing tasks, such as tasks loaded from storage.
     *
     * @param tasks Existing tasks to manage.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "TaskList should wrap an existing task collection.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks currently in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the task list has no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns whether the specified one-based task number exists in the list.
     */
    public boolean hasTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= size();
    }

    /**
     * Returns the task with the specified one-based task number.
     */
    public Task getTask(int taskNumber) {
        assert hasTaskNumber(taskNumber) : "Caller should validate task number before retrieving a task.";
        return tasks.get(taskNumber - 1);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns a detached copy of this task list and its tasks.
     */
    public TaskList copy() {
        ArrayList<Task> copiedTasks = tasks.stream()
                .map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(copiedTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        assert task != null : "TaskList should not store null tasks.";
        tasks.add(task);
    }

    /**
     * Deletes and returns the task with the specified one-based task number.
     *
     * @throws EmptyDeletionException If the list is empty.
     * @throws InvalidNumberDeleteException If the task number is outside the list.
     */
    public Task deleteTask(int taskNumber) throws EmptyDeletionException, InvalidNumberDeleteException {
        if (isEmpty()) {
            throw new EmptyDeletionException();
        }
        if (!hasTaskNumber(taskNumber)) {
            throw new InvalidNumberDeleteException(size());
        }

        Task removedTask = tasks.remove(taskNumber - 1);
        assert removedTask != null : "Deleting a valid task number should return the removed task.";
        return removedTask;
    }

    /**
     * Returns tasks with descriptions containing the phrase or an exact matching tag, ignoring case.
     *
     * @throws NoFindException If no task description or tag matches the phrase.
     */
    public ArrayList<FindResult> findTasks(String phrase) throws NoFindException {
        assert phrase != null : "Find phrase should be an empty string instead of null.";
        String normalizedPhrase = normalizeFindPhrase(phrase);
        ArrayList<FindResult> results = IntStream.range(0, size())
                .filter(index -> !normalizedPhrase.isEmpty()
                        && tasks.get(index).matchesSearch(normalizedPhrase))
                .mapToObj(index -> new FindResult(index + 1, tasks.get(index)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (results.isEmpty()) {
            throw new NoFindException();
        }

        return results;
    }

    /**
     * Marks and returns the task with the specified one-based task number.
     *
     * @throws TaskAlreadyMarkedException If the task is already marked as done.
     */
    public Task markTask(int taskNumber) throws TaskAlreadyMarkedException {
        assert hasTaskNumber(taskNumber) : "Caller should validate task number before marking a task.";
        Task task = getTask(taskNumber);
        if (task.isDone()) {
            throw new TaskAlreadyMarkedException();
        }

        task.markAsDone();
        assert task.isDone() : "Task should be done after markAsDone is called.";
        return task;
    }

    /**
     * Unmarks and returns the task with the specified one-based task number.
     *
     * @throws TaskAlreadyUnmarkedException If the task is already not done.
     */
    public Task unmarkTask(int taskNumber) throws TaskAlreadyUnmarkedException {
        assert hasTaskNumber(taskNumber) : "Caller should validate task number before unmarking a task.";
        Task task = getTask(taskNumber);
        if (!task.isDone()) {
            throw new TaskAlreadyUnmarkedException();
        }

        task.unmarkAsDone();
        assert !task.isDone() : "Task should not be done after unmarkAsDone is called.";
        return task;
    }

    /**
     * Adds tags to the task with the specified one-based task number.
     *
     * @param taskNumber One-based task number.
     * @param tags Tags to add.
     * @return The updated task.
     */
    public Task tagTask(int taskNumber, Collection<String> tags) {
        assert hasTaskNumber(taskNumber) : "Caller should validate task number before tagging a task.";
        assert tags != null && !tags.isEmpty() : "Tagging should receive at least one tag.";
        Task task = getTask(taskNumber);
        task.addTags(tags);
        return task;
    }

    /**
     * Removes tags from the task with the specified one-based task number.
     *
     * @param taskNumber One-based task number.
     * @param tags Tags to remove.
     * @return The updated task.
     */
    public Task untagTask(int taskNumber, Collection<String> tags) {
        assert hasTaskNumber(taskNumber) : "Caller should validate task number before untagging a task.";
        assert tags != null && !tags.isEmpty() : "Untagging should receive at least one tag.";
        Task task = getTask(taskNumber);
        for (String tag : tags) {
            task.removeTag(tag);
        }
        return task;
    }

    /**
     * Returns tasks that contain the specified tag, ignoring tag case.
     *
     * @param tag Exact tag to find.
     * @return Matching tasks with their original task numbers.
     * @throws NoFindException If no task has the tag.
     */
    public ArrayList<FindResult> findTasksByTag(String tag) throws NoFindException {
        assert Task.isValidTag(Task.normalizeTag(tag)) : "Find tag should be validated before searching.";
        ArrayList<FindResult> results = new ArrayList<>();
        String normalizedTag = Task.normalizeTag(tag);
        int index = 0;
        while (index < size()) {
            Task task = tasks.get(index);
            if (task.hasTag(normalizedTag)) {
                results.add(new FindResult(index + 1, task));
            }
            index++;
        }

        if (results.isEmpty()) {
            throw new NoFindException();
        }
        return results;
    }

    private String normalizeFindPhrase(String phrase) {
        return phrase.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
