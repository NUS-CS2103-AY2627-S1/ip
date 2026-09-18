package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;

/** Command representing an empty user input. */
public class EmptyCommand extends Command {
    /** Creates a command for empty input. */
    public EmptyCommand(Parser parser) {
        super(parser);
    }

    /** Reports that the user must enter a command. */
    @Override
    public void execute(AppContext context) throws OreoException {
        throw new OreoException("Please enter a command.");
    }
}
