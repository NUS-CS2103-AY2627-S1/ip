package gooble;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.function.Consumer;

import gooble.command.Command;
import gooble.command.CommandContext;
import gooble.command.Parser;
import gooble.storage.Storage;
import gooble.task.TaskList;
import gooble.ui.Ui;

/**
 * Composes Gooble's application services and runs the command loop.
 */
public class Gooble {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final CommandContext commandContext;

    /**
     * Creates a Gooble application backed by the supplied task file.
     *
     * @param filePath path to the task storage file
     */
    public Gooble(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(Path.of(filePath));
        tasks = new TaskList(storage);
        commandContext = new CommandContext(tasks, ui, parser);
    }

    /** Runs the interactive command loop until an exit command is received. */
    public void run() {
        ui.showWelcome();
        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;

        while (scanner.hasNextLine() && !isExit) {
            String input = scanner.nextLine().trim();
            ui.showDivider();
            try {
                Command command = parser.parse(input);
                command.execute(commandContext);
                isExit = command.isExit();
            } catch (GoobleException e) {
                System.out.println(e.getMessage());
            } finally {
                ui.showDivider();
            }
        }
    }

    /** Executes one command and sends its output to the supplied consumer. */
    public boolean executeCommand(String input, Consumer<String> output) {
        Command command = parser.parse(input.trim());
        Ui commandUi = new Ui(output);
        CommandContext context = new CommandContext(tasks, commandUi, parser);
        try {
            command.execute(context);
        } catch (GoobleException e) {
            commandUi.showMessage(e.getMessage());
        }
        return command.isExit();
    }

    /** Starts Gooble with its default storage file. */
    public static void main(String[] args) {
        new Gooble("data/Gooble.txt").run();
    }
}
