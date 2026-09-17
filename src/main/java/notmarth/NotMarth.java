package notmarth;

import notmarth.command.Command;
import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.parser.Parser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/**
 * The main entry point for the NotMarth chatbot.
 */
public class NotMarth {
    private static final int MAX_TASKS = 100;

    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final ContactList contacts;
    private final String startupWarning;

    /**
     * Creates a NotMarth session using the supplied task archive path.
     *
     * @param filePath the path of the task archive.
     */
    public NotMarth(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);

        Storage.LoadResult loadResult = storage.load(MAX_TASKS);
        tasks = new TaskList(loadResult.getTasks(), MAX_TASKS);
        contacts = new ContactList(loadResult.getContacts());
        startupWarning = loadResult.getWarning();
    }

    /** Runs the command loop until the user enters {@code bye} or input ends. */
    public void run() {
        ui.showWelcome();
        if (startupWarning != null) {
            ui.showStartupWarning(startupWarning);
            return;
        }
        ui.showPrompt();

        boolean isExit = false;
        while (!isExit && ui.hasNextLine()) {
            try {
                String fullCommand = ui.readCommand();
                if (!fullCommand.equals("bye")) {
                    ui.showCommand(fullCommand);
                }
                Command command = parser.parse(fullCommand);
                command.execute(tasks, contacts, ui, storage);
                isExit = command.isExit();
            } catch (NotMarthException exception) {
                ui.showError(exception.getMessage());
            } finally {
                if (!isExit) {
                    ui.showLine();
                }
            }
        }
    }

    /**
     * Starts NotMarth with its default battle-plan archive.
     *
     * @param args command-line arguments, which are currently unused.
     */
    public static void main(String[] args) {
        new NotMarth("data/notmarth.txt").run();
    }
}
