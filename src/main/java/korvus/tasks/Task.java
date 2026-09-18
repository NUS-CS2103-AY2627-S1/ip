package korvus.tasks;

import korvus.utils.DateTimeParser;

/**
 * Task containing the task name and its done state.
 */
public class Task {
    protected enum TaskType {
        TODO("-t "),
        DEADLINE("-d "),
        EVENT("-e ");

        protected String flag;

        TaskType(String str) {
            this.flag = str;
        }
    }

    protected static String DATA_SEP = " // ";

    protected final DateTimeParser dateTimeParser;
    private boolean isDone;
    private String name;

    /**
     * Returns an instance of Task.
     *
     * @param name Name of Task.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Task(String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        this(false, name, dateTimeParser);
    }

    /**
     * Returns an instance of Task.
     *
     * @param isDone Boolean on whether task is completed.
     * @param name Name of Task.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Task(boolean isDone, String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        if (name.startsWith("-")) {
            throw new InvalidTaskException(
                    "Task name cannot start with '-'! Are you sure that you typed the name correctly?");
        }
        this.name = name;
        this.isDone = isDone;
        this.dateTimeParser = dateTimeParser;
    }

    /**
     * Marks the task as done.
     *
     * @return whether the task is already done before this.
     */
    public boolean doTask() {
        if (isDone) {
            return false;
        }
        isDone = true;

        return true;
    }

    /**
     * Marks the task as not done.
     *
     * @return whether the task was not done before this.
     */
    public boolean undoTask() {
        if (!isDone) {
            return false;
        }
        isDone = false;

        return true;
    }

    /**
     * Returns the name of the task.
     *
     * @return the task name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an instance of a Task, given an input with parameters.
     *
     * @param taskInput String of the input command, without any processing.
     * @param dateTimeParser Custom parser to convert String to DateTime and vice-versa.
     * @return a task after parsing the input command.
     */
    public static Task generateTask(String taskInput,
                                    DateTimeParser dateTimeParser) throws InvalidTaskException {
        Task newTask = switch (taskInput) {
            case String s when s.startsWith(TaskType.TODO.flag) -> {
                yield new ToDo(s.substring(3), dateTimeParser);
            }
            case String s when s.startsWith(TaskType.DEADLINE.flag) -> {
                String[] args = s.substring(3).split(DATA_SEP, 2);
                yield args.length > 1
                        ? new Deadline(args[0], args[1], dateTimeParser)
                        : new Deadline(args[0], dateTimeParser);
            }
            case String s when s.startsWith(TaskType.EVENT.flag) -> {
                String[] args = s.substring(3).split(DATA_SEP, 3);
                yield args.length > 2
                        ? new Event(args[0], args[1], args[2], dateTimeParser)
                        : new Event(args[0], dateTimeParser);
            }
            default -> new ToDo(taskInput, dateTimeParser);
        };

        assert newTask.name != null;
        assert !newTask.name.startsWith("-");
        assert newTask.dateTimeParser.equals(dateTimeParser);

        return newTask;
    }

    /**
     * Returns an instance of a Task, given an input from a file.
     *
     * @param fileInput String of the input command, without any processing.
     * @param dateTimeParser Custom parser to convert String to DateTime and vice-versa.
     * @return a task after parsing the input command.
     */
    protected static Task readTaskFromFile(String fileInput,
                                           DateTimeParser dateTimeParser) throws InvalidTaskException {
        return switch (fileInput) {
            case String s when s.startsWith(TaskType.TODO.flag) -> {
                String[] args = s.split(DATA_SEP, 3);
                yield new ToDo(Boolean.parseBoolean(args[2]), args[1], dateTimeParser);
            }
            case String s when s.startsWith(TaskType.DEADLINE.flag) -> {
                String[] args = s.split(DATA_SEP, 4);
                yield new Deadline(Boolean.parseBoolean(args[2]), args[1], args[3], dateTimeParser);
            }
            case String s when s.startsWith(TaskType.EVENT.flag) -> {
                String[] args = s.split(DATA_SEP, 5);
                yield new Event(Boolean.parseBoolean(args[2]), args[1], args[3], args[4], dateTimeParser);
            }
            default -> {
                throw new InvalidTaskException(String.format("Task format not supported!\nInput: %s", fileInput));
            }
        };
    }

    /**
     * Returns the task in a format for writing to a file.
     *
     * @return Task as a String to be written to a file.
     */
    protected String writeToStore() {
        return String.format("%s%s%s",
                this.name,
                DATA_SEP,
                this.isDone);
    };

    /**
     * Returns a String representation of the Task, to be used in the CLI.
     *
     * @return Task formatted in a human-readable form.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", isDone ? "x" : " ", name);
    }
}
