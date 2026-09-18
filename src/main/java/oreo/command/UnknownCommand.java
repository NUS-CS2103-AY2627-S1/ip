package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;

/** Command representing input that is not recognized. */
public class UnknownCommand extends Command {
    /** Creates a command for unrecognized input. */
    public UnknownCommand(Parser parser) {
        super(parser);
    }

    /** Reports that the input is not a recognized command. */
    @Override
    public void execute(AppContext context) throws OreoException {
        throw new OreoException("I cannot comprehend your English.");
    }
}
