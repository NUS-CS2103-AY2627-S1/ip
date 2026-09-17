package jonathan.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import jonathan.JonathanException;

/**
 * Represents and manages the in-memory list of tasks.
 * Provides operations to add, delete, retrieve, and track the size of the task list.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private Task[] tasks;
    private int itemCount;

    /**
     * Constructs an empty {@code TaskList} with a maximum capacity of 100 tasks.
     */
    public TaskList() {
        this.tasks = new Task[MAX_TASKS];
        this.itemCount = 0;
    }

    /**
     * Constructs a task list by rebuilding tasks from saved text lines.
     *
     * @param fileLines raw lines from the save file, each representing one task
     * @throws JonathanException if a saved task cannot be reconstructed
     */
    public TaskList(List<String> fileLines) throws JonathanException {
        this.tasks = new Task[MAX_TASKS];
        this.itemCount = 0;

        for (String line : fileLines) {
            String[] parts = line.split(" \\| ");
            if (parts.length < 3) {
                continue;
            }

            String type = parts[0];
            String status = parts[1];
            String description = parts[2];
            Task task = null;

            switch (type) {
                case "T":
                    task = new ToDo(description);
                    break;
                case "D":
                    if (parts.length >= 4) {
                        LocalDate parsedDate = LocalDate.parse(
                                parts[3], DateTimeFormatter.ofPattern("MMM d yyyy"));
                        task = new Deadlines(description, parsedDate.toString());
                    }
                    break;
                case "E":
                    if (parts.length >= 5) {
                        LocalDate fromDate = LocalDate.parse(
                                parts[3], DateTimeFormatter.ofPattern("MMM d yyyy"));
                        LocalDate toDate = LocalDate.parse(
                                parts[4], DateTimeFormatter.ofPattern("MMM d yyyy"));
                        task = new Event(description, fromDate.toString(), toDate.toString());
                    }
                    break;
                default:
                    throw new JonathanException("Unknown task type in the saved data: " + type);
            }

            if (task != null) {
                if (status.equals("1")) {
                    task.markAsDone();
                }
                int priorityIndex = switch (type) {
                    case "T" -> 3;
                    case "D" -> 4;
                    case "E" -> 5;
                    default -> -1;
                };
                if (parts.length > priorityIndex) {
                    try {
                        task.setPriority(Priority.fromInput(parts[priorityIndex]));

                    } catch (IllegalArgumentException exception) {
                        throw new JonathanException("Invalid priority in saved data");
                    }
                }
                addTask(task);
            }
        }
    }

    /**
     * Adds a new task to the list and increments the item count.
     *
     * @param task The {@code Task} to be added to the list.
     */
    public void addTask(Task task) {
        assert task != null : "Task must not be null";
        assert itemCount < tasks.length : "Task list must have capacity";
        this.tasks[itemCount] = task;
        this.itemCount++;

        assertValidState();
    }

    /**
     * Checks whether the task list has reached its maximum capacity.
     *
     * @return true if no additional tasks can be added
     */
    public boolean isFull() {
        return itemCount >= tasks.length;
    }

    /**
     * Returns all tasks with the specified priority without changing their order.
     *
     * @param priority priority to filter by
     * @return tasks with the requested priority
     */
    public List<Task> getTasksByPriority(Priority priority) {
        assert priority != null : "Priority must not be null";
        return Arrays.stream(tasks, 0, itemCount)
                .filter(task -> task.getPriority() == priority)
                .toList();
    }

    /**
     * Returns a sorted view of the active tasks from high priority to low priority.
     * Tasks without a priority are placed after low-priority tasks.
     * Tasks with the same priority retain their original order.
     *
     * @return sorted view of the active tasks
     */
    public List<Task> getTasksSortedByPriority() {
        return Arrays.stream(tasks, 0, itemCount)
                .sorted(Comparator.comparingInt(TaskList::getPriorityRank))
                .toList();
    }

    private static int getPriorityRank(Task task) {
        return task.getPriority() == Priority.NONE
                ? Integer.MAX_VALUE
                : task.getPriority().getLevel();
    }


    /**
     * Deletes the task at the specified index, shifts the remaining tasks to fill the gap,
     * and decrements the item count.
     *
     * @param index The zero-based index of the task to be removed.
     * @return The {@code Task} that was successfully removed from the list.
     */
    public Task deleteTask(int index) {
        assert index >= 0 && index < itemCount : "Delete index must be valid";
        Task removedTask = tasks[index];
        for (int i = index; i < itemCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[itemCount - 1] = null;
        itemCount--;
        assertValidState();
        return removedTask;
    }

    /**
     * Retrieves the task located at the specified index without removing it.
     *
     * @param index The zero-based index of the desired task.
     * @return The {@code Task} at the specified index.
     */
    public Task getTask(int index) {
        assert index >= 0 && index < itemCount : "Task index must be valid";
        return tasks[index];
    }

    /**
     * Gets the total number of active tasks currently stored in the list.
     *
     * @return The integer count of current tasks.
     */
    public int getSize() {
        return this.itemCount;
    }

    /**
     * Retrieves the underlying array containing all tasks.
     *
     * @return The array of {@code Task} objects.
     */
    public Task[] getAllTasks() {
        return this.tasks;
    }

    private void assertValidState() {
        assert itemCount >= 0 && itemCount <= tasks.length
                : "Task count must remain within capacity";
        for (int i = 0; i < itemCount; i++) {
            assert tasks[i] != null : "Active task slots must not be null";
        }
        for (int i = itemCount; i < tasks.length; i++) {
            assert tasks[i] == null : "Unused task slots must be null";
        }
    }

}
