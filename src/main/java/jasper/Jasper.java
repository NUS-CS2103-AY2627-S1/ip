package jasper;

import java.util.Optional;

import jasper.command.Command;
import jasper.command.CommandResult;
import jasper.command.CommandType;
import jasper.parser.Parser;
import jasper.storage.Storage;
import jasper.task.TaskList;

/**
 * Represents the main application class that initializes and coordinates the application components.
 */
public class Jasper {
    /** Storage component handling data persistence */
    private final Storage storage;
    /** List component managing user tasks */
    private final TaskList tasks;
    private final Optional<String> startupWarning;

    /**
     * Constructs a Jasper instance and initializes core application components.
     *
     * @param parent Parent directory path for the storage file.
     * @param filename Name of the storage file.
     */
    public Jasper(String parent, String filename) {
        storage = new Storage(parent, filename);
        TaskList tmpTasks;
        Optional<String> tmpWarning;
        try {
            tmpTasks = storage.load();
            tmpWarning = Optional.empty();
        } catch (JasperException e) {
            tmpTasks = new TaskList();
            try {
                String backupName = storage.backup();
                tmpWarning = Optional.of("Oh dear, your savefile appears to corrupted!\n"
                        + "Backup is at " + backupName
                        + " and a new, blank task list you shall be graced with.");
            } catch (JasperException ex) {
                tmpWarning = Optional.of("CRITICAL: Savefile corrupted and could not be backed up, "
                        + "and will be rewritten with a new, blank list of tasks!");
            }

        }
        tasks = tmpTasks;
        startupWarning = tmpWarning;
    }

    /**
     * Returns the result of parsing and executing the user input command.
     *
     * @param input String input provided by the user.
     * @return Result containing the command type and execution response.
     */
    public CommandResult getResult(String input) {
        try {
            Command c = Parser.parseCmd(input);
            assert tasks != null : "Task list missing";
            CommandResult result = c.execute(tasks);
            switch (result.commandType()) {
                case FIND:
                    // Fallthrough
                case LIST:
                    break;
                default:
                    storage.save(tasks);
                    break;
            }
            return result;
        } catch (JasperException e) {
            return new CommandResult(CommandType.ERROR, "Error: " + e.getMessage());
        }
    }

    public Optional<String> getStartupWarning() {
        return startupWarning;
    }
}
