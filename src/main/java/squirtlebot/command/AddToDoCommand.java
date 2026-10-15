package squirtlebot.command;

import squirtlebot.exception.CommandException;
import squirtlebot.parser.Parser;
import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;
import squirtlebot.ui.Ui;

/**
 * Represents the todo command within {@code SquirtleBot}.
 */
public class AddToDoCommand extends Command {
    private ToDo toDoToAdd;

    /**
     * Constructs a new AddToDoCommand using inputs provided by a user.
     *
     * @param inputTokens array containing user inputs required to create a ToDo object.
     */
    public AddToDoCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Adds previously created ToDo task to existing task list.
     * Updates user on current state of the task list.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int sizeBeforeAdding = tasks.size();

        tasks.add(toDoToAdd);

        super.updateStorage(tasks, storage);

        assert sizeBeforeAdding == tasks.size() - 1;

        ui.setSavedMessage(String.format("\tadded: %s to your list of tasks\n"
                + "You now have %d tasks", toDoToAdd, tasks.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof AddToDoCommand otherAddToDoCommand) {
            return toDoToAdd.equals(otherAddToDoCommand.toDoToAdd);
        } else {
            return false;
        }
    }

    /**
     * Reassembles user input to form task description for ToDo object.
     * Creates ToDo task according to user input.
     *
     * @param inputTokens array containing user inputs required to create a ToDo object.
     * @throws CommandException if taskDescription is empty.
     */
    private void setAttributes(String[] inputTokens) {
        Parser parser = new Parser();

        String taskDescription = parser.parseDescription(inputTokens);

        if (taskDescription.isEmpty()) {
            throw new CommandException("Please provide the correct arguments for ToDo!");
        }

        this.toDoToAdd = new ToDo(taskDescription);
    }
}
