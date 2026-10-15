package nano;

import nano.command.Command;
import nano.command.Parser;
import nano.task.TaskList;
import nano.ui.Ui;

/**
 * Runs the Nano chatbot and handles user commands.
 */
public class Nano {
    private static final String DATA_FILE = "./data/nano.txt";

    private final Parser parser = new Parser();
    private final Storage storage = new Storage(DATA_FILE);
    private TaskList tasks;

    /**
     * Creates a Nano chatbot and loads saved tasks.
     */
    public Nano() {
        try {
            tasks = storage.load();
        } catch (NanoException e) {
            System.out.println(
                    "Warning: Could not load saved tasks. "
                            + "Starting with an empty list."
            );
            tasks = new TaskList();
        }
    }

    /**
     * Starts the Nano chatbot and processes user commands.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Nano nano = new Nano();

        ui.showWelcome();

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();

            if (input.trim().equals("bye")) {
                ui.showGoodbye();
                isRunning = false;
                continue;
            }

            ui.showMessage(nano.getResponse(input));
        }

        ui.close();
    }

    /**
     * Returns Nano's response to the user's input.
     *
     * @param input the user's command.
     * @return Nano's response to the command.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Oops! Please enter a command.";
        }

        try {
            Command command = parser.parse(input.trim());
            assert command != null;
            return command.execute(tasks, storage);
        } catch (NanoException e) {
            return "Oops! " + e.getMessage();
        }
    }
}
