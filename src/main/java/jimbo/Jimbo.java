package jimbo;

import javafx.application.Platform;
import jimbo.command.Command;
import jimbo.ui.Ui;

/**
 * The main app class.
 *
 * Jimbo can add and delete tasks, mark and unmark them as deleted,
 * and save and load them from a while.
 */
public class Jimbo {
    private Ui ui;
    private Storage storage;
    private TaskList tasks;
    private Parser parser;
    private String initialisationError;


    /**
     * Initialises Jimbo.
     */
    public Jimbo(String file_path) {
        ui = new Ui();
        storage = new Storage(file_path);

        try {
            tasks = new TaskList(storage.load());
        } catch (JimboException e) {
            initialisationError = e.getMessage();
        }
        parser = new Parser();
    }

    /**
     * Returns any error encountered during initialisation.
     *
     * @return The error message encountered during initialisation, or null if there was none.
     */
    public String getInitialisationError() {
        return initialisationError;
    }

    /**
     * Gets Jimbo's response to user input.
     *
     * @param input The user input.
     * @return Jimbo's response.
     * @throws JimboException if an error occurs while processing the user input.
     */
    public String getResponse(String input) throws JimboException {
        String response;
        try {
            response = processInput(input);
        } catch (JimboException e) {
            throw new JimboException("uh oh\n" + e.getMessage());
        }
        return response;
    }

    private String processInput(String input) throws JimboException{
        Command command = parser.parse(input);
        String response = command.execute(ui, tasks, storage);
        if (command.shouldSave()) {
            storage.save(tasks.getInternalList());
        }
        if (command.shouldQuit()) {
            Platform.exit();
        }
        return response;
    }
}
