package longfrog.command;
import longfrog.ui.Ui;

/** Ends the application after displaying a farewell message. */
public class ExitCommand implements Command {
    /** Displays the farewell message and signals that the application should exit. */
    @Override
    public boolean execute(Ui ui) {
        ui.showExitMessage();
        return true;
    }
}
