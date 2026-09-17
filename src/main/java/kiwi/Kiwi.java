package kiwi;

import java.util.List;

import kiwi.command.Command;
import kiwi.command.ExitCommand;
import kiwi.task.TaskList;

/**
 * Kiwi is a simple chatbot that stores to-dos, deadlines, and events in memory,
 * lists them, finds them by keyword, finds free time slots, and can mark, unmark, or delete them.
 * <p>
 * This class wires {@link Ui}, {@link Parser}, {@link TaskList}, and {@link Storage}
 * together. Each user line becomes a {@link Command} that executes itself.
 */
public class Kiwi {
    /** Default save file used when launching from {@link #main(String[])}. */
    public static final String DEFAULT_FILE_PATH = "./data/kiwi.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExit;
    private boolean isError;

    /**
     * Creates a Kiwi chatbot that loads tasks from {@code filePath}.
     *
     * @param filePath path to the task save file.
     */
    public Kiwi(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the chatbot loop until an {@link ExitCommand} is executed.
     */
    public void run() {
        ui.showLoadWarnings(storage.getLoadWarnings());
        ui.showWelcome();
        boolean shouldExit = false;
        while (!shouldExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                shouldExit = command.isExit();
            } catch (KiwiException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.printResponse();
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes one user command, then returns the reply for the GUI.
     *
     * @param input the full command line from the user.
     * @return the text to show in a dialog box.
     */
    public String getResponse(String input) {
        isExit = false;
        isError = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (KiwiException e) {
            ui.showError(e.getMessage());
            isError = true;
        }
        return ui.consumeResponse();
    }

    /**
     * Returns whether the last {@link #getResponse(String)} command should end the session.
     *
     * @return {@code true} after a successful {@code bye}; otherwise {@code false}.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the last {@link #getResponse(String)} call failed with a {@link KiwiException}.
     * The GUI uses this to style error replies differently from ordinary messages.
     *
     * @return {@code true} if the last command produced an error; otherwise {@code false}.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns the greeting shown when the GUI window opens.
     * Omits the ASCII banner so the message stays readable in a small window.
     * Includes any save-file warnings from startup so GUI users see them too.
     *
     * @return a short welcome message, possibly preceded by load warnings.
     */
    public String getWelcomeMessage() {
        List<String> warnings = storage.getLoadWarnings();
        if (warnings.isEmpty()) {
            return ui.getGuiWelcome();
        }
        StringBuilder welcome = new StringBuilder();
        for (String warning : warnings) {
            welcome.append(warning).append('\n');
        }
        welcome.append(ui.getGuiWelcome());
        return welcome.toString();
    }

    /**
     * Starts Kiwi using the default save file {@link #DEFAULT_FILE_PATH}.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Kiwi(DEFAULT_FILE_PATH).run();
    }
}
