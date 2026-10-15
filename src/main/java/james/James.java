package james;

import james.command.CommandProcessor;
import james.command.CommandResponse;
import james.ui.Ui;

/**
 * Orchestrates interactions between
 * Ui, Storage, Parser, and TaskList components.
 */
public class James {
    private static final String DEFAULT_STORAGE_PATH = "data/james.txt";


    private final Ui ui;
    private final CommandProcessor commandProcessor;

    /**
     * Initializes the chatbot application with the given storage file path.
     *
     * @param filePath Path to the persistent storage file.
     */
    public James(String filePath) {
        ui = new Ui();
        commandProcessor = new CommandProcessor(filePath);
    }

    /**
     * Returns startup diagnostics for tasks that could not be loaded.
     *
     * @return Warning and recovery guidance, or an empty string when loading succeeded.
     */
    public String getLoadWarning() {
        return commandProcessor.getLoadWarning();
    }

    /**
     * Processes a command and returns its response for display.
     *
     * @param input Command entered by the user.
     * @return Response containing the command result and display information.
     */
    public CommandResponse getResponse(String input) {
        return commandProcessor.process(input);
    }

    /**
     * Starts the main command processing loop.
     */
    public void run() {
        ui.greet();
        if (!getLoadWarning().isEmpty()) {
            ui.showResponse(getLoadWarning());
        }
        boolean isRunning = true;
        while (isRunning && ui.hasNextCommand()) {
            CommandResponse response = commandProcessor.process(ui.readCommand());
            ui.showResponse(response.getMessage());
            isRunning = !response.isExit();
        }
        ui.close();
    }

    /**
     * Starts the console application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new James(DEFAULT_STORAGE_PATH).run();
    }
}
