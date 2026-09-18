package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Lists the commands Bibi understands, with worked examples on request.
 */
public class HelpCommand extends Command {
    private final boolean isExamplesRequested;

    /**
     * Creates a command that will show the command list, and optionally the
     * examples and reference detail that go with it.
     *
     * @param isExamplesRequested whether the user asked for the worked examples
     */
    public HelpCommand(boolean isExamplesRequested) {
        this.isExamplesRequested = isExamplesRequested;
    }

    /**
     * Shows every command Bibi understands, followed by the examples when they
     * were asked for.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // The command list comes first either way, so that help --examples reads
        // as the short help with more added rather than as a different reply.
        ui.showHelp();
        if (isExamplesRequested) {
            ui.showHelpExamples();
        }
    }
}
