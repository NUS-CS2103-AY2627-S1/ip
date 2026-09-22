package steph;

import java.io.IOException;
import java.util.List;

import steph.task.Task;

/**
 * Entry point and top-level coordinator. A Steph owns the three collaborators
 * it needs -- a {@link Ui} for talking to the user, a {@link Storage} for the
 * save file, and a {@link TaskList} for the tasks -- and turns one line of
 * input into one reply.
 *
 * <p>Two front ends share that logic. {@link #run()} is the console
 * read-parse-act loop; {@link #getResponse(String)} does the same work for the
 * JavaFX GUI but hands the reply back as a string instead of printing it.
 * Parsing lives in {@link Parser}, so this class only orchestrates.
 */
public class Steph {

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /** The command handled by the most recent {@link #getResponse} call, or null if it failed. */
    private Command lastCommand;

    /** Whether the most recent {@link #getResponse} call failed (a caught exception, not "bye"). */
    private boolean lastResponseIsError;

    /** Set once the user asks to end the session with "bye". */
    private boolean isExit;

    /**
     * Sets up the collaborators and loads any previously saved tasks.
     *
     * @param filePath Where the task list is read from and written to.
     */
    public Steph(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            loaded = new TaskList();
        }
        tasks = loaded;
    }

    /**
     * Runs the console command loop until the user types "bye" or the input
     * ends: read a line, turn it into a reply with {@link #getResponse}, and
     * print that reply framed by the UI's rules.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.equals("bye")) {
                break;
            }
            ui.showMessage(getResponse(command));
        }

        ui.showGoodbye();
        ui.close();
    }

    /**
     * Turns one line of input into Steph's reply. Parses the line, acts on it
     * (updating the task list and save file for any command that changes
     * them), and returns the text to show the user. A malformed command or a
     * failed save is reported by returning its message rather than throwing.
     *
     * @param fullCommand The whole line as typed.
     * @return The reply text for that command.
     */
    public String getResponse(String fullCommand) {
        String command = fullCommand.trim();
        if (command.equals("bye")) {
            isExit = true;
            lastCommand = null;
            lastResponseIsError = false;
            return "Goodbye. Hope to see you again soon!";
        }

        // Parser turns the line into a Command and its argument text, and
        // throws StephException on anything malformed; the catch below turns
        // that into the reply, so no handler has to report input errors itself.
        try {
            Command commandType = Parser.parseCommand(command);
            assert commandType != null : "Parser.parseCommand throws StephException instead of returning null";
            String argument = Parser.parseArguments(command);
            String response = switch (commandType) {
                case LIST -> handleList();
                case MARK -> handleMark(argument, true);
                case UNMARK -> handleMark(argument, false);
                case TODO -> addTask(Parser.parseToDo(argument));
                case DEADLINE -> addTask(Parser.parseDeadline(argument));
                case EVENT -> tasks.addEvent(Parser.parseEvent(argument));
                case DELETE -> handleDeleteTask(argument);
                case FIND -> handleFindTask(Parser.parseFind(argument));
                default -> throw new StephException("Uh oh... I dont understand that");
            };
            lastCommand = commandType;
            lastResponseIsError = false;

            // "list" and "find" only read the task list; every other command
            // changes it, so only those need the file rewritten.
            boolean isReadOnlyCommand = commandType == Command.LIST || commandType == Command.FIND;
            if (!isReadOnlyCommand) {
                storage.save(tasks.asList());
            }
            return response;

        } catch (StephException e) {
            lastCommand = null;
            lastResponseIsError = true;
            return e.getMessage();
        } catch (IOException e) {
            lastCommand = null;
            lastResponseIsError = true;
            return "Sorry, I couldn't save your tasks: " + e.getMessage();
        }
    }

    /**
     * Returns the name of the {@link Command} handled by the most recent
     * {@link #getResponse} call (e.g. {@code "TODO"}), or {@code ""} if that
     * call was a "bye" or failed. The GUI uses it to tint the reply bubble.
     *
     * @return The last command's name, or {@code ""}.
     */
    public String getCommandType() {
        return lastCommand == null ? "" : lastCommand.name();
    }

    /**
     * Returns whether the most recent {@link #getResponse} call failed (a
     * malformed command, an out-of-range index, a rejected event clash, or a
     * failed save) rather than "bye" or a successful command. The GUI uses it
     * to style the reply bubble as an error.
     *
     * @return True if the most recent call's reply is an error message.
     */
    public boolean isLastResponseError() {
        return lastResponseIsError;
    }

    /**
     * Returns whether the user has ended the session with "bye". The GUI polls
     * this after each reply to decide when to close the window.
     *
     * @return True once "bye" has been handled.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the greeting shown when a session starts.
     *
     * @return The welcome message.
     */
    public static String getWelcomeMessage() {
        return "Hello! I'm Steph.\nGlad to see you!\nWhat can I help you with?";
    }

    /**
     * Launches the console version of Steph, using {@code ./data/steph.txt} as
     * the save file. The GUI is launched from {@link Launcher} instead.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Steph("./data/steph.txt").run();
    }

    private String handleList() {
        return numberedList("Here are the tasks in your list:", tasks.asList());
    }

    private String handleMark(String argument, boolean markAsDone) throws StephException {
        String commandWord = markAsDone ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(argument, tasks.size(), commandWord);

        Task task = tasks.get(taskIndex);
        if (markAsDone) {
            task.complete();
            return "Awesome! I've marked this task as done:\n  " + task;
        }
        task.uncomplete();
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    private String handleDeleteTask(String argument) throws StephException {
        int taskIndex = Parser.parseTaskIndex(argument, tasks.size(), "delete");
        Task deletedTask = tasks.remove(taskIndex);
        return "Okay! I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleFindTask(String keyword) {
        List<Task> matched = tasks.findMatch(keyword);
        if (matched.isEmpty()) {
            return "I couldn't find any tasks matching \"" + keyword + "\".";
        }
        return numberedList("Here are the matching tasks in your list:", matched);
    }

    /**
     * Builds a display block with {@code header} on the first line, then every
     * task on its own line numbered from 1 -- the format both "list" and "find"
     * print.
     *
     * @param header The first line, describing what the list is.
     * @param items  The tasks to number, in the order given.
     * @return The assembled multi-line string.
     */
    private static String numberedList(String header, List<Task> items) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < items.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(items.get(i));
        }
        return message.toString();
    }

    private String addTask(Task newTask) {
        tasks.add(newTask);
        return "Got it. I've added this task:\n  " + newTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }
}
