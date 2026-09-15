package penny;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Loads in commands from previous sessions, and answers user input either via
 * the CLI ({@link #main}) or the GUI ({@code MainWindow}, via {@link #respond}).
 */
public class Penny {
    private static final String FILE_NAME = "commands.txt";

    private TaskList taskList = new TaskList();
    private final ArrayList<String> commands = new ArrayList<>();
    private final String fileName;
    private String startupWarning = "";
    private boolean savingBlocked;

    /**
     * Creates a Penny instance, loading tasks saved from a previous session.
     */
    public Penny() {
        this(FILE_NAME);
    }

    /**
     * Loads a chosen save file, allowing tests to use isolated temporary files.
     * Stops at the first damaged command because later task numbers may depend on it.
     */
    Penny(String fileName) {
        this.fileName = fileName;
        try {
            loadCommands(FileManager.readData(fileName));
        } catch (PennyException e) {
            startupWarning = e.getMessage();
            savingBlocked = true;
        }
        if (savingBlocked) {
            startupWarning += " Changes are disabled to protect saved data. Repair the file and restart Penny.";
        }
    }

    public String getStartupWarning() {
        return startupWarning;
    }

    /**
     * Replays saved lines until the first invalid command, preserving its line number in the error.
     */
    private void loadCommands(String save) throws PennyException {
        String[] lines = save.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String command = lines[i].strip();
            if (command.isEmpty()) {
                continue;
            }
            try {
                if (Parser.handleInput(taskList, command, true).shouldExit()) {
                    throw new PennyException("Exit commands do not belong in saved history.");
                }
                commands.add(command);
            } catch (PennyException e) {
                throw new PennyException("Stopped loading at line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Processes one line of user input: persists task changes before applying them,
     * and turns any {@link PennyException} into an error message
     * instead of propagating it. Package-private so the GUI controller can act on
     * {@link ParseResult#shouldExit()} as well as the message text.
     */
    ParseResult respond(String input) {
        try {
            String normalized = input == null ? "" : input.strip();
            Command command = Command.parse(normalized.split("\\s+", 2)[0]);
            if (!changesTasks(command)) {
                return Parser.handleInput(taskList, input, false);
            }
            if (savingBlocked) {
                return new ParseResult(startupWarning, false);
            }
            return applyTaskChange(input, normalized);
        } catch (PennyException e) {
            return new ParseResult(e.getMessage(), false);
        }
    }

    /**
     * Identifies commands that need a successful save before changing the live task list.
     */
    private boolean changesTasks(Command command) {
        return command == Command.TODO || command == Command.DEADLINE || command == Command.EVENT
                || command == Command.MARK || command == Command.UNMARK || command == Command.DELETE;
    }

    /**
     * Executes on a separate list and publishes the result only after saving succeeds.
     */
    private ParseResult applyTaskChange(String input, String normalized) throws PennyException {
        TaskList candidate = replayCommands();
        ParseResult result = Parser.handleInput(candidate, input, false);
        assert result != null : "Parser.handleInput should always return a ParseResult";
        try {
            saveCommand(normalized);
        } catch (PennyException e) {
            return new ParseResult(e.getMessage() + " No changes were applied. Please retry.", false);
        }
        commands.add(normalized);
        taskList = candidate;
        return result;
    }

    /**
     * Reconstructs a separate task list so a failed save cannot change the live tasks.
     */
    private TaskList replayCommands() throws PennyException {
        TaskList candidate = new TaskList();
        for (String savedCommand : commands) {
            Parser.handleInput(candidate, savedCommand, true);
        }
        return candidate;
    }

    /**
     * Saves the proposed history without changing the in-memory command history.
     */
    private void saveCommand(String command) throws PennyException {
        ArrayList<String> updated = new ArrayList<>(commands);
        updated.add(command);
        FileManager.writeData(fileName, String.join("\n", updated));
    }

    public static void main(String[] args) {
        Penny penny = new Penny();
        Scanner scanner = new Scanner(System.in);

        if (!penny.getStartupWarning().isEmpty()) {
            UI.display(penny.getStartupWarning());
        }
        if (!penny.commands.isEmpty()) {
            UI.display("I have loaded your tasks from our previous session.");
        }
        UI.display("Hi, I'm Penny, what can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            ParseResult result = penny.respond(input);
            UI.display(result.message());
            if (result.shouldExit()) {
                break;
            }
        }
        if (scanner.ioException() != null) {
            UI.display("Could not read further input. Previously saved tasks are safe.");
        }
    }
}
