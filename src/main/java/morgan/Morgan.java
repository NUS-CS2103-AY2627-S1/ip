package morgan;

import morgan.command.Command;
import morgan.exception.MorganException;
import morgan.parser.Parser;
import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents the main entry point for the Morgan chatbot application.
 * Handles application initialization and input execution for CLI and GUI interfaces.
 */
public class Morgan {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private String commandType;
    private boolean isExit = false;

    /**
     * Constructs a new Morgan instance using the default data storage path.
     */
    public Morgan() {
        this("./data/morgan.txt");
    }

    /**
     * Constructs a new Morgan instance with the specified data storage path.
     *
     * @param filePath the path where task data is persisted.
     */
    public Morgan(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input Full command line entered by the user in the GUI.
     * @return Execution response text to be displayed in the GUI.
     */
    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            this.isExit = c.execute(tasks, ui, storage);
            commandType = c.getClass().getSimpleName();

            if (isExit) {
                ui.showGoodbye();
            }
            return ui.flushResponse();
        } catch (MorganException e) {
            commandType = "Error";
            return e.getMessage();
        } catch (Exception e) {
            commandType = "Error";
            return "Meow! That's not a fish! " + e.getMessage();
        }
    }

    public String getCommandType() {
        return commandType;
    }

    public boolean isExit() {
        return isExit;
    }
}
