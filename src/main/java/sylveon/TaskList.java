package sylveon;

import java.util.ArrayList;
import java.util.Comparator;

/** Manages the collection of tasks in sylveon.Sylveon. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Adds a task to the list. */
    public void add(Task task) { tasks.add(task); }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) { return tasks.get(index); }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) { return tasks.remove(index); }

    /** Removes and returns the specified task. */
    public Task delete(Task task) {
        assert task != null : "Task to delete must not be null";
        int index = tasks.indexOf(task);
        return index == -1 ? null : tasks.remove(index);
    }

    /** Sorts all tasks alphabetically and returns the sorted tasks. */
    public ArrayList<Task> sortAlphabetically() {
        tasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
        return new ArrayList<>(tasks);
    }

    /**
     * Sorts tasks of the specified type and returns only the sorted tasks.
     * Other task types retain their positions in the main list.
     *
     * @param taskType the task type to sort
     * @return the sorted tasks of the requested type
     */
    public ArrayList<Task> sortByType(String taskType) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        Comparator<Task> comparator;

        switch (taskType) {
        case "deadline":
            comparator = Comparator.comparing((Task task) -> ((Deadline) task).getBy())
                    .thenComparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
            break;
        case "event":
            comparator = Comparator.comparing((Task task) -> ((Event) task).getFrom())
                    .thenComparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
            break;
        case "todo":
            comparator = Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
            break;
        default:
            throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }

        for (Task task : tasks) {
            if (isType(task, taskType)) {
                matchingTasks.add(task);
            }
        }
        matchingTasks.sort(comparator);

        int sortedIndex = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (isType(tasks.get(i), taskType)) {
                tasks.set(i, matchingTasks.get(sortedIndex));
                sortedIndex++;
            }
        }

        return matchingTasks;
    }

    private static boolean isType(Task task, String taskType) {
        return (taskType.equals("deadline") && task instanceof Deadline)
                || (taskType.equals("event") && task instanceof Event)
                || (taskType.equals("todo") && task instanceof Todo);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks whose descriptions contain the specified keyword.
     *
     * @param keyword keyword to search for
     * @return tasks whose descriptions contain the keyword
     */
    public ArrayList<Task> find(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    /** Returns whether there are no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks for display and storage operations.
     *
     * @return the task collection
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
