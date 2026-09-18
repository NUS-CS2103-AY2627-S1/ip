package oreo.command;

import oreo.core.AppContext;
import oreo.core.Parser;

/** Command that displays the available commands and their usage. */
public class HelpCommand extends Command {
    /** Creates a command that displays the command help. */
    public HelpCommand(Parser parser) {
        super(parser);
    }

    /** Displays all commands currently supported by Oreo. */
    @Override
    public void execute(AppContext context) {
        context.getUi().showHelp();
    }
}
