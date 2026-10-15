package pebby;

import java.io.IOException;
import java.util.List;

import pebby.command.AddCommand;
import pebby.command.Command;
import pebby.command.DeleteCommand;
import pebby.command.ExitCommand;
import pebby.command.ListCommand;
import pebby.command.ScheduleCommand;
import pebby.parser.CommandType;
import pebby.parser.ParsedCommand;
import pebby.parser.Parser;
import pebby.storage.Storage;
import pebby.task.Task;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * A command-line task chatbot that keeps tasks in a local data file.
 */
public class Pebby {
    private static TaskList tasks = new TaskList();
    private static final Storage storage = new Storage();

    /**
     * Creates Pebby and loads any tasks saved from an earlier session.
     */
    public Pebby() {
        loadTasks();
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input the message entered by the user
     * @return Pebby's response
     */
    public String getResponse(String input) {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);
        try {
            ParsedCommand parsedCommand = Parser.parse(input);
            if (parsedCommand.hasError()) {
                return "Invalid command: " + parsedCommand.getErrorMessage();
            }
            if (parsedCommand.getType() == CommandType.BYE) {
                return "Bye Bye!";
            }
            executeCommand(parsedCommand, ui);
        } catch (RuntimeException exception) {
            return "Sorry, Pebby could not process that command: " + safeMessage(exception);
        }
        return response.toString().trim();
    }

    /**
     * Marks the task at the specified zero-based index as complete and saves the change.
     */
    public static String markTask(int taskNo) {
        Task task = tasks.markAsDone(taskNo);
        String output = "Nice! I've marked this task as done: \n";
        return output + "  " + task.toString() + saveTasks();
    }

    /**
     * Marks the task at the specified zero-based index as incomplete and saves the change.
     */
    public static String unmarkTask(int taskNo) {
        Task task = tasks.markAsNotDone(taskNo);
        String output = "OK, I've marked this task as not done yet: \n";
        return output + "  " + task.toString() + saveTasks();
    }

    /**
     * Finds and formats tasks whose descriptions contain the supplied keyword.
     */
    public static String handleFind(String keyword) {
        if (keyword.isBlank()) {
            return "Invalid find: Please provide a keyword to search for.";
        }

        List<Task> matchingTasks = tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found.\n";
        }

        StringBuilder matches = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int index = 0; index < matchingTasks.size(); index++) {
            matches.append(index + 1).append(". ").append(matchingTasks.get(index)).append('\n');
        }
        return matches.toString();
    }

    /**
     * Returns a grouped reference for every command Pebby understands.
     *
     * @return the formatted command reference
     */
    private static String helpText() {
        return "Here is what I can do:\n\n"
                + "ADD TASKS\n"
                + "• todo <task description>\n"
                + "• deadline <task description> /by <yyyy-MM-dd or d MMMM yyyy>\n"
                + "• event <task description> /from <yyyy-MM-dd or d MMMM yyyy> "
                + "/to <yyyy-MM-dd or d MMMM yyyy>\n\n"
                + "MANAGE TASKS\n"
                + "• list\n"
                + "• mark <task number>\n"
                + "• unmark <task number>\n"
                + "• delete <task number>\n\n"
                + "FIND AND PLAN\n"
                + "• find <keyword>\n"
                + "• schedule <yyyy-MM-dd or d MMMM yyyy>\n\n"
                + "OTHER\n"
                + "• help\n"
                + "• bye";
    }

    /**
     * Saves the current list and converts an I/O failure into a helpful UI message.
     */
    private static String saveTasks() {
        try {
            storage.save(tasks.asList());
            return "";
        } catch (IOException | IllegalArgumentException exception) {
            return "\nWarning: your task was changed, but Pebby could not save it: "
                    + exception.getMessage();
        }
    }

    /**
     * Loads prior tasks without allowing a missing or damaged file to stop Pebby.
     */
    private static String loadTasks() {
        try {
            Storage.LoadResult result = storage.load();
            tasks = new TaskList(result.tasks);
            if (result.skippedRecords > 0) {
                return "Warning: ignored " + result.skippedRecords
                        + " invalid saved task record(s).";
            }
            return "";
        } catch (IOException exception) {
            return "Warning: Pebby could not load saved tasks: " + exception.getMessage();
        }
    }

    /**
     * Converts a valid one-based task number into a zero-based index.
     */
    private static int taskIndexFrom(String argument) {
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new IllegalArgumentException(
                        "Please choose a task number from 1 to " + tasks.size() + ".");
            }
            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "A validated task number must produce a valid zero-based index.";
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a whole task number.");
        }
    }

    /**
     * Executes one parsed command and sends its response to the supplied user interface.
     *
     * @param parsedCommand command to execute
     * @param ui user interface that receives the response
     */
    private static void executeCommand(ParsedCommand parsedCommand, Ui ui) {
        if (parsedCommand.hasError()) {
            ui.showLine("Invalid command: " + parsedCommand.getErrorMessage());
            return;
        }
        CommandType commandType = parsedCommand.getType();
        String argument = parsedCommand.getArgument();

        switch (commandType) {
            case LIST:
                new ListCommand().execute(tasks, ui, storage);
                break;
            case MARK:
                try {
                    ui.showLine(markTask(taskIndexFrom(argument)));
                } catch (IllegalArgumentException exception) {
                    ui.showLine("Invalid mark: " + exception.getMessage());
                }
                break;
            case UNMARK:
                try {
                    ui.showLine(unmarkTask(taskIndexFrom(argument)));
                } catch (IllegalArgumentException exception) {
                    ui.showLine("Invalid unmark: " + exception.getMessage());
                }
                break;
            case TODO:
            case DEADLINE:
            case EVENT:
                new AddCommand(commandType, argument).execute(tasks, ui, storage);
                break;
            case FIND:
                ui.show(handleFind(argument));
                break;
            case SCHEDULE:
                new ScheduleCommand(argument).execute(tasks, ui, storage);
                break;
            case DELETE:
                new DeleteCommand(argument).execute(tasks, ui, storage);
                break;
            case HELP:
                ui.showLine(helpText());
                break;
            case BYE:
            case UNKNOWN:
            default:
                ui.showLine("Hmmm... What does this mean? My pebble brain cant understand.");
                break;
        }
    }

    /** Returns a safe explanation for an unexpected command-processing failure. */
    private static String safeMessage(RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? "an unexpected error occurred." : exception.getMessage();
    }

    /**
     * Starts Pebby and processes commands until the user exits.
     *
     * @param args Command-line arguments, which Pebby does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        String loadMessage = loadTasks();
        ui.showWelcome();
        if (!loadMessage.isEmpty()) {
            ui.showLine(loadMessage);
            ui.showSeparator();
        }

        boolean isExit = false;
        while (ui.hasNextCommand() && !isExit) {
            String command = ui.readCommand();
            try {
                ParsedCommand parsedCommand = Parser.parse(command);
                if (parsedCommand.getType() == CommandType.BYE && !parsedCommand.hasError()) {
                    Command exitCommand = new ExitCommand();
                    exitCommand.execute(tasks, ui, storage);
                    isExit = exitCommand.isExit();
                } else {
                    ui.showSeparator();
                    executeCommand(parsedCommand, ui);
                    ui.showSeparator();
                }
            } catch (RuntimeException exception) {
                ui.showSeparator();
                ui.showLine("Sorry, Pebby could not process that command: " + safeMessage(exception));
                ui.showSeparator();
            }
        }
        ui.showGoodbye();
    }
}
