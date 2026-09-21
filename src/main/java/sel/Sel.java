package sel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import sel.exception.SelException;
import sel.parser.Parser;
import sel.storage.Storage;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.TaskList;
import sel.task.ToDo;
import sel.ui.Ui;

/**
 * Entry of Sel chatbot.
 *
 * <p>The GUI calls {@link #getResponse(String)} and the command line calls
 * {@link #run()}. Both funnel every command through the same validation
 * and task-list helpers, so a rule enforced for one interface is enforced
 * for the other; only the way results are displayed differs.
 */
public class Sel {

    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Rephrase your words, no idea what u mean bro.";
    private static final String INVALID_NUMBER_MESSAGE =
            "Bro, give me a valid task number :(";

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /** Problem found while loading the save file, if there was one. */
    private String startupWarning;

    /**
     * Creates a new Sel instance, loading any previously saved tasks from
     * the given file path. If loading fails, starts with an empty task
     * list instead and remembers why, so the user can be told rather than
     * silently wondering where their tasks went.
     *
     * @param filePath path to the save file to load tasks from and save
     *     tasks to.
     */
    public Sel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
            startupWarning = describeSkippedData(storage.getLoadWarnings());
        } catch (SelException e) {
            // The reason is kept in startupWarning and shown once by the
            // interface in use, rather than printed to a terminal the GUI
            // user cannot see.
            tasks = new TaskList();
            startupWarning = e.getMessage()
                    + " I've started you off with an empty list, so don't add anything you "
                    + "care about until that's fixed.";
        }
    }

    /**
     * Returns anything the user should know about the state of their saved
     * data, found while starting up.
     *
     * @return the warning to show once at startup, or an empty
     *     {@code Optional} if the save file loaded cleanly.
     */
    public Optional<String> getStartupWarning() {
        return Optional.ofNullable(startupWarning);
    }

    /**
     * Processes a user command and returns Sel's response.
     *
     * <p>The returned {@link Response} also records whether the command
     * failed, so the GUI can highlight bad commands without having to
     * guess from the message text.
     *
     * @param input the raw command entered by the user.
     * @return Sel's response to the command.
     */
    public Response getResponse(String input) {
        String command = Parser.normalise(input);

        if (command.isEmpty()) {
            return Response.ofError("Bro, type something first :(");
        }

        try {
            return Response.of(respondTo(command));
        } catch (SelException e) {
            // Anything the user could have typed differently.
            return Response.ofError(e.getMessage());
        } catch (RuntimeException e) {
            // A bug in Sel itself. Reporting it beats letting the exception
            // escape into the GUI's event handler, where it would leave the
            // window looking like it had frozen.
            return Response.ofError("Bro, something broke on my end (" + e
                    + "). Your list is unchanged - try a different command.");
        }
    }

    /**
     * Runs the main command loop: greets the user, repeatedly reads and
     * handles commands until {@code bye} is entered or input ends, then
     * says goodbye.
     */
    public void run() {
        ui.showWelcome();
        getStartupWarning().ifPresent(ui::showError);

        boolean isRunning = true;
        while (isRunning) {
            String rawCommand = ui.readCommand();
            if (rawCommand == null) {
                break;
            }

            String command = Parser.normalise(rawCommand);
            if (command.isEmpty()) {
                continue;
            }

            try {
                isRunning = handle(command);
            } catch (SelException e) {
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                ui.showError("Something broke on my end: " + e);
            }
        }

        ui.close();
    }

    /**
     * Carries out one command and prints the result on the command line.
     *
     * @param command the normalised command typed by the user.
     * @return {@code true} to keep reading commands, {@code false} to stop.
     * @throws SelException if the command cannot be carried out.
     */
    private boolean handle(String command) throws SelException {
        switch (Parser.parseCommandType(command)) {
            case BYE:
                Parser.requireNoArguments(command, "bye");
                ui.showGoodbye();
                return false;

            case LIST:
                Parser.requireNoArguments(command, "list");
                ui.showTaskList(tasks);
                return true;

            case MARK:
                ui.showTaskMarked(markTask(command));
                return true;

            case UNMARK:
                ui.showTaskUnmarked(unmarkTask(command));
                return true;

            case DELETE:
                ui.showTaskDeleted(deleteTask(command), tasks.size());
                return true;

            case TODO:
                ui.showTaskAdded(addTask(createTodo(command)), tasks.size());
                return true;

            case DEADLINE:
                ui.showTaskAdded(addTask(createDeadline(command)), tasks.size());
                return true;

            case EVENT:
                ui.showTaskAdded(addTask(createEvent(command)), tasks.size());
                return true;

            case FIND:
                ui.showMatchingTasks(findTasks(command));
                return true;

            default:
                throw new SelException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Carries out one command and returns the reply the GUI should show.
     *
     * @param command the normalised command typed by the user.
     * @return the text of Sel's reply.
     * @throws SelException if the command cannot be carried out.
     */
    private String respondTo(String command) throws SelException {
        switch (Parser.parseCommandType(command)) {
            case BYE:
                Parser.requireNoArguments(command, "bye");
                return "Bye see ya later alligator.";

            case LIST:
                Parser.requireNoArguments(command, "list");
                return getTaskListResponse();

            case MARK:
                return "Marked task as done:\n" + markTask(command);

            case UNMARK:
                return "Unmarked task:\n" + unmarkTask(command);

            case DELETE:
                return getDeleteResponse(deleteTask(command));

            case TODO:
                return getAddedTaskResponse(addTask(createTodo(command)));

            case DEADLINE:
                return getAddedTaskResponse(addTask(createDeadline(command)));

            case EVENT:
                return getAddedTaskResponse(addTask(createEvent(command)));

            case FIND:
                return getFindResponse(findTasks(command));

            default:
                throw new SelException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Builds a todo from a {@code todo} command.
     *
     * @param command the normalised command typed by the user.
     * @return the task to add.
     * @throws SelException if the description is missing or unusable.
     */
    private Task createTodo(String command) throws SelException {
        return new ToDo(Parser.parseDescription(command, "todo",
                "Bro, you need to tell me what's the task :("));
    }

    /**
     * Builds a deadline from a {@code deadline} command.
     *
     * @param command the normalised command typed by the user.
     * @return the task to add.
     * @throws SelException if any part of the command is missing or invalid.
     */
    private Task createDeadline(String command) throws SelException {
        String[] args = Parser.parseDeadlineArgs(command);
        LocalDateTime ddl = Parser.parseDateTime(args[1]);
        return new Deadline(args[0], ddl);
    }

    /**
     * Builds an event from an {@code event} command.
     *
     * @param command the normalised command typed by the user.
     * @return the task to add.
     * @throws SelException if any part of the command is missing or
     *     invalid, including a time range that ends before it starts.
     */
    private Task createEvent(String command) throws SelException {
        String[] args = Parser.parseEventArgs(command);
        LocalDateTime from = Parser.parseDateTime(args[1]);
        LocalDateTime to = Parser.parseDateTime(args[2]);
        return new Event(args[0], from, to);
    }

    /**
     * Adds a task to the list and saves it, refusing tasks that are already
     * on the list.
     *
     * @param task the task to add.
     * @return the task that was added.
     * @throws SelException if the task duplicates an existing one, or if
     *     the updated list could not be saved.
     */
    private Task addTask(Task task) throws SelException {
        Optional<Task> duplicate = tasks.findDuplicateOf(task);
        if (duplicate.isPresent()) {
            throw new SelException("Bro, that's already on your list:\n" + duplicate.get()
                    + "\nNo need to add it twice.");
        }

        tasks.add(task);
        storage.save(tasks.asList());
        return task;
    }

    /**
     * Marks the task identified by the command and saves the updated task list.
     *
     * @param command the normalised mark command.
     * @return the task that was marked.
     * @throws SelException if the task index is missing or invalid, the
     *     task is already done, or the list could not be saved.
     */
    private Task markTask(String command) throws SelException {
        int index = parseExistingIndex(command, "mark",
                "Bro, you need to tell me which task to mark :(");
        Task task = tasks.get(index);

        if (task.isDone()) {
            throw new SelException("Bro, that one's already done:\n" + task);
        }

        tasks.mark(index);
        storage.save(tasks.asList());
        return task;
    }

    /**
     * Unmarks the task identified by the command and saves the updated task list.
     *
     * @param command the normalised unmark command.
     * @return the task that was unmarked.
     * @throws SelException if the task index is missing or invalid, the
     *     task was not done anyway, or the list could not be saved.
     */
    private Task unmarkTask(String command) throws SelException {
        int index = parseExistingIndex(command, "unmark",
                "Bro, you need to tell me which task to unmark :(");
        Task task = tasks.get(index);

        if (!task.isDone()) {
            throw new SelException("Bro, that one wasn't done in the first place:\n" + task);
        }

        tasks.unmark(index);
        storage.save(tasks.asList());
        return task;
    }

    /**
     * Deletes the task identified by the command and saves the updated task list.
     *
     * @param command the normalised delete command.
     * @return the task that was deleted.
     * @throws SelException if the task index is missing or invalid, or if
     *     the list could not be saved.
     */
    private Task deleteTask(String command) throws SelException {
        int index = parseExistingIndex(command, "delete",
                "Bro, you need to tell me which task to delete :(");

        Task deletedTask = tasks.delete(index);
        storage.save(tasks.asList());
        return deletedTask;
    }

    /**
     * Reads a task number from a command and checks that it refers to a
     * task that actually exists.
     *
     * @param command the normalised command typed by the user.
     * @param commandWord the command word, e.g. {@code "mark"}.
     * @param missingMessage message to use if no number was given.
     * @return the zero-based index of an existing task.
     * @throws SelException if the number is missing, malformed, or out of
     *     range for the current list.
     */
    private int parseExistingIndex(String command, String commandWord, String missingMessage)
            throws SelException {
        int index = Parser.parseIndex(command, commandWord, missingMessage, INVALID_NUMBER_MESSAGE);

        if (!tasks.isValidIndex(index)) {
            throw new SelException(outOfRangeMessage(index));
        }
        return index;
    }

    /**
     * Builds the message for a task number that does not exist, telling the
     * user what they could have typed instead.
     *
     * @param index the zero-based index the user asked for.
     * @return the message to show the user.
     */
    private String outOfRangeMessage(int index) {
        if (tasks.size() == 0) {
            return "Bro, your list is empty - there's no task " + (index + 1) + " yet.";
        }
        return "Bro, there's no task " + (index + 1) + ". Pick a number from 1 to "
                + tasks.size() + ".";
    }

    /**
     * Finds the tasks matching a {@code find} command.
     *
     * @param command the normalised find command.
     * @return the matching tasks, in list order.
     * @throws SelException if no keyword was given.
     */
    private List<Task> findTasks(String command) throws SelException {
        String keyword = Parser.parseSimpleArgument(command, "find",
                "Bro, you need to tell me what to search for :(");
        return tasks.find(keyword);
    }

    /**
     * Summarises anything skipped while loading the save file.
     *
     * @param warnings the per-line warnings collected by {@link Storage}.
     * @return the message to show once at startup, or {@code null} if
     *     there is nothing to report.
     */
    private String describeSkippedData(List<String> warnings) {
        if (warnings.isEmpty()) {
            return null;
        }
        if (warnings.size() == 1) {
            return "Heads up: there was a line in my save file I couldn't use ("
                    + warnings.get(0) + "), so I skipped it. Everything else loaded fine.";
        }
        return "Heads up: I skipped " + warnings.size()
                + " lines of my save file that I couldn't use:\n- "
                + String.join("\n- ", warnings)
                + "\nEverything else loaded fine.";
    }

    private String getTaskListResponse() {
        if (tasks.size() == 0) {
            return "Your task list is empty bro.";
        }

        StringBuilder response = new StringBuilder(
                "Bro why do you want to see the list??? anyway here it is:\n");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1)
                    .append(".")
                    .append(tasks.get(i));

            if (i < tasks.size() - 1) {
                response.append("\n");
            }
        }

        return response.toString();
    }

    private String getDeleteResponse(Task deletedTask) {
        return "Yay! You have fewer tasks now!\n"
                + deletedTask
                + "\nNow "
                + tasks.size()
                + " task(s) on your list bruh...";
    }

    private String getFindResponse(List<Task> matches) {
        if (matches.isEmpty()) {
            return "Bro, nothing in your list matches that keyword :(";
        }

        StringBuilder response =
                new StringBuilder("Here are the matching tasks in your list:\n");

        for (int i = 0; i < matches.size(); i++) {
            response.append(i + 1)
                    .append(".")
                    .append(matches.get(i));

            if (i < matches.size() - 1) {
                response.append("\n");
            }
        }

        return response.toString();
    }

    private String getAddedTaskResponse(Task task) {
        return "Why more work for you?!?!\n"
                + task
                + "\nNow "
                + tasks.size()
                + " task(s) on your list bruh...";
    }

    /**
     * Starts the Sel application, loading and saving tasks to
     * {@code data/sel.txt} relative to the working directory.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        new Sel("data/sel.txt").run();
    }
}
