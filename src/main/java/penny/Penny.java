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
            String save = FileManager.readData(fileName);
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
                    startupWarning = "Stopped loading at line " + (i + 1) + ": " + e.getMessage();
                    savingBlocked = true;
                    break;
                }
            }
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
     * Processes one line of user input: persists task changes before applying them,
     * and turns any {@link PennyException} into an error message
     * instead of propagating it. Package-private so the GUI controller can act on
     * {@link ParseResult#shouldExit()} as well as the message text.
     */
    ParseResult respond(String input) {
        try {
            String normalized = input == null ? "" : input.strip();
            Command command = Command.parse(normalized.split("\\s+", 2)[0]);
            boolean changesTasks = command == Command.TODO || command == Command.DEADLINE || command == Command.EVENT
                    || command == Command.MARK || command == Command.UNMARK || command == Command.DELETE;
            if (!changesTasks) {
                return Parser.handleInput(taskList, input, false);
            }
            if (savingBlocked) {
                return new ParseResult(startupWarning, false);
            }
            // Execute against a replayed copy so a failed save cannot change the live task list.
            TaskList candidate = new TaskList();
            for (String savedCommand : commands) {
                Parser.handleInput(candidate, savedCommand, true);
            }
            ParseResult result = Parser.handleInput(candidate, input, false);
            assert result != null : "Parser.handleInput should always return a ParseResult";
            ArrayList<String> updated = new ArrayList<>(commands);
            updated.add(normalized);
            try {
                FileManager.writeData(fileName, String.join("\n", updated));
            } catch (PennyException e) {
                return new ParseResult(e.getMessage() + " No changes were applied. Please retry.", false);
            }
            commands.add(normalized);
            taskList = candidate;
            return result;
        } catch (PennyException e) {
            return new ParseResult(e.getMessage(), false);
        }
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
