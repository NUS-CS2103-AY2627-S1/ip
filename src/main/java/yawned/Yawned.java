package yawned;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import yawned.alias.AliasManager;
import yawned.alias.AliasResult;
import yawned.exception.YawnedException;
import yawned.parser.AliasAction;
import yawned.parser.AliasCommand;
import yawned.parser.CommandType;
import yawned.parser.Parser;
import yawned.storage.AliasStorage;
import yawned.storage.Storage;
import yawned.task.Task;
import yawned.task.TaskList;
import yawned.ui.Ui;

/**
 * Coordinates the UI, command parser, task list, and storage for Yawned.
 */
public class Yawned {
    private static final String WELCOME_MESSAGE = "*yawn* Yawned is awake enough to help.\nWhat can I do for you?\n";

    private final Ui ui;
    private final Parser parser;
    private final AliasManager aliasManager;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates the chatbot and loads its saved tasks.
     *
     * @param saveFile Path of the task storage file.
     */
    public Yawned(Path saveFile) {
        ui = new Ui(new Scanner(System.in));
        parser = new Parser();
        storage = new Storage(saveFile);
        aliasManager = new AliasManager(new AliasStorage(saveFile.resolveSibling("aliases.txt")));
        tasks = new TaskList(storage.loadTasks());
    }

    /**
     * Processes a user command and returns the resulting response.
     *
     * @param input User command to process.
     * @return Response generated after processing the command.
     */
    public String getResponse(String input) {
        CommandType commandType = getCommandType(input);
        return switch (commandType) {
            case LIST -> taskListMessage();
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case DELETE -> deleteTaskMessage(input);
            case FIND -> findTaskMessage(input);
            case ALIAS -> aliasMessage(input);
            case TODO, DEADLINE, EVENT, UNKNOWN -> addTaskMessage(commandType, input);
        };
    }

    /**
     * Returns the resolved command type for a user input.
     *
     * @param input User input to resolve.
     * @return Resolved canonical command type.
     */
    public CommandType getCommandType(String input) {
        return aliasManager.resolveCommandType(input);
    }

    /** Starts the interactive chatbot session. */
    public void run() {
        ui.showWelcome();
        String response = WELCOME_MESSAGE;
        while (true) {
            ui.showMessage(response);
            ui.showBreakLine();
            if (!ui.hasNextCommand()) {
                return;
            }
            response = getResponse(ui.readCommand());
            ui.showBreakLine();
        }
    }

    /**
     * Creates and saves the task described by a task-creation command.
     *
     * @param commandType Type of task to create.
     * @param command Complete user command.
     * @return Confirmation message or validation error.
     */
    private String addTaskMessage(CommandType commandType, String command) {
        try {
            Task task = parser.parseTask(commandType, command);
            addTask(task);
            return addedTaskMessage(task, tasks.size());
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Adds a task and saves the changed task list.
     *
     * @param task New task to add.
     */
    private void addTask(Task task) {
        tasks.addTask(task);
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Removes a task and saves the changed task list.
     *
     * @param taskNumber One-based number of the task to remove.
     * @return Removed task.
     */
    private Task deleteTask(int taskNumber) {
        Task deletedTask = tasks.deleteTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return deletedTask;
    }

    /**
     * Formats the confirmation shown after a task is successfully added.
     *
     * @param task Added task.
     * @param taskCounter Updated number of tasks.
     * @return Confirmation message.
     */
    private static String addedTaskMessage(Task task, int taskCounter) {
        return "Noted. I've tucked this into your task list:\n  " + task
                + "\nYou now have " + taskCounter + " task(s).";
    }

    /**
     * Formats the confirmation shown after a task is deleted.
     *
     * @param task Deleted task.
     * @param taskCounter Updated number of tasks.
     * @return Confirmation message.
     */
    private static String deletedTaskMessage(Task task, int taskCounter) {
        return "One less thing to carry around. Removed:\n  " + task
                + "\n" + taskCounter + " task(s) remain.";
    }

    /**
     * Formats the complete current task list.
     *
     * @return Task-list message.
     */
    private String taskListMessage() {
        if (tasks.isEmpty()) {
            return "Nothing on the list. A rare moment of peace.";
        }
        return taskListMessage("Here's what's keeping you busy:", tasks.getTasks());
    }

    /**
     * Formats a labeled list of tasks.
     *
     * @param heading Heading to display before the tasks.
     * @param taskItems Tasks to display.
     * @return Formatted task-list message.
     */
    private static String taskListMessage(String heading, List<Task> taskItems) {
        StringBuilder message = new StringBuilder(heading);
        for (int index = 0; index < taskItems.size(); index++) {
            message.append('\n')
                    .append(index + 1)
                    .append('.')
                    .append(taskItems.get(index));
        }
        return message.toString();
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command and formats the result.
     *
     * @param command User command.
     * @return Deletion confirmation or validation message.
     */
    private String deleteTaskMessage(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.DELETE, command);
            if (!isValidTaskNumber(taskNumber)) {
                return "I can't find a task with that number. Try one from the list.";
            }
            Task deletedTask = deleteTask(taskNumber);
            return deletedTaskMessage(deletedTask, tasks.size());
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param command User command.
     * @return Result message for the user.
     */
    private String markTask(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.MARK, command);
            if (!isValidTaskNumber(taskNumber)) {
                return "I can't find a task with that number. Try one from the list.";
            }
            Task task = tasks.markTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return "Finally, I can sleep in peace without this task bothering me... marked complete: " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     *
     * @param command User command.
     * @return Result message for the user.
     */
    private String unmarkTask(String command) {
        try {
            int taskNumber = parser.parseTaskNumber(CommandType.UNMARK, command);
            if (!isValidTaskNumber(taskNumber)) {
                return "I can't find a task with that number. Try one from the list.";
            }
            Task task = tasks.unmarkTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return "Back on the radar. I've marked this incomplete:\n  " + task;
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Returns whether a task number identifies a task in the current list.
     *
     * @param taskNumber One-based task number.
     * @return Whether the task number is in the current list's valid range.
     */
    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Finds tasks selected by a {@code find <keyword>} command and formats the results.
     *
     * @param command user command
     * @return Matching-task list or validation message.
     */
    private String findTaskMessage(String command) {
        try {
            List<Task> matchingTasks = tasks.findTasks(parser.parseFindKeyword(command));
            if (matchingTasks.isEmpty()) {
                return "I couldn't find anything matching that. It may be hiding under a blanket.";
            }
            return taskListMessage("I found these before my attention drifted:", matchingTasks);
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Creates or removes a custom command alias.
     *
     * @param command Complete alias command.
     * @return Confirmation or validation message.
     */
    private String aliasMessage(String command) {
        try {
            AliasCommand aliasCommand = parser.parseAliasCommand(command);
            if (aliasCommand.action() == AliasAction.LIST) {
                return aliasListMessage(aliasManager.getAliases());
            }
            String aliasName = AliasManager.normalize(aliasCommand.aliasName());
            AliasResult result = aliasCommand.action() == AliasAction.REMOVE
                    ? aliasManager.removeAlias(aliasName)
                    : aliasManager.defineAlias(aliasName, aliasCommand.targetCommand());
            return aliasResultMessage(aliasCommand, aliasName, result);
        } catch (YawnedException exception) {
            return exception.getMessage();
        }
    }

    /** Returns the user-facing message for an alias operation result. */
    private static String aliasResultMessage(AliasCommand aliasCommand, String aliasName, AliasResult result) {
        return switch (result) {
            case SUCCESS -> aliasCommand.action() == AliasAction.REMOVE
                    ? "All set. Alias '" + aliasName + "' has been removed. One less thing to remember - what a relief."
                    : "All set. Alias '" + aliasName + "' now runs '" + aliasCommand.targetCommand()
                            + "'. *yawn* Remembering another alias is tiring.";
            case INVALID_NAME -> "I need an alias name made of letters only.";
            case RESERVED_NAME -> "That alias name is already reserved, even I can't nap through that rule.";
            case INVALID_TARGET -> "Aliases can only run a standard task command.";
            case NOT_FOUND -> "I couldn't find an alias named '" + aliasName + "'.";
            case SAVE_FAILED -> "I couldn't save that alias. Please try again.";
        };
    }

    /**
     * Formats every built-in shortcut and custom alias.
     *
     * @param customAliases Custom aliases in creation order.
     * @return Formatted shortcut list.
     */
    private static String aliasListMessage(Map<String, CommandType> customAliases) {
        StringBuilder message = new StringBuilder("Here are the shortcuts I know:\n\nBuilt-in:");
        for (CommandType commandType : CommandType.values()) {
            for (String shortcut : commandType.getAliases()) {
                message.append("\n  ").append(shortcut).append(" -> ").append(commandType.getWord());
            }
        }
        if (customAliases.isEmpty()) {
            return message.append("\n\nNo custom aliases yet. Add one with: alias <name> <command>").toString();
        }
        message.append("\n\nYour aliases:");
        for (Map.Entry<String, CommandType> alias : customAliases.entrySet()) {
            message.append("\n  ").append(alias.getKey()).append(" -> ").append(alias.getValue().getWord());
        }
        return message.toString();
    }

    /** Starts Yawned using its standard storage path. */
    public static void main(String[] args) {
        new Yawned(ApplicationPaths.taskFile()).run();
    }
}
