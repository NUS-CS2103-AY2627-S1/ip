package vector;

import vector.command.Command;
import vector.parser.Parser;
import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents the main entry point for the Vector application.
 * Initializes the UI, Storage, and TaskList, and starts the main application loop.
 */
public class Vector {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private String commandType = "";

    /**
     * Constructs a Vector application instance.
     *
     * @param filePath The path to the data file where tasks are saved.
     */
    public Vector(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path cannot be null or empty";
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (VectorException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input User input string.
     * @return Response string.
     */
    public String getResponse(String input) {
        assert input != null : "User input should not be null";
        try {
            Command c = Parser.parse(input);
            String response = c.execute(tasks, ui, storage);
            commandType = c.getClass().getSimpleName();
            return response;
        } catch (VectorException e) {
            commandType = "Error";
            return "SYSTEM FAULT: " + e.getMessage();
        } catch (NumberFormatException e) {
            commandType = "Error";
            return "SYSTEM FAULT: Invalid task number format.";
        }
    }

    /**
     * Gets the command type of the last executed command.
     *
     * @return The simple class name of the command.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Runs the main loop of the Vector application, continuously reading and executing user commands.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            if (fullCommand == null) {
                break;
            }
            if (fullCommand.trim().isEmpty()) {
                continue;
            }

            try {
                ui.showLine(); // show the divider line ("_______")
                Command c = Parser.parse(fullCommand);
                String response = c.execute(tasks, ui, storage);
                ui.showMessage(response);
                isExit = c.isExit();
            } catch (VectorException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showError("The task number provided is invalid.");
            } catch (IndexOutOfBoundsException e) {
                ui.showError("That task number does not exist in your list.");
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts the Vector application.
     *
     * @param args Command-line arguments. Accepts "--clear-data" to clear existing task data.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--clear-data")) {
            java.io.File file = new java.io.File("./data/vector.txt");
            if (file.exists()) {
                file.delete();
            }
        }
        new Vector("./data/vector.txt").run();
    }
}
