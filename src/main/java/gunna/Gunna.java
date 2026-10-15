package gunna;

import java.io.File;

import gunna.command.Command;

/**
 * Main class for the Gunna task management application.
 * Coordinates the UI, storage, and task list components.
 */
public class Gunna {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Constructs a Gunna instance with the specified file path for data storage.
     *
     * @param filePath The path to the data file for saving/loading tasks.
     */
    public Gunna(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.loadTasks());
    }

    /**
     * Generates a response for the user's input.
     * Used by the GUI to get chatbot responses.
     *
     * @param input The user's input command.
     * @return The chatbot's response.
     */
    public String getResponse(String input) {
        return getResponseWithStatus(input).message();
    }

    /**
     * Generates a response and indicates whether it resulted from an invalid command.
     * This lets a user interface present errors without inspecting the response wording.
     *
     * @param input The user's input command.
     * @return The response text together with its error status.
     */
    public Response getResponseWithStatus(String input) {
        try {
            Command c = Parser.parse(input);
            return new Response(c.execute(tasks, ui, storage), false);
        } catch (DukeException e) {
            return new Response(e.getMessage(), true);
        }
    }

    /**
     * Represents a chatbot response and whether it reports a command error.
     *
     * @param message The response shown to the user.
     * @param isError Whether command processing produced an error.
     */
    public record Response(String message, boolean isError) {
    }

    /**
     * Returns the welcome message for the application.
     *
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        return "Gunna online.\nWhat are we tackling today?";
    }

    /**
     * Runs the main command loop of the application.
     * Displays welcome message, processes commands, and handles user interaction.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parse(fullCommand);
                String response = c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        String filePath = "data" + File.separator + "duke.txt";
        new Gunna(filePath).run();
    }
}
