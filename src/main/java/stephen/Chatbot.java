package stephen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import stephen.command.Command;
import stephen.exception.ChatbotException;
import stephen.parser.Parser;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Provides Orbit's stateful command-processing logic independently of any input interface. */
public class Chatbot {
    private static final String WELCOME_MESSAGE = "Orbit online.\nReady to plan your next move?";

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final String startupMessage;

    /** Creates a chatbot that stores its tasks at the given path. */
    public Chatbot(Path dataFile) {
        parser = new Parser();
        storage = new Storage(dataFile);

        List<Task> loadedTasks = List.of();
        String loadMessage = "";
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            loadMessage = "\nNavigation alert: I couldn't load your tasks. Starting with an empty list.";
        }
        tasks = new TaskList(loadedTasks);
        startupMessage = WELCOME_MESSAGE + loadMessage;
    }

    /** Returns the greeting, including a recovery notice if stored tasks could not be loaded. */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Processes one command and returns all output that it produced.
     *
     * @param input user command
     * @return response text and exit signal
     */
    public ChatbotResponse getResponse(String input) {
        List<String> outputLines = new ArrayList<>();
        Ui responseUi = new ResponseUi(outputLines);
        Command command;
        try {
            command = parser.parse(input.trim(), tasks);
            command.execute(tasks, responseUi, storage);
        } catch (ChatbotException e) {
            responseUi.showError(e.getMessage());
            return new ChatbotResponse(String.join("\n", outputLines), false, true);
        }
        return new ChatbotResponse(String.join("\n", outputLines), command.isExit(), false);
    }

    /** Captures command output without the console UI's decorative divider lines. */
    private static class ResponseUi extends Ui {
        private final List<String> outputLines;

        ResponseUi(List<String> outputLines) {
            this.outputLines = outputLines;
        }

        @Override
        public void showLine(String message) {
            outputLines.add(message);
        }

        @Override
        public void showDivider() {
            // Chat bubbles provide the visual separation used by the GUI.
        }
    }
}
