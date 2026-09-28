package baron.core.task;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import baron.core.exception.BaronException;

/**
 * Represents a task that can be marked as completed.
 */
public abstract class Task {
    private final UUID uuid;
    private final TaskType taskType;
    private final String description;
    private boolean isDone;
    private TaskList requiredTasks;
    private TaskList unlockedTasks;

    /**
     * Creates a task with its immutable identity and an empty dependency set.
     *
     * @param uuid The task identifier.
     * @param taskType The kind of task.
     * @param description The task description.
     */
    protected Task(UUID uuid, TaskType taskType, String description) {
        assert uuid != null : "Tasks must have a task id";
        assert taskType != null : "Tasks must have a task type";
        assert description != null : "Task descriptions must not be null";
        this.uuid = uuid;
        this.taskType = taskType;
        this.description = description;
        isDone = false;
        requiredTasks = new TaskList();
        unlockedTasks = new TaskList();
    }

    /**
     * Returns this task's unique identifier.
     *
     * @return The task identifier.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return Whether this task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code X} for a completed task, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as completed.
     *
     * @return This task after updating its completion status.
     */
    public Task markAsDone() {
        isDone = true;
        return this;
    }

    /**
     * Marks this task as incomplete.
     *
     * @return This task after updating its completion status.
     */
    public Task markAsNotDone() {
        isDone = false;
        return this;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword The keyword to search for.
     * @return Whether the task description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Removes this task's current relationship with required tasks.
     */
    public void clearRequiredTasks() {
        for (Task requiredTask : requiredTasks.getTasks()) {
            requiredTask.unlockedTasks.deleteTask(this);
        }
        requiredTasks = new TaskList();
    }

    /**
     * Removes this task's current relationships with unlocked tasks.
     */
    public void clearUnlockedTasks() {
        for (Task unlockedTask : unlockedTasks.getTasks()) {
            unlockedTask.requiredTasks.deleteTask(this);
        }
        unlockedTasks = new TaskList();
    }

    /**
     * Sets the tasks that must be completed before this task.
     *
     * @param allTasks All tasks, used to format validation errors.
     * @param requiredTasks The prerequisite tasks.
     * @throws BaronException If the dependencies create a cycle.
     */
    public void setRequiredTasks(TaskList allTasks, TaskList requiredTasks) throws BaronException {
        assert requiredTasks != null : "Tasks required must not be null";
        clearRequiredTasks();
        for (Task requiredTask : requiredTasks.getTasks()) {
            if (isDone && !requiredTask.isDone) {
                clearRequiredTasks();
                throw new BaronException(String.format(
                        "%1$s requires %2$s, but %1$s is done and %2$s is not done",
                        allTasks.getTaskNumber(this),
                        allTasks.getTaskNumber(requiredTask)));
            }
            if (willUnlock(requiredTask)) {
                clearRequiredTasks();
                throw new BaronException(String.format(
                        "%1$s cannot require %2$s because %1$s unlocks %2$s",
                        allTasks.getTaskNumber(this),
                        allTasks.getTaskNumber(requiredTask)));
            }
            requiredTask.unlockedTasks.addTask(this);
            this.requiredTasks.addTask(requiredTask);
        }
    }

    /**
     * Sets the tasks that this task unlocks upon completion.
     *
     * @param allTasks All tasks, used to format validation errors.
     * @param unlockedTasks The dependent tasks.
     * @throws BaronException If the dependencies create a cycle.
     */
    public void setUnlockedTasks(TaskList allTasks, TaskList unlockedTasks) throws BaronException {
        assert unlockedTasks != null : "Tasks unlocked must not be null";
        clearUnlockedTasks();
        for (Task unlockedTask : unlockedTasks.getTasks()) {
            if (!isDone && unlockedTask.isDone) {
                clearUnlockedTasks();
                throw new BaronException(String.format(
                        "%1$s unlocks %2$s, but %1$s is not done and %2$s is done",
                        allTasks.getTaskNumber(this),
                        allTasks.getTaskNumber(unlockedTask)));
            }
            if (willRequire(unlockedTask)) {
                clearUnlockedTasks();
                throw new BaronException(String.format(
                        "%1$s cannot unlock %2$s because %1$s requires %2$s",
                        allTasks.getTaskNumber(this),
                        allTasks.getTaskNumber(unlockedTask)));
            }
            unlockedTask.requiredTasks.addTask(this);
            this.unlockedTasks.addTask(unlockedTask);
        }
    }

    /**
     * Returns every task that must be completed before this task.
     *
     * @return The direct and indirect prerequisite tasks.
     */
    protected TaskList getAllRequiredTasks() {
        if (requiredTasks.size() == 0) {
            return new TaskList();
        }
        Set<Task> allRequiredTasks = requiredTasks.getTasks().stream()
                .flatMap(requiredTask -> requiredTask.getAllRequiredTasks().getTasks().stream())
                .collect(Collectors.toSet());
        allRequiredTasks.addAll(requiredTasks.getTasks());
        return new TaskList(allRequiredTasks);
    }

    /**
     * Returns every task unlocked by completing this task.
     *
     * @return The direct and indirect dependent tasks.
     */
    protected TaskList getAllUnlockedTasks() {
        if (unlockedTasks.size() == 0) {
            return new TaskList();
        }
        Set<Task> allUnlockedTasks = unlockedTasks.getTasks().stream()
                .flatMap(unlockedTask -> unlockedTask.getAllUnlockedTasks().getTasks().stream())
                .collect(Collectors.toSet());
        allUnlockedTasks.addAll(unlockedTasks.getTasks());
        return new TaskList(allUnlockedTasks);
    }

    /**
     * Returns whether completing this task requires the specified task, directly or indirectly.
     *
     * @param task The task to look for.
     * @return Whether this task requires the specified task.
     */
    private boolean willRequire(Task task) {
        if (equals(task)) {
            return true;
        }
        return requiredTasks.getTasks().stream().anyMatch(requiredTask -> requiredTask.willRequire(task));
    }

    /**
     * Returns whether completing this task unlocks the specified task, directly or indirectly.
     *
     * @param task The task to look for.
     * @return Whether this task unlocks the specified task.
     */
    private boolean willUnlock(Task task) {
        if (equals(task)) {
            return true;
        }
        return unlockedTasks.getTasks().stream().anyMatch(unlockedTask -> unlockedTask.willUnlock(task));
    }

    /**
     * Returns this task's completion status and description.
     *
     * @return The formatted task details.
     */
    private String getTaskString() {
        return String.format("[%s][%s] %s", taskType.getFileCode(), getStatusIcon(), description);
    }

    /**
     * Returns this task in the file format used for persistent storage.
     *
     * @return The persistent representation of this task.
     */
    public String toFileString() {
        return String.join(
                " | ",
                uuid.toString(),
                taskType.getFileCode(),
                isDone ? "1" : "0",
                description,
                requiredTasks.toUuidString());
    }

    /**
     * Returns a user-facing representation containing this task's completion status and
     * description.
     *
     * @return The formatted task description.
     */
    @Override
    public String toString() {
        return getTaskString();
    }

    /**
     * Returns this task with its list number and optionally its relationships.
     *
     * @param allTasks All tasks, used to determine this task's number.
     * @param showRequiredTasks Whether to include prerequisite tasks.
     * @param showUnlockedTasks Whether to include dependent tasks.
     * @return The numbered task and requested relationships.
     */
    public String getNumberedTask(TaskList allTasks, boolean showRequiredTasks, boolean showUnlockedTasks) {
        String numberedTask = "task " + allTasks.getTaskNumber(this) + " " + this;
        numberedTask += showRequiredTasks ? getRequiredTasks(allTasks) : "";
        numberedTask += showUnlockedTasks ? getUnlockedTasks(allTasks) : "";
        return numberedTask;
    }

    private String getRequiredTasks(TaskList allTasks) {
        return requiredTasks.size() == 0 ? "" : requiredTasks.getTasks().stream()
                .map(requiredTask -> allTasks.getTaskNumber(requiredTask) + " " + requiredTask.getTaskString())
                .collect(Collectors.joining("\n", "\nrequires:\n", ""));
    }

    private String getUnlockedTasks(TaskList allTasks) {
        return unlockedTasks.size() == 0 ? "" : unlockedTasks.getTasks().stream()
                .map(unlockedTask -> allTasks.getTaskNumber(unlockedTask) + " " + unlockedTask.getTaskString())
                .collect(Collectors.joining("\n", "\nunlocks:\n", ""));
    }
}
