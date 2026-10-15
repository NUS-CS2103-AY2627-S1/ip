package computa;
import java.time.LocalDate;

public class Computa {
    private static final String TASK_FILE_PATH = "./tasks.txt";
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private boolean lastCommandHadError;

    public Computa(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("The task file path must be usable");
        }
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (ComputaException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }
        if (tasks.generateRecurringTasks(LocalDate.now())) {
            storage.saveTasks(tasks);
        }
    }

    public void run() {
        ui.showWelcome();
        boolean shouldExit = false;
        while (!shouldExit) {
            try {
                shouldExit = Parser.parse(ui.readCommand(), tasks, ui, storage);
            } catch (IndexOutOfBoundsException e) {
                ui.showError("that task number doesnt exist girl");
            } catch (ComputaException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showError("bruh put a real number");
            }
        }
        ui.showGoodbye();
    }

    /**
     * Processes one command and returns the text that should be shown to a GUI.
     *
     * @param command the command entered by the user
     * @return the chatbot response, including any error message
     */
    public String processCommand(String command) {
        assert command != null : "A command must be provided for processing";
        lastCommandHadError = false;
        try {
            Parser.parse(command, tasks, ui, storage);
        } catch (IndexOutOfBoundsException e) {
            lastCommandHadError = true;
            ui.showError("that task number doesnt exist girl");
        } catch (ComputaException e) {
            lastCommandHadError = true;
            ui.showError(e.getMessage());
        } catch (NumberFormatException e) {
            lastCommandHadError = true;
            ui.showError("bruh put a real number");
        }
        return ui.collectOutput();
    }

    /** Returns whether the most recently processed GUI command failed. */
    public boolean lastCommandHadError() {
        return lastCommandHadError;
    }
    /**
     * The main method that serves as the entry point for the application.
     *
     * @param args Command line arguments (not used).
     */

    public static void main(String[] args) {
        new Computa(TASK_FILE_PATH).run();
    }

    public static class ComputaException extends Exception {
        public ComputaException(String message) {
            super(message);
        }
    }
}
