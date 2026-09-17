package botavius;

import botavius.exception.BotaviusException;
import botavius.parser.Parser;
import botavius.storage.Storage;
import botavius.tasklist.TaskList;
import botavius.ui.Ui;

/** Entry point and coordinator for the Botavius command-line application. */
public class Botavius {
    /** Default file used to persist tasks when no other path is specified. */
    private static final String DEFAULT_SAVE_FILE = "save.txt";
    /** Provides file-based persistence for the current task list. */
    private static Storage storage;
    /** Holds the tasks loaded for the current session. */
    private static TaskList tasks;
    /** Reads input and prints user-facing messages. */
    private static Ui ui;
    /** Converts user commands into task-list operations. */
    private static Parser parser;

    /**
     * Creates an application using the specified save-file path.
     *
     * @param filePath path of the file used to load and save tasks
     */
    public Botavius(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        String taskData = storage.load();
        try {
            tasks = new TaskList(taskData);
        } catch (BotaviusException exception) {
            tasks = new TaskList("");
        }
        parser = new Parser();

        // These objects are required by run(); failure here indicates a programming error during setup.
        assert ui != null : "UI must be created before the session starts";
        assert storage != null : "Storage must be created before the session starts";
        assert tasks != null : "Task list must be created before the session starts";
        assert parser != null : "Parser must be created before the session starts";
    }

    /** Runs the interactive session until the user enters {@code bye}. */
    public static void run() {
        // The constructor establishes these invariants before run() is called.
        assert ui != null : "UI must be initialized before run()";
        assert storage != null : "Storage must be initialized before run()";
        assert tasks != null : "Task list must be initialized before run()";
        assert parser != null : "Parser must be initialized before run()";
        String command = "";
        ui.printBanner();
        System.out.println(ui.greet());
        while (!command.equalsIgnoreCase("bye")) {
            try {
                command = ui.getUserInput();
                command = parser.process(command, tasks);
                System.out.println(ui.printFormattedMessage(command));
            } catch (BotaviusException e) {
                System.out.println(e.getMessage());
            }
        }
        
        storage.save(tasks.getTaskStrings());
        System.out.println(ui.goodbye());
    }

    /** Starts Botavius using {@code save.txt} as its save file.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        new Botavius(DEFAULT_SAVE_FILE).run();
    }
}
