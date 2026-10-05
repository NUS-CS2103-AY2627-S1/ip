package friday;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Scanner;

import friday.storage.Storage;
import friday.task.RepeatFrequency;
import friday.task.Task;
import friday.task.TaskList;

/**
 * Runs the Friday chatbot.
 */
public class Friday {
    private static final String DEFAULT_FILE_PATH = "data/duke.txt";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_REMINDERS = "reminders";
    private static final String COMMAND_REPEAT = "repeat";
    private static final String COMMAND_SORT = "sort";
    private static final String COMMAND_UNMARK = "unmark";
    private static final long UPCOMING_REMINDER_DAYS = 2;

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private boolean lastResponseWasError;
    private String loadingError;

    /**
     * Creates a Friday chatbot that stores tasks at the given file path.
     *
     * @param filePath path of the file used to save tasks
     */
    public Friday(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        this.tasks = loadTaskList();
    }

    /**
     * Starts the chatbot and handles user commands until the user says bye.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(getWelcome());

        while (true) {
            String rawInput = scanner.nextLine();
            assert rawInput != null : "Scanner input should not be null";

            String input = rawInput.trim();
            System.out.println(getResponse(input));
            if (isExitCommand(input)) {
                break;
            }
        }
    }

    /**
     * Starts Friday using the default save file.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Friday(DEFAULT_FILE_PATH).run();
    }

    /**
     * Returns Friday's response to one line of user input.
     *
     * @param input user input from the command line or GUI
     * @return response to show to the user
     */
    public String getResponse(String input) {
        assert input != null : "User input should not be null";

        String trimmedInput = input.trim();
        if (trimmedInput.isBlank()) {
            lastResponseWasError = true;
            return ui.getError("Apologies, please enter a command sir");
        }
        if (isExitCommand(trimmedInput)) {
            lastResponseWasError = false;
            return ui.getBye();
        }

        TaskList previousTasks = tasks;
        try {
            tasks = copyTasks();
            String response = handleCommand(trimmedInput);
            lastResponseWasError = false;
            return response;
        } catch (FridayException e) {
            tasks = previousTasks;
            lastResponseWasError = true;
            return ui.getError(e.getMessage());
        }
    }

    /**
     * Returns whether the most recent response reports an invalid command or failed operation.
     *
     * @return true if the most recent response is an error
     */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Returns Friday's welcome message.
     *
     * @return welcome message to show when the app starts
     */
    public String getWelcome() {
        LocalDateTime now = LocalDateTime.now();
        TaskList upcomingReminders = tasks.getUpcomingReminders(now, now.plusDays(UPCOMING_REMINDER_DAYS));
        String welcome = ui.getWelcome(upcomingReminders);
        return loadingError == null ? welcome : welcome + "\n\n" + loadingError;
    }

    private TaskList loadTaskList() {
        try {
            return new TaskList(storage.load());
        } catch (FridayException e) {
            loadingError = e.getMessage() + "\nChanges are disabled to protect your saved tasks. "
                    + "Back up and repair the save file, then restart Friday.";
            return new TaskList();
        }
    }

    private TaskList copyTasks() throws FridayException {
        TaskList copy = new TaskList();
        for (Task task : tasks.asList()) {
            copy.add(Parser.parseSavedTask(task.toFileString()));
        }
        return copy;
    }

    private String handleCommand(String input) throws FridayException {
        assert !input.isBlank() : "Blank input should be handled before command parsing";

        String command = Parser.getCommand(input);
        String details = Parser.getDetails(input);
        assert !command.isBlank() : "Parsed command should not be blank";

        String normalizedCommand = command.toLowerCase(Locale.ROOT);

        if (loadingError != null && !normalizedCommand.equals(COMMAND_HELP)
                && !normalizedCommand.equals(COMMAND_LIST) && !normalizedCommand.equals(COMMAND_FIND)
                && !normalizedCommand.equals(COMMAND_REMINDERS)) {
            throw new FridayException(loadingError);
        }

        if (normalizedCommand.equals("edit")) {
            return editDescription(details);
        }

        if (normalizedCommand.equals(COMMAND_HELP)) {
            requireNoDetails(normalizedCommand, details);
            return ui.getHelp();
        }

        if (normalizedCommand.equals(COMMAND_LIST)) {
            requireNoDetails(normalizedCommand, details);
            return ui.getTaskList(tasks);
        }

        if (normalizedCommand.equals(COMMAND_MARK)) {
            int previousSize = tasks.size();
            Task task = tasks.mark(Parser.parseTaskNumber(details));
            storage.save(tasks);
            return ui.getTaskMarked(task) + (tasks.size() > previousSize
                    ? "\nThe next occurrence has been added as task " + tasks.size() + "." : "");
        }

        if (normalizedCommand.equals(COMMAND_UNMARK)) {
            Task task = tasks.unmark(Parser.parseTaskNumber(details));
            storage.save(tasks);
            return ui.getTaskUnmarked(task);
        }

        if (normalizedCommand.equals(COMMAND_DELETE)) {
            Task removedTask = tasks.delete(Parser.parseTaskNumber(details));
            storage.save(tasks);
            return ui.getTaskDeleted(removedTask, tasks.size());
        }

        if (normalizedCommand.equals(COMMAND_FIND)) {
            TaskList matchingTasks = tasks.find(Parser.parseKeyword(details));
            return ui.getMatchingTasks(matchingTasks);
        }

        if (normalizedCommand.equals(COMMAND_SORT)) {
            requireNoDetails(normalizedCommand, details);
            tasks.sortByReminderDateTime();
            storage.save(tasks);
            return ui.getTasksSorted(tasks);
        }

        if (normalizedCommand.equals(COMMAND_REPEAT)) {
            int taskIndex = Parser.parseRepeatTaskNumber(details);
            RepeatFrequency repeatFrequency = Parser.parseRepeatFrequency(details);
            Task task = tasks.setRepeatFrequency(taskIndex, repeatFrequency);
            storage.save(tasks);
            return ui.getTaskRepeated(task, repeatFrequency);
        }

        if (normalizedCommand.equals(COMMAND_REMINDERS)) {
            requireNoDetails(normalizedCommand, details);
            return ui.getReminders(tasks.getTasksWithReminders());
        }

        Task task = Parser.parseTask(normalizedCommand, details);
        assert task != null : "Parser should return a task for add commands";
        tasks.add(task);
        storage.save(tasks);
        return ui.getTaskAdded(task, tasks.size());
    }

    private boolean isExitCommand(String input) {
        return input.equalsIgnoreCase(COMMAND_BYE);
    }

    private String editDescription(String details) throws FridayException {
        String description = Parser.getDetails(details);
        if (description.isBlank()) {
            throw new FridayException("Please use: edit <task number> <description>");
        }
        int index = Parser.parseTaskNumber(Parser.getCommand(details));
        Parser.validateDescription(description);
        Task task = tasks.get(index);
        task.setDescription(description);
        storage.save(tasks);
        return "Updated task description, sir:\n" + task;
    }

    private void requireNoDetails(String command, String details) throws FridayException {
        if (!details.isBlank()) {
            throw new FridayException("Apologies, the " + command + " command does not accept extra details sir");
        }
    }
}
