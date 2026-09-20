package nexus;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Processes commands for the Nexus chatbot.
 */
public class Nexus {
    private static final Path DATA_FILE = Path.of("data", "nexus.txt");
    private static final String BANNER = " _   _                      \n"
            + "| \\ | | _____  ___   _ ___ \n"
            + "|  \\| |/ _ \\ \\/ / | | / __|\n"
            + "| |\\  |  __/>  <| |_| \\__ \\\n"
            + "|_| \\_|\\___/_/\\_\\__,_|___/\n";

    private final Storage storage;
    private final TaskList tasks;
    private final String startupWarning;

    /** Creates Nexus using the default data file. */
    public Nexus() {
        this(DATA_FILE);
    }

    /**
     * Creates Nexus using a specified data file.
     *
     * @param dataFile file used to persist tasks.
     */
    Nexus(Path dataFile) {
        storage = new Storage(dataFile);
        LoadResult loadResult;
        String loadWarning = "";
        try {
            loadResult = storage.load();
            if (loadResult.skippedRecords() > 0) {
                String recordWord = loadResult.skippedRecords() == 1 ? "record" : "records";
                loadWarning = "I restored your valid tasks but skipped "
                        + loadResult.skippedRecords() + " unreadable saved " + recordWord + ".";
            }
        } catch (IOException exception) {
            loadResult = new LoadResult(List.of(), 0);
            loadWarning = "I couldn't read your saved tasks, so I started with an empty list.";
        }
        tasks = new TaskList(loadResult.tasks());
        startupWarning = loadWarning;
    }

    /**
     * Starts the console interface for Nexus.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Nexus nexus = new Nexus();
        Ui ui = new Ui();
        ui.showWelcome(BANNER, nexus.getGreeting());

        Scanner scanner = new Scanner(System.in);
        nexus.runConsole(scanner, ui::showResponse);
    }

    /**
     * Processes console input until it ends or a response requests exit.
     *
     * @param scanner source of user commands.
     * @param display destination for response text.
     */
    void runConsole(Scanner scanner, Consumer<String> display) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            Response response = getCommandResponse(input);
            display.accept(response.text());
            if (response.shouldExit()) {
                break;
            }
        }
    }

    /**
     * Returns the greeting shown when Nexus starts.
     *
     * @return the two-line startup greeting
     */
    public String getGreeting() {
        List<String> lines = new ArrayList<>();
        lines.add("Hey! I'm Nexus, your calm productivity sidekick.");
        lines.add("What's our next move?");
        if (!startupWarning.isEmpty()) {
            lines.add(startupWarning);
        }
        return Ui.formatLines(lines.toArray(String[]::new));
    }

    /**
     * Processes one user command and returns Nexus's response.
     *
     * @param input command entered by the user.
     * @return a user-facing response
     */
    public String getResponse(String input) {
        return getCommandResponse(input).text();
    }

    /**
     * Processes one user command and describes how its response should be presented.
     *
     * @param input command entered by the user.
     * @return response text and presentation metadata
     */
    public Response getCommandResponse(String input) {
        String command = normalizeWhitespace(input);
        try {
            if (command.isEmpty()) {
                throw new NexusException("Please enter a command. Type list to see your tasks.");
            }
            if (command.equals("bye")) {
                return Response.exit("You're all set. See you next time!");
            }
            return Response.success(executeCommand(command));
        } catch (NexusException exception) {
            return Response.error(exception.getMessage());
        } catch (DateTimeParseException exception) {
            return Response.error("I couldn't read that date. Please use YYYY-MM-DD.");
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return Response.error("That task number is invalid.");
        }
    }

    /** Routes a valid command to the operation that handles it. */
    private String executeCommand(String input) throws NexusException {
        if (input.equals("list")) {
            return showTasks(tasks.getTasks(), "Here are the tasks in your list:");
        }
        if (input.equals("sort")) {
            return sortTasks();
        }
        if (input.equals("find")) {
            throw new NexusException("Please include a keyword after find.");
        }
        if (input.startsWith("find ")) {
            return findTasks(input.substring(5));
        }
        if (input.equals("mark")) {
            throw new NexusException("Please include a task number after mark.");
        }
        if (input.startsWith("mark ")) {
            return updateTask(input.substring(5), true);
        }
        if (input.equals("unmark")) {
            throw new NexusException("Please include a task number after unmark.");
        }
        if (input.startsWith("unmark ")) {
            return updateTask(input.substring(7), false);
        }
        if (input.equals("delete")) {
            throw new NexusException("Please include a task number after delete.");
        }
        if (input.startsWith("delete ")) {
            return deleteTask(input.substring(7));
        }
        return addTask(input);
    }

    /** Returns matching tasks with one-based match numbers. */
    private String findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.getTasks().stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
        return showTasks(matchingTasks, "Here are the matching tasks in your list:");
    }

    /** Sorts tasks alphabetically, saves the new order, and displays it. */
    private String sortTasks() throws NexusException {
        List<Task> originalOrder = tasks.getTasks();
        tasks.sortByDescription();
        try {
            saveTasks();
        } catch (NexusException exception) {
            tasks.replaceAll(originalOrder);
            throw exception;
        }
        return showTasks(tasks.getTasks(), "Here are your tasks sorted alphabetically:");
    }

    /** Returns a numbered display of the supplied tasks. */
    private String showTasks(List<Task> tasksToShow, String heading) {
        List<String> lines = new ArrayList<>();
        lines.add(heading);
        for (int index = 0; index < tasksToShow.size(); index++) {
            lines.add((index + 1) + "." + tasksToShow.get(index));
        }
        return Ui.formatLines(lines.toArray(String[]::new));
    }

    /** Creates, saves, and describes a new task. */
    private String addTask(String input) throws NexusException {
        Task task = Parser.createTask(input);
        tasks.add(task);
        try {
            saveTasks();
        } catch (NexusException exception) {
            tasks.delete(tasks.size());
            throw exception;
        }
        return Ui.formatLines("Locked in! I've added this task:", task.toString());
    }

    /** Updates and saves a task's completion state. */
    private String updateTask(String indexText, boolean isDone) throws NexusException {
        Task task = tasks.get(parseTaskIndex(indexText));
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
            saveTasksOrRestore(task, wasDone);
            return Ui.formatLines("Nice progress! I've marked this task as done:",
                    task.toString());
        }

        task.unmark();
        saveTasksOrRestore(task, wasDone);
        return Ui.formatLines("No pressure—I've marked this task as not done yet:",
                task.toString());
    }

    /** Deletes, saves, and describes a task. */
    private String deleteTask(String indexText) throws NexusException {
        int index = parseTaskIndex(indexText);
        Task task = tasks.delete(index);
        try {
            saveTasks();
        } catch (NexusException exception) {
            tasks.insert(index, task);
            throw exception;
        }
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        return Ui.formatLines("Cleared from your path:", task.toString(),
                "Now you have " + tasks.size() + " " + taskWord + " in the list.");
    }

    /** Saves all current tasks or reports the failure as invalid input. */
    private void saveTasks() throws NexusException {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            throw new NexusException(
                    "I couldn't save your tasks. Check that the data folder is writable.");
        }
    }

    /** Saves an updated task, restoring its previous completion state on failure. */
    private void saveTasksOrRestore(Task task, boolean wasDone) throws NexusException {
        try {
            saveTasks();
        } catch (NexusException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.unmark();
            }
            throw exception;
        }
    }

    /** Parses a one-based task number and verifies it refers to an existing task. */
    private int parseTaskIndex(String indexText) throws NexusException {
        if (tasks.size() == 0) {
            throw new NexusException("Your task list is empty. Add a task first.");
        }
        final int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException exception) {
            throw new NexusException("Task numbers must be whole numbers.");
        }
        if (index < 1 || index > tasks.size()) {
            throw new NexusException("Choose a task number from 1 to " + tasks.size() + ".");
        }
        return index;
    }

    /** Trims input and collapses repeated whitespace to a single space. */
    private String normalizeWhitespace(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }
}
