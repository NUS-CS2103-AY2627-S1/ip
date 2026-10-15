package ralph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import ralph.command.Command;
import ralph.command.CommandHandler;
import ralph.exception.RalphException;
import ralph.model.TaskList;
import ralph.storage.Storage;
import ralph.ui.Ui;

/**
 * Main entrypoint that wires Ui, Storage, Parser and TaskList together.
 */
public class Ralph {
    private final Ui ui;
    private final Storage storage;
    private final CommandHandler commandHandler;
    private final TaskList tasks;
    private String commandType;

    /**
     * Constructs the main application wiring together UI, storage and the command handler.
     *
     * @param filePath file path used for data persistence
     */
    public Ralph(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.commandHandler = new CommandHandler(new ralph.parser.Parser());
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /**
     * Generates a response for a GUI-style chat message using the same command execution path as the CLI loop.
     *
     * @param input input entered by the user
     * @return the command result as displayed by the UI
     */
    public String getResponse(String input) {
        if (input == null || input
            .trim()
            .isEmpty()) {
            commandType = "";
            return "Oh no! You didn't say anything — try typing a command.";
        }

        commandType = input
            .trim()
            .split("\\s+", 2)[0].toLowerCase();
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        try {
            System.setOut(capture);
            boolean shouldExit = false;
            try {
                Command command = commandHandler.parse(input);
                shouldExit = command.execute(tasks, ui, storage);
            } catch (RalphException e) {
                ui.showError(e.getMessage());
            }
            if (shouldExit) {
                try {
                    javafx.application.Platform.exit();
                } catch (Throwable t) {
                    System.exit(0);
                }
            }
            return buffer
                .toString(StandardCharsets.UTF_8)
                .trim();
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * Returns the command type for the most recent user input.
     *
     * @return the first word of the latest input
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns the banner text from the UI so GUI can display the same startup banner as the CLI.
     *
     * @return the banner string
     */
    public String getBanner() {
        return ui.getBanner();
    }

    /**
     * Runs the main read-eval-print loop reading from stdin until the user exits.
     */
    public void run() {
        ui.printBanner();
        Scanner scanner = new Scanner(System.in);
        ui.printPrompt();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            ui.printSeparator();
            try {
                Command command = commandHandler.parse(line);
                boolean shouldExit = command.execute(tasks, ui, storage);
                ui.printSeparator();
                if (shouldExit) {
                    break;
                }
            } catch (RalphException e) {
                ui.showError(e.getMessage());
                ui.printSeparator();
            }
        }
        scanner.close();
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments (ignored)
     */
    static void main(String[] args) {
        new Ralph("data/ralph.txt").run();
    }
}
