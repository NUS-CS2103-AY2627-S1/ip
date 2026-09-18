package davidgoggins;

import java.util.Arrays;
import java.util.List;

import davidgoggins.parser.Parser;
import davidgoggins.storage.Storage;
import davidgoggins.task.Task;
import davidgoggins.task.TaskList;
import davidgoggins.ui.Ui;

/**
 * Entry point for the David Goggins chatbot.
 *
 * <p>Reads commands until the user types {@code bye}: {@code todo}, {@code deadline},
 * {@code event}, {@code list}, {@code mark <n>} and {@code unmark <n>}. {@link Ui} owns
 * the screen, {@link TaskList} the tasks and {@link Storage} the disk between runs.
 */
public class DavidGoggins {

    /** Where the task list is kept between runs, relative to the folder we run in. */
    private static final String FILE_PATH = "data/tasks.txt";

    /** The command that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that shows how to use every other command. */
    private static final String HELP_COMMAND = "help";

    /** The command that shows every task. */
    private static final String LIST_COMMAND = "list";

    /**
     * What the user is told when a bug, rather than their input, stops a command.
     *
     * <p>Kept vague on purpose: the details go to the console for whoever fixes it, and
     * would only confuse the user, who can do nothing about them.
     */
    private static final String UNEXPECTED_ERROR_MESSAGE = "Something broke on my end. That one's on me,"
            + " not you. Try again, and if it keeps happening, restart me.";

    /**
     * The help page, one entry per line, without the leading space the text UI adds.
     *
     * <p>Kept beside {@link #handleCommand} so that adding a command and documenting it
     * happen in the same file. The examples come from {@link Parser}, so the help page
     * and the error messages always suggest the same syntax.
     */
    private static final String[] HELP_LINES = {
        "Here are the commands I understand. Learn them, then use them:",
        "todo <description>",
        "Example: " + Parser.TODO_EXAMPLE,
        "deadline <description> /by <yyyy-mm-dd>",
        "Example: " + Parser.DEADLINE_EXAMPLE,
        "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>",
        "Example: " + Parser.EVENT_EXAMPLE,
        "list",
        "find <keyword>",
        "mark <task number>",
        "unmark <task number>",
        "delete <task number>",
        "help",
        "bye",
    };

    /** Handles all reading from and writing to the console. */
    private final Ui ui;

    /** Reads the saved list at start-up and writes it back whenever it changes. */
    private final Storage storage;

    /** The tasks the user has added so far. */
    private final TaskList tasks;

    /** Whether the reply last returned by {@link #getResponse} reported an error. */
    private boolean isLastResponseError;

    /** Whether the reply last returned by {@link #getResponse} was a warning, e.g. a task already marked. */
    private boolean isLastResponseWarning;

    /** Warnings raised while loading the save file, one per line, or empty if there were none. */
    private final String loadWarnings;

    /**
     * Builds a chatbot that keeps its tasks in the given file.
     *
     * <p>Creates the parts in dependency order: Storage reports a bad save file
     * through the Ui, and TaskList loads itself through the Storage. Loading here
     * rather than in run() prints any save-file warning before the greeting.
     *
     * @param filePath where to keep the saved list, e.g. {@code "data/tasks.txt"}
     */
    public DavidGoggins(String filePath) {
        ui = new Ui();
        // The path is passed in rather than fixed inside Storage, so the one decision
        // about where tasks live is made in the class that assembles the program.
        storage = new Storage(filePath, ui);
        // Captured rather than printed, so the GUI can show them as well as the text UI.
        ui.startCapture();
        tasks = new TaskList(storage);
        loadWarnings = ui.takeCaptured();
    }

    /**
     * Greets the user, shows any tasks restored from the save file, then reads and
     * carries out commands until they type {@code bye} or the input runs out.
     */
    public void run() {
        ui.showCapturedWarnings(loadWarnings);
        ui.showWelcome();

        // The tasks were already read from disk by the TaskList built in the
        // constructor, so this only displays them rather than loading them again.
        // Nothing is shown for an empty list: a first run on a new machine should not
        // announce a list the user has not started yet.
        if (tasks.size() > 0) {
            showTasks();
        }

        try {
            // hasNextCommand() is false at end of input, so redirected input that
            // omits "bye" exits cleanly instead of throwing.
            while (ui.hasNextCommand()) {
                String userInput = ui.readCommand();

                if (userInput.equalsIgnoreCase(EXIT_COMMAND)) {
                    break;
                }

                try {
                    handleCommand(userInput);
                } catch (DavidGogginsException e) {
                    // Every expected problem ends up here, so the error format is
                    // defined once instead of in each command method.
                    ui.showError(e.getMessage());
                } catch (RuntimeException e) {
                    // A bug should not end the session and lose the user's place.
                    reportUnexpected(e);
                }
            }
        } finally {
            // finally, so the input is released even if a command fails unexpectedly.
            ui.close();
        }

        ui.showFarewell();
    }

    /**
     * Creates a chatbot that keeps its tasks in the default save file.
     *
     * <p>Used by the GUI, which has no place to ask the user for a path and wants the
     * same file the text UI uses.
     */
    public DavidGoggins() {
        this(FILE_PATH);
    }

    /**
     * Returns the chatbot's reply to one line of input, instead of printing it.
     *
     * <p>The GUI needs the reply as text it can put in a dialog bubble, so the Ui is
     * asked to collect the reply rather than print it. Every command is carried out by
     * the same {@link #handleCommand} the text UI uses, so the two interfaces can never
     * disagree about what a command does.
     *
     * @param userInput the line the user typed, which need not be trimmed
     * @return the reply to show, already worded for the user
     */
    public String getResponse(String userInput) {
        ui.startCapture();
        String trimmedInput = userInput.trim();
        isLastResponseError = false;
        isLastResponseWarning = false;

        if (isExitCommand(trimmedInput)) {
            ui.show(ui.getFarewell());
        } else if (isHelpCommand(trimmedInput)) {
            // The GUI shows the page in its own window, so the chat only confirms it.
            ui.show("The help window is open. Study it, then get back to work.");
        } else {
            try {
                handleCommand(trimmedInput);
            } catch (DavidGogginsException e) {
                isLastResponseError = true;
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                // Caught here so a bug shows as a reply instead of freezing the window.
                isLastResponseError = true;
                reportUnexpected(e);
            }
        }

        return ui.takeCaptured();
    }

    /**
     * Tells the user a command failed because of a bug, and logs the details.
     *
     * @param e the unexpected exception
     */
    private void reportUnexpected(RuntimeException e) {
        // Standard error, so the stack trace reaches a developer without entering the reply.
        e.printStackTrace();
        ui.showError(UNEXPECTED_ERROR_MESSAGE);
    }

    /**
     * Returns the warnings raised while loading the save file, for the GUI to show.
     *
     * @return the warnings, one per line, or an empty string if loading went smoothly
     */
    public String getLoadWarnings() {
        return loadWarnings;
    }

    /**
     * Returns true if the reply last returned by {@link #getResponse} was an error.
     *
     * <p>Lets the GUI style an error differently without parsing the reply's words.
     *
     * @return true if the last command could not be carried out
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns true if the reply last returned by {@link #getResponse} was a warning.
     *
     * <p>A warning means the command was valid but changed nothing, such as marking a
     * task that is already done, so the GUI can style it apart from a normal reply.
     *
     * @return true if the last command was understood but had no effect
     */
    public boolean isLastResponseWarning() {
        return isLastResponseWarning;
    }

    /**
     * Returns true if the given input ends the conversation.
     *
     * <p>The {@code bye} command is kept for the GUI as well as the text UI: closing the
     * app by typing a command matches the way the rest of it is driven.
     *
     * @param userInput the line the user typed
     * @return true if the user asked to quit
     */
    public boolean isExitCommand(String userInput) {
        return userInput.trim().equalsIgnoreCase(EXIT_COMMAND);
    }

    /**
     * Returns true if the given input asks for the help page.
     *
     * <p>Only a bare {@code help} counts, so {@code help deadline} is left to
     * {@link #getResponse} to reject instead of opening the help window.
     *
     * @param userInput the line the user typed
     * @return true if the user asked for help with nothing after it
     */
    public boolean isHelpCommand(String userInput) {
        return userInput.trim().equalsIgnoreCase(HELP_COMMAND);
    }

    /**
     * Returns the help page for the GUI's help window.
     *
     * @return the help lines, one per line, without the text UI's indentation
     */
    public String getHelp() {
        return String.join(System.lineSeparator(), HELP_LINES);
    }

    /**
     * Returns the greeting the GUI shows before the first command.
     *
     * @return the greeting lines, without the text UI's banner
     */
    public String getGreeting() {
        return ui.getGreeting();
    }

    /**
     * Returns the tasks restored from the save file, worded for the GUI.
     *
     * @return the restored list, or an empty string when there is nothing to show
     */
    public String getRestoredTasks() {
        if (tasks.size() == 0) {
            return "";
        }
        ui.startCapture();
        showTasks();
        return ui.takeCaptured();
    }

    /**
     * Starts the chatbot with the default save file.
     *
     * @param args ignored; where tasks are kept is fixed by {@link #FILE_PATH}
     */
    public static void main(String[] args) {
        new DavidGoggins(FILE_PATH).run();
    }

    /**
     * Works out which command the user typed and carries it out.
     *
     * <p>The parsing is left to {@link Parser}; what is left here is the decision about
     * which task-list operation each command maps to, and what to say afterwards.
     *
     * @param userInput the line the user typed, already trimmed
     * @throws DavidGogginsException if the command is unknown or its details are wrong
     */
    private void handleCommand(String userInput) throws DavidGogginsException {
        String command = Parser.parseCommand(userInput);
        String argument = Parser.parseArgument(userInput);

        switch (command) {
            case "" -> throw new DavidGogginsException(
                    "You typed nothing. Silence won't get it done. Give me a command, e.g. list.");
            case LIST_COMMAND -> {
                requireNoDetails(argument, LIST_COMMAND);
                showTasks();
            }
            // A bare "bye" never gets here: it ends the conversation before parsing.
            case EXIT_COMMAND -> requireNoDetails(argument, EXIT_COMMAND);
            case "mark" -> setDone(argument, true);
            case "unmark" -> setDone(argument, false);
            case "todo" -> addTask(Parser.parseTodo(Parser.rejectSeparator(argument)));
            case "deadline" -> addTask(Parser.parseDeadline(Parser.rejectSeparator(argument)));
            case "event" -> addTask(Parser.parseEvent(Parser.rejectSeparator(argument)));
            case "delete" -> deleteTask(argument);
            case "find" -> findTasks(Parser.parseKeyword(argument));
            case HELP_COMMAND -> showHelp(argument);
            default -> throw new DavidGogginsException(
                    "I don't know the command \"" + command + "\". Stop guessing. "
                            + "Type help to see the commands I understand.");
        }
    }

    /**
     * Shows the help page.
     *
     * @param argument everything typed after the word "help", which must be empty
     * @throws DavidGogginsException if anything was typed after "help"
     */
    private void showHelp(String argument) throws DavidGogginsException {
        requireNoDetails(argument, HELP_COMMAND);
        // The leading space matches every other reply in the text UI.
        String[] indentedLines = Arrays.stream(HELP_LINES)
                .map(line -> " " + line)
                .toArray(String[]::new);
        ui.show(indentedLines);
    }

    /**
     * Refuses details typed after a command that takes none.
     *
     * <p>Silently ignoring them would hide a mistake: {@code list done} looks like it
     * should filter the list, so the user is told it does not rather than left guessing.
     *
     * @param argument    everything typed after the command word
     * @param commandName the command, named in the advice
     * @throws DavidGogginsException if the argument is not empty
     */
    private static void requireNoDetails(String argument, String commandName) throws DavidGogginsException {
        if (!argument.isEmpty()) {
            throw new DavidGogginsException("The " + commandName + " command takes no details. Try: "
                    + commandName);
        }
    }

    /** Prints every task, numbered from 1, followed by how far the user has got. */
    private void showTasks() {
        if (tasks.size() == 0) {
            ui.show(" Your list is empty. Comfortable, aren't you? Add something hard.");
            return;
        }
        ui.show(" Here's what you signed up for:", tasks.toString(), " " + formatProgress());
    }

    /**
     * Returns a one-line progress callout for the list, in Goggins' voice.
     *
     * <p>Split three ways because the push is different at each stage: nothing done
     * needs a start, some done needs to keep going, and all done needs a new challenge.
     */
    private String formatProgress() {
        int doneCount = tasks.countDone();
        int total = tasks.size();
        if (doneCount == 0) {
            return "0 of " + total + " done. Stop planning and start doing.";
        }
        if (doneCount < total) {
            return doneCount + " of " + total + " done. You're not finished.";
        }
        return "All " + total + " done. Now go find something harder.";
    }

    /**
     * Shows the tasks whose description contains the keyword.
     *
     * <p>The matches are numbered from 1, so the numbers shown belong to the search
     * results and not to the full list: {@code mark 1} after a find still refers to
     * the first task in the whole list.
     *
     * @param keyword the text the user asked to search for
     */
    private void findTasks(String keyword) {
        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            ui.show(" No tasks match \"" + keyword + "\". Nothing to hide behind.");
            return;
        }
        ui.show(" Here are the matching tasks. Pick one and get it done:", TaskList.format(matches));
    }

    /**
     * Marks a task as done or not done and confirms the change.
     *
     * <p>If the task is already in that state, a warning is shown instead and nothing changes.
     *
     * @param argument the task number the user typed, as text
     * @param isDone   true to mark as done, false to mark as not done yet
     * @throws DavidGogginsException if the number is missing, not a number, or out of range
     */
    private void setDone(String argument, boolean isDone) throws DavidGogginsException {
        String commandName = isDone ? "mark" : "unmark";
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Which task? Give me the number, e.g. " + commandName + " 2.");
        }

        int taskNumber = parseExistingTaskNumber(argument, commandName);
        // Nothing is changed or saved when the task is already in the requested state,
        // but the user is told so, rather than getting a confirmation for a no-op.
        Task task = tasks.get(taskNumber);
        if (task.isDone() == isDone) {
            isLastResponseWarning = true;
            String state = isDone ? "already done" : "already not done";
            ui.showCommandWarning("Task " + taskNumber + " is " + state + ": " + task
                    + ". Nothing changed.");
            return;
        }

        task = isDone ? tasks.mark(taskNumber) : tasks.unmark(taskNumber);
        String message = isDone
                ? " DONE. That's one less excuse:"
                : " Not done after all? Then it's still waiting for you:";
        ui.show(message, "   " + task);
    }

    /**
     * Returns the list size worded for a sentence: {@code "1 task"} but
     * {@code "2 tasks"}.
     *
     * <p>Both the add and the delete confirmations need this, so it lives in one
     * method rather than being written out (and mis-worded) in each of them.
     */
    private String formatTaskCount() {
        int count = tasks.size();
        return count + (count == 1 ? " task" : " tasks");
    }

    /**
     * Adds a task to the list and confirms it, including the new list size.
     *
     * @param task the task to add
     * @throws DavidGogginsException if the same task is already in the list
     */
    private void addTask(Task task) throws DavidGogginsException {
        int duplicateNumber = tasks.findDuplicateNumber(task);
        if (duplicateNumber > 0) {
            throw new DavidGogginsException("You already logged that as task " + duplicateNumber + ": "
                    + tasks.get(duplicateNumber) + ". Writing it down twice won't get it done twice.");
        }

        int sizeBefore = tasks.size();
        tasks.add(task);
        // The confirmation below quotes the new size, so it must reflect this one addition.
        assert tasks.size() == sizeBefore + 1 : "adding a task should grow the list by one";
        ui.show(" Logged. This one's on you now:",
                "   " + task,
                " You have " + formatTaskCount() + " in the list. Get after it.");
    }

    /**
     * Removes a task from the list and confirms it, including the new list size.
     *
     * @param argument the task number the user typed, as text
     * @throws DavidGogginsException if the number is missing, not a number, or out of range
     */
    private void deleteTask(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Which task? Give me the number, e.g. delete 2.");
        }

        int taskNumber = parseExistingTaskNumber(argument, "delete");
        Task removedTask = tasks.remove(taskNumber);
        ui.show(" Gone. I've taken this off your list:",
                "   " + removedTask,
                " You have " + formatTaskCount() + " in the list.");
    }

    /**
     * Returns the task number the user typed, checked against the current list.
     *
     * <p>Shared by every command that picks a task by number, so they all refuse a bad
     * number with the same advice.
     *
     * @param argument    the task number the user typed, as text, not empty
     * @param commandName the command it was typed for, named in the error message
     * @return a 1-based number that refers to an existing task
     * @throws DavidGogginsException if the argument is not a number or is out of range
     */
    private int parseExistingTaskNumber(String argument, String commandName) throws DavidGogginsException {
        int taskNumber = Parser.parseTaskNumber(argument, commandName);

        // A number outside the list is the user's mistake, not a bug, so it is
        // reported the same way as any other bad command.
        if (!tasks.isValidTaskNumber(taskNumber)) {
            String advice = tasks.size() == 0
                    ? "your list is empty, so add a task first."
                    : "pick a number from 1 to " + tasks.size() + ".";
            throw new DavidGogginsException("There's no task " + taskNumber + " in your list: " + advice);
        }
        return taskNumber;
    }
}
