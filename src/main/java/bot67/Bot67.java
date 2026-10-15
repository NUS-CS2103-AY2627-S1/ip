package bot67;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import bot67.exception.Bot67Exception;
import bot67.parser.Command;
import bot67.parser.Parser;
import bot67.storage.Storage;
import bot67.task.Deadline;
import bot67.task.Event;
import bot67.task.Task;
import bot67.task.TaskList;
import bot67.task.Todo;
import bot67.ui.Ui;

/** Runs Bot67's task-management logic for either the console or JavaFX UI. */
public class Bot67 {
    private static final String BANNER = "_____       ___    __  ______\n"
            + "| __ )  ___ | |_  / /  |___  |\n"
            + "|  _ \\ / _ \\| __|/ /_     / /\n"
            + "| |_) | (_) | |_| '_ \\   / /\n"
            + "|____/ \\___/ \\__|\\___/  /_/\n";
    private static final String PERSONALITY_ART = "⠀⠀⢀⠤⣂⣤⣬⣭⣭⣭⣔⡠⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
            + "⠀⠔⣵⣾⣿⣿⣿⢿⣿⣿⣿⣿⣎⢂⠀⢲⣤⣤⣤⣤⣀⣒⣒⣒⣒⣂⡠⠤⠤⣄\n"
            + "⠐⣾⣿⣿⣿⡏⣾⡿⢎⣛⣫⣭⣴⣾⠆⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢼\n"
            + "⡇⣿⣿⣿⣿⣟⡿⢀⣐⣻⣛⡩⢁⠀⠀⣘⣛⣛⡛⠿⠿⠿⢿⣿⣿⣿⣿⣿⢟⣾\n"
            + "⡇⣿⣿⣿⣿⣷⣾⣿⣿⣿⣿⣿⣶⡕⠄⠉⠛⠛⠛⠛⡻⣣⣾⣿⣿⣿⢟⣵⣿⠛\n"
            + "⠃⣿⣿⣿⣿⣿⢋⣥⠭⡻⣿⣿⣿⣿⡌⡄⠀⠀⠀⡐⣼⣿⣿⣿⡿⣣⣾⠏⠀⠀\n"
            + "⠨⢻⣿⣿⣿⣧⢻⠁⠀⠘⢸⣿⣿⣿⡇⣿⠀⠀⠌⣼⣿⣿⣿⡿⢱⣿⠃⠀⠀⠀\n"
            + "⠀⢦⢻⣿⣿⣿⣦⣐⣀⣊⣼⣿⣿⡿⢱⡿⠀⠰⣸⣿⣿⣿⣿⢣⣿⠃⠀⠀⠀⠀\n"
            + "⠀⠀⠣⣙⠿⣿⣿⣿⣿⣿⣿⠿⢛⣵⡿⠃⢀⢃⣿⣿⣿⣿⡟⣾⡇⠀⠀⠀⠀⠀\n"
            + "⠀⠀⠀⠈⠛⠶⣮⣭⣭⣴⣶⡿⠿⠋⠀⠀⢨⣘⣿⡻⠿⠿⢇⣿⠀⠀⠀⠀⠀⠀\n"
            + "⠀⠀⢀⠔⠒⠂⠠⠤⠭⡀⠀⠀⠀⠀⠀⠀⠀⠙⠛⠛⠛⠛⠻⠃⠀⠀⠀⠀⠀⠀\n"
            + "⢀⠆⠁⠀⡄⠀⠀⠀⠀⠈⢂⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠒⠁⠀⠀⠒⢤⡀⠀⠀\n"
            + "⠣⠤⢤⠞⠂⠀⣀⠰⠃⠀⠘⣆⢀⣀⠀⠀⠀⠀⢀⠎⠀⢠⡀⠀⠀⠀⢀⠀⠙⡀\n"
            + "⠀⠀⢸⠀⠈⠭⡀⢈⣡⠔⢶⠁⣹⢩⠃⠀⢀⠀⢸⠀⠀⠀⣑⣠⣤⠀⠙⡦⣀⠜\n"
            + "⠀⠀⠀⠣⠀⢂⠞⠱⠴⣈⡸⠰⢇⠘⠀⠰⡭⠷⢝⡤⣂⣄⠒⢤⡐⠀⠀⡇⠀⠀\n"
            + "⠀⠀⠀⠀⠱⠄⣀⢜⢁⡠⠥⠊⠀⠀⠀⠀⠡⡘⡄⠐⡂⠘⢌⡀⠉⠂⡸⠀⠀⠀\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⠄⠹⢅⣀⠹⠒⠊⠀⠀⠀⠠";

    private final Parser parser = new Parser();
    private final Storage storage;
    private final TaskList tasks;
    private String startupWarning = "";
    private boolean isStorageBlocked;
    private boolean isExitRequested;
    private boolean isLastResponseError;

    /** Loads saved tasks and prepares Bot67 to receive commands. */
    public Bot67() {
        this(new Storage());
    }

    /** Loads tasks through the supplied storage, including isolated storage used by tests. */
    public Bot67(Storage storage) {
        this.storage = storage;
        this.tasks = new TaskList(loadTasks());
    }

    /** Returns a startup warning for both interfaces, or an empty string after a successful load. */
    public String getStartupWarning() {
        return startupWarning;
    }

    /** Starts the original text interface, which remains useful for automated testing. */
    public static void main(String[] args) {
        Bot67 bot = new Bot67();
        Ui ui = new Ui();
        ui.showWelcome(BANNER);
        if (!bot.getStartupWarning().isEmpty()) {
            ui.showError(bot.getStartupWarning());
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            ui.showSeparator();
            bot.execute(scanner.nextLine(), ui);
            if (bot.isExitRequested()) {
                break;
            }
            ui.showSeparator();
        }
    }

    /** Processes one GUI command and returns Bot67's response without console separators. */
    public String getResponse(String input) {
        isLastResponseError = false;
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(response, true, StandardCharsets.UTF_8)) {
            execute(input, new Ui(output));
        }
        return isExitRequested ? Ui.GOODBYE : response.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /** Returns whether the latest command requested application shutdown. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Supplies the original ASCII banner to the graphical welcome message. */
    public static String getBanner() {
        return BANNER;
    }

    /** Supplies the original Braille art for graphical rendering without font dependencies. */
    public static String getPersonalityArt() {
        return PERSONALITY_ART;
    }

    /** Reports failure separately from response text so the GUI can highlight errors. */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /** Executes one command using the supplied output UI. */
    private void execute(String command, Ui ui) {
        isLastResponseError = false;
        try {
            command = parser.normalize(command);
            requireCommandArguments(command);
            Command parsedCommand = parser.parse(command, PERSONALITY_ART);
            if (parsedCommand != null) {
                parsedCommand.execute(tasks, ui, storage);
                isExitRequested = parsedCommand.isExit();
            } else {
                executeTaskCommand(command, ui);
            }
        } catch (Bot67Exception e) {
            isLastResponseError = true;
            ui.showError(e.getMessage());
        } catch (RuntimeException e) {
            isLastResponseError = true;
            ui.showError("I could not process that command. Please check its format.");
        }
    }

    /** Executes commands that are not represented by a {@link Command} object yet. */
    private void executeTaskCommand(String command, Ui ui) throws Bot67Exception {
        if (command.equals("list")) {
            showList(ui);
        } else if (command.equals("sort")) {
            sortTasks(ui);
        } else if (command.startsWith("find ")) {
            showFindResults(command, ui);
        } else if (isIncompleteAddCommand(command)) {
            rejectIncompleteAddCommand(command);
        } else if (command.startsWith("mark ")) {
            changeTaskStatus(command.substring(5), true, ui);
        } else if (command.startsWith("unmark ")) {
            changeTaskStatus(command.substring(7), false, ui);
        } else if (command.startsWith("delete ")) {
            deleteTask(command.substring(7), ui);
        } else if (command.startsWith("todo ")) {
            parser.requireText(command.substring(5));
            addTask(new Todo(command), ui);
        } else if (command.startsWith("deadline ")) {
            parser.requireValidDeadline(command);
            addTask(new Deadline(command), ui);
        } else if (command.startsWith("event ")) {
            parser.requireValidEvent(command);
            addTask(new Event(command), ui);
        } else {
            throw new Bot67Exception("I do not recognize that command.");
        }
    }

    /** Gives usage guidance for missing arguments and rejects extra arguments to simple commands. */
    private void requireCommandArguments(String command) throws Bot67Exception {
        switch (command) {
            case "find":
                throw new Bot67Exception("Use: find <keyword>.");
            case "mark":
            case "unmark":
            case "delete":
                throw new Bot67Exception("Use: " + command + " <task number>.");
            default:
                String keyword = command.split(" ", 2)[0];
                if (List.of("list", "sort", "bye").contains(keyword) && !command.equals(keyword)) {
                    throw new Bot67Exception("Use: " + keyword + " (no arguments).");
                }
        }
    }

    /** Returns whether an add command is missing all required arguments. */
    private boolean isIncompleteAddCommand(String command) {
        return command.equals("todo") || command.equals("deadline") || command.equals("event");
    }

    /** Reports the command-specific usage message for an incomplete add command. */
    private void rejectIncompleteAddCommand(String command) throws Bot67Exception {
        switch (command) {
            case "todo":
                throw new Bot67Exception("A todo description cannot be empty.");
            case "deadline":
                throw new Bot67Exception("Use: deadline <description> /by <date or time>.");
            case "event":
                throw new Bot67Exception("Use: event <description> /from <start> /to <end>.");
            default:
                throw new IllegalArgumentException("Expected an incomplete add command: " + command);
        }
    }

    /** Displays every task in its numbered position. */
    private void showList(Ui ui) {
        if (tasks.size() == 0) {
            ui.showLine("Six seven! No tasks in the list yet. Let's start small: todo read a book");
            return;
        }
        ui.showLine("Six seven! Here's your task lineup. One at a time, we've got this:");
        showTaskRows(ui);
    }

    /** Displays numbered task rows without a command-specific introduction. */
    private void showTaskRows(Ui ui) {
        IntStream.rangeClosed(1, tasks.size())
                .mapToObj(taskNumber -> taskNumber + "." + tasks.get(taskNumber).getDescription())
                .forEach(ui::showLine);
    }

    /** Sorts tasks alphabetically, saves the new order, and displays it. */
    private void sortTasks(Ui ui) throws Bot67Exception {
        requireWritableStorage();
        List<Task> previousOrder = new ArrayList<>(tasks.asList());
        tasks.sortByName();
        try {
            saveTasks();
        } catch (Bot67Exception e) {
            tasks.asList().clear();
            tasks.asList().addAll(previousOrder);
            throw e;
        }
        ui.showLine("Six seven! I've sorted your tasks alphabetically. Even 67 likes a little order:");
        showTaskRows(ui);
    }

    /** Displays tasks containing the requested keyword. */
    private void showFindResults(String command, Ui ui) throws Bot67Exception {
        String keyword = command.substring(5).trim();
        if (keyword.isEmpty()) {
            throw new Bot67Exception("Use: find <keyword>.");
        }
        List<String> matches = IntStream.rangeClosed(1, tasks.size())
                .filter(taskNumber -> tasks.get(taskNumber).getDescription().contains(keyword))
                .mapToObj(taskNumber -> taskNumber + "." + tasks.get(taskNumber).getDescription())
                .toList();
        if (matches.isEmpty()) {
            ui.showLine("Six seven... no matching tasks this time. Try another keyword!");
        } else {
            ui.showLine("Six seven! Found them. Here are the matching tasks in your list:");
            matches.forEach(ui::showLine);
        }
    }

    /** Marks or unmarks the task at the supplied position. */
    private void changeTaskStatus(String value, boolean isDone, Ui ui) throws Bot67Exception {
        int taskNumber = requireExistingTaskNumber(value);
        requireWritableStorage();
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.getStatusIcon().equals("X");
        if (isDone) {
            task.mark();
        } else {
            task.unmark();
        }
        try {
            saveTasks();
        } catch (Bot67Exception e) {
            if (wasDone) {
                task.mark();
            } else {
                task.unmark();
            }
            throw e;
        }
        if (isDone) {
            ui.showLine("Six seven! One task down! I've marked this task as done:",
                    "  [X] " + tasks.get(taskNumber).getName());
        } else {
            ui.showLine("Six seven! Another round? I've marked this task as not done yet:",
                    "  [ ] " + tasks.get(taskNumber).getName());
        }
    }

    /** Validates task positions consistently for mark, unmark, and delete. */
    private int requireExistingTaskNumber(String value) throws Bot67Exception {
        int taskNumber = parser.parseTaskNumber(value);
        if (taskNumber > tasks.size()) {
            throw new Bot67Exception("Task number is out of range.");
        }
        return taskNumber;
    }

    /** Deletes the task at the supplied position. */
    private void deleteTask(String value, Ui ui) throws Bot67Exception {
        int taskNumber = requireExistingTaskNumber(value);
        requireWritableStorage();
        Task deletedTask = tasks.delete(taskNumber);
        try {
            saveTasks();
        } catch (Bot67Exception e) {
            tasks.asList().add(taskNumber - 1, deletedTask);
            throw e;
        }
        ui.showLine("Six seven. Making room! I've removed this task:",
                "  " + deletedTask.getDescription(),
                "Now you have " + taskCount() + " in the list.");
    }

    /** Adds and saves one task. */
    private void addTask(Task task, Ui ui) throws Bot67Exception {
        requireWritableStorage();
        tasks.add(task);
        try {
            saveTasks();
        } catch (Bot67Exception e) {
            tasks.delete(tasks.size());
            throw e;
        }
        ui.showLine("Six seven! On it. I've added this task:",
                "  " + task.getDescription(),
                "You have " + taskCount() + " in the list. 67!");
    }

    /** Formats the actual task count without letting the catchphrase obscure it. */
    private String taskCount() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }

    /** Stops writes after a failed load so an unreadable file cannot be overwritten. */
    private void requireWritableStorage() throws Bot67Exception {
        if (isStorageBlocked) {
            throw new Bot67Exception("Changes are disabled because saved tasks could not be loaded. "
                    + "Fix " + storage.getSaveFile() + " and restart Bot67.");
        }
    }

    /** Warns and disables changes if the entire save file cannot be loaded safely. */
    private List<Task> loadTasks() {
        try {
            return storage.load();
        } catch (IOException | SecurityException e) {
            isStorageBlocked = true;
            startupWarning = "Could not load saved tasks. No tasks were loaded; "
                    + "changes are disabled to protect your file."
                    + "\nCheck " + storage.getLoadFile() + " and restart Bot67.";
            if (e.getMessage() != null && e.getMessage().startsWith("Invalid saved task at line ")) {
                startupWarning += "\n" + e.getMessage();
            }
            return List.of();
        }
    }

    /** Saves the current task list. */
    private void saveTasks() throws Bot67Exception {
        try {
            storage.save(tasks.asList());
        } catch (IOException | SecurityException e) {
            throw new Bot67Exception("Could not save tasks. Your change was not applied. Check "
                    + storage.getSaveFile() + " and its folder permissions, then try again.");
        }
    }
}
