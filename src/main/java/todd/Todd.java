package todd;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Runs the Todd task-management chatbot and processes user commands. */
public class Todd {
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "todd.txt");
    private static final int REMINDER_WINDOW_DAYS = 7;

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private final String loadingError;

    /** Creates Todd using the default data-file location. */
    public Todd() {
        this(DEFAULT_DATA_PATH);
    }

    /** Creates Todd using a specified data file, primarily for testing. */
    public Todd(Path dataPath) {
        ui = new Ui();
        storage = new Storage(dataPath);

        TaskList loadedTasks;
        String error = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TodException e) {
            loadedTasks = new TaskList();
            error = ui.formatLoadingError(e.getMessage());
        }
        tasks = loadedTasks;
        loadingError = error;
    }

    /** Starts Todd's text-based interface. */
    public static void main(String[] args) {
        new Todd().run();
    }

    /** Returns the startup message shown by both interfaces. */
    public String getWelcomeMessage() {
        if (loadingError == null) {
            return ui.formatWelcome();
        }
        return ui.formatWelcome() + System.lineSeparator() + loadingError;
    }

    /** Returns a compact GUI welcome while retaining any warning about saved data. */
    public String getGuiWelcomeMessage() {
        if (loadingError == null) {
            return ui.formatGuiWelcome();
        }
        return ui.formatGuiWelcome() + "\n\n" + Ui.formatForGui(loadingError);
    }

    /**
     * Processes one command and returns the response for either user interface.
     *
     * @param input command entered by the user
     * @return response to display
     */
    public String getResponse(String input) {
        return getReply(input).text();
    }

    /** Processes a command once and includes the information needed to style its reply. */
    public Response getReply(String input) {
        try {
            if (input == null || input.isBlank()) {
                throw new TodException("Type a command first. Try help to see what I can do.");
            }
            Command command = Parser.parse(input);
            return new Response(execute(command, input), false, command == Command.HELP, command == Command.BYE);
        } catch (TodException e) {
            return new Response(ui.formatError(e.getMessage()), true, false, false);
        }
    }

    private void run() {
        ui.show(getWelcomeMessage());

        while (true) {
            String input = ui.readCommand();
            Response response = getReply(input);
            ui.show(response.text());
            if (response.exit()) {
                return;
            }
        }
    }

    private String execute(Command command, String input) throws TodException {
        switch (command) {
            case BYE, LIST, HELP, REMINDERS, GREETING -> Parser.validateNoArguments(input);
            case MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE -> ensureStorageLoaded();
            default -> { }
        }
        return switch (command) {
            case BYE -> ui.formatGoodbye();
            case LIST -> ui.formatTaskList(tasks.asList());
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case TODO -> addTask(Parser.parseTodo(input));
            case DEADLINE -> addTask(Parser.parseDeadline(input));
            case EVENT -> addTask(Parser.parseEvent(input));
            case DELETE -> deleteTask(input);
            case ON -> findTasksOnDate(input);
            case FIND -> findTasksByKeyword(input);
            case GREETING -> "Supp";
            case HELP -> ui.formatHelp();
            case REMINDERS -> showReminders();
            case UNKNOWN -> throw new TodException("What does that mean dawg? Sorry I am a little dumb.\n\n"
                    + "You can type help for all the commands I know");
        };
    }

    /** Marks the task selected by a mark command and saves the updated list. */
    private String markTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "mark", tasks.size());
        Task task = tasks.mark(index);
        try {
            saveTasks();
        } catch (TodException e) {
            task.markAsUndone();
            throw e;
        }
        return ui.formatMarked(task);
    }

    /** Unmarks the task selected by an unmark command and saves the updated list. */
    private String unmarkTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "unmark", tasks.size());
        Task task = tasks.unmark(index);
        try {
            saveTasks();
        } catch (TodException e) {
            task.markAsDone();
            throw e;
        }
        return ui.formatUnmarked(task);
    }

    /** Adds a parsed task, saves it, and formats a confirmation. */
    private String addTask(Task task) throws TodException {
        tasks.add(task);
        try {
            saveTasks();
        } catch (TodException e) {
            tasks.delete(tasks.size() - 1);
            throw e;
        }
        return ui.formatAdded(task, tasks.size());
    }

    /** Deletes the task selected by a delete command and saves the updated list. */
    private String deleteTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "delete", tasks.size());
        List<Task> originalTasks = tasks.asList();
        Task removed = tasks.delete(index);
        try {
            saveTasks();
        } catch (TodException e) {
            tasks = new TaskList(originalTasks);
            throw e;
        }
        return ui.formatDeleted(removed, tasks.size());
    }

    /** Formats tasks that occur on the date supplied by an on command. */
    private String findTasksOnDate(String input) throws TodException {
        LocalDate date = Parser.parseDate(input);
        List<Integer> taskNumbers = tasks.findTaskNumbersOn(date);
        return ui.formatTasksOnDate(date, tasks.asList(), taskNumbers);
    }

    /** Formats tasks matching the keyword supplied by a find command. */
    private String findTasksByKeyword(String input) throws TodException {
        String keyword = Parser.parseKeyword(input);
        return ui.formatMatchingTasks(tasks.asList(), tasks.findTaskNumbers(keyword));
    }

    /** Shows incomplete deadlines and events for today and the next six days, inclusive. */
    private String showReminders() {
        LocalDate startDate = LocalDate.now();
        List<Integer> taskNumbers = tasks.findUpcomingTaskNumbers(startDate, REMINDER_WINDOW_DAYS);
        return ui.formatReminders(startDate, REMINDER_WINDOW_DAYS, tasks.asList(), taskNumbers);
    }

    /** Protects unreadable saved data from being overwritten by a new empty list. */
    private void ensureStorageLoaded() throws TodException {
        if (loadingError != null) {
            throw new TodException("Task changes are disabled to protect your saved data. "
                    + "Fix the data file or its access permissions, then restart Todd.");
        }
    }

    private void saveTasks() throws TodException {
        storage.save(tasks.asList());
    }
}
