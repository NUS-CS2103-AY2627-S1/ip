package hamtaro;

import hamtaro.command.Command;
import hamtaro.exception.HamtaroException;
import hamtaro.task.TaskList;
import hamtaro.util.Utils;

public class Hamtaro {

    /**
     * Runs the command-object based command loop.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        TaskList tasks = Utils.loadTasks();

        boolean isRunning = true;

        ui.showWelcome();
        while (isRunning) {
            try {
                Command command = parser.parseCommand(ui.readCommand());
                command.execute(tasks, ui);
                if (command.changesTasks()) {
                    Utils.saveTasks(tasks);
                }
                isRunning = !command.isExit();
            } catch (HamtaroException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    public String getResponse(String input) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        TaskList tasks = Utils.loadTasks();

        try {
            Command command = parser.parseCommand(input);
            String response = command.execute(tasks, ui);
            if (command.changesTasks()) {
                Utils.saveTasks(tasks);
            }
            return response;

        } catch (HamtaroException e) {
            return ui.showError(e.getMessage());
        }
    }
}


