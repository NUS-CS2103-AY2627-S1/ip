package atom.command;

import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;
import atom.task.ToDo;

/**
 * Represents a todo command.
 */
public class ToDoCommand extends Command {
    private static final String COMMAND_NAME = "todo";

    private static final int EXPECTED_ARGUMENTS = 1;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 2;

    /**
     * Instantiates a <code>ToDoCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public ToDoCommand(String ... args) throws AtomMismatchedArgumentsException {
        super(args, EXPECTED_ARGUMENTS);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        ToDo toDo = new ToDo(args[0].strip());
        tasks.add(toDo);
        storage.writeTask(Storage.TaskType.T, args);
        return getOutputMessage(toDo.toString(), Integer.toString(tasks.size()));
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Alright! I've added this task:\n"
                + outputArgs[0]
                + "\nYou now have "
                + outputArgs[1]
                + " tasks in the list.";
    }
}
