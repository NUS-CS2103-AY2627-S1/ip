package trayce;

import java.io.IOException;
import trayce.parser.Parser;
import trayce.storage.Storage;
import trayce.task.Task;
import trayce.task.TaskList;
import trayce.ui.Ui;

/** Coordinates user input, task management, and persistent storage for Trayce. */
public class Trayce {
    private final Ui ui = new Ui();
    private final Storage storage = new Storage();
    private final Parser parser = new Parser();
    private TaskList taskList = new TaskList();

    /** Creates a Trayce application with its required collaborators. */
    public Trayce() { }

    /** Processes a command for the graphical interface and returns a response. */
    public String getResponse(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equalsIgnoreCase("help")) {
            return "Here are the commands you can use:\n\n"
                    + "todo <description>\n"
                    + "  Add a task\n\n"
                    + "deadline <description> /by <YYYY-MM-DD>\n"
                    + "  Add a deadline\n\n"
                    + "event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>\n"
                    + "  Add an event\n\n"
                    + "list\n"
                    + "  Show all tasks\n\n"
                    + "mark <number> / unmark <number>\n"
                    + "  Complete or reopen a task\n\n"
                    + "delete <number>\n"
                    + "  Delete a task\n\n"
                    + "find <keyword>\n"
                    + "  Search your tasks";
        }
        if (command.equalsIgnoreCase("list")) {
            if (taskList.size() == 0) {
                return "Your task list is empty.";
            }
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                result.append(i + 1).append(". [").append(task.getStatusIcon()).append("] ")
                        .append(task.getDescription()).append("\n");
            }
            return result.toString().trim();
        }
        if (trimmedCommand.toLowerCase().startsWith("mark ")) {
            return updateTaskStatus(trimmedCommand.substring(5), true);
        }
        if (trimmedCommand.toLowerCase().startsWith("unmark ")) {
            return updateTaskStatus(trimmedCommand.substring(7), false);
        }
        if (trimmedCommand.toLowerCase().startsWith("delete ")) {
            Integer index = parseTaskNumber(trimmedCommand.substring(7));
            Task deleted = index == null ? null : taskList.delete(index);
            return deleted == null ? "Please provide a valid task number." : "Deleted: " + deleted.getDescription();
        }
        if (trimmedCommand.toLowerCase().startsWith("find ")) {
            String keyword = trimmedCommand.substring(5).trim();
            return taskList.find(keyword).stream().map(Task::getDescription)
                    .reduce("", (a, b) -> a + b + "\n").trim();
        }
        Task task = parser.parseTask(trimmedCommand);
        if (task != null) {
            taskList.add(task);
            return "Added task: " + task.getDescription();
        }
        return "I do not understand that command.";
    }

    private String updateTaskStatus(String number, boolean markDone) {
        Integer index = parseTaskNumber(number);
        Task task = index == null ? null : taskList.get(index);
        if (task == null) {
            return "Please provide a valid task number.";
        }
        if (markDone) {
            task.markAsDone();
            return "Marked task as done: " + task.getDescription();
        }
        task.markAsNotDone();
        return "Marked task as not done: " + task.getDescription();
    }

    private Integer parseTaskNumber(String number) {
        try {
            int oneBasedIndex = Integer.parseInt(number.trim());
            return oneBasedIndex > 0 ? oneBasedIndex - 1 : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /** Starts the command-line interface. */
    public void run() {
        ui.showWelcome();
        taskList = loadTasks();
        while (true) {
            ui.showLine();
            String command = ui.readCommand();
            ui.showLine();
            if (command.equalsIgnoreCase("bye")) { ui.showGoodbye(); break; }
            getResponse(command);
        }
    }

    private TaskList loadTasks() {
        try { return new TaskList(storage.loadTasks()); }
        catch (IOException exception) { return new TaskList(); }
    }

    /** Starts Trayce. */
    public static void main(String[] args) { new Trayce().run(); }
}
