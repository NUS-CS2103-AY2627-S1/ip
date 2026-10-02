package florkofcows;

import java.io.IOException;

import florkofcows.command.Command;
import florkofcows.exception.FlorkingExceptions;
import florkofcows.parser.Parser;
import florkofcows.storage.Storage;
import florkofcows.task.TaskList;
import florkofcows.ui.Ui;

/**
 * Main application entry point for FlorkOfCows.
 *
 * Sets up UI, storage and the task list, then enters the main command loop.
 * Supports todos, deadlines, and events.
 */
public class FlorkOfCows {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean lastCommandError;
    private boolean exitRequested;

    /**
     * Creates a new FlorkOfCows instance with a fresh UI and storage.
     */
    public FlorkOfCows() {
        this.ui = new Ui();
        this.storage = new Storage();
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            tasks = new TaskList();
            ui.showLoadingError(e.getMessage());
        }
    }

    /**
     * Starts the chatbot: loads any saved tasks, then repeatedly reads,
     * parses, and executes user commands until an exit command is given.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        new FlorkOfCows().run();
    }

    /**
     * Runs the main command loop of the chatbot.
     *
     * Loads saved tasks from disk, then repeatedly reads user input,
     * parses it into commands, and executes them until an exit command is given.
     */
    public void run() {
        String banner = "  ______ _            _     ____   __  _____                  \n"
                + " |  ____| |          | |   / __ \\ / _|/ ____|                 \n"
                + " | |__  | | ___  _ __| | _| |  | | |_| |     _____      _____ \n"
                + " |  __| | |/ _ \\| '__| |/ / |  | |  _| |    / _ \\ \\ /\\ / / __|\n"
                + " | |    | | (_) | |  |   <| |__| | | | |___| (_) \\ V  V /\\__ \\\n"
                + " |_|    |_|\\___/|_|  |_|\\_\\____/|_|  \\_____\\___/ \\_/\\_/ |___/\n";

        ui.showWelcome(banner);

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand.isEmpty()) {
                    continue;
                }
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (FlorkingExceptions e) {
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                ui.showError("" + e.getMessage());
            }
        }
    }

    public String getResponse(String input) {
        lastCommandError = false;
        exitRequested = false;
        // Redirect System.out so ui.showX() calls write here instead of the console
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            exitRequested = command.isExit();
        } catch (FlorkingExceptions e) {
            lastCommandError = true;
            ui.showError(e.getMessage());
        } catch (RuntimeException e) {
            lastCommandError = true;
            ui.showError("" + e.getMessage());
        } finally {
            System.setOut(originalOut); // always restore, even if something throws
        }

        return outContent.toString();
    }

    public boolean isLastCommandError() {
        return lastCommandError;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }
}
