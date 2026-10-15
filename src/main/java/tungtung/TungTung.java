package tungtung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides the console user interface for the Tung Tung task manager.
 */
public class TungTung {
    private static final Ui UI = new Ui();
    private static final Parser PARSER = new Parser();
    private static final String DIVIDER = "_____________________________________________________________";
    private static final String BANNER = "  _____          _    _ ______ _____  ______ \n"
            + " / ____|   /\\   | |  | |  ____|  __ \\|  ____|\n"
            + "| (___    /  \\  | |__| | |__  | |__) | |__   \n"
            + " \\___ \\  / /\\ \\ |  __  |  __| |  _  /|  __|  \n"
            + " ____) |/ ____ \\| |  | | |____| | \\ \\| |____ \n"
            + "|_____//_/    \\_\\_|  |_|______|_|  \\_\\______|\n";
    private static final String GREETING = "Hello! Tung Tung Sahere!\nHow can I assist?";
    private static final String FAREWELL = "Bye! Tung Tung Sagone!";
    private static final String INVALID_TASK_NUMBER = "OOPS!!! Please provide a valid task number.";
    private static final String SAVE_ERROR = "OOPS!!! I could not save your tasks to disk.";
    private static final String LOAD_ERROR =
            "OOPS!!! I could not load your saved tasks. Exiting without changing the saved file.";
    private static final String INVALID_FIND_KEYWORD = "OOPS!!! Please provide a keyword to find.";

    /**
     * Starts Tung Tung and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments; they are not used by this application
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage("data/tungtung.txt");

        UI.showGreeting();
        TaskList tasks;
        try {
            tasks = loadTasks(storage);
        } catch (IOException | SecurityException exception) {
            printError(LOAD_ERROR);
            return;
        }
        processCommands(scanner, tasks, storage);
        UI.showFarewell();
    }

    /** Prints the banner and greeting used by {@link Ui}. */
    public static void printGreetingBanner() {
        System.out.println(BANNER);
        System.out.println(GREETING);
    }

    /**
     * Repeatedly reads and processes commands from the user.
     *
     * @param scanner source of console input
     * @param tasks list of tasks to update
     */
    private static void processCommands(Scanner scanner, TaskList tasks, Storage storage) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.equals("bye")) {
                return;
            }
            handleCommand(input, tasks, storage);
        }
    }

    /**
     * Routes one command to the method responsible for it.
     *
     * @param input command entered by the user
     * @param tasks list of tasks to inspect or update
     */
    private static void handleCommand(String input, TaskList tasks, Storage storage) {
        Parser.CommandType commandType = PARSER.identify(input);
        if (commandType == Parser.CommandType.LIST) {
            printTaskList(tasks);
        } else if (commandType == Parser.CommandType.FIND) {
            findTasks(input, tasks);
        } else if (commandType == Parser.CommandType.SORT) {
            sortTasks(input, tasks, storage);
        } else if (commandType == Parser.CommandType.MARK) {
            markTask(input, tasks, true, storage);
        } else if (commandType == Parser.CommandType.UNMARK) {
            markTask(input, tasks, false, storage);
        } else if (commandType == Parser.CommandType.DELETE) {
            deleteTask(input, tasks, storage);
        } else {
            addTask(input, tasks, storage);
        }
    }

    /** Displays tasks whose descriptions contain the keyword in a find command. */
    private static void findTasks(String input, TaskList tasks) {
        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length != 2 || parts[1].isBlank()) {
            printError(INVALID_FIND_KEYWORD);
            return;
        }

        ArrayList<Task> matchingTasks = tasks.find(parts[1]);
        printDivider();
        System.out.println("Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            Task task = matchingTasks.get(index);
            System.out.println((tasks.indexOf(task) + 1) + "." + task);
        }
        printDivider();
    }

    /**
     * Displays every task currently stored in the task list.
     *
     * @param tasks tasks to display
     */
    private static void printTaskList(TaskList tasks) {
        printDivider();
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }
        printDivider();
    }

    /** Sorts tasks by deadline or event start date and displays the sorted list. */
    private static void sortTasks(String input, TaskList tasks, Storage storage) {
        String command = input.trim();
        if (command.equals("sort") || command.equals("sort by")) {
            printError("OOPS!!! Please specify a sort order. Use: sort by deadline.");
            return;
        }
        if (!command.equals("sort by deadline")) {
            if (command.startsWith("sort by deadline ")) {
                printError("OOPS!!! Descending sort is not supported. Use: sort by deadline.");
            } else if (command.startsWith("sort by ")) {
                printError("OOPS!!! Unsupported sort order. Use: sort by deadline.");
            } else {
                printError("OOPS!!! Invalid sort syntax. Use: sort by deadline.");
            }
            return;
        }
        if (tasks.size() == 0) {
            printError("OOPS!!! Nothing to sort here!");
            return;
        }

        ArrayList<Task> originalOrder = tasks.toArrayList();
        tasks.sortByDate();
        if (!saveTasks(tasks, storage)) {
            for (int index = tasks.size() - 1; index >= 0; index--) {
                tasks.remove(index);
            }
            for (Task task : originalOrder) {
                tasks.add(task);
            }
            return;
        }
        printDivider();
        System.out.println("Here are your tasks sorted by deadline:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }
        printDivider();
    }

    /**
     * Marks the requested task as done or not done.
     *
     * @param input mark or unmark command entered by the user
     * @param tasks tasks to update
     * @param isDone whether the task should be marked as completed
     */
    private static void markTask(String input, TaskList tasks, boolean isDone, Storage storage) {
        Task task;
        try {
            task = getTask(input, tasks);
        } catch (TungTungException exception) {
            printError(exception.getMessage());
            return;
        }
        boolean wasDone = task.isDone;
        if (isDone) {
            task.setDone();
        } else {
            task.setUndone();
        }
        if (!saveTasks(tasks, storage)) {
            if (wasDone) {
                task.setDone();
            } else {
                task.setUndone();
            }
            return;
        }

        printDivider();
        String message = isDone ? "Nice! I've marked this task as done:\n  "
                : "OK, I've marked this task as not done yet:\n  ";
        System.out.println(message + task);
        printDivider();
    }

    /**
     * Removes the requested task and reports the remaining number of tasks.
     *
     * @param input delete command entered by the user
     * @param tasks tasks to update
     */
    private static void deleteTask(String input, TaskList tasks, Storage storage) {
        int taskIndex;
        try {
            taskIndex = getTaskIndex(input, tasks);
        } catch (TungTungException exception) {
            printError(exception.getMessage());
            return;
        }
        Task removedTask = tasks.remove(taskIndex);
        if (!saveTasks(tasks, storage)) {
            tasks.add(taskIndex, removedTask);
            return;
        }
        printDivider();
        System.out.println("Noted. I've removed this task:\n  " + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        printDivider();
    }

    /**
     * Extracts the one-based task number from a mark, unmark, or delete command.
     *
     * @param input command containing a task number
     * @return the task number supplied by the user
     */
    private static Task getTask(String input, TaskList tasks) throws TungTungException {
        return tasks.get(getTaskIndex(input, tasks));
    }

    /**
     * Converts a command's one-based task number to a valid list index.
     *
     * @param input command containing a task number
     * @param tasks tasks that can be selected
     * @return the zero-based index of the selected task
     * @throws TungTungException if the command has no valid task number
     */
    private static int getTaskIndex(String input, TaskList tasks) throws TungTungException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new TungTungException(INVALID_TASK_NUMBER);
        }
        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TungTungException(INVALID_TASK_NUMBER);
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new TungTungException(INVALID_TASK_NUMBER);
        }
    }

    /**
     * Creates a task from an add-task command and adds it to the task list.
     *
     * @param input command entered by the user
     * @param tasks tasks to update
     */
    private static void addTask(String input, TaskList tasks, Storage storage) {
        try {
            Task newTask = PARSER.parseTask(input);
            tasks.add(newTask);
            if (!saveTasks(tasks, storage)) {
                tasks.remove(tasks.size() - 1);
                return;
            }
            printAddedTask(newTask, tasks.size());
        } catch (TungTungException exception) {
            printError(exception.getMessage());
        }
    }

    /**
     * Saves the current task list after a command changes it.
     *
     * @param tasks tasks to save
     */
    private static boolean saveTasks(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.toArrayList());
            return true;
        } catch (java.io.IOException | SecurityException exception) {
            printError(SAVE_ERROR + " " + exception.getMessage());
            return false;
        }
    }

    /**
     * Restores the saved task list when the chatbot starts.
     *
     * @param storage storage containing the saved tasks.
     * @return the restored task list.
     * @throws IOException if the saved tasks cannot be read safely.
     */
    private static TaskList loadTasks(Storage storage) throws IOException {
        return new TaskList(storage.load());
    }

    /**
     * Reports that a task was added to the task list.
     *
     * @param task task that was added
     * @param taskCount number of tasks now in the list
     */
    private static void printAddedTask(Task task, int taskCount) {
        printDivider();
        System.out.println("Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.");
        printDivider();
    }

    /**
     * Displays an error message in the standard console format.
     *
     * @param message error message to display
     */
    private static void printError(String message) {
        printDivider();
        System.out.println(message);
        printDivider();
    }

    /** Prints the greeting shown when the application starts. */
    private static void printGreeting() {
        printDivider();
        System.out.println(BANNER);
        System.out.println(GREETING);
        printDivider();
    }

    /** Prints the farewell shown when the application exits. */
    private static void printFarewell() {
        printDivider();
        System.out.println(FAREWELL);
        printDivider();
    }

    /** Prints a separator used to make console output easier to read. */
    private static void printDivider() {
        System.out.println(DIVIDER);
    }
}
