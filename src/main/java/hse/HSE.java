package hse;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import hse.command.Command;
import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

//CHECKSTYLE.OFF: AbbreviationAsWordInName
/** Coordinates the application's user interface, command parsing, tasks, and storage. */
public class HSE {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /** Creates an application that stores its tasks at the supplied path. */
    public HSE(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = storage.load();
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /** Processes one user command and returns the response text for graphical display. */
    public String getResponse(String fullCommand) {
        ui.setGuiMode();
        try {
            Command command = Parser.parse(fullCommand.trim());
            command.execute(tasks, ui, storage);
        } catch (NumberFormatException e) {
            ui.showError("Please enter a valid task number.");
        } catch (IndexOutOfBoundsException e) {
            ui.showError("That task number does not exist.");
        } catch (DateTimeParseException e) {
            ui.showError("Please use yyyy-MM-dd or d/M/yyyy, with optional HHmm time.");
        } catch (IllegalArgumentException e) {
            ui.showError(e.getMessage());
        } catch (IOException e) {
            ui.showError("Could not save your tasks.");
        }
        return ui.getResponse();
    }

    /** Runs the command loop until the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (NumberFormatException e) {
                ui.showError("Please enter a valid task number.");
            } catch (IndexOutOfBoundsException e) {
                ui.showError("That task number does not exist.");
            } catch (DateTimeParseException e) {
                ui.showError("Please use yyyy-MM-dd or d/M/yyyy, with optional HHmm time.");
            } catch (IllegalArgumentException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Could not save your tasks.");
            } finally {
                ui.showLine();
            }
        }
    }

    /** Starts the application using its standard data file. */
    public static void main(String[] args) {
        new HSE("data/hse.txt").run();
    }
}
//CHECKSTYLE.ON: AbbreviationAsWordInName
