package janet;

/**
 * Parses storage lines representing <code>Todo</code> task type.
 */
public class StorageTodoParser extends StorageTaskParser {

    private static final String TASK_NAME = "Todo";
    private static final int POSITIONAL_ARG_COUNT = 0;
    /**
     * Parses <code>Todo</code> specific String values.
     */
    public StorageTodoParser(TaskField taskField) throws JanetFileException {
        super(taskField.isDone(), taskField.taskLabel());

        int argsLength = taskField.args().length;
        if (argsLength > StorageTodoParser.POSITIONAL_ARG_COUNT) {
            throw new JanetFileException(
                    super.generateArgLengthExceptionMessage(
                            StorageTodoParser.TASK_NAME, StorageTodoParser.POSITIONAL_ARG_COUNT, argsLength
                    )
            );
        }
    }

    @Override
    public TaskList.CommandResult processStorageCommand(TaskList taskList) throws JanetException {
        return taskList.addTask(new Todo(super.isDone, super.taskLabel));
    }
}
