package echo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import echo.parser.Parser;
import echo.storage.Storage;
import echo.task.Task;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Entry point and orchestrator for the Echo chatbot. Wires together Ui,
 * Storage, and TaskList, then runs the read-command/dispatch/respond loop.
 */
public class Echo {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates an Echo bound to the given save-file path, loading any
     * previously saved tasks immediately.
     *
     * @param filePath Relative path to the save file, e.g. "data/echo.txt".
     */
    public Echo(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the read-then-decide loop: reads a line, then checks for the
     * exit word. Using `while (true) + break` keeps the "read, then
     * decide" order obvious and avoids the priming/re-read pair a
     * condition-driven loop would need.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            if (input.equals("bye")) {
                break;
            }
            ui.showLine();
            try {
                String cmd = Parser.getCommandWord(input);
                String rest = Parser.getArguments(input);
                executeCommand(cmd, rest);
            } catch (EchoException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
        ui.close();
        ui.showGoodbye();
    }

    /**
     * Returns Echo's reply to one command, instead of printing it. Used
     * by the GUI, which sends one message per round trip rather than
     * driving the console read-loop that run() uses.
     *
     * <p>Ui's show* methods still print to System.out under the hood, so
     * this temporarily redirects System.out into a buffer for the
     * duration of the call and returns what was captured. This is safe
     * because Echo only ever runs one command at a time on a single
     * thread (either via run() or via the GUI, never both at once).
     *
     * @param input Raw text the user typed into the GUI.
     * @return Everything Echo would otherwise have printed for this
     *     command, with no trailing divider lines. Lines are always
     *     joined with "\n", regardless of the host platform's line
     *     separator, so the result is consistent wherever Echo runs.
     */
    public String getResponse(String input) {
        if (input.trim().equals("bye")) {
            return Ui.GOODBYE_MESSAGE;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            String cmd = Parser.getCommandWord(input);
            String rest = Parser.getArguments(input);
            executeCommand(cmd, rest);
        } catch (EchoException e) {
            ui.showError(e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString(StandardCharsets.UTF_8)
                .replace(System.lineSeparator(), "\n")
                .trim();
    }

    /**
     * Executes one already-split command word and argument string against
     * the current task list, showing the result via Ui and persisting any
     * change via Storage. Pulled out of run()'s loop so a future caller
     * (e.g. a GUI controller) can dispatch a single command the same way
     * the console loop does, without duplicating this switch.
     *
     * @param cmd Command word, e.g. "todo" or "mark".
     * @param rest Text after the command word.
     * @throws EchoException If the command is unrecognised or its
     *     arguments are invalid.
     */
    private void executeCommand(String cmd, String rest) throws EchoException {
        switch (cmd) {
            case "list":
                ui.showList(tasks.getAll());
                break;
            case "mark": {
                int idx = Parser.parseTaskIndex(rest, tasks.size());
                tasks.get(idx).markAsDone();
                ui.showTaskMarked(tasks.get(idx));
                storage.save(tasks.getAll());
                break;
            }
            case "unmark": {
                int idx = Parser.parseTaskIndex(rest, tasks.size());
                tasks.get(idx).markAsNotDone();
                ui.showTaskUnmarked(tasks.get(idx));
                storage.save(tasks.getAll());
                break;
            }
            case "delete": {
                int idx = Parser.parseTaskIndex(rest, tasks.size());
                Task removed = tasks.remove(idx);
                ui.showTaskRemoved(removed, tasks.size());
                storage.save(tasks.getAll());
                break;
            }
            case "todo":
                addTask(Parser.parseTodo(rest));
                break;
            case "deadline":
                addTask(Parser.parseDeadline(rest));
                break;
            case "event":
                addTask(Parser.parseEvent(rest));
                break;
            case "on": {
                LocalDate date = Parser.parseOnDate(rest);
                List<Task> matches = tasks.getAll().stream()
                        .filter(task -> task.occursOn(date))
                        .collect(Collectors.toList());
                ui.showTasksOnDate(date, matches);
                break;
            }
            case "archive": {
                List<Task> archived = tasks.clearAll();
                ui.showArchived(archived.size());
                storage.archive(archived);
                storage.save(tasks.getAll());
                break;
            }
            case "find": {
                String keyword = Parser.parseFindKeyword(rest);
                List<Task> matches = tasks.getAll().stream()
                        .filter(task -> task.matchesKeyword(keyword))
                        .collect(Collectors.toList());
                ui.showMatchingTasks(matches);
                break;
            }
            default:
                throw new EchoException("I don't quite recognize that command. "
                        + "Try 'todo', 'deadline', 'event', or 'list'.");
        }
    }

    /**
     * Adds the given task to the list, shows the "task added" confirmation,
     * and persists the updated list. Shared by the todo/deadline/event
     * cases in executeCommand(), which otherwise repeat this exact
     * three-step sequence with only the parsed Task differing.
     *
     * @param t The newly parsed task to add.
     * @throws EchoException If an equivalent task already exists in the list.
     */
    private void addTask(Task t) throws EchoException {
        if (tasks.containsDuplicateOf(t)) {
            throw new EchoException("This task already exists on your list: " + t);
        }
        tasks.add(t);
        ui.showTaskAdded(t, tasks.size());
        storage.save(tasks.getAll());
    }

    /**
     * Starts Echo with the default save-file path.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Echo("data/echo.txt").run();
    }
}
