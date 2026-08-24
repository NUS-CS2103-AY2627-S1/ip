package ultron;

import ultron.command.Command;
import ultron.exception.UltronException;
import ultron.model.Deadline;
import ultron.model.Event;
import ultron.model.TaskList;
import ultron.model.Todo;
import ultron.parser.Parser;
import ultron.storage.Storage;
import ultron.ui.Ui;

/**
 * A command-line task list that can add, list, mark, and unmark tasks.
 */
public class Ultron {

    /**
     * Runs the chatbot command loop.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {

        Ui ui = new Ui();
        ui.showWelcome();
        Parser parser = new Parser();

        Storage storage = new Storage(System.getProperty("ultron.saveFile", "data/ultron.txt"));
        TaskList tasks = new TaskList(storage.loadTasks());
        while (true) {
            String input = ui.readCommand();

            try {
                Command commandObject = parser.parseCommandObject(input);
                if (commandObject != null) {
                    commandObject.execute(tasks, ui, storage);
                    if (commandObject.isExit()) {
                        ui.close();
                        return;
                    }
                    continue;
                }

                Parser.CommandType command = parser.parseCommand(input);
                switch (command) {
                case TODO:
                    String description = parser.getArgument(input, command);
                    if (description.isEmpty()) {
                        throw new UltronException("You FOOL! The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(description));
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(tasks.get(tasks.size() - 1));
                    ui.showTaskCount(tasks.size());
                    ui.showSeparator();
                    break;
                case DEADLINE:
                    String input1 = parser.getArgument(input, command);
                    tasks.add(new Deadline(input1));
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(tasks.get(tasks.size() - 1));
                    ui.showTaskCount(tasks.size());
                    ui.showSeparator();
                    break;
                case EVENT:
                    String eventDetails = parser.getArgument(input, command);
                    tasks.add(new Event(eventDetails));
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(tasks.get(tasks.size() - 1));
                    ui.showTaskCount(tasks.size());
                    ui.showSeparator();
                    break;
                case UNKNOWN:
                    throw new UltronException("INVALID INPUT");
                case BYE:
                case LIST:
                case MARK:
                case UNMARK:
                case DELETE:
                case FIND:
                    throw new UltronException("Invalid command");
                }
            } catch (UltronException e) {
                ui.showError(e.getMessage());
            }
        }
    }

}
