package nubish.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import nubish.tasks.Deadline;
import nubish.tasks.Task;

/**
 * Stores and provides access to the current tasks.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the end of the list.
     *
     * @param newTasks tasks to add
     */
    public void add(Task... newTasks) {
        for (Task newTask : newTasks) {
            validateUniqueDescription(newTask);
        }
        validateUniqueNewDescriptions(newTasks);
        tasks.addAll(Arrays.asList(newTasks));
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to retrieve
     * @return task at the given index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    private void validateUniqueDescription(Task newTask) {
        boolean hasDuplicateDescription = tasks.stream()
                .anyMatch(task -> task.getDescription().equalsIgnoreCase(newTask.getDescription()));
        if (hasDuplicateDescription) {
            throw new NubishException("Hrmmm... A task with the same details already exists.");
        }
    }

    private void validateUniqueNewDescriptions(Task... newTasks) {
        for (int i = 0; i < newTasks.length; i++) {
            for (int j = i + 1; j < newTasks.length; j++) {
                if (newTasks[i].getDescription().equalsIgnoreCase(newTasks[j].getDescription())) {
                    throw new NubishException("Hrmmm... A task with the same details already exists.");
                }
            }
        }
    }

    /**
     * Returns a task list containing tasks whose descriptions contain the keyword.
     *
     * @param keyword text to search for in task descriptions
     * @return task list containing matching tasks
     */
    public TaskList find(String keyword) {
        TaskList tasklist = new TaskList();
        Stream<Task> taskStream = this.tasks.stream().filter(task -> task.getDescription().contains(keyword));

        tasklist.add(taskStream.toList().toArray(new Task[0]));

        return tasklist;
    }

    /**
     * Returns a task list containing tasks whose deadlines are today.
     */
    public TaskList remind() {
        TaskList tasklist = new TaskList();
        Stream<Task> taskStream = this.tasks.stream().filter(task -> task instanceof Deadline)
                .filter(task -> ((Deadline) task).getDeadline().toLocalDate().isEqual(LocalDate.now()));

        tasklist.add(taskStream.toList().toArray(new Task[0]));

        return tasklist;
    }

    @Override
    public String toString() {
        if (this.tasks.isEmpty()) {
            return "";
        }

        StringBuilder list = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task t = this.get(i);
            list.append(String.format("%d. %s\n", i + 1, t.toString()));
        }

        return (list.toString() + "\n");
    }
}
