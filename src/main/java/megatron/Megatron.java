package megatron;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import megatron.command.Command;
import megatron.exception.MegatronException;
import megatron.parser.CommandType;
import megatron.parser.Parser;
import megatron.storage.Storage;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Runs the Megatron chatbot.
 */
public class Megatron {
    public static final String FILE_PATH = "./data/megatron.txt";
    /** Starts the Megatron command-line application. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();

        String banner = " __  __ _____ ____    _  _____ ____   ___  _   _\n"
                + "|  \\/  | ____/ ___|  / \\|_   _|  _ \\ / _ \\| \\ | |\n"
                + "| |\\/| |  _|| |  _  / _ \\ | | | |_) | | | |  \\| |\n"
                + "| |  | | |__| |_| |/ ___ \\| | |  _ <| |_| | |\\  |\n"
                + "|_|  |_|_____\\____/_/   \\_\\|_| |_| \\_\\___/|_| \\_|\n";
        System.out.println(banner);
        System.out.println("At last. Megatron is online.");
        System.out.println("Your schedule will be conquered. State your command.");

        Storage storage = new Storage(FILE_PATH);
        TaskList tasks = new TaskList(storage.load());
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                try {
                    Command commandObject = parser.parse(command);
                    commandObject.execute(tasks, ui, storage);
                    if (commandObject.isExit()) {
                        break;
                    }
                } catch (MegatronException exception) {
                    ui.showError(exception.getMessage());
                }
            }
        }
    }

    /**
     * Returns whether a command begins with a complete command keyword.
     *
     * @param command the user input
     * @param keyword the command keyword to check
     * @return whether the input uses the keyword
     */
    private static boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Classifies a user command into one of Megatron's supported command types.
     *
     * @param command the user input
     * @return the matching command type, or {@code UNKNOWN}
     */
    private static CommandType getCommandType(String command) {
        if (isCommand(command, "todo")) {
            return CommandType.TODO;
        }
        if (isCommand(command, "deadline")) {
            return CommandType.DEADLINE;
        }
        if (isCommand(command, "event")) {
            return CommandType.EVENT;
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (isCommand(command, "mark")) {
            return CommandType.MARK;
        }
        if (isCommand(command, "unmark")) {
            return CommandType.UNMARK;
        }
        if (isCommand(command, "delete")) {
            return CommandType.DELETE;
        }
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Creates a task from a valid task-creation command.
     *
     * @param command the user input
     * @param commandType the previously identified command type
     * @return the corresponding task
     * @throws MegatronException if the command is unknown or incomplete
     */
    public static Task createTaskForCommand(String command, CommandType commandType) throws MegatronException {
        if (commandType == CommandType.TODO) {
            String description = command.substring(4).trim();
            if (description.isEmpty()) {
                throw new MegatronException("a to-do needs a description after 'todo'.");
            }
            return new Todo(description);
        }
        if (commandType == CommandType.DEADLINE) {
            String remainder = command.substring(8).trim();
            int byIndex = remainder.indexOf(" /by ");
            if (byIndex <= 0 || byIndex == remainder.length() - 5) {
                throw new MegatronException("use 'deadline <description> /by <time>'.");
            }
            String description = remainder.substring(0, byIndex).trim();
            String by = remainder.substring(byIndex + 5).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new MegatronException("a deadline needs both a description and a time.");
            }
            try {
                return new Deadline(description, by);
            } catch (IllegalArgumentException exception) {
                throw new MegatronException("the deadline must use yyyy-MM-dd or d/M/yyyy HHmm.");
            }
        }
        if (commandType == CommandType.EVENT) {
            String remainder = command.substring(5).trim();
            int fromIndex = remainder.indexOf(" /from ");
            int toIndex = remainder.indexOf(" /to ");
            if (fromIndex <= 0 || toIndex <= fromIndex + 7 || toIndex == remainder.length() - 4) {
                throw new MegatronException("use 'event <description> /from <start> /to <end>'.");
            }
            String description = remainder.substring(0, fromIndex).trim();
            String from = remainder.substring(fromIndex + 7, toIndex).trim();
            String to = remainder.substring(toIndex + 4).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new MegatronException("an event needs a description, start, and end time.");
            }
            try {
                return new Event(description, from, to);
            } catch (IllegalArgumentException exception) {
                throw new MegatronException("event dates must use yyyy-MM-dd or d/M/yyyy HHmm.");
            }
        }
        throw new MegatronException(
                "I don't recognise that command. Try todo, deadline, event, list, mark, delete, or bye.");
    }

    /**
     * Parses and validates the task number supplied to mark or unmark.
     *
     * @param command the complete user input
     * @param action the command keyword
     * @param taskCount the number of stored tasks
     * @return the zero-based task index
     * @throws MegatronException if the task number is missing, invalid, or out of range
     */
    public static int taskIndexForCommand(String command, String action, int taskCount) throws MegatronException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new MegatronException("provide a task number after '" + action + "'.");
        }
        try {
            int taskIndex = Integer.parseInt(numberText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new MegatronException("task numbers run from 1 to " + taskCount + ".");
            }
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new MegatronException("the task number after '" + action + "' must be a whole number.");
        }
    }

    /**
     * Saves the complete task list to the data file.
     *
     * @param tasks the tasks to save
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        Path filePath = Paths.get(FILE_PATH);
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException exception) {
            System.out.println("Sorry, I could not create the folder for your tasks.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }
        } catch (IOException exception) {
            System.out.println("Sorry, I could not save your tasks.");
        }
    }

    /**
     * Loads saved tasks from the data file, or returns an empty list when no file exists yet.
     *
     * @return the saved tasks
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                try {
                    tasks.add(parseTask(fileScanner.nextLine()));
                } catch (IllegalArgumentException exception) {
                    System.out.println("Sorry, I could not read one of your saved tasks.");
                }
            }
        } catch (FileNotFoundException exception) {
            System.out.println("Sorry, I could not load your saved tasks.");
        }
        return tasks;
    }

    /**
     * Recreates a task from its saved display format.
     *
     * @param line one line from the saved task file
     * @return the recreated task
     */
    private static Task parseTask(String line) {
        if (line.length() < 7 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[' || line.charAt(5) != ']') {
            throw new IllegalArgumentException("Invalid saved task format");
        }
        boolean isDone = line.charAt(4) == 'X';
        String taskText = line.substring(7);
        Task task;

        if (line.startsWith("[T]")) {
            task = new Todo(taskText);
        } else if (line.startsWith("[D]")) {
            int byIndex = taskText.lastIndexOf(" (by: ");
            if (byIndex < 0 || !taskText.endsWith(")")) {
                throw new IllegalArgumentException("Invalid saved deadline format");
            }
            String description = taskText.substring(0, byIndex);
            String by = taskText.substring(byIndex + 6, taskText.length() - 1);
            task = new Deadline(description, by);
        } else if (line.startsWith("[E]")) {
            int fromIndex = taskText.lastIndexOf(" (from: ");
            int toIndex = taskText.lastIndexOf(" to: ");
            if (fromIndex < 0 || toIndex <= fromIndex || !taskText.endsWith(")")) {
                throw new IllegalArgumentException("Invalid saved event format");
            }
            String description = taskText.substring(0, fromIndex);
            String from = taskText.substring(fromIndex + 8, toIndex);
            String to = taskText.substring(toIndex + 5, taskText.length() - 1);
            task = new Event(description, from, to);
        } else {
            throw new IllegalArgumentException("Invalid saved task type");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
