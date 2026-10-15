package probe;

import probe.handler.CommandHandler;
import probe.parser.Parser;
import probe.storage.Storage;
import probe.task.TaskList;
import probe.ui.Ui;

/**
 * Coordinates the user interface, parser, task list, and storage.
 */
public class Probe {
    private static final String DEFAULT_TASK_FILE_PATH = "probe.txt";
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final CommandHandler commandHandler;

    /**
     * Creates an application using the specified task-storage file.
     *
     * @param filePath Path of the task-storage file.
     */
    public Probe(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList(storage.load());
        commandHandler = new CommandHandler(storage, tasks, parser);
    }

    /**
     * Runs the command loop until the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String command = ui.readCommand();
            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }
            try {
                ui.showMessage(commandHandler.execute(command));
            } catch (ProbeException e) {
                ui.showMessage(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Starts the application using the default storage file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Probe(DEFAULT_TASK_FILE_PATH).run(); 
    }
}
