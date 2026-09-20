package atom.command;

import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;
import javafx.application.Platform;

/**
 * Represents a bye command.
 */
public class ByeCommand extends Command {
    private static final String COMMAND_NAME = "bye";

    private static final int EXPECTED_ARGUMENTS = 0;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 0;

    /**
     * Instantiates a <code>ByeCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *      does not match the expected number.
     */
    public ByeCommand(String ... args) throws AtomMismatchedArgumentsException {
        super(args, EXPECTED_ARGUMENTS);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        Platform.exit();
        return getOutputMessage();
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Bye bye! ('^')/";
    }
}
