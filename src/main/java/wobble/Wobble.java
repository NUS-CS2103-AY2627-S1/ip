package wobble;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Scanner;

import wobble.exceptions.WobbleException;
import wobble.parser.CommandSuggester;
import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;
import wobble.ui.Ui;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;

    /** Creates Wobble with its standard console UI, storage, and parser. */
    public Wobble() {
        this(new Ui(), new Storage(), new Parser());
    }

    /** Creates Wobble with supplied collaborators, allowing isolated controller tests. */
    Wobble(Ui ui, Storage storage, Parser parser) {
        this.ui = ui;
        this.storage = storage;
        this.parser = parser;
    }

    /** Starts the chatbot and processes commands until the user exits. */
    public static void main(String[] args) {
        new Wobble().run();
    }

    /** Runs the command loop until the user exits or input ends. */
    void run() {
        ui.showWelcome();
        TaskList taskList = loadTasks();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = ui.readCommand(scanner);

            if (processCommand(command, taskList)) {
                break;
            }
        }
        scanner.close();
    }

    /** Loads saved tasks or starts with an empty list when loading fails. */
    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (IOException | SecurityException exception) {
            System.out.println("Wobble diagnostic: saved tasks could not be loaded; "
                    + "starting with an empty tray.");
            return new TaskList();
        }
    }

    /** Processes one command and returns whether the application should exit. */
    private boolean processCommand(String command, TaskList taskList) {
        command = Parser.normalizeCommand(command);
        String lowerCaseCommand = command.toLowerCase(Locale.ROOT);
        if (lowerCaseCommand.equals("bye")) {
            ui.showGoodbye();
            return true;
        }
        try {
            if (lowerCaseCommand.equals("due on") || lowerCaseCommand.startsWith("due on ")) {
                handleDateCommand(command, taskList, parser);
            } else if (lowerCaseCommand.equals("find") || lowerCaseCommand.startsWith("find ")) {
                handleFindCommand(command, taskList, ui);
            } else if (lowerCaseCommand.equals("help")) {
                ui.showHelp();
            } else if (lowerCaseCommand.equals("reminders") || lowerCaseCommand.startsWith("reminders ")) {
                int days = parser.parseReminderDays(command);
                LocalDateTime now = LocalDateTime.now();
                ui.showReminders(taskList, taskList.findUpcoming(now, days), now, days);
            } else if (lowerCaseCommand.equals("list")) {
                ui.showTasks(taskList);
            } else if (lowerCaseCommand.equals("delete") || lowerCaseCommand.startsWith("delete ")
                    || lowerCaseCommand.equals("remove") || lowerCaseCommand.startsWith("remove ")) {
                handleDeleteCommand(command, taskList, storage);
            } else if (lowerCaseCommand.equals("mark") || lowerCaseCommand.startsWith("mark ")
                    || lowerCaseCommand.equals("unmark") || lowerCaseCommand.startsWith("unmark ")) {
                handleStatusCommand(command, taskList, storage);
            } else {
                Task task = parser.parseTask(command);
                if (taskList.containsEquivalent(task)) {
                    throw new WobbleException("that task is already in the memory tray.", false);
                }
                taskList.add(task);
                try {
                    storage.save(taskList);
                } catch (IOException | SecurityException exception) {
                    taskList.delete(taskList.size());
                    throw exception;
                }
                ui.showTaskAdded(task, taskList.size());
            }
        } catch (WobbleException exception) {
            String diagnostic = exception.getMessage();
            if (exception.shouldSuggestCommand()) {
                diagnostic += System.lineSeparator()
                        + "Do you mean " + CommandSuggester.suggest(command) + "?";
            }
            ui.showDiagnostic(diagnostic);
        } catch (IOException | SecurityException exception) {
            ui.showDiagnostic("changes could not be saved.");
        }
        return false;
    }

    /** Validates a find command and asks the UI to display matching tasks. */
    private static void handleFindCommand(String command, TaskList taskList, Ui ui) throws WobbleException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new WobbleException("a search keyword is required.");
        }
        ui.showMatchingTasks(taskList, keyword);
    }

    /** Displays deadlines and events that occur on a requested date. */
    private static void handleDateCommand(String command, TaskList taskList, Parser parser) throws WobbleException {
        LocalDate date = parser.parseDueDate(command);
        System.out.println("Time scanner locked onto " + date + ":");
        int matches = 0;
        for (int i = 1; i <= taskList.size(); i++) {
            Task task = taskList.get(i);
            boolean occursOnDate = task instanceof Deadline deadline
                    && deadline.getBy().toLocalDate().equals(date);
            if (task instanceof Event event) {
                occursOnDate = !date.isBefore(event.getFrom().toLocalDate())
                        && !date.isAfter(event.getTo().toLocalDate());
            }
            if (occursOnDate) {
                System.out.println(i + ". " + task);
                matches++;
            }
        }
        if (matches == 0) {
            System.out.println("No deadlines or events are wobbling on that date.");
        }
    }

    /** Deletes the task referred to by a delete command. */
    private static void handleDeleteCommand(String command, TaskList taskList, Storage storage)
            throws WobbleException, IOException {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            throw new WobbleException("a task number is required.");
        }
        int taskNumber = Parser.parseTaskNumber(parts[1]);
        Task removedTask = taskList.delete(taskNumber);
        if (removedTask == null) {
            throw new WobbleException(invalidTaskNumberMessage(taskList), false);
        }
        try {
            storage.save(taskList);
        } catch (IOException | SecurityException exception) {
            taskList.addAt(taskNumber, removedTask);
            throw exception;
        }
        System.out.println("Memory tray update: removed:");
        System.out.println("  " + removedTask);
        System.out.println("The tray now holds " + taskList.size() + " tasks.");
    }

    /** Marks or unmarks the task referred to by a status command. */
    private static void handleStatusCommand(String command, TaskList taskList, Storage storage)
            throws WobbleException, IOException {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            throw new WobbleException("a task number is required to mark or unmark a task.");
        }

        int taskNumber = Parser.parseTaskNumber(parts[1]);
        Task task = taskList.get(taskNumber);
        if (task == null) {
            throw new WobbleException(invalidTaskNumberMessage(taskList), false);
        }

        boolean wasDone = task.isDone();
        boolean markingDone = parts[0].equalsIgnoreCase("mark");
        if (markingDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(taskList);
        } catch (IOException | SecurityException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        String status = markingDone ? "marked as done" : "marked as not done";
        System.out.println("Status sync complete. Task " + status + ":");
        System.out.println("  " + task);
    }

    /** Returns a clear explanation of the valid task-number range. */
    private static String invalidTaskNumberMessage(TaskList taskList) {
        if (taskList.size() == 0) {
            return "There are no tasks in the list yet.";
        }
        return "That task number is off my radar. Choose a number from 1 to "
                + taskList.size() + ".";
    }
}
