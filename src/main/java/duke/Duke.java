package duke;

import duke.command.Command;
import duke.exception.DukeException;
import duke.parser.Parser;
import duke.place.PlaceList;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents the main entry point for the Duke chatbot application.
 */
public class Duke {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Initializes the Duke chatbot using the default storage file path.
     */
    public Duke() {
        this("./data/tbc.txt");
    }

    /**
     * Initializes the Duke chatbot with the specified storage file path.
     *
     * @param filePath The path to the file used for loading and saving tasks.
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
        try {
            storage.loadPlaces();
        } catch (DukeException e) {
            storage.setPlaceList(new PlaceList());
        }
    }

    /**
     * Generates a response for the user's input string.
     *
     * @param input The raw user command string.
     * @return The response string produced by Duke.
     */
    public String getResponse(String input) {
        assert input != null : "Input command must not be null";
        assert tasks != null : "TaskList must be initialized";
        assert ui != null : "Ui must be initialized";
        assert storage != null : "Storage must be initialized";
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            String response = ui.getLastResponse();
            assert response != null : "Response must not be null";
            return response;
        } catch (DukeException e) {
            return e.getMessage();
        }
    }

    /**
     * Runs the main execution loop of the Duke chatbot.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * The main entry point for the Duke application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Duke("./data/tbc.txt").run();
    }
}
