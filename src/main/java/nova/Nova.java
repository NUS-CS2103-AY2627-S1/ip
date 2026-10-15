package nova;

import nova.command.CommandHandler;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    /**
     * Starts the Nova chatbot and processes commands until the input ends.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showGreeting();
        Storage storage = new Storage();
        TaskList tasks = storage.load();
        Parser parser = new Parser();
        CommandHandler handler = new CommandHandler(storage, tasks, ui);
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            try {
                Parser.ParsedCommand parsedCommand = parser.parse(command);
                if (handler.execute(parsedCommand)) {
                    break;
                }
            } catch (NovaException exception) {
                ui.showError(exception);
            }

            ui.showDivider();
        }
    }

}
