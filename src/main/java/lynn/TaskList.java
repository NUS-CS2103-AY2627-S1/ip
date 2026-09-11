package lynn;

import java.util.stream.IntStream;

/** Stores and manages Lynn's fixed-capacity task list. */
class TaskList {
    static final int MAX_TASKS = 100;

    private final Task[] tasks;

    private int taskCount;

    /** Creates an empty task list with the fixed project capacity. */
    TaskList() {
        this(new Task[MAX_TASKS]);
    }

    /** Creates a task list from the contiguous tasks loaded from storage. */
    TaskList(Task[] tasks) {
        this.tasks = tasks;
        taskCount = countTasks(tasks);
    }

    /** Adds a task at the end of the list. */
    void add(Task task) throws LynnException {
        assert task != null : "task must not be null";
        assert taskCount >= 0 && taskCount <= MAX_TASKS : "task count must stay within capacity";
        if (taskCount == MAX_TASKS) {
            throw new LynnException("Your task list is full.");
        }
        tasks[taskCount] = task;
        taskCount++;
        assert tasks[taskCount - 1] == task : "new task must be stored at the end";
    }

    Task get(int index) {
        assert index >= 0 && index < taskCount : "task index must refer to an existing task";
        assert tasks[index] != null : "existing task slots must not be null";
        return tasks[index];
    }

    /** Removes the task at an already-validated zero-based index. */
    Task delete(int index) {
        assert index >= 0 && index < taskCount : "task index must refer to an existing task";
        assert tasks[index] != null : "existing task slots must not be null";
        Task deletedTask = tasks[index];
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        taskCount--;
        tasks[taskCount] = null;
        return deletedTask;
    }

    /** Inserts a task at an existing zero-based index, restoring a prior deletion if needed. */
    void insert(int index, Task task) {
        assert index >= 0 && index <= taskCount : "insert index must be within the task list";
        assert task != null : "task must not be null";
        assert taskCount < MAX_TASKS : "task list must have room for an inserted task";
        for (int i = taskCount; i > index; i--) {
            tasks[i] = tasks[i - 1];
        }
        tasks[index] = task;
        taskCount++;
    }

    Task[] find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "search keyword must not be blank";
        return IntStream.range(0, taskCount)
                .mapToObj(i -> tasks[i])
                .filter(task -> task.containsKeyword(keyword))
                .toArray(Task[]::new);
    }

    int size() {
        return taskCount;
    }

    /** Counts the contiguous non-null tasks that were loaded from storage. */
    private static int countTasks(Task[] loadedTasks) {
        int loadedTaskCount = 0;
        while (loadedTaskCount < loadedTasks.length && loadedTasks[loadedTaskCount] != null) {
            loadedTaskCount++;
        }
        return loadedTaskCount;
    }
}
