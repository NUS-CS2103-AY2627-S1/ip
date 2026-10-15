package kpei;

import kpei.commands.user.*;
import kpei.datatypes.Task;
import kpei.datatypes.TaskList;
import kpei.exceptions.BertException;
import kpei.exceptions.InvalidIndexException;
import kpei.exceptions.UnknownCommandException;
import kpei.commands.parser.CommandParser;
import kpei.commands.parser.ParsedCommand;
import kpei.storage.Storage;
import kpei.ui.Cli;
import kpei.ui.controllers.MainWindowController;

import java.util.List;

/**
 * Core coordinator and Command handler for the BERT assistant.
 * Processes user input commands, mutates application state and storage,
 * and instructs the user interface on what to display.
 */
public class CommandCenter {

    private final Storage storage;
    private final TaskList taskList;
    private final Cli cli;
    private final MainWindowController mainWindowController;
    private final boolean isGuiEnabled;

    private TaskList displayList;

    private final TodoCommand todoCommand;
    private final DeadlineCommand deadlineCommand;
    private final EventCommand eventCommand;
    private final FindCommand findCommand;
    private final MarkCommand markCommand;
    private final UnmarkCommand unmarkCommand;
    private final RemoveCommand removeCommand;
    private final ListCommand listCommand;
    private final ExitCommand exitCommand;

    /**
     * Constructs a {@code CommandCenter} instance with the given storage, task list, and CLI interface.
     *
     * @param storage Storage instance used for task persistence.
     * @param taskList Task list holding the user tasks.
     * @param cli CLI interface used to display messages to the user.
     */
    public CommandCenter(Storage storage, TaskList taskList, Cli cli) {
        this(storage, taskList, cli, null, false);
    }

    /**
     * Constructs a {@code CommandCenter} instance with the given storage, task list, CLI interface,
     * and main window controller.
     *
     * @param storage Storage instance used for task persistence.
     * @param taskList Task list holding the user tasks.
     * @param cli CLI interface used to display messages to the user.
     * @param mainWindowController Controller for the main GUI window.
     */
    public CommandCenter(Storage storage, TaskList taskList, Cli cli, MainWindowController mainWindowController) {
        this(storage, taskList, cli, mainWindowController, true);
    }


    private CommandCenter(Storage storage, TaskList taskList, Cli cli, MainWindowController mainWindowController,
                          boolean isGuiEnabled) {
        this.storage = storage;
        this.taskList = taskList;
        this.cli = cli;
        this.mainWindowController = mainWindowController;
        this.isGuiEnabled = isGuiEnabled;

        todoCommand = new TodoCommand();
        deadlineCommand = new DeadlineCommand();
        eventCommand = new EventCommand();
        findCommand = new FindCommand();
        markCommand = new MarkCommand();
        unmarkCommand = new UnmarkCommand();
        removeCommand = new RemoveCommand();
        listCommand = new ListCommand();
        exitCommand = new ExitCommand();

        try {
            this.storage.load(taskList);
        } catch (BertException e) {
            this.cli.warning(e.getMessage());
        }

        this.cli.greeting();
        this.cli.horizontalLine();

        try {
            setDisplayList(this.taskList);
            refreshDisplayListGui();
        } catch (BertException e) {
            this.cli.error(e.getMessage());
        }
    }

    /**
     * Dispatches a parsed Command to the appropriate handler method.
     *
     * @param userPrompt The raw Command string entered by the user.
     * @return {@code true} if an exit Command was executed, {@code false} otherwise.
     */
    public boolean executeCommand(String userPrompt) {
        try {
            CommandParser commandParser = new CommandParser();

            cli.horizontalLine();

            List<String> tokens = commandParser.tokenize(userPrompt);
            ParsedCommand cmd = commandParser.parse(tokens);

            if (todoCommand.isMatch(cmd)) {
                Task task = todoCommand.parse(cmd);
                handleAdd(task);
            } else if (deadlineCommand.isMatch(cmd)) {
                Task task = deadlineCommand.parse(cmd);
                handleAdd(task);
            } else if (eventCommand.isMatch(cmd)) {
                Task task = eventCommand.parse(cmd);
                handleAdd(task);
            } else if (findCommand.isMatch(cmd)) {
                String keyword = findCommand.parse(cmd);
                handleFind(keyword);
            } else if (markCommand.isMatch(cmd)) {
                int index = markCommand.parse(cmd);
                handleMark(index);
            } else if (unmarkCommand.isMatch(cmd)) {
                int index = unmarkCommand.parse(cmd);
                handleUnmark(index);
            } else if (removeCommand.isMatch(cmd)) {
                int index = removeCommand.parse(cmd);
                handleDelete(index);
            } else if (listCommand.isMatch(cmd)) {
                if (listCommand.parse(cmd)) {
                    handleList();
                }
            } else if (exitCommand.isMatch(cmd)) {
                if (exitCommand.parse(cmd)) {
                    cli.farewell();
                    return true;
                }
            } else {
                throw new UnknownCommandException(cmd.getCommandType());
            }
        } catch (BertException e) {
            cli.error(e.getMessage());
        } finally {
            cli.horizontalLine();
        }
        return false;
    }

    private void handleAdd(Task task) throws BertException {
        taskList.add(task);
        saveList();
        cli.print("Added " + task.getType());
        cli.printTask(taskList.size(), task);
        setDisplayList(taskList);
        refreshDisplayListGui();
    }

    private void handleList() throws InvalidIndexException {
        if (taskList.isEmpty()) {
            cli.print("List is empty.");
            return;
        }
        cli.print(String.format("Displaying list of size %d.", taskList.size()));
        setDisplayList(taskList);
        printDisplayListCli();
        refreshDisplayListGui();
    }

    private void handleFind(String keyword) throws InvalidIndexException {
        TaskList matchingTasks = taskList.find(keyword);
        cli.print(String.format("Found %d matching tasks.", matchingTasks.size()));
        setDisplayList(matchingTasks);
        printDisplayListCli();
        refreshDisplayListGui();
    }

    private void handleMark(int index) throws BertException {
        Task task = displayList.get(index);
        if (task.isMarked()) {
            cli.print("Already marked " + task.getType());
            cli.printTask(index, task);
        } else {
            displayList.mark(index);
            cli.print("Marked " + task.getType());
            cli.printTask(index, task);
            refreshDisplayListGui();
            saveList();
        }
    }

    private void handleUnmark(int index) throws BertException {
        Task task = displayList.get(index);
        if (!task.isMarked()) {
            cli.print("Already unmarked " + task.getType());
            cli.printTask(index, task);
        } else {
            displayList.unmark(index);
            cli.print("Unmarked " + task.getType());
            cli.printTask(index, task);
            refreshDisplayListGui();
            saveList();
        }
    }

    private void handleDelete(int index) throws BertException {
        Task removedTask = displayList.remove(index);
        cli.print("Removed " + removedTask.getType());
        cli.printTask(index, removedTask);
        refreshDisplayListGui();
        saveList();
    }

    private void saveList() throws BertException {
        storage.save(taskList); // do NOT save displaylist
    }

    private void setDisplayList(TaskList taskList) {
        this.displayList = taskList;
    }

    private void printDisplayListCli() throws InvalidIndexException {
        if (!isGuiEnabled) {
            cli.printList(displayList);
        }
    }

    private void refreshDisplayListGui() throws InvalidIndexException {
        if (isGuiEnabled) {
            mainWindowController.refreshTaskList(displayList);
        }
    }
}
