package arthur.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import arthur.tasks.Task;

// AI-assisted: Added shared task storage for the add and list commands.
/**
 * Stores the shared state that commands need during a program session.
 */
public record CommandContext(ArrayList<Task> tasks, HashMap<String, String> commandAliases) implements Serializable {
    public CommandContext() {
        this(new ArrayList<>(), new HashMap<>());
    }

    /**
     * Returns a command context with all collections initialized.
     *
     * @return A new CommandContext with all fields initialized.
     */
    public CommandContext fixed() {
        ArrayList<Task> tasks = this.tasks;
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        HashMap<String, String> commandAliases = this.commandAliases;
        if (commandAliases == null) {
            commandAliases = new HashMap<>();
        }

        return new CommandContext(tasks, commandAliases);
    }
}
