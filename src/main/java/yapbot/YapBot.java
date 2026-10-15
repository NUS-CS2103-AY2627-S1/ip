package yapbot;

import yapbot.command.Command;
import yapbot.exception.YapBotException;
import yapbot.parser.Parser;
import yapbot.storage.Storage;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Entry point for yapBot, a command-line task-tracking chatbot.
 */
public class YapBot {

    /**
     * Runs the yapBot command loop, reading commands from standard input
     * until the user issues the "bye" command.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Task[] loaded = new Task[TaskList.MAX_CAPACITY];
        int loadedCount = Storage.load(loaded);
        TaskList taskList = new TaskList(loaded, loadedCount);

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(taskList, ui);
                isExit = command.isExit();
            } catch (YapBotException e) {
                ui.showMessage(e.getMessage());
            } catch (Exception e) {
                // Safety net for unanticipated bad input
                ui.showMessage("Oops, something went wrong: " + e.getMessage());
            }
        }
    }
}