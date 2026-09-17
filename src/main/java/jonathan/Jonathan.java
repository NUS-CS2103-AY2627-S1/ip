package jonathan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import jonathan.command.Command;
import jonathan.parser.Parser;
import jonathan.storage.Storage;
import jonathan.task.TaskList;
import jonathan.ui.UI;


/**
 * A chatbot that stores tasks in memory and responds to simple commands.
 */
public class Jonathan {
    private TaskList tasklist;
    private Storage storage;
    private UI ui;

    /**
     * Creates a chatbot that stores its tasks at the given file path.
     *
     * @param filePath path of the task data file
     */
    public Jonathan(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        try {
            tasklist = new TaskList(storage.load());
        } catch (IOException | JonathanException e) {
            ui.showError("Failed to load tasks from file. Starting with an empty list.");
            tasklist = new TaskList();
        }

    }

    /** Runs the chatbot's read-parse-execute loop. */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parse(fullCommand);
                c.execute(tasklist, ui, storage);
                isExit = c.isExit();
            } catch (JonathanException | IOException e) {
                ui.showError(e.getMessage());
            }
        }
    }
    /**
     * Starts the chatbot.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String... args) {
        new Jonathan("data/jonathan.txt").run();
    }

    /**
     * Processes one command from the GUI and returns the chatbot response.
     *
     * @param input the command entered by the user
     * @return the response to display in the GUI
     */
    public String getResponse(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer);
        UI guiUi = new UI(output);

        try {
            Command command = Parser.parse(input);
            command.execute(tasklist, guiUi, storage);
            output.flush();
            return buffer.toString(StandardCharsets.UTF_8);
        } catch (JonathanException | IOException exception) {
            return "Error: " + exception.getMessage();
        }
    }
}
