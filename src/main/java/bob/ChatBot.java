package bob;

import java.io.IOException;

import bob.command.Command;
import bob.exception.BobException;
import bob.parser.Parser;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Processes chatbot commands and coordinates the console input loop.
 */
public class ChatBot {
    private final Ui ui;
    private final Parser parser;
    private final TaskList tasks;
    private final Storage storage;

    /**
     * Creates a chatbot with its user interface and input parser.
     *
     * @throws BobException if saved tasks cannot be loaded.
     */
    public ChatBot() throws BobException {
        this("data/bob.txt");
    }

    /**
     * Creates a chatbot that persists tasks at the supplied path.
     *
     * @param filePath path of the file used to load and save tasks.
     * @throws BobException if saved tasks cannot be loaded.
     */
    public ChatBot(String filePath) throws BobException {
        this.ui = new Ui();
        this.parser = new Parser();
        this.storage = new Storage(filePath);

        // Load once when the chatbot starts
        try {
            this.tasks = new TaskList(storage.load());
        } catch (IOException exception) {
            throw new BobException("I could not load your saved tasks", exception);
        }
    }

    /**
     * Processes a user command and returns the response for a user interface to display.
     *
     * @param fullCommand complete command entered by the user.
     * @return result containing the response text and whether the session should end.
     */
    public ChatResponse processCommand(String fullCommand) {
        try {
            Command command = parser.parse(fullCommand);
            String response = command.execute(tasks, storage);
            return new ChatResponse(response, command.isExit(), command.getResponseType());
        } catch (BobException | IOException exception) {
            String response = "I hit a problem: " + exception.getMessage();
            return new ChatResponse(response, false, ResponseType.ERROR);
        }
    }

    /**
     * Runs the chatbot until the user enters an exit command.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextLine()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            ChatResponse response = processCommand(fullCommand);
            ui.showResponse(response.text());
            ui.showLine();

            if (response.isExit()) {
                break;
            }
        }
        ui.close();
    }
}
