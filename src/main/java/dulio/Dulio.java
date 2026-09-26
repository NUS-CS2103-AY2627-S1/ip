package dulio;

import java.io.IOException;

import dulio.command.Command;
import dulio.exception.DulioException;
import dulio.gui.GuiUi;
import dulio.parser.Parser;
import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Coordinates Dulio's user interface, parser, commands, and task list.
 */
public class Dulio {
    private Ui ui;
    private TaskList tasks;

    /**
     * Creates a Dulio application with its default UI and task list.
     */
    public Dulio() {
        ui = new Ui();
        tasks = new TaskList();
    }

    /**
     * Starts a Dulio session.
     *
     * @param args Command-line arguments, which are not used.
     * @throws IOException If task data cannot be saved.
     */
    public static void main(String[] args) throws IOException {
        new Dulio().run();
    }

    /**
     * Runs the command-processing loop until the user exits or input ends.
     *
     * @throws IOException If task data cannot be saved.
     */
    public void run() throws IOException {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String line = ui.readCommand();
            if (line == null) {
                break;
            }
            try {
                Command command = Parser.parseCommand(line);
                command.execute(tasks, ui);
                isExit = command.isExit();
            } catch (DulioException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Unable to save your task right now. Please check that the data file is writable.");
            }
        }
        ui.close();
    }

    /**
     * Returns Dulio's response after processing one command for a graphical interface.
     *
     * @param input The command to process.
     * @return Dulio's response text.
     */
    public String getResponse(String input) {
        GuiUi guiUi = new GuiUi();
        try {
            Command command = Parser.parseCommand(input);
            command.execute(tasks, guiUi);
        } catch (DulioException e) {
            guiUi.showError(e.getMessage());
        } catch (IOException e) {
            guiUi.showError("Unable to save your task right now.");
        }
        return guiUi.getResponse();
    }

}
