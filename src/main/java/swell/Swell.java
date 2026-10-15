package swell;

import java.util.Scanner;

import swell.exception.SwellException;
import swell.parser.Parser;
import swell.storage.Storage;
import swell.task.Task;
import swell.task.TaskList;
import swell.ui.Ui;

/**
 * Starts the Swell chatbot.
 */
public class Swell {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String FIND_TAG_COMMAND = "findtag";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String EMPTY_COMMAND_ERROR =
            "I need a command before we can move. Try: todo read book";
    private static final String UNKNOWN_COMMAND_ERROR =
            "That signal is not on my chart yet. Try todo, deadline, event, list, find, findtag, "
                    + "mark, unmark, delete, or bye.";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit;

    /**
     * Creates a Swell chatbot and loads saved tasks.
     */
    public Swell() {
        this(new Storage());
    }

    /**
     * Creates a Swell chatbot using the given storage component.
     *
     * @param storage storage component used to load and save tasks.
     */
    Swell(Storage storage) {
        ui = new Ui();
        this.storage = storage;
        tasks = loadTasks(storage, ui);
        isExit = false;
    }

    /**
     * Runs the chatbot until the user enters the bye command.
     *
     * @param args command line arguments supplied to the program.
     */
    public static void main(String[] args) {
        Swell swell = new Swell();

        try (Scanner scanner = new Scanner(System.in)) {
            swell.ui.printGreeting();

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                if (command.equals(BYE_COMMAND)) {
                    break;
                }

                String response = swell.getResponse(command);
                swell.ui.printMessage(response);
            }

            swell.ui.printGoodbye();
        }
    }

    /**
     * Returns the greeting message for GUI clients.
     *
     * @return greeting message.
     */
    public String getGreeting() {
        return ui.getGreeting();
    }

    /**
     * Returns Swell's response to one user command.
     *
     * @param command user command to process.
     * @return response message.
     */
    public String getResponse(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals(BYE_COMMAND)) {
            isExit = true;
            return ui.getGoodbye();
        }

        try {
            return processCommand(trimmedCommand);
        } catch (SwellException e) {
            return ui.getErrorText(e.getMessage());
        }
    }

    /**
     * Returns whether the user has entered the bye command.
     *
     * @return true if Swell should stop accepting input.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Loads saved tasks without stopping Swell if the data file cannot be read.
     *
     * @param storage storage component used to load saved tasks.
     * @param ui user interface component used to show load errors.
     * @return loaded task list, or an empty task list if loading fails.
     */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return storage.loadTasks();
        } catch (SwellException e) {
            ui.printError(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Processes one user command.
     *
     * @param command user command to process.
     * @return response text for the command.
     * @throws SwellException if the command is invalid.
     */
    private String processCommand(String command) throws SwellException {
        if (command.isEmpty()) {
            throw new SwellException(EMPTY_COMMAND_ERROR);
        }

        String commandWord = Parser.getCommandWord(command);

        switch (commandWord) {
            case LIST_COMMAND:
                return ui.getTasksText(tasks);
            case FIND_COMMAND:
                return ui.getMatchingTasksText(tasks.findTasks(Parser.getFindKeyword(command)));
            case FIND_TAG_COMMAND:
                return ui.getMatchingTasksText(tasks.findTasksByTag(Parser.getTagKeyword(command)));
            case TODO_COMMAND:
            case DEADLINE_COMMAND:
            case EVENT_COMMAND:
                return addTask(command);
            case MARK_COMMAND:
                return markTask(command);
            case UNMARK_COMMAND:
                return unmarkTask(command);
            case DELETE_COMMAND:
                return deleteTask(command);
            default:
                throw new SwellException(UNKNOWN_COMMAND_ERROR);
        }
    }

    /**
     * Adds a todo, deadline, or event based on the user command.
     *
     * @param command user command containing the task details.
     * @return task-added confirmation text.
     * @throws SwellException if the command does not contain a valid task.
     */
    private String addTask(String command) throws SwellException {
        Task task = Parser.getTask(command);

        tasks.add(task);
        storage.saveTasks(tasks);
        return ui.getTaskAddedText(task, tasks.size());
    }

    private String markTask(String command) throws SwellException {
        Task markedTask = tasks.markTask(Parser.getTaskNumber(command, MARK_COMMAND));
        storage.saveTasks(tasks);
        return ui.getTaskMarkedText(markedTask);
    }

    private String unmarkTask(String command) throws SwellException {
        Task unmarkedTask = tasks.unmarkTask(Parser.getTaskNumber(command, UNMARK_COMMAND));
        storage.saveTasks(tasks);
        return ui.getTaskUnmarkedText(unmarkedTask);
    }

    private String deleteTask(String command) throws SwellException {
        Task deletedTask = tasks.deleteTask(Parser.getTaskNumber(command, DELETE_COMMAND));
        storage.saveTasks(tasks);
        return ui.getTaskDeletedText(deletedTask, tasks.size());
    }
}
