package esther;

import java.io.IOException;
import java.nio.file.Path;

import esther.command.CommandProcessor;
import esther.command.CommandResult;
import esther.exception.EstherException;
import esther.storage.Storage;
import esther.task.TaskList;
import esther.ui.Ui;

/**
 * Represents the Esther personal assistant chatbot.
 */
public class Esther {
    /**
     * Creates an Esther application.
     */
    public Esther() {
    }

    /**
     * Runs Esther's command-line interface.
     *
     * @param args Command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "esther.txt"));

        TaskList tasks;

        try {
            tasks = storage.load();
        } catch (IOException | EstherException exception) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        CommandProcessor commandProcessor = new CommandProcessor(storage, tasks);
        ui.showWelcome();

        while (true) {
            String command = ui.readCommand();
            CommandResult result = commandProcessor.process(command);

            ui.showResponse(result.messages());
            if (result.shouldExit()) {
                break;
            }
        }

        ui.close();
    }
}
