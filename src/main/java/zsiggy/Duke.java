package zsiggy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zsiggy.task.Deadline;
import zsiggy.task.Event;
import zsiggy.task.Task;
import zsiggy.task.Todo;

/**
 * Coordinates Zsiggy's commands, task list, and persistent storage for both interfaces.
 */
public class Duke {
    private static final Pattern EVENT_FORMAT = Pattern.compile("(.+) /from (\\S+) /to (\\S+)");
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private String startupWarning = "";

    public Duke() {
        this("data/tasks.txt");
    }

    /**
     * Creates a chatbot with an explicit storage location for isolated sessions.
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /**
     * Runs the console interface using the same command handling as the GUI.
     */
    public void run() {
        loadTasks();
        ui.showWelcome();
        if (!startupWarning.isEmpty()) {
            ui.showError(startupWarning);
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            System.out.println(getResponse(input));
            if (input.strip().equals("bye")) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Handles a command, returning user-facing errors and rolling back failed saves.
     *
     * @param input The command entered by the user.
     * @return Zsiggy's response.
     */
    public String getResponse(String input) {
        TaskList previousTasks = tasks.copy();
        try {
            String command = normalize(input);
            return execute(command);
        } catch (DateTimeParseException e) {
            return "Oi. That's not a real date. Use YYYY-MM-DD.";
        } catch (ZsiggyException e) {
            return "Oi. " + e.getMessage();
        } catch (IOException e) {
            tasks = previousTasks;
            return "Oi. Couldn't save your tasks. No changes were kept. Check data/tasks.txt and folder permissions.";
        }
    }

    /**
     * Normalizes spaces while rejecting characters that cannot be stored safely.
     */
    private String normalize(String input) throws ZsiggyException {
        if (input == null || input.isBlank()) {
            throw new ZsiggyException("...Saying nothing won't make your work disappear. Give me a command.");
        }
        if (input.contains("|") || input.contains("\n") || input.contains("\r")) {
            throw new ZsiggyException("Keep commands on one line and leave out the | character.");
        }
        return input.strip().replaceAll("[\t ]+", " ");
    }

    /**
     * Dispatches the command to the appropriate feature.
     */
    private String execute(String input) throws ZsiggyException, IOException {
        String[] parts = input.split(" ", 2);
        String argument = parts.length == 2 ? parts[1] : "";
        switch (parts[0]) {
            case "bye":
                requireNoArgument(argument);
                return "Hmph. Bye. Go drink your green milk tea.";
            case "list":
                requireNoArgument(argument);
                return listTasks("");
            case "find":
                if (argument.isEmpty()) {
                    throw new ZsiggyException("You want me to find... what exactly? Give me a keyword.");
                }
                return listTasks(argument);
            case "mark":
            case "unmark":
            case "delete":
                return updateTask(parts[0], argument);
            case "todo":
            case "t":
                return addTask(createTodo(argument), "Got it. Added to your never-ending pile:");
            case "deadline":
                return addTask(createDeadline(argument), "Tick-tock. Added this ticking time bomb:");
            case "event":
                return addTask(createEvent(argument), "Locked it into your schedule:");
            default:
                throw new ZsiggyException("That's not a command I understand.");
        }
    }

    private void requireNoArgument(String argument) throws ZsiggyException {
        if (!argument.isEmpty()) {
            throw new ZsiggyException("This command doesn't take extra arguments.");
        }
    }

    /**
     * Lists matches using stable full-list indices for subsequent update commands.
     */
    private String listTasks(String keyword) {
        StringBuilder response = new StringBuilder(keyword.isEmpty()
                ? "Fine. Here's what you've dumped on me:" : "Fine. Here are the matching tasks:");
        for (int i = 0; i < tasks.getTaskCount(); i++) {
            if (tasks.get(i).getDescription().contains(keyword)) {
                response.append(System.lineSeparator()).append(i + 1).append(". ").append(tasks.get(i));
            }
        }
        return response.toString();
    }

    /**
     * Validates an index, applies the requested change, then persists it.
     */
    private String updateTask(String command, String argument) throws ZsiggyException, IOException {
        int index = Parser.parseTaskNumber(argument, 0);
        if (!tasks.isValidIndex(index)) {
            throw new ZsiggyException("That task doesn't exist.");
        }
        Task task = tasks.get(index);
        String response;
        if (command.equals("mark")) {
            tasks.mark(index);
            response = "Wait, you actually finished something? Wonders never cease."
                    + System.lineSeparator() + "Marked this one done:" + System.lineSeparator() + task;
        } else if (command.equals("unmark")) {
            tasks.unmark(index);
            response = "Caught you faking it, huh?" + System.lineSeparator()
                    + "Whatever, it's unmarked now:" + System.lineSeparator() + task;
        } else {
            tasks.delete(index);
            response = "Finally, one less thing cluttering your life:" + System.lineSeparator() + task
                    + System.lineSeparator() + "Now you've got " + tasks.getTaskCount() + " task(s) left.";
        }
        saveTasks();
        return response;
    }

    private Task createTodo(String description) throws ZsiggyException {
        if (description.isEmpty()) {
            throw new ZsiggyException("A todo needs an actual description. I can't organise invisible tasks.");
        }
        return new Todo(description);
    }

    /**
     * Requires exactly one deadline separator and a non-empty description/date.
     */
    private Task createDeadline(String argument) throws ZsiggyException {
        String[] parts = argument.split(" /by ", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new ZsiggyException("That's not much of a deadline. Use: deadline DESCRIPTION /by YYYY-MM-DD");
        }
        return new Deadline(parts[0], parts[1]);
    }

    /**
     * Validates event marker order, dates, and chronology before adding anything.
     */
    private Task createEvent(String argument) throws ZsiggyException {
        Matcher matcher = EVENT_FORMAT.matcher(argument);
        if (!matcher.matches() || matcher.group(1).contains("/from") || matcher.group(1).contains("/to")) {
            throw new ZsiggyException("Give me the whole event. "
                    + "Use: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD");
        }
        LocalDate start = LocalDate.parse(matcher.group(2));
        LocalDate end = LocalDate.parse(matcher.group(3));
        if (end.isBefore(start)) {
            throw new ZsiggyException("Time travel again? The event can't end before it starts.");
        }
        return new Event(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    /**
     * Rejects duplicates independent of completion state and persists the new task.
     */
    private String addTask(Task task, String response) throws ZsiggyException, IOException {
        Task candidate = task.copy();
        candidate.unmark();
        for (int i = 0; i < tasks.getTaskCount(); i++) {
            Task existing = tasks.get(i).copy();
            existing.unmark();
            if (Storage.toRecord(existing).equals(Storage.toRecord(candidate))) {
                throw new ZsiggyException("That task is already on your list.");
            }
        }
        tasks.add(task);
        saveTasks();
        return response + System.lineSeparator() + task;
    }

    private void saveTasks() throws IOException {
        if (!startupWarning.isEmpty()) {
            throw new IOException("Resolve the loading error before saving.");
        }
        storage.save(tasks);
    }

    /**
     * Loads saved tasks, retaining a visible warning and blocking writes on failure.
     */
    public void loadTasks() {
        try {
            tasks = storage.load();
            startupWarning = "";
        } catch (IOException e) {
            tasks = new TaskList();
            startupWarning = "Couldn't load saved tasks: " + e.getMessage()
                    + " Fix or restore data/tasks.txt, then restart. Saving is disabled to protect your file.";
        }
    }

    public String getStartupWarning() {
        return startupWarning;
    }

    public static void main(String[] args) {
        new Duke().run();
    }
}
