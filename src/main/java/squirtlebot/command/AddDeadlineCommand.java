package squirtlebot.command;

import java.time.temporal.Temporal;

import squirtlebot.exception.CommandException;
import squirtlebot.parser.DateParser;
import squirtlebot.parser.Parser;
import squirtlebot.storage.Storage;
import squirtlebot.task.Deadline;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Represents the deadline command within {@code SquirtleBot}.
 */
public class AddDeadlineCommand extends Command {
    private Deadline deadlineToAdd;

    /**
     * Constructs a new AddDeadlineCommand using inputs provided by a user.
     *
     * @param inputTokens array containing user inputs required to create a Deadline object.
     */
    public AddDeadlineCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Adds previously created deadline task to an existing task list.
     * Updates user on current state of task list.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int sizeBeforeAdding = tasks.size();

        tasks.add(deadlineToAdd);

        super.updateStorage(tasks, storage);

        assert sizeBeforeAdding == tasks.size() - 1;

        ui.setSavedMessage(String.format("\tadded: %s to your list of tasks\n"
                + "You now have %d tasks", deadlineToAdd, tasks.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof AddDeadlineCommand otherAddDeadlineCommand) {
            return deadlineToAdd.equals(otherAddDeadlineCommand.deadlineToAdd);
        } else {
            return false;
        }
    }

    /**
     * Extracts {@code taskDescription} and {@code dueDate} from the array of user inputs.
     * Creates deadline task to be added later.
     *
     * @param inputTokens array containing user inputs required to create a Deadline object.
     * @throws CommandException if dueDate or taskDescription is empty, or if dueDate is not in a valid format.
     */
    private void setAttributes(String[] inputTokens) {
        Parser parser = new Parser();

        String taskDescription = parser.parseDescription(inputTokens);
        String dueDateString = parser.parseTokens(inputTokens, "/by");

        if (dueDateString.isEmpty() || taskDescription.isEmpty()) {
            throw new CommandException("Please provide the correct arguments for Deadline!");
        }

        DateParser dateParser = new DateParser();
        Temporal dueDateTemporal = dateParser.parseTemporal(dueDateString)
                .orElseThrow(() -> new CommandException("Please enter a valid due date/datetime!"));

        deadlineToAdd = new Deadline(taskDescription, dueDateTemporal);

    }
}
