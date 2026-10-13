package ruby;

import ruby.command.Command;
import ruby.command.ExitCommand;
import ruby.command.Parser;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;
import ruby.ui.Ui;

/**
 * Starts the Ruby chatbot application.
 */
public class Ruby {
    private final Storage storage;
    private final TaskList taskList;
    private final ContactList contactList;
    private final RubyException startupError;
    private boolean isExitRequested;

    /**
     * Creates Ruby and restores any tasks saved at the given file path.
     *
     * @param filePath Location of Ruby's saved task data.
     */
    public Ruby(String filePath) {
        storage = new Storage(filePath);

        TaskList loadedTaskList;
        ContactList loadedContactList;
        RubyException loadError = null;
        try {
            Storage.Data data = storage.load();
            loadedTaskList = data.tasks();
            loadedContactList = data.contacts();
        } catch (RubyException exception) {
            loadedTaskList = new TaskList();
            loadedContactList = new ContactList();
            loadError = exception;
        }
        taskList = loadedTaskList;
        contactList = loadedContactList;
        startupError = loadError;
    }

    /**
     * Answers one command with the text Ruby would display in response.
     *
     * @param input The user's raw command text.
     * @return Ruby's response to the command.
     */
    public String getResponse(String input) {
        isExitRequested = false;
        if (startupError != null) {
            return errorMessage(startupError);
        }
        try {
            Command command = Parser.parse(input);
            String response = command.execute(taskList, contactList, storage);
            isExitRequested = command instanceof ExitCommand;
            return response;
        } catch (RubyException exception) {
            return errorMessage(exception);
        }
    }

    /**
     * Returns whether the most recent response followed a successful exit command.
     *
     * @return True only when the most recent command executed successfully and requested exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Formats a user-facing error with Ruby's voice.
     *
     * @param exception Error raised while processing a command.
     * @return The error text with Ruby's error prefix.
     */
    private static String errorMessage(RubyException exception) {
        return "Hold on — " + exception.getMessage();
    }

    /**
     * Runs Ruby in the console until the user exits or the input stream ends.
     */
    public void run() {
        Ui ui = new Ui();
        ui.printWelcome();
        if (startupError != null) {
            ui.printMessage(errorMessage(startupError));
            return;
        }
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            try {
                Command command = Parser.parse(ui.readCommand());
                String response = command.execute(taskList, contactList, storage);
                ui.printMessage(response);
                isExit = command instanceof ExitCommand;
            } catch (RubyException exception) {
                ui.printMessage(errorMessage(exception));
            }
        }
    }

    /**
     * Starts Ruby in the console and processes commands until the input ends
     * or the user exits.
     *
     * @param args Command-line arguments; not used by Ruby.
     */
    public static void main(String[] args) {
        new Ruby("data/ruby.txt").run();
    }

}
