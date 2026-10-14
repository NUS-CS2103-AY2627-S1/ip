package bingus;

import bingus.command.Command;
import bingus.command.CommandType;
import bingus.exception.BingusException;
import bingus.parser.Parser;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Coordinates command parsing, task storage, and user-interface responses.
 */
public class Bingus {

    static final String DEFAULT_SAVE_FILE_PATH = "data/bingus.txt";

    private final Storage storage;
    private CommandType commandType = CommandType.NONE;
    private TaskList tasks;
    private final Parser parser;
    private final Ui ui;
    private String loadErrorMessage;

    /**
     * Creates the application using the specified task save file.
     *
     * @param filePath path of the task save file.
     */
    public Bingus(String filePath) {
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (BingusException e) {
            tasks = new TaskList();
            loadErrorMessage = e.getMessage();
        }
        parser = new Parser();
        ui = new Ui();

    }

    /**
     * Returns a response for a command submitted through the GUI.
     *
     * @param input command entered by the user.
     * @return response message for the user.
     */
    public String getResponse(String input) {
        try {
            Command command = parser.parse(input, tasks);
            commandType = command.getType();
            return command.execute(tasks, ui, storage);
        } catch (BingusException e) {
            commandType = CommandType.INVALID;
            return e.getMessage();
        }
    }

    /**
     * Returns Bingus's greeting for a new GUI session.
     *
     * @return welcome message.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns the type of the latest command submitted through the GUI.
     *
     * @return type of the latest command.
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Returns the storage-loading error, or {@code null} if loading succeeded.
     *
     * @return storage-loading error, or {@code null} if there was no error.
     */
    public String getLoadErrorMessage() {
        return loadErrorMessage;
    }

}
