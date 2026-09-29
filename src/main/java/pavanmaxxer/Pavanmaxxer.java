package pavanmaxxer;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates the Pavanmaxxer task manager's collaborators.
 */
public class Pavanmaxxer {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Pavanmaxxer using the supplied data-file location.
     *
     * @param filePath Relative or absolute task data path.
     */
    public Pavanmaxxer(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (PavanmaxxerException exception) {
            ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Executes one command and returns its user-facing response.
     *
     * @param input Raw command entered by the user.
     * @return Response suitable for either the CLI or GUI.
     */
    public String getResponse(String input) {
        Command command = Parser.parseCommand(input);
        try {
            return switch (command) {
                case BYE -> "Bye. Hope to see you again soon!";
                case LIST -> listTasks();
                case MARK -> updateMark(input, true);
                case UNMARK -> updateMark(input, false);
                case DELETE -> deleteTask(input);
                case TODO, DEADLINE, EVENT -> addTask(input, command);
                case FIND -> findTasks(input);
                case UNKNOWN -> throw new PavanmaxxerException(
                        "I'm sorry, but I don't know what that means :-(");
                default -> throw new PavanmaxxerException(
                        "I'm sorry, but I don't know what that means :-(");
            };
        } catch (PavanmaxxerException exception) {
            return "OOPS!!! " + exception.getMessage();
        }
    }

    private String updateMark(String input, boolean isDone)
            throws PavanmaxxerException {
        String commandWord = isDone ? "mark" : "unmark";
        int index = Parser.parseTaskIndex(input, commandWord, tasks.size());
        Task task = isDone ? tasks.mark(index) : tasks.unmark(index);
        storage.save(tasks);
        String introduction = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return introduction + "\n  " + task;
    }

    private String deleteTask(String input) throws PavanmaxxerException {
        int index = Parser.parseTaskIndex(input, "delete", tasks.size());
        Task task = tasks.delete(index);
        storage.save(tasks);
        return "Noted. I've removed this task:\n  " + task
                + "\n" + formatTaskCount(tasks.size());
    }

    private String addTask(String input, Command command)
            throws PavanmaxxerException {
        Task task = Parser.parseTask(input, command);
        boolean isDuplicate = tasks.containsSameDetails(task);
        tasks.add(task);
        storage.save(tasks);
        String introduction = isDuplicate
                ? "This task already exists in your list, but I've added it again:"
                : "Got it. I've added this task:";
        return introduction + "\n  " + task
                + "\n" + formatTaskCount(tasks.size());
    }

    private String findTasks(String input) throws PavanmaxxerException {
        String keyword = Parser.parseFindKeyword(input);
        List<Task> matchingTasks = tasks.find(keyword.split("\\s+"));
        if (matchingTasks.isEmpty()) {
            return "No tasks matched \"" + keyword + "\".";
        }
        return "Here are the matching tasks in your list:\n"
                + formatTasks(matchingTasks);
    }

    private String listTasks() {
        if (tasks.size() == 0) {
            return "Your task list is empty.";
        }
        return formatTasks(tasks.asList());
    }

    private static String formatTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + noun + " in the list.";
    }

    private static String formatTasks(List<Task> tasks) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                result.append('\n');
            }
            result.append(i + 1).append('.').append(tasks.get(i));
        }
        return result.toString();
    }

    /**
     * Reads and executes commands until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        boolean isRunning = true;
        while (isRunning && ui.hasNextCommand()) {
            String input = ui.readCommand();
            Command command = Parser.parseCommand(input);
            if (command == Command.BYE) {
                isRunning = false;
            } else {
                ui.showResponse(getResponse(input));
            }
        }
        ui.showGoodbye();
    }

    /**
     * Launches Pavanmaxxer using its default relative data path.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Pavanmaxxer(Path.of("data", "pavanmaxxer.txt")).run();
    }
}
