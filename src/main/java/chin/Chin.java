package chin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.stream.IntStream;

import chin.task.Task;
import chin.util.ChinException;
import chin.util.Command;
import chin.util.Parser;
import chin.util.Storage;
import chin.util.TaskList;
import chin.util.Ui;

/**
 * Entry point of the Chin chatbot. Wires together {@link Ui}, {@link Storage},
 * and {@link TaskList}, then runs the command loop until the user exits.
 */
public class Chin {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a new Chin instance backed by the given data file.
     *
     * @param dataPath path to the persistent task file
     */
    public Chin(Path dataPath) {
        this.ui = new Ui();
        this.storage = new Storage(dataPath);
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Runs the interactive read-eval loop until the user issues {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String userInput = ui.readCommand();
            Command cmd = Command.fromInput(userInput);
            if (cmd == Command.BYE) {
                break;
            }
            ui.showLine();
            try {
                executeCommand(cmd, userInput);
                storage.save(tasks.asList());
            } catch (ChinException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
        ui.showGoodbye();
    }

    /**
     * Processes a single input line and returns the response as a string,
     * without printing to stdout. Used by the JavaFX GUI in {@link Main}.
     */
    public String getResponse(String userInput) {
        Command cmd = Command.fromInput(userInput);
        if (cmd == Command.BYE) {
            return "Bye. Hope to see you again soon!";
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buf));
        try {
            try {
                executeCommand(cmd, userInput);
                storage.save(tasks.asList());
            } catch (ChinException e) {
                System.out.println(" " + e.getMessage());
            }
        } finally {
            System.setOut(originalOut);
        }
        return buf.toString().stripTrailing();
    }

    private void executeCommand(Command cmd, String userInput) throws ChinException {
        switch (cmd) {
        case LIST:
            IntStream.range(0, tasks.size())
                    .forEach(i -> ui.show((i + 1) + "." + tasks.get(i)));
            return;
        case MARK: {
            int idx = Parser.parseIndex(userInput.substring(5), tasks.size());
            tasks.get(idx).mark();
            ui.show("Nice! I've marked this task as done:");
            ui.showIndented(tasks.get(idx).toString());
            return;
        }
        case UNMARK: {
            int idx = Parser.parseIndex(userInput.substring(7), tasks.size());
            tasks.get(idx).unmark();
            ui.show("OK, I've marked this task as not done yet:");
            ui.showIndented(tasks.get(idx).toString());
            return;
        }
        case DELETE: {
            int idx = Parser.parseIndex(userInput.substring(7), tasks.size());
            Task removed = tasks.remove(idx);
            ui.show("Noted. I've removed this task:");
            ui.showIndented(removed.toString());
            ui.show("Now you have " + tasks.size() + " tasks in the list.");
            return;
        }
        case SORT: {
            tasks.sortByDeadline();
            ui.show("Sorted tasks by deadline (earliest first, non-deadlines last).");
            for (int i = 0; i < tasks.size(); i++) {
                ui.show((i + 1) + "." + tasks.get(i));
            }
            return;
        }
        case FIND: {
            String keyword = userInput.length() > 4 ? userInput.substring(5).trim() : "";
            if (keyword.isEmpty()) {
                throw new ChinException("OOPS!!! Please give a keyword to find.");
            }
            java.util.List<Task> matches = tasks.find(keyword);
            if (matches.isEmpty()) {
                ui.show("No matching tasks found.");
            } else {
                ui.show("Here are the matching tasks in your list:");
                IntStream.range(0, matches.size())
                        .forEach(i -> ui.show((i + 1) + "." + matches.get(i)));
            }
            return;
        }
        case TODO:
            // Fallthrough
        case DEADLINE:
            // Fallthrough
        case EVENT: {
            Task task = Parser.parseNewTask(cmd, userInput);
            tasks.add(task);
            ui.show("Got it. I've added this task:");
            ui.showIndented(task.toString());
            ui.show("Now you have " + tasks.size() + " tasks in the list.");
            return;
        }
        default:
            throw new ChinException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Program entry point.
     */
    public static void main(String[] args) {
        new Chin(Path.of("data", "chin.txt")).run();
    }
}
