package twizzy;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Coordinates Twizzy's UI, parser, task list, and storage components. */
public class Twizzy {
    private static final Path DATA_FILE_PATH = Path.of("data", "twizzy.txt");

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

    private final Parser parser;

    private final Storage storage;

    private final Ui ui;

    private TaskList tasks;

    private String guiStartupError;

    private boolean isGuiStorageReadOnly;

    /** Creates a Twizzy application using its default local data file. */
    public Twizzy() {
        this(new Storage(DATA_FILE_PATH));
    }

    /**
     * Creates a Twizzy application using the supplied storage implementation.
     *
     * @param storage storage used to persist tasks
     */
    Twizzy(Storage storage) {
        parser = new Parser();
        this.storage = storage;
        ui = new Ui();
    }

    /** Starts the Twizzy application. */
    public static void main(String[] args) {
        new Twizzy().run();
    }

    /** Reads and executes commands until the user exits or input ends. */
    public void run() {
        ui.showWelcome();
        loadTasks();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = CommandType.from(command);
            ui.showDivider();
            if (commandType == CommandType.BYE) {
                ui.showGoodbye();
                ui.showDivider();
                break;
            }
            ui.showResponse(getResponse(command));
            ui.showDivider();
        }
        ui.close();
    }

    /**
     * Processes one command and returns the response for a graphical user interface.
     *
     * @param command complete command entered by the user
     * @return user-facing response for the command
     */
    public String getResponse(String command) {
        CommandType commandType = CommandType.from(command);
        if (isGuiStorageReadOnly && isTaskChangingCommand(commandType)) {
            return "Nah, you gotta lock in, gang. Saved task data could not be read, so changes are locked "
                    + "to protect it. Fix or remove data/twizzy.txt, then restart Twizzy.";
        }
        try {
            return switch (commandType) {
            case BYE -> "Catch you later, broski. Don't ghost your tasks.";
            case HELP -> helpText();
            case LIST -> command.equals("list snoozed")
                    ? formatSnoozedTaskList(tasks.getSnoozedTasks(LocalDate.now()))
                    : formatTaskList(tasks.getActiveTasks(LocalDate.now()), "Your active tasks, gang:",
                    "No active tasks yet, twin. Add one with todo <description>.");
            case FIND -> formatTaskList(tasks.findActiveTasks(parser.parseFindKeyword(command), LocalDate.now()),
                    "Here are the matching tasks in your list:", "No tasks matched that, twin. Try another keyword.");
            case MARK -> formatStatusChange(command, commandType, true);
            case UNMARK -> formatStatusChange(command, commandType, false);
            case DELETE -> formatDeletion(command, commandType);
            case SNOOZE -> formatSnooze(command);
            case UNSNOOZE -> formatUnsnooze(command);
            case TODO, DEADLINE, EVENT, UNKNOWN -> formatAddition(command, commandType);
            };
        } catch (TwizzyException | IOException exception) {
            return "Nah, you gotta lock in, gang. " + exception.getMessage();
        }
    }

    /** Loads saved tasks for a graphical user interface without printing to standard output. */
    public void initializeForGui() {
        try {
            tasks = new TaskList(storage.load());
            guiStartupError = null;
            isGuiStorageReadOnly = false;
        } catch (IOException exception) {
            tasks = new TaskList(List.of());
            guiStartupError = "Nah, you gotta lock in, gang. I couldn't read saved tasks: "
                    + exception.getMessage();
            isGuiStorageReadOnly = true;
        }
    }

    /** Returns the data-loading problem to display in the GUI, if one occurred. */
    public String getGuiStartupError() {
        return guiStartupError;
    }

    /** Returns a compact summary of all tasks for display in the graphical interface. */
    public String getTaskSummary() {
        LocalDate today = LocalDate.now();
        int completedTasks = (int) tasks.asList().stream().filter(Task::isDone).count();
        int snoozedTasks = tasks.getSnoozedTasks(today).size();
        return "Tasks: " + tasks.size() + " total · " + completedTasks + " done · " + snoozedTasks + " snoozed";
    }

    /** Returns whether a command would alter task data. */
    private boolean isTaskChangingCommand(CommandType commandType) {
        return switch (commandType) {
        case TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, SNOOZE, UNSNOOZE -> true;
        case BYE, HELP, LIST, FIND, UNKNOWN -> false;
        };
    }

    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
            isGuiStorageReadOnly = false;
        } catch (IOException exception) {
            tasks = new TaskList(List.of());
            isGuiStorageReadOnly = true;
            ui.showError("I couldn't load saved tasks, so changes are locked to protect it: "
                    + exception.getMessage());
            ui.showDivider();
        }
    }

    private String formatTaskList(List<Task> displayedTasks, String heading, String emptyMessage) {
        StringBuilder response = new StringBuilder(heading);
        if (displayedTasks.isEmpty()) {
            return response.append(System.lineSeparator()).append(emptyMessage).toString();
        }
        String previousGroup = "";
        for (int i = 0; i < displayedTasks.size(); i++) {
            Task task = displayedTasks.get(i);
            String group = getTaskGroupName(task);
            if (!group.equals(previousGroup)) {
                response.append(System.lineSeparator());
                if (!previousGroup.isEmpty()) {
                    response.append(System.lineSeparator());
                }
                response.append(group);
                previousGroup = group;
            }
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(task);
        }
        return response.toString();
    }

    private String helpText() {
        return "Commands, gang:" + System.lineSeparator()
                + "  todo <description>" + System.lineSeparator()
                + "  deadline <description> /by <yyyy-MM-dd>" + System.lineSeparator()
                + "  event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>" + System.lineSeparator()
                + "  list | list snoozed | find <keyword>" + System.lineSeparator()
                + "  mark <number> | unmark <number> | delete <number>" + System.lineSeparator()
                + "  snooze <number> /until <yyyy-MM-dd> | unsnooze <number>" + System.lineSeparator()
                + "  bye";
    }

    private String formatSnoozedTaskList(List<Task> snoozedTasks) {
        StringBuilder response = new StringBuilder("Here are the snoozed tasks:");
        if (snoozedTasks.isEmpty()) {
            return response.append(System.lineSeparator())
                    .append("No snoozed tasks right now, gang.").toString();
        }
        String previousGroup = "";
        for (int i = 0; i < snoozedTasks.size(); i++) {
            Task task = snoozedTasks.get(i);
            String group = getTaskGroupName(task);
            if (!group.equals(previousGroup)) {
                response.append(System.lineSeparator());
                if (!previousGroup.isEmpty()) {
                    response.append(System.lineSeparator());
                }
                response.append(group);
                previousGroup = group;
            }
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(task)
                    .append(System.lineSeparator())
                    .append("  ↳ Returns: ")
                    .append(task.getSnoozedUntil().format(DISPLAY_DATE_FORMAT));
        }
        return response.toString();
    }

    /** Returns the user-facing heading for a task type. */
    private String getTaskGroupName(Task task) {
        if (task instanceof Todo) {
            return "Todos";
        }
        if (task instanceof Deadline) {
            return "Deadlines";
        }
        return "Events";
    }

    private String formatAddition(String command, CommandType commandType)
            throws TwizzyException, IOException {
        Task task = parser.parseTask(command, commandType);
        rejectDuplicate(task);
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.add(task);
        storage.save(updatedTasks);
        tasks.add(task);
        return "Locked in, gang. I added:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + formatTaskCount();
    }

    private String formatStatusChange(String command, CommandType commandType, boolean isDone)
            throws TwizzyException, IOException {
        LocalDate today = LocalDate.now();
        int taskIndex = parser.parseTaskIndex(command, commandType, tasks.getActiveTasks(today).size());
        Task task = tasks.getActive(taskIndex, today);
        if (task.isDone() == isDone) {
            String status = isDone ? "done" : "not done";
            throw new TwizzyException("That task is already marked as " + status + ".");
        }
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            if (isDone) {
                task.markAsNotDone();
            } else {
                task.markAsDone();
            }
            throw exception;
        }
        String message = isDone ? "Marked done, twin:" : "Marked pending, gang:";
        return message + System.lineSeparator() + "  " + task;
    }

    private String formatDeletion(String command, CommandType commandType)
            throws TwizzyException, IOException {
        LocalDate today = LocalDate.now();
        int taskIndex = parser.parseTaskIndex(command, commandType, tasks.getActiveTasks(today).size());
        Task removedTask = tasks.getActive(taskIndex, today);
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.remove(removedTask);
        storage.save(updatedTasks);
        tasks.removeActive(taskIndex, today);
        return "Deleted, broski:" + System.lineSeparator()
                + "  " + removedTask + System.lineSeparator()
                + formatTaskCount();
    }

    private String formatSnooze(String command) throws TwizzyException, IOException {
        LocalDate today = LocalDate.now();
        Parser.SnoozeDetails details = parser.parseSnooze(command, tasks.getActiveTasks(today).size());
        Task task = tasks.getActive(details.taskIndex(), today);
        LocalDate previousSnoozeDate = task.getSnoozedUntil();
        task.snoozeUntil(details.until());
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            restoreSnoozeDate(task, previousSnoozeDate);
            throw exception;
        }
        return "Snoozed, gang:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "  ↳ Returns: " + details.until().format(DISPLAY_DATE_FORMAT);
    }

    /** Returns a snoozed task to the active list using its snoozed-list number. */
    private String formatUnsnooze(String command) throws TwizzyException, IOException {
        LocalDate today = LocalDate.now();
        List<Task> snoozedTasks = tasks.getSnoozedTasks(today);
        int taskIndex = parser.parseTaskIndex(command, CommandType.UNSNOOZE, snoozedTasks.size());
        Task task = snoozedTasks.get(taskIndex);
        LocalDate previousSnoozeDate = task.getSnoozedUntil();
        task.unsnooze();
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            restoreSnoozeDate(task, previousSnoozeDate);
            throw exception;
        }
        return "Unsnoozed, twin:" + System.lineSeparator() + "  " + task;
    }

    private String formatTaskCount() {
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        return "You're juggling " + tasks.size() + " " + taskWord + " now, twin.";
    }

    /** Rejects a task that would repeat an existing task's user-visible details. */
    private void rejectDuplicate(Task task) throws TwizzyException {
        if (tasks.containsDuplicate(task)) {
            throw new TwizzyException("That task is already on your list, gang. Use list to find it, "
                    + "or complete it before adding it again.");
        }
    }

    private void restoreSnoozeDate(Task task, LocalDate snoozeDate) {
        if (snoozeDate == null) {
            task.unsnooze();
        } else {
            task.snoozeUntil(snoozeDate);
        }
    }

}
