package pixel;

import java.nio.file.Path;
import java.util.ArrayList;

import pixel.command.Command;
import pixel.parser.Parser;
import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Starts the Pixel chatbot application.
 */
public class Pixel {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;
    private final ArrayList<String> loadWarnings;
    private boolean isLastResponseError;

    /**
     * Creates a Pixel chatbot backed by the specified data file.
     *
     * @param filePath Path to the task data file.
     */
    public Pixel(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        loadWarnings = new ArrayList<>();
        tasks = new TaskList(storage.load(loadWarnings));
    }

    /**
     * Executes a command and returns all messages produced by Pixel.
     *
     * @param input Command entered by the user.
     * @return Pixel's response, with one message per line.
     */
    public String getResponse(String input) {
        isLastResponseError = false;
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(message -> {
            if (response.length() > 0) {
                response.append(System.lineSeparator());
            }
            response.append(message);
        });

        try {
            Command command = parser.parse(input.trim());
            command.execute(tasks, responseUi, storage);
            isLastResponseError = command.isError();
        } catch (IllegalArgumentException exception) {
            isLastResponseError = true;
            response.append(exception.getMessage());
        }
        return response.toString();
    }

    /**
     * Returns whether the latest GUI response reports invalid user input.
     *
     * @return Whether the latest response should use error presentation.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /** Starts Pixel's console command loop. */
    public void run() {
        ui.showWelcome();
        for (String warning : loadWarnings) {
            ui.showMessage(warning);
        }
        if (!loadWarnings.isEmpty()) {
            ui.showLine();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            try {
                Command command = parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (IllegalArgumentException exception) {
                ui.showMessage(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts Pixel using the default relative, OS-independent data path.
     *
     * @param args Command-line arguments, which Pixel does not currently use.
     */
    public static void main(String... args) {
        new Pixel(Path.of("data", "pixel.txt")).run();
    }
}
