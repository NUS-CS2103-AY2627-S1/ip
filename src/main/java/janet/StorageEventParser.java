package janet;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parses storage lines representing <code>Event</code> task type.
 */
public class StorageEventParser extends StorageTaskParser {

    private static final String TASK_NAME = "Event";
    private static final int POSITIONAL_ARG_COUNT = 2;
    private static final int FROM_INDEX = 0;
    private static final int TO_INDEX = 1;

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Parses <code>Event</code> specific String values.
     */
    public StorageEventParser(TaskField taskField) throws JanetFileException {
        super(taskField.isDone(), taskField.taskLabel());

        int argsLength = taskField.args().length;
        if (argsLength > StorageEventParser.POSITIONAL_ARG_COUNT) {
            throw new JanetFileException(
                    super.generateArgLengthExceptionMessage(
                            StorageEventParser.TASK_NAME, StorageEventParser.POSITIONAL_ARG_COUNT, argsLength
                    )
            );
        }
        String fromStr = taskField.args()[StorageEventParser.FROM_INDEX];
        String toStr = taskField.args()[StorageEventParser.TO_INDEX];
        try {
            this.from = LocalDate.parse(fromStr);
            this.to = LocalDate.parse(toStr);
        } catch (DateTimeParseException e) {
            throw new JanetFileException(
                    String.format(
                            "Invalid date format: from: %s, to: %s. Error: %s",
                            fromStr,
                            toStr,
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public TaskList.CommandResult processStorageCommand(TaskList taskList) throws JanetException {
        return taskList.addTask(new Event(super.isDone, super.taskLabel, this.from, this.to));
    }
}
