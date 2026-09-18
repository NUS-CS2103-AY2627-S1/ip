package oreo.command;

import oreo.core.AppContext;
import oreo.core.Parser;

/** Command that displays the goodbye message and ends the application. */
public class ExitCommand extends Command {
    /** Creates a command that exits the application. */
    public ExitCommand(Parser parser) {
        super(parser);
    }

    /** Displays the goodbye message. */
    @Override
    public void execute(AppContext context) {
        context.getUi().showGoodbye();
    }

    /** Returns {@code true} because this command ends the application. */
    @Override
    public boolean isExit() {
        return true;
    }
}
