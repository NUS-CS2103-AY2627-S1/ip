package ace;

import ace.exception.AceException;
import ace.parser.Parser;
import ace.storage.Storage;
import ace.task.TaskManager;
import ace.ui.Ui;

/**
 * Entry point for the Ace task manager. Wires up the UI, task list, and command
 * parser, loads any previously saved tasks, then runs the main read-parse-report
 * loop until the user exits with "bye".
 */
public class Ace {

    /**
     * Starts Ace: shows the welcome banner, loads saved tasks, then repeatedly
     * reads a command, executes it, and reports the result until the user exits.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskManager taskManager = new TaskManager();

        ui.showWelcome();
        Storage.load(taskManager);

        while (true) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                boolean isExit = Parser.parseCommand(input, taskManager, ui);
                if (isExit) {
                    break;
                }
            } catch (AceException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showLine();
        }
    }
}
