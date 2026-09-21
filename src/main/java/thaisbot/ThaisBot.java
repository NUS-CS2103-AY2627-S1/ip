package thaisbot;

import java.nio.file.Path;
import java.nio.file.Paths;

import thaisbot.command.Command;
import thaisbot.command.Parser;
import thaisbot.task.TaskList;

/**
 * Entry point for the application. Responsible for bootstrapping components (UI, storage,
 * parser) and running the main interactive loop that accepts user commands until exit.
 */
public class ThaisBot {
    private static final Path DATA_FILE = Paths.get("data", "tasks.txt");

    /**
     * Main method that starts the application.
     * @param args ignored
     */
    public static void main(String[] args) {
        Parser parser = new Parser();
        Storage storage = new Storage(DATA_FILE.toString(), parser);
        Ui ui = new Ui();
        ui.showWelcome();

        TaskList tasks;
        try {
            tasks = storage.load();
        } catch (ThaisBotException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }

        while (true) {
            String userInput = ui.readCommand();

            try {
                Command command = parser.parse(userInput);
                command.execute(tasks, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (ThaisBotException e) {
                ui.showError(e.getMessage());
            }
        }
    }
}
