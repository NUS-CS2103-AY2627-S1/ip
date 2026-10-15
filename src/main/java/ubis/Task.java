package ubis;

import java.util.Arrays;

/**
 * Represents an abstract task with a name, completion status, and task type.
 */
public abstract class Task {
    private static final String COMPLETED_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";

    protected String name;
    protected boolean isDone;
    protected TaskType type;
    private String initialisationError;

    /**
     * Constructs a default Task instance.
     */
    public Task() {
        this.name = null;
        this.isDone = false;
        this.type = null;
        this.initialisationError = null;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks the task as undone.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Initializes the task given the input argument string. Specific to each task type.
     *
     * @param input String of arguments for task creation.
     * @return The initialized task, or null if input was invalid.
     */
    public abstract Task initialise(String input);

    /**
     * Initializes the task given the arguments parsed from storage or test inputs.
     *
     * @param segments Input argument segments.
     * @return The initialized task, or null if arguments are invalid.
     */
    public static Task initialise(String... segments) {
        if (segments == null || segments.length < 3) {
            System.out.println("Invalid String[]: " + Arrays.toString(segments));
            return null;
        }

        TaskType taskType = TaskType.fromSymbol(segments[0]);
        if (taskType == null) {
            System.out.println("Invalid case: " + segments[0]);
            return null;
        }

        if (!segments[1].equals(COMPLETED_STATUS) && !segments[1].equals(INCOMPLETE_STATUS)) {
            System.out.println("Invalid task status: " + segments[1]);
            return null;
        }

        if (!hasExpectedNumberOfSegments(taskType, segments.length)) {
            System.out.println("Invalid number of fields for " + taskType + ": " + segments.length);
            return null;
        }

        if (segments[2].isBlank()) {
            System.out.println("Task name cannot be blank.");
            return null;
        }

        boolean isDone = segments[1].equals(COMPLETED_STATUS);
        Task task;
        switch (taskType) {
            case TODO:
                task = initialiseTodo(segments[2]);
                break;
            case DEADLINE:
                task = initialiseDeadline(segments);
                break;
            case EVENT:
                task = initialiseEvent(segments);
                break;
            default:
                return null;
        }

        if (task != null) {
            task.isDone = isDone;
        }
        return task;
    }

    /**
     * Records why task initialization failed so the parser can show the reason to the user.
     *
     * @param message User-facing validation message.
     */
    protected void setInitialisationError(String message) {
        this.initialisationError = message;
    }

    /**
     * Returns the validation message recorded during a failed initialization.
     *
     * @return User-facing validation message, or null if no validation error was recorded.
     */
    public String getInitialisationError() {
        return initialisationError;
    }

    /**
     * Checks that a stored task has exactly the number of fields required by its type.
     *
     * @param taskType Stored task type.
     * @param segmentCount Number of fields in the storage record.
     * @return True if the field count matches the task type.
     */
    private static boolean hasExpectedNumberOfSegments(TaskType taskType, int segmentCount) {
        switch (taskType) {
            case TODO:
                return segmentCount == 3;
            case DEADLINE:
                return segmentCount == 4;
            case EVENT:
                return segmentCount == 5;
            default:
                return false;
        }
    }

    /**
     * Creates a todo task from storage data.
     *
     * @param name Stored todo task name.
     * @return Initialized todo task.
     */
    private static Task initialiseTodo(String name) {
        Task task = new Todo();
        return task.initialise(name);
    }

    /**
     * Creates a deadline task from storage data.
     *
     * @param segments Stored deadline task segments.
     * @return Initialized deadline task, or null when its date is missing.
     */
    private static Task initialiseDeadline(String[] segments) {
        if (segments.length < 4) {
            System.out.println("Not enough arguments for deadline: " + Arrays.toString(segments));
            return null;
        }

        Task task = new Deadline();
        return task.initialise(segments[2] + " /by " + segments[3]);
    }

    /**
     * Creates an event task from storage data.
     *
     * @param segments Stored event task segments.
     * @return Initialized event task, or null when a date is missing.
     */
    private static Task initialiseEvent(String[] segments) {
        if (segments.length < 5) {
            System.out.println("Not enough arguments for event: " + Arrays.toString(segments));
            return null;
        }

        Task task = new Event();
        return task.initialise(segments[2] + " /from " + segments[3] + " /to " + segments[4]);
    }

    /**
     * Formats the task into a string representation suitable for file storage.
     *
     * @return String of the task formatted for storage.
     */
    public String stringify() {
        String status = this.isDone ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        return "{" + this.type.getSymbol() + "}" + "{" + status + "}" + "{" + this.name + "}";
    }

    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]" + "[" + (this.isDone ? "X" : " ") + "] " + this.name;
    }

    /**
     * Returns the name/description of the task.
     *
     * @return Name of the task.
     */
    public String getName() {
        return this.name;
    }
}
