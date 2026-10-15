package janet;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parses storage lines representing <code>Deadline</code> task type.
 */
public class StorageDeadlineParser extends StorageTaskParser {

    private static final String TASK_NAME = "Deadline";
    private static final int POSITIONAL_ARG_COUNT = 2;
    private static final int DEADLINE_INDEX = 0;

    private final LocalDate deadline;

    /**
     * Parses <code>Deadline</code> specific String values.
     */
    public StorageDeadlineParser(TaskField taskField) throws JanetFileException {
        super(taskField.isDone(), taskField.taskLabel());

        int argsLength = taskField.args().length;
        if (taskField.args().length > StorageDeadlineParser.POSITIONAL_ARG_COUNT) {
            throw new JanetFileException(
                    super.generateArgLengthExceptionMessage(
                            StorageDeadlineParser.TASK_NAME, StorageDeadlineParser.POSITIONAL_ARG_COUNT, argsLength
                    )
            );
        }

        String deadlineStr = taskField.args()[StorageDeadlineParser.DEADLINE_INDEX];
        try {
            this.deadline = LocalDate.parse(deadlineStr);
        } catch (DateTimeParseException e) {
            throw new JanetFileException(
                    String.format(
                            "Invalid date format: Deadline: %s. Error: %s",
                            deadlineStr,
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public TaskList.CommandResult processStorageCommand(TaskList taskList) throws JanetException {
        return taskList.addTask(new Deadline(super.isDone, super.taskLabel, this.deadline));
    }
}
