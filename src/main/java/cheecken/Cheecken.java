package cheecken;

import java.time.Clock;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Runs the Cheecken chatbot and coordinates command handling, storage, and UI.
 */
public class Cheecken {
    private final TaskList list = new TaskList();
    private final Storage storage;
    private final Clock clock;
    private final Parser parser = new Parser();
    private final Ui ui;
    private final StringBuilder response = new StringBuilder();
    private boolean isFinished;
    private boolean isLoaded;
    private boolean hasResponseError;

    /**
     * Creates a chatbot using the normal persistence file.
     */
    public Cheecken() {
        this("data/cheecken.txt");
    }

    /**
     * Creates an independent chatbot with a chosen persistence file.
     * @param filePath path to the task persistence file
     */
    public Cheecken(String filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates a chatbot with a supplied clock for deterministic date resolution.
     * @param filePath path to the task persistence file
     * @param clock clock used to resolve relative dates and times
     */
    public Cheecken(String filePath, Clock clock) {
        this.clock = clock;
        ui = new Ui(this::appendResponse);
        storage = new Storage(filePath, this::appendError);
    }

    /**
     * Adds output to the current response without redirecting global console streams.
     * @param message message to display or append
     */
    private void appendResponse(String message) {
        response.append(message).append("\n");
    }

    /**
     * Records a storage error and marks the current response for GUI styling.
     * @param message message to display or append
     */
    private void appendError(String message) {
        hasResponseError = true;
        appendResponse(message);
    }

    /**
     * Returns whether the latest initialization or command response contains an error.
     * @return true if the latest response contains an error; otherwise false
     */
    public boolean hasResponseError() {
        return hasResponseError;
    }

    /**
     * Loads persisted tasks once and returns any startup warnings.
     * @return startup warnings, or an empty string if there are none
     */
    public String initialize() {
        response.setLength(0);
        hasResponseError = false;
        if (!isLoaded) {
            loadTasks();
            isLoaded = true;
        }
        return response.toString().strip();
    }

    /**
     * Executes one command and returns plain text for a chat bubble.
     * @param input command input to process
     * @return plain-text reply for the submitted command
     */
    public String getResponse(String input) {
        response.setLength(0);
        hasResponseError = false;
        if (isFinished) {
            return "This conversation has ended. Close the window to exit.";
        }
        if (!isLoaded) {
            loadTasks();
            isLoaded = true;
        }
        isFinished = executeCommand(input);
        return response.toString().lines()
                .filter(line -> !line.equals("____________________________________________________________"))
                .collect(Collectors.joining("\n")).strip();
    }

    /**
     * Returns whether the user has ended the conversation with bye.
     * @return true if the user has ended the conversation; otherwise false
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Processes one raw command and returns whether the chatbot should exit.
     * @param rawInput command entered by the user
     * @return true when the command requests exit; otherwise false
     */
    private boolean executeCommand(String rawInput) {
        // Both entry points must load persisted tasks before accepting commands.
        assert isLoaded : "Tasks must be loaded before processing commands";
        Clock commandClock = Clock.fixed(clock.instant(), clock.getZone());
        String input = parser.normalize(rawInput);
        CommandType command = parser.parseCommand(input);
        try {
            if (command == null) {
                throw new CheeckenUnknownException();
            }
            return switch (command) {
                case BYE -> handleBye();
                case LIST -> handleList();
                case MARK -> handleMark(input);
                case UNMARK -> handleUnmark(input);
                case DEADLINE -> handleDeadline(input, commandClock);
                case EVENT -> handleEvent(input, commandClock);
                case TODO -> handleTodo(input);
                case DELETE -> handleDelete(input);
                case FIND -> handleFind(input);
            };
        } catch (Exception e) {
            hasResponseError = true;
            ui.showError(e);
            return false;
        }
    }

    /**
     * Displays the farewell message.
     * @return true to terminate the command loop
     */
    private boolean handleBye() {
        ui.showBye();
        return true;
    }

    /**
     * Displays all tasks currently in the task list.
     * @return false to continue the command loop
     */
    private boolean handleList() {
        ui.showList(list.asList());
        return false;
    }

    /**
     * Marks the task selected by a one-based index as complete.
     * @param input complete mark command
     * @return false to continue the command loop
     */
    private boolean handleMark(String input) {
        Task task = list.get(parseIndex(input, "mark"));
        task.mark();
        saveTasks();
        ui.showTaskMessage("Nice! I've marked this task as done:", task);
        return false;
    }

    /**
     * Marks the task selected by a one-based index as incomplete.
     * @param input complete unmark command
     * @return false to continue the command loop
     */
    private boolean handleUnmark(String input) {
        Task task = list.get(parseIndex(input, "unmark"));
        task.unmark();
        saveTasks();
        ui.showTaskMessage("OK, I've marked this task as not done yet:", task);
        return false;
    }

    /**
     * Creates and stores a todo task from the command text.
     * @param input complete todo command
     * @return false to continue the command loop
     */
    private boolean handleTodo(String input) {
        if (input.length() <= 4) {
            throw new CheeckenEmptyException("todo");
        }
        String taskText = input.substring(5);
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("todo");
        }
        addTask(new Todo(taskText));
        return false;
    }

    /**
     * Creates and stores a deadline task from the command text.
     * @param input complete deadline command
     * @param commandClock time captured when the command was submitted
     * @return false to continue the command loop
     */
    private boolean handleDeadline(String input, Clock commandClock) {
        String details = input.substring("deadline".length()).strip();
        int by = findFlag(details, "/by");
        if (details.isBlank() || by == 0) {
            throw new CheeckenEmptyException("deadline");
        }
        if (by == -1) {
            throw new CheeckenDateTimeException("deadline");
        }
        String taskText = details.substring(0, by).strip();
        String deadline = details.substring(by + "/by".length()).strip();
        addTask(new Deadline(taskText, DateTimeValue.parse(deadline, "deadline", commandClock)));
        return false;
    }

    /**
     * Creates and stores an event task from the command text.
     * @param input complete event command
     * @param commandClock shared reference time for both event endpoints
     * @return false to continue the command loop
     */
    private boolean handleEvent(String input, Clock commandClock) {
        String details = input.substring("event".length()).strip();
        int from = findFlag(details, "/from");
        int to = findFlag(details, "/to");
        if (details.isBlank() || from == 0) {
            throw new CheeckenEmptyException("event");
        }
        if (from == -1 || to < from + "/from".length()) {
            throw new CheeckenDateTimeException("event");
        }
        String taskText = details.substring(0, from).strip();
        String start = details.substring(from + "/from".length(), to).strip();
        String end = details.substring(to + "/to".length()).strip();
        addTask(new Event(taskText, start, end, commandClock));
        return false;
    }

    /**
     * Finds an exact lowercase flag token, excluding words such as /byx and /today.
     * @param input command input to process
     * @param flag exact lowercase flag token to locate
     * @return starting offset of the flag, or -1 if it is absent
     */
    private int findFlag(String input, String flag) {
        Matcher matcher = Pattern.compile("(?<!\\S)" + Pattern.quote(flag) + "(?=\\s|$)").matcher(input);
        return matcher.find() ? matcher.start() : -1;
    }

    /**
     * Deletes the task selected by a one-based index.
     * @param input complete delete command
     * @return false to continue the command loop
     */
    private boolean handleDelete(String input) {
        Task task = deleteTask(parseIndex(input, "delete"));
        ui.showTaskMessage("Noted. I've removed this task:", task);
        return false;
    }

    /**
     * Displays tasks matching the supplied nonempty search keyword.
     * @param input command input to process
     * @return false to continue the command loop
     */
    private boolean handleFind(String input) {
        String keyword = input.substring("find".length()).strip();
        if (keyword.isBlank()) {
            throw new CheeckenFindException();
        }
        ui.showFind(list.find(keyword));
        return false;
    }

    /**
     * Adds and persists a task before displaying confirmation.
     * @param task task to add
     */
    private void addTask(Task task) {
        list.add(task);
        saveTasks();
        // Confirmation must describe the task just appended to the list.
        assert list.size() > 0 && list.get(list.size() - 1) == task
                : "The added task must be the last task in the list";
        ui.showAdded(task, list.size());
    }

    /**
     * Removes and persists the task at the specified zero-based index.
     * @param taskIndex zero-based task index
     * @return removed task
     */
    private Task deleteTask(int taskIndex) {
        // The command handler must validate user input through parseIndex first.
        assert taskIndex >= 0 && taskIndex < list.size() : "Deletion requires a validated task index";
        Task task = list.remove(taskIndex);
        saveTasks();
        return task;
    }

    /**
     * Converts the numeric argument of a command into a validated index.
     * @param input complete command
     * @param command command keyword
     * @return zero-based task index
     */
    private int parseIndex(String input, String command) {
        String value = input.substring(command.length()).strip();
        int index = Integer.parseInt(value) - 1;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Task index out of range");
        }
        return index;
    }

    /**
     * Persists the current task list, reporting storage failures to the user.
     */
    private void saveTasks() {
        try {
            storage.save(list.asList());
        } catch (RuntimeException e) {
            appendError("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads valid persisted tasks when the chatbot starts.
     */
    private void loadTasks() {
        // Loading twice would append duplicate records to the in-memory list.
        assert !isLoaded && list.size() == 0 : "Tasks must only be loaded into an uninitialized, empty list";
        storage.load().forEach(list::add);
    }

    /**
     * Starts the interactive chatbot loop and processes commands until exit.
     */
    public void run() {
        initialize();
        ui.showWelcome();
        System.out.print(response);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();
            response.setLength(0);
            boolean shouldExit = executeCommand(input);
            System.out.print(response);
            if (shouldExit) {
                break;
            }
        }
        scanner.close();
    }

    /**
     * Launches the Cheecken chatbot application.
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        new Cheecken().run();
    }
}
