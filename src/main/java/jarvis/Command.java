package jarvis;

import java.util.List;

/** Represents one user command that can be executed by Jarvis. */
public abstract class Command {
    /** Executes this command using the application's shared collaborators. */
    public abstract void execute(List<Task> tasks, Ui ui, TaskStorage storage) throws JarvisException;

    /** Returns whether executing this command should end the application. */
    public boolean isExit() {
        return false;
    }
}
