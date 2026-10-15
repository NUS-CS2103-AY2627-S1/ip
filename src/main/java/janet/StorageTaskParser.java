package janet;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handles the fields common to all task-related comments.
 */
public abstract class StorageTaskParser {
    /**
     * Represents the fields common to all <code>Task</code> types and any remaining arguments.
     *
     * @param isDone    Whether the <code>Task</code> has been marked as 'done'.
     * @param taskLabel The description of each task.
     * @param args      Any remaining arguments for specific <code>Task</code> types, if any.
     */
    public record TaskField(boolean isDone, String taskLabel, String[] args) {
    }

    protected static final String INLINE_SEP = "|";

    private static final Map<String, StorageCommandHandler> taskMap = Map.of(
            "T", (x) -> new StorageTodoParser(x),
            "D", (x) -> new StorageDeadlineParser(x),
            "E", (x) -> new StorageEventParser(x)
    );

    private static final int TASK_TYPE_INDEX = 0;
    private static final int IS_DONE_INDEX = 1;
    private static final int TASK_LABEL_INDEX = 2;
    private static final int COMMAND_ARG_INDEX = StorageTaskParser.TASK_LABEL_INDEX + 1;
    private static final String IS_DONE_STR = "1";
    private static final String NOT_DONE_STR = "0";

    protected final boolean isDone;
    protected final String taskLabel;

    /**
     * Initializes storage configuration with fields common to all <code>Task</code> types.
     */
    public StorageTaskParser(boolean isDone, String taskLabel) {
        this.isDone = isDone;
        this.taskLabel = taskLabel;
    }

    /**
     * Parses a serialized task command and creates the parser for its task type.
     * The command must contain the task type, completion status, and task label,
     * separated by {@code |}. Any remaining fields are passed to the specific task parser.
     *
     * @param storageCommand The serialized task command read from storage
     * @return The parser corresponding to the command's task type
     */
    public static StorageTaskParser processBaseTask(String storageCommand) throws JanetFileException {
        String[] tokens = storageCommand.split(Pattern.quote(StorageTaskParser.INLINE_SEP));
        if (tokens.length < StorageTaskParser.TASK_LABEL_INDEX + 1) {
            throw new JanetFileException(
                    String.format("All tasks take at least %d positional arguments but %d given: [%s]",
                            StorageTaskParser.TASK_LABEL_INDEX + 1,
                            tokens.length,
                            storageCommand
                    )
            );
        }

        String taskType = tokens[StorageTaskParser.TASK_TYPE_INDEX];
        boolean isDone = StorageTaskParser.isDone(tokens[StorageTaskParser.IS_DONE_INDEX]);
        String taskLabel = tokens[StorageTaskParser.TASK_LABEL_INDEX];

        String[] args = Arrays.copyOfRange(
                tokens,
                StorageTaskParser.COMMAND_ARG_INDEX,
                tokens.length
        );

        TaskField taskFields = new TaskField(isDone, taskLabel, args);

        if (!StorageTaskParser.taskMap.containsKey(taskType)) {
            throw new JanetFileException(
                    String.format("Unrecognized task type: %s. Line: %s", taskType, storageCommand)
            );
        }
        return StorageTaskParser.taskMap
                .get(taskType)
                .handle(taskFields);
    }

    private static boolean isDone(String doneStr) throws JanetFileException {
        if (!(doneStr.equals(StorageTaskParser.IS_DONE_STR) || doneStr.equals(StorageTaskParser.NOT_DONE_STR))) {
            throw new JanetFileException(String.format("Unrecognized mark type: %s", doneStr));
        }
        return doneStr.equals(StorageTaskParser.IS_DONE_STR);
    }

    public abstract TaskList.CommandResult processStorageCommand(TaskList taskList) throws JanetException;

    /**
     * Creates a description of an invalid number of serialized task arguments.
     *
     * @param taskName Name of the task type being parsed.
     * @param positionalArgCount Expected number of task-specific arguments.
     * @return Description of the invalid argument count.
     */
    public String generateArgLengthExceptionMessage(String taskName, int positionalArgCount, int actualCount) {
        return String.format(
                "%s takes %d positional arguments but %d was given. Task: [%s]",
                taskName,
                positionalArgCount,
                actualCount,
                this.taskLabel
        );
    }
}
