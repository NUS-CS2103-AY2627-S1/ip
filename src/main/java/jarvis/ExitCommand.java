package jarvis;

import java.util.List;

/** Represents the command that ends the Jarvis session. */
public class ExitCommand extends Command {
    /** Displays the goodbye message; task arguments are unused for this command. */
    @Override
    public void execute(List<Task> tasks, Ui ui, TaskStorage storage) {
        ui.showGoodbye();
    }

    /** Returns {@code true} because this command ends the application. */
    @Override
    public boolean isExit() {
        return true;
    }
}
