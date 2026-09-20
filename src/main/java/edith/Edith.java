package edith;

import edith.command.Command;
import edith.command.Parser;
import edith.task.TaskList;

/** Runs Edith through the console interface. */
public class Edith {
    private static final String TASK_FILE = System.getProperty("edith.taskFile", "./data/edith.txt");

    /**
     * Starts the console chatbot.
     *
     * @param args command-line arguments, which Edith does not use
     */
    public static void main(String[] args) {
        Storage storage = new Storage(TASK_FILE);
        Ui ui = new Ui();
        TaskList tasks;
        try {
            tasks = storage.load();
        } catch (EdithException error) {
            ui.showWelcome();
            ui.showError(error.getMessage());
            ui.showLine();
            return;
        }

        ui.showWelcome();
        runCommandLoop(tasks, ui, storage);
    }

    /** Reads and executes commands until the input ends or the user exits. */
    private static void runCommandLoop(TaskList tasks, Ui ui, Storage storage) {
        String commandText;
        while ((commandText = ui.readCommand()) != null) {
            ui.showLine();
            try {
                Command command = Parser.parse(commandText);
                command.execute(tasks, ui, storage);
                ui.showLine();
                if (command.isExit()) {
                    return;
                }
            } catch (EdithException error) {
                ui.showError(error.getMessage());
                ui.showLine();
            }
        }
    }
}
