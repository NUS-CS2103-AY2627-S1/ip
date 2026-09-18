package chud;

import chud.command.Command;
import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.TaskList;
import chud.ui.Ui;

/**
 * Entry point for the Chud chatbot. Wires together {@link Ui}, {@link Storage}, and
 * {@link TaskList}, then repeatedly reads a command, parses it via {@link Parser}, and executes
 * it, until an exit command (i.e. {@code bye}) ends the loop.
 */
public class Chud {
    /**
     * Starts the chatbot, reading commands from standard input until the user exits.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/chud.txt");
        TaskList tasks = new TaskList(storage.load());

        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ChudException e) {
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                // A command should only ever fail with ChudException (its expected, user-facing
                // failure mode) -- this is a last-resort safety net against an unanticipated bug
                // in a command's own logic, so one bad command can't crash the whole session.
                ui.showError("something broke (" + e.getClass().getSimpleName() + "). Try again.");
            } finally {
                ui.showLine();
            }
        }
    }
}
