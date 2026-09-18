package martin.task;

import java.util.List;
import java.util.Objects;

import martin.exception.IllegalCommandException;
import martin.util.DateTimeUtil;

/** Represents a task that can be completed or left incomplete. */
public abstract class Task {
    /** The delimiter used to specify a task priority in a command input. */
    public static final String PRIORITY_DELIMITER = "/p";
    protected final String description;
    protected boolean isDone = false;
    private final TaskType taskType;
    private final Priority priority;

    /**
     * Constructs a {@code Task} with the specified description.
     *
     * @param description The description of the task.
     * @param taskType    The type of the task.
     * @param priority    The priority of the task.
     */
    protected Task(String description, TaskType taskType, Priority priority) {
        this.description = description;
        this.taskType = Objects.requireNonNull(taskType);
        this.priority = Objects.requireNonNull(priority);
    }

    /**
     * Creates the task specified by a task-creation command.
     *
     * @param input The complete command entered by the user.
     * @return The corresponding todo, deadline, or event.
     * @throws IllegalCommandException If the command is unknown or is missing a
     *                                 required value.
     */
    public static Task of(String input) {
        if (input.startsWith("todo")) {
            return Todo.parseTodoFromInputString(input);
        }
        if (input.startsWith("deadline")) {
            return Deadline.parseDeadlineFromInputString(input);
        }
        if (input.startsWith("event")) {
            return Event.parseEventFromInputString(input);
        }
        throw new IllegalCommandException("I'm sorry, I don't know what that means!");
    }

    /**
     * Separates an optional final priority parameter from task details.
     *
     * @param details The task details after the command word.
     * @return The task details without the priority parameter and its priority.
     * @throws IllegalCommandException If the priority parameter is repeated or
     *                                 invalid.
     */
    protected static BaseTaskDetails parsePriority(String details) {
        int delimiterIndex = details.indexOf(" " + PRIORITY_DELIMITER);
        int priorityIndex = details.startsWith(PRIORITY_DELIMITER)
                ? 0
                : (delimiterIndex < 0 ? -1 : delimiterIndex + 1);
        if (priorityIndex < 0) {
            return new BaseTaskDetails(details.trim(), Priority.LOW);
        }

        int nextPriorityIndex = details.indexOf(" " + PRIORITY_DELIMITER,
                priorityIndex + PRIORITY_DELIMITER.length());
        if (nextPriorityIndex >= 0) {
            throw new IllegalCommandException("A task can have only one /p priority.");
        }

        String priorityName = details.substring(priorityIndex + PRIORITY_DELIMITER.length()).trim();
        return new BaseTaskDetails(details.substring(0, priorityIndex).trim(), Priority.fromUserInput(priorityName));
    }

    /**
     * Returns this task's completion status, description and priority.
     *
     * @return The display representation of this task.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s", this.getDoneString(), this.priority, this.description);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon representing completion status.
     *
     * @return "X" if the task is done, or " " if not done.
     */
    public String getDoneString() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the type of this task.
     *
     * @return The task type.
     */
    public TaskType getTaskType() {
        return this.taskType;
    }

    /**
     * Returns the priority of this task.
     *
     * @return The task priority.
     */
    public Priority getPriority() {
        return this.priority;
    }

    /**
     * Prints the tasks in the order that the user entered them.
     *
     * @param tasks The list of tasks to print.
     */
    public static void printTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Returns the formatted string representation of this task for persistent
     * storage.
     *
     * @return The string formatted for file storage.
     */
    public abstract String toDataFormat();

    /**
     * Creates a {@code Task} instance by decoding a line from the storage file.
     *
     * @param line The line of text from the storage file.
     * @return The reconstructed {@code Task} instance.
     * @throws IllegalArgumentException If the stored line format is invalid or
     *                                  corrupted.
     */
    public static Task fromDataFormat(String line) {
        String[] parts = line.split(" \\| ", -1);

        // Validate minimum parts for all task types
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid task format in storage file: " + line);
        }

        TaskType taskType = TaskType.fromStorageCode(parts[0]);
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Priority priority = Priority.getPriorityFromStorage(taskType, parts);
        Task task = createTaskFromType(taskType, description, parts, priority);

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Creates a task instance based on the task type.
     *
     * @param taskType    The task type.
     * @param description The task description.
     * @param parts       The parsed parts from the storage line.
     * @param priority    The priority of the task.
     * @return The created task instance.
     * @throws IllegalArgumentException If the task type is unknown or format is
     *                                  invalid.
     */
    private static Task createTaskFromType(TaskType taskType, String description, String[] parts, Priority priority) {
        return switch (taskType) {
            case TODO -> new Todo(description, priority);
            case DEADLINE -> {
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Invalid deadline format in storage file.");
                }
                yield new Deadline(description, DateTimeUtil.parse(parts[3]), priority);
            }
            case EVENT -> {
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Invalid event format in storage file.");
                }
                yield new Event(description, DateTimeUtil.parse(parts[3]), DateTimeUtil.parse(parts[4]), priority);
            }
        };
    }

    /** Holds task details after separating an optional priority parameter. */
    protected record BaseTaskDetails(String details, Priority priority) {
    }
}
