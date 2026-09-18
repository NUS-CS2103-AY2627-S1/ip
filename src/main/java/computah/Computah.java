package computah;

import computah.command.Command;
import computah.exception.ComputahException;
import computah.model.Model;
import computah.parser.Parser;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Entry point and main coordinator for the Computah chatbot.
 */
public class Computah {
    /**
     * Prevents instantiation of this entry-point class.
     */
    private Computah() {
    }

    /**
     * Starts Computah, loads saved tasks, and runs the command loop.
     *
     * @param args command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/duke.txt", "data/clients.txt");
        Model model = new Model();
        ui.showWelcome();

        try {
            model.getTasks().addAll(storage.loadTasks());
        } catch (ComputahException e) {
            ui.showError(e.getMessage());
        }
        try {
            model.getClients().addAll(storage.loadClients());
        } catch (ComputahException e) {
            ui.showError(e.getMessage());
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            try {
                Command command = Parser.parse(input, model.getTaskCount(), model.getClientCount());
                command.execute(model, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (ComputahException e) {
                ui.showError(e.getMessage());
            }
        }
    }
}
