package answerme.command;

import answerme.task.ToDo;

/**
 * Represents a command that adds a todo task to the task list.
 */
public class AddToDoCommand extends AddCommand {
    /**
     * Creates a command that adds a todo task with the specified description.
     *
     * @param description The description of the todo task.
     */
    public AddToDoCommand(String description) {
        super(new ToDo(description));
    }
}
