package loki;

import java.io.IOException;

import loki.exception.LokiExceptions;
import loki.model.Task;
import loki.model.TaskList;
import loki.parser.Parser;
import loki.parser.TaskUpdate;
import loki.storage.Storage;
import loki.ui.Ui;

/**
 * Coordinates user interaction, command parsing, task operations, and storage.
 */
public class Loki {
    /** Creates an application coordinator. */
    public Loki() {
    }

    /**
     * Starts the Loki task manager and processes commands until the session ends.
     *
     * @param args command-line arguments, which are not used.
     * @throws IOException if a welcome asset cannot be read.
     */
    public static void main(String[] args) throws IOException {
        Ui ui = new Ui();
        Storage storage = new Storage();
        Parser parser = new Parser();
        TaskList tasks = new TaskList();

        try {
            tasks.addAll(storage.load());
        } catch (LokiExceptions exception) {
            ui.showError(exception.getMessage());
            ui.close();
            return;
        }

        try {
            ui.showWelcome();
            boolean activeSession = true;
            while (activeSession && ui.hasNextLine()) {
                String input = ui.readLine();
                try {
                    String keyword = parser.parseKeyword(input);
                    switch (keyword) {
                        case "faretheewell":
                            // Fallthrough
                        case "exit":
                            storage.save(tasks);
                            activeSession = false;
                            ui.showFarewell();
                            break;
                        case "list":
                            if (tasks.isEmpty()) {
                                ui.echo("You lack any tasks");
                            } else {
                                ui.showTaskList(tasks);
                            }
                            break;
                        case "help":
                            if (!input.trim().equalsIgnoreCase("help")) {
                                throw LokiExceptions.unknownCommand();
                            }
                            ui.showHelp();
                            break;
                        case "mark":
                            ui.showSuccess(tasks.mark(parser.parseTaskNumber(input)).toString());
                            break;
                        case "unmark":
                            ui.showSuccess(tasks.unmark(parser.parseTaskNumber(input)).toString());
                            break;
                        case "todo":
                            // Fallthrough
                        case "deadline":
                            // Fallthrough
                        case "event":
                            Task task = parser.parseTask(input);
                            tasks.add(task);
                            ui.showTaskAdded(task, tasks.size());
                            break;
                        case "delete":
                            Task deletedTask = tasks.delete(parser.parseTaskNumber(input));
                            ui.showTaskDeleted(deletedTask, tasks.size());
                            break;
                        case "update":
                            TaskUpdate update = parser.parseUpdate(input);
                            Task updatedTask;
                            try {
                                updatedTask = tasks.update(update);
                            } catch (LokiExceptions | IllegalArgumentException exception) {
                                throw LokiExceptions.invalidUpdate();
                            }
                            ui.echo("Updated: " + updatedTask);
                            break;
                        default:
                            throw LokiExceptions.unknownCommand();
                    }
                } catch (LokiExceptions | IllegalArgumentException exception) {
                    ui.showError(exception.getMessage());
                }
            }
        } finally {
            ui.close();
        }
    }
}
