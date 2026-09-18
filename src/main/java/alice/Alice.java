package alice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Entry point and command dispatcher for the Alice chatbot.
 *
 * <p>An {@code Alice} instance owns the {@link Ui}, {@link Storage} and
 * {@link TaskList} it needs, and exposes {@link #getResponse(String)} as the
 * single place where a line of user input is parsed and executed. Both the
 * text UI (the {@link #main} command loop below) and the JavaFX GUI (see
 * {@link MainWindow}) call this same method, so the two front ends can never
 * drift apart in behaviour.
 */
public class Alice {
    private static final String DEFAULT_STORAGE_PATH = "./data/alice.txt";
    private static final String KNOWN_COMMANDS =
            "todo, deadline, event, list, find, mark, unmark, delete, snooze, bye";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Constructs an Alice chatbot backed by the default data file.
     */
    public Alice() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Constructs an Alice chatbot backed by the given data file, loading
     * any tasks already saved there.
     *
     * @param storageFilePath path to the file used to persist tasks.
     */
    public Alice(String storageFilePath) {
        assert storageFilePath != null && !storageFilePath.isBlank()
                : "storageFilePath must be a non-blank path";
        this.ui = new Ui();
        this.storage = new Storage(storageFilePath);
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Runs the Alice chatbot as a text-based command-line application.
     * Reads user input from the console in a loop and prints Alice's
     * replies, until the user types "bye".
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        Alice alice = new Alice();
        Scanner scanner = new Scanner(System.in);

        alice.ui.showWelcome();
        // Building the warning prints it, which is all the text UI needs.
        alice.getStartupWarning();
        while (true) {
            String input = scanner.nextLine();
            alice.getResponse(input);
            if (alice.isExit(input)) {
                break;
            }
        }
    }

    /**
     * Returns Alice's fixed greeting, for front ends (such as the GUI) that
     * want to show it without going through {@link #getResponse}.
     *
     * @return the greeting message.
     */
    public String getGreeting() {
        return ui.showWelcome();
    }

    /**
     * Returns a warning about any problems found while loading the saved
     * task list, so a front end can show it once at start-up.
     *
     * @return the warning to display, or empty if the data file loaded cleanly.
     */
    public Optional<Response> getStartupWarning() {
        List<String> warnings = storage.getLoadWarnings();
        if (warnings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Response.ofError(ui.showLoadWarnings(warnings)));
    }

    /**
     * Returns the current task list, for a front end that shows the tasks
     * alongside the conversation.
     *
     * <p>The list is unmodifiable so that a view can never edit the tasks
     * behind the command handling's back: every change still has to go
     * through {@link #getResponse}.
     *
     * @return the tasks, in list order.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks.getAllTasks());
    }

    /**
     * Returns whether the given input should end the chatbot session.
     *
     * @param input the raw user input.
     * @return true if the input is the exit command "bye".
     */
    public boolean isExit(String input) {
        return input.equals("bye");
    }

    /**
     * Parses and executes a single line of user input, returning Alice's
     * reply so it can be printed (text UI) or shown in a dialog box (GUI).
     *
     * <p>Every failure is turned into an error {@link Response} rather than
     * being allowed to propagate, so that no input a user can type is able
     * to terminate the program.
     *
     * @param input the raw line of user input.
     * @return Alice's reply, flagged as an error if the command failed.
     */
    public Response getResponse(String input) {
        try {
            return Response.ofSuccess(execute(input));
        } catch (AliceException e) {
            return Response.ofError(ui.showError(e.getMessage()));
        } catch (RuntimeException e) {
            // A last line of defence: an unanticipated bug should show up as
            // a message in the conversation, not as a stack trace that kills
            // the text UI or is silently swallowed by the JavaFX event loop.
            return Response.ofError(ui.showError("Something went wrong handling that command ("
                    + e.getClass().getSimpleName() + "). Your tasks are unchanged."));
        }
    }

    /**
     * Executes a single command, returning the reply to show on success.
     *
     * @param input the raw line of user input.
     * @return the reply describing what was done.
     * @throws AliceException if the input is not a command Alice understands,
     *         or the command cannot be carried out as asked.
     */
    private String execute(String input) throws AliceException {
        if (input.isBlank()) {
            // Guards against silently saving a task with an empty
            // description (e.g. from pressing Enter on an empty line),
            // which previously produced a corrupted line in the data
            // file that crashed loading on the next run.
            throw new AliceException("I didn't quite catch that - please type a command.");

        } else if (input.equals("bye")) {
            return ui.showGoodbye();

        } else if (isCommand(input, "todo")) {
            String description = Parser.parseTodoDescription(input);
            tasks.add(new Todo(description));
            save();
            return ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());

        } else if (isCommand(input, "deadline")) {
            Deadline deadline = Parser.parseDeadline(input);
            tasks.add(deadline);
            save();
            return ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());

        } else if (isCommand(input, "event")) {
            Event event = Parser.parseEvent(input);
            tasks.add(event);
            save();
            return ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());

        } else if (input.equals("list")) {
            return ui.showTaskList(tasks);

        } else if (isCommand(input, "find")) {
            String[] keywords = Parser.parseFindKeywords(input);
            ArrayList<Task> matches = tasks.find(keywords);
            return ui.showFoundTasks(matches);

        } else if (isCommand(input, "mark")) {
            int zeroBasedIndex = requireExistingTask(Parser.parseIndexArgument(input, "mark"));
            tasks.get(zeroBasedIndex).markAsDone();
            save();
            return ui.showMarked(tasks.get(zeroBasedIndex));

        } else if (isCommand(input, "unmark")) {
            int zeroBasedIndex = requireExistingTask(Parser.parseIndexArgument(input, "unmark"));
            tasks.get(zeroBasedIndex).markAsNotDone();
            save();
            return ui.showUnmarked(tasks.get(zeroBasedIndex));

        } else if (isCommand(input, "delete")) {
            int zeroBasedIndex = requireExistingTask(Parser.parseIndexArgument(input, "delete"));
            Task removedTask = tasks.remove(zeroBasedIndex);
            save();
            return ui.showDeleted(removedTask, tasks.size());

        } else if (isCommand(input, "snooze")) {
            return snooze(input);

        } else {
            // Unrecognised input used to be stored as a task, which turned
            // every typo into clutter in the task list and hid mistakes such
            // as "mark" typed without a task number.
            throw new AliceException("I don't know the command '" + firstWord(input) + "'."
                    + "\nTry one of: " + KNOWN_COMMANDS + ".");
        }
    }

    /**
     * Pushes back the deadline named by a "snooze" command.
     *
     * @param input the full raw user input, e.g. "snooze 2 3".
     * @return the reply describing the new deadline.
     * @throws AliceException if the command is malformed, or names a task
     *         that cannot be snoozed.
     */
    private String snooze(String input) throws AliceException {
        int[] snoozeArgs = Parser.parseSnooze(input);
        int zeroBasedIndex = requireExistingTask(snoozeArgs[0]);
        int days = snoozeArgs[1];

        Task task = tasks.get(zeroBasedIndex);
        if (!(task instanceof Deadline)) {
            throw new AliceException("Only deadlines can be snoozed.");
        }
        Deadline deadline = (Deadline) task;
        if (deadline.isDone()) {
            throw new AliceException("That task is already marked as done.");
        }
        deadline.snooze(days);
        save();
        return ui.showSnoozed(deadline);
    }

    /**
     * Returns whether the input invokes the given command, either on its own
     * or followed by arguments.
     *
     * <p>Matching the bare word too (rather than only "word ") means a
     * command typed without its arguments is handled by that command's own
     * branch, which can explain what is missing, instead of falling through
     * to the unknown-command message.
     *
     * @param input the full raw user input.
     * @param commandWord the command to test for.
     * @return true if the input invokes that command.
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Returns the first whitespace-separated word of the input, used to name
     * the offending command back to the user.
     *
     * @param input the full raw user input.
     * @return the first word of the input.
     */
    private static String firstWord(String input) {
        String trimmed = input.trim();
        int firstSpace = trimmed.indexOf(' ');
        return firstSpace < 0 ? trimmed : trimmed.substring(0, firstSpace);
    }

    /**
     * Checks that an index refers to an existing task, so that every command
     * taking a task number rejects an out-of-range one the same way.
     *
     * @param zeroBasedIndex the index parsed from the user's command.
     * @return the same index, once validated.
     * @throws AliceException if the index does not refer to an existing task.
     */
    private int requireExistingTask(int zeroBasedIndex) throws AliceException {
        if (!tasks.isValidIndex(zeroBasedIndex)) {
            throw new AliceException("That task number doesn't exist! You have "
                    + tasks.size() + " task(s).");
        }
        return zeroBasedIndex;
    }

    /**
     * Persists the current task list to storage.
     *
     * <p>Called before the confirmation message is built, so that a failed
     * save is reported on its own rather than after Alice has already told
     * the user the change succeeded.
     *
     * @throws AliceException if the task list could not be saved.
     */
    private void save() throws AliceException {
        storage.save(tasks.getAllTasks());
    }
}
