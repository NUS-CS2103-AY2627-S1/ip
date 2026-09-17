package quu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import quu.exception.InvalidFileContents;
import quu.exception.QuuException;
import quu.exception.UnexpectedArgumentException;
import quu.exception.UnknownCommandException;
import quu.parser.Parser;
import quu.storage.Storage;
import quu.task.Task;
import quu.task.TaskList;
import quu.ui.Ui;

/**
 * The Quu chatbot.
 *
 * <p>A {@code Quu} owns the task list for one session, together with the {@link Parser},
 * {@link Storage} and {@link Ui} it needs to act on that list. Commands arrive one at a
 * time through {@link #getResponse(String)}, which returns the reply as text instead of
 * printing it. This keeps the chatbot independent of its terminal and JavaFX front ends.
 */
public class Quu {
    private static final String NAME = "Quu";
    private static final String TASK_FILE = "./data/Quu.txt";
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";

    /**
     * The commands the chatbot understands, shown to the user when they type something
     * else. Kept next to {@link #executeCommand(String[])} so that the two are read and
     * edited together.
     */
    private static final String KNOWN_COMMANDS =
            "todo, deadline, event, list, mark, unmark, edit, delete, find, bye";

    private final Ui ui = new Ui();
    private final Parser parser = new Parser();
    private final Storage storage;
    private final TaskList taskList;
    private final String loadMessage;
    private CommandType commandType = CommandType.NONE;

    /**
     * Creates a chatbot whose task list is loaded from the default save file.
     */
    public Quu() {
        this(TASK_FILE);
    }

    /**
     * Creates a chatbot whose task list is loaded from the named save file.
     *
     * <p>A missing or corrupted save file is not fatal. The session starts with an empty
     * list, and the reason is retained so that the front end can show it at startup.
     *
     * @param filePath path of the save file, which need not exist yet
     */
    public Quu(String filePath) {
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String message;
        try {
            loadedTasks = storage.readFile();
            message = "";
        } catch (FileNotFoundException e) {
            // Not an error: the file is created on the first save.
            loadedTasks = new TaskList();
            message = ui.getLoadingError("No save file yet. One will be created at " + filePath + ".");
        } catch (InvalidFileContents e) {
            loadedTasks = new TaskList();
            message = ui.getException(e) + System.lineSeparator() + setAsideDamagedFile();
        } catch (QuuException e) {
            // A save file the user has to fix themselves. The session goes on with an
            // empty list rather than refusing to start.
            loadedTasks = new TaskList();
            message = ui.getException(e);
        }
        taskList = loadedTasks;
        loadMessage = message;
    }

    /**
     * Moves the damaged save file aside and returns what to tell the user about it.
     *
     * <p>Called only when the file could not be read. Doing this during construction, rather
     * than when the first save happens, means there is no window in which a command could
     * overwrite the file.
     *
     * @return the explanation to append to the load message
     */
    private String setAsideDamagedFile() {
        try {
            return ui.getLoadingError("The damaged file has been moved to "
                    + storage.setAsideDamagedFile() + ", so nothing in it is lost.");
        } catch (IOException e) {
            return ui.getSaveError("The damaged file could not be moved aside: " + e.getMessage()
                    + ". Copy it somewhere safe before adding any tasks, or it will be overwritten.");
        }
    }

    /**
     * Returns the reply to one line of user input.
     *
     * <p>Failures are returned as ordinary replies so that one bad command does not end
     * the session. The task list is saved after every command that succeeds.
     *
     * @param input one line of user input, as typed
     * @return the text to show the user
     */
    public String getResponse(String input) {
        String[] parts = splitCommand(input);
        assert parts.length > 0 : "splitting with a positive limit always yields at least the command word";

        if (parts[0].isEmpty()) {
            commandType = CommandType.ERROR;
            return ui.getEmptyInput();
        }
        if (isExitCommand(input)) {
            commandType = CommandType.NONE;
            return ui.getGoodbye();
        }
        try {
            String response = executeCommand(parts);
            try {
                storage.writeFile(taskList);
            } catch (IOException e) {
                commandType = CommandType.ERROR;
                return response + System.lineSeparator()
                        + ui.getSaveError(String.format("Unable to write to file, %s", e.getMessage()));
            }
            return response;
        } catch (QuuException e) {
            commandType = CommandType.ERROR;
            return ui.getException(e);
        }
    }

    /**
     * Returns the category of the command handled by the last call to {@link #getResponse(String)}.
     *
     * @return the command category, or {@link CommandType#NONE} before a command is handled
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Returns whether a line of input asks to end the session.
     *
     * @param input one line of user input, as typed
     * @return true if the input is the exit command
     */
    public boolean isExitCommand(String input) {
        String[] parts = splitCommand(input);
        return parts.length == 1 && parts[0].equals(EXIT_COMMAND);
    }

    /**
     * Splits one line of input into its command word and the rest of its arguments.
     *
     * <p>The command word is lowercased and surrounding whitespace is dropped, so that
     * {@code TODO read book} and {@code "  list"} are understood as readily as the exact
     * forms. Only the command word is normalised: a task description is kept as typed.
     *
     * @param input one line of user input, as typed
     * @return the command word, followed by the arguments if there are any
     */
    private static String[] splitCommand(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        parts[0] = parts[0].toLowerCase();
        return parts;
    }

    /**
     * Returns the program's ASCII-art logo.
     *
     * @return the logo
     */
    public String getBanner() {
        return ui.getBanner();
    }

    /**
     * Returns the opening greeting.
     *
     * @return the greeting
     */
    public String getGreeting() {
        return ui.getGreeting(NAME);
    }

    /**
     * Returns the problem encountered while loading the saved tasks, if any.
     *
     * @return the explanation, or an empty string if the tasks loaded cleanly
     */
    public String getLoadMessage() {
        return loadMessage;
    }

    /**
     * Runs the chatbot in a terminal until input ends or the user exits.
     *
     * @param args ignored; the chatbot takes no command-line arguments
     */
    public static void main(String[] args) {
        Quu quu = new Quu();
        System.out.println(quu.getBanner());
        System.out.println(quu.getGreeting());
        if (!quu.getLoadMessage().isEmpty()) {
            System.out.println(quu.getLoadMessage());
        }
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(quu.getResponse(input));
            if (quu.isExitCommand(input)) {
                return;
            }
        }
    }

    /**
     * Carries out a single command and returns its reply.
     *
     * <p>This method decides only <em>which</em> command was typed. What each command does
     * lives in its own handler below, so that this switch stays a readable summary of the
     * commands Quu understands.
     *
     * @param parts the user input split into command and arguments
     * @return the text describing what the command did
     * @throws QuuException if the command is unknown or its arguments are unusable
     */
    private String executeCommand(String[] parts) throws QuuException {
        return switch (parts[0]) {
            case LIST_COMMAND -> handleList(parts);
            // Reached only with arguments: a bare bye has already ended the session.
            case EXIT_COMMAND -> throw new UnexpectedArgumentException(EXIT_COMMAND);
            case "mark" -> handleMark(parts);
            case "unmark" -> handleUnmark(parts);
            case "todo" -> handleAdd(parser.parseToDo(parts));
            case "deadline" -> handleAdd(parser.parseDeadline(parts));
            case "event" -> handleAdd(parser.parseEvent(parts));
            case "delete" -> handleDelete(parts);
            case "edit" -> handleEdit(parts);
            case "find" -> handleFind(parts);
            default -> throw new UnknownCommandException(parts[0], KNOWN_COMMANDS);
        };
    }

    /**
     * Shows every task currently in the list.
     *
     * @param parts the user input split into command and arguments
     * @return the numbered task list
     * @throws UnexpectedArgumentException if anything was typed after the command
     */
    private String handleList(String[] parts) throws UnexpectedArgumentException {
        if (parts.length > 1) {
            commandType = CommandType.ERROR;
            throw new UnexpectedArgumentException(LIST_COMMAND);
        }
        commandType = CommandType.LIST;
        return ui.getList(taskList);
    }

    /**
     * Marks the task the user named as done.
     *
     * @param parts the user input split into command and arguments
     * @return the confirmation that the task was marked
     * @throws QuuException if the task number is missing, not a number, or out of range
     */
    private String handleMark(String[] parts) throws QuuException {
        commandType = CommandType.MARK;
        Task task = taskList.markTask(parser.parseTaskNumber(parts));
        return ui.getMarked(task);
    }

    /**
     * Marks the task the user named as not done.
     *
     * @param parts the user input split into command and arguments
     * @return the confirmation that the task was unmarked
     * @throws QuuException if the task number is missing, not a number, or out of range
     */
    private String handleUnmark(String[] parts) throws QuuException {
        commandType = CommandType.UNMARK;
        Task task = taskList.unmarkTask(parser.parseTaskNumber(parts));
        return ui.getUnmarked(task);
    }

    /**
     * Adds an already-parsed task to the list and reports it.
     *
     * <p>Shared by the todo, deadline and event commands, which differ only in how the
     * task is built and not in what happens to it once it exists. The caller does the
     * parsing, so this method never has to ask which kind of task it was given.
     *
     * @param task the task to add
     * @return the confirmation that the task was added
     */
    private String handleAdd(Task task) {
        commandType = CommandType.ADD;
        taskList.addTask(task);
        return ui.getAdded(task, taskList.getSize());
    }

    /**
     * Removes the task the user named.
     *
     * @param parts the user input split into command and arguments
     * @return the confirmation that the task was removed
     * @throws QuuException if the task number is missing, not a number, or out of range
     */
    private String handleDelete(String[] parts) throws QuuException {
        commandType = CommandType.DELETE;
        Task task = taskList.removeTask(parser.parseTaskNumber(parts));
        return ui.getRemoved(task, taskList.getSize());
    }

    /**
     * Changes the details of the task the user named, leaving its position alone.
     *
     * <p>The arguments carry two things, so the task number comes off first and whatever
     * follows is read as edits. Which details may be edited depends on the kind of task, a
     * question this method never has to ask: the task itself rejects an edit it cannot use.
     *
     * @param parts the user input split into command and arguments
     * @return the confirmation showing the task as it now stands
     * @throws QuuException if the task number or the edits are missing or unusable
     */
    private String handleEdit(String[] parts) throws QuuException {
        commandType = CommandType.UPDATE;
        String[] numberAndEdits = parser.parseEditArguments(parts);
        int taskNumber = parser.parseTaskNumber(numberAndEdits[0]);
        Task editedTask = taskList.editTask(taskNumber, parser.parseEdits(numberAndEdits[1]));
        return ui.getEdited(editedTask);
    }

    /**
     * Lists the tasks whose description contains the keyword the user gave.
     *
     * @param parts the user input split into command and arguments
     * @return the numbered list of matching tasks
     * @throws QuuException if the keyword is missing or blank
     */
    private String handleFind(String[] parts) throws QuuException {
        commandType = CommandType.FIND;
        return ui.getFound(taskList.buildFoundList(parser.parseKeyword(parts)));
    }
}
