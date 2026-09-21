package echo;

import java.nio.file.Path;

import echo.command.Command;
import echo.exception.EchoException;
import echo.exception.StorageException;
import echo.parser.Parser;
import echo.storage.Storage;
import echo.task.TaskList;
import echo.ui.EchoResponse;
import echo.ui.ResponseType;
import echo.ui.Ui;

/**
 * Entry point of the Echo chatbot application. Wires together the task list,
 * UI, parser, and storage components, then runs the read-evaluate-print loop.
 */
public class Echo {
    private final TaskList taskList = new TaskList();
    private final Ui ui = new Ui();
    private final Storage storage;
    private final Parser parser = new Parser();
    private Command prevCommand = null;

    /**
     * Instantiates {@link Echo} chatbot instance with default
     * parameters.
     */
    public Echo() {
        this.storage = new Storage();
    }

    /**
     * Instantiates {@link Echo} chatbot instance with custom
     * file path.
     */
    public Echo(Path fileSavePath) {
        this.storage = new Storage(fileSavePath);
    }

    /**
     * Starts the chatbot: loads any previously saved tasks, greets the user.
     */
    public String start() {
        try {
            var loadedTasks = storage.read();
            assert loadedTasks != null : "Storage.read must return a task collection.";
            taskList.addAll(loadedTasks);
            return ui.greet();
        } catch (StorageException exception) {
            return ui.joinLines(ui.greet(), ui.showError(exception));
        }
    }

    /**
     * Evaluates if the bye command has been issued and should terminate the program.
     * @return Whether the bye command has been issued.
     */
    public boolean isExit() {
        return prevCommand != null && prevCommand.isExit();
    }

    /**
     * Processes the user input and returns Echo's response.
     *
     * @param input User input.
     * @return Echo's output.
     */
    public String getResponse(String input) {
        return getTypedResponse(input).text();
    }

    /**
     * Processes user input and returns text with its GUI presentation type.
     *
     * @param input User input.
     * @return Response text and presentation type.
     */
    public EchoResponse getTypedResponse(String input) {
        try {
            Command command = parser.parse(input);
            prevCommand = command;
            return new EchoResponse(command.execute(taskList, ui, storage), ResponseType.NORMAL);
        } catch (EchoException exception) {
            return new EchoResponse(ui.showError(exception), ResponseType.ERROR);
        }
    }
}
