package bobby;

import java.util.ArrayList;

import bobby.storage.Storage;
import bobby.task.TaskList;
import bobby.ui.Ui;

/**
 * Runs Bobby as a console application.
 */
public class BobbyConsole {
    private static final String BYE_COMMAND = "bye";

    /**
     * Starts Bobby, loads saved tasks, and handles user commands until the user exits.
     *
     * @param args command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskList tasks = loadTasks(ui);
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            if (command.equals(BYE_COMMAND)) {
                ui.showGoodbye();
                ui.showLine();
                break;
            }

            try {
                ui.showResponse(Bobby.handleCommand(command, tasks));
            } catch (BobbyException e) {
                ui.showCommandError(e.getMessage());
            }

            ui.showLine();
        }
    }

    /**
     * Loads tasks from storage and falls back to an empty task list if loading fails.
     *
     * @param ui UI used to show any loading error.
     * @return loaded task list, or an empty list if loading fails.
     */
    private static TaskList loadTasks(Ui ui) {
        try {
            return new TaskList(Storage.loadTasks());
        } catch (BobbyException e) {
            ui.showLoadingError(e.getMessage());
            return new TaskList(new ArrayList<>());
        }
    }
}
