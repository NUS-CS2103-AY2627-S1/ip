package baby;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Runs the Baby command-line task manager.
 */
public class Baby {
    private Ui ui;
    private TaskList tasks;
    private boolean isExit;

    /**
     * Creates a Baby chatbot for non-console use, such as the JavaFX GUI.
     */
    public Baby() {
        this(new Ui(false));
    }

    /**
     * Creates a Baby chatbot using the given UI.
     *
     * @param ui The UI used to format or print responses.
     */
    public Baby(Ui ui) {
        this.ui = ui;
        tasks = new TaskList(Storage.loadTasks());
        isExit = false;
    }

    /**
     * Starts the command loop for the task manager.
     *
     * @param args Command-line arguments that are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Baby baby = new Baby(ui);

        ui.showWelcome();

        while (true) {
            if (!ui.hasNextLine()) {
                break;
            }

            String input = ui.readCommand();

            baby.getResponse(input);
            if (baby.isExit()) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Processes one user command and returns Baby's response.
     *
     * @param input The user command.
     * @return The response that should be shown to the user.
     */
    public String getResponse(String input) {
        input = Parser.normalizeInput(input);

        if (input.equals("bye")) {
            ui.showGoodbye();
            isExit = true;
        } else if (input.equals("list")) {
            ui.printTaskList(tasks.getTasks());
        } else if (input.equals(Command.MARK.getCommandWord())
                || input.startsWith(Command.MARK.getCommandWord() + " ")) {
            markTask(input, tasks, ui);
        } else if (input.equals(Command.UNMARK.getCommandWord())
                || input.startsWith(Command.UNMARK.getCommandWord() + " ")) {
            unmarkTask(input, tasks, ui);
        } else if (input.equals(Command.DELETE.getCommandWord())
                || input.startsWith(Command.DELETE.getCommandWord() + " ")) {
            deleteTask(input, tasks, ui);
        } else if (input.equals(Command.TODO.getCommandWord())
                || input.startsWith(Command.TODO.getCommandWord() + " ")) {
            addTodo(input);
        } else if (input.equals(Command.DEADLINE.getCommandWord())
                || input.startsWith(Command.DEADLINE.getCommandWord() + " ")) {
            addDeadline(input);
        } else if (input.equals(Command.EVENT.getCommandWord())
                || input.startsWith(Command.EVENT.getCommandWord() + " ")) {
            addEvent(input);
        } else if (input.equals(Command.FIND.getCommandWord())
                || input.startsWith(Command.FIND.getCommandWord() + " ")) {
            findTasks(input, tasks, ui);
        } else if (input.isEmpty()) {
            ui.printError("Oh snow! Please enter a command.");
        } else {
            ui.printError("Oh snow! I don't recognise that command. Try todo, deadline, event, list, "
                    + "mark, unmark, delete, find, or bye.");
        }

        return ui.getLastResponse();
    }

    /**
     * Returns Baby's welcome message for interfaces that do not start the console loop.
     *
     * @return The formatted welcome response.
     */
    public String getWelcomeResponse() {
        ui.showWelcome();
        return ui.getLastResponse();
    }

    /**
     * Returns whether the user has asked Baby to exit.
     *
     * @return True if the latest command was bye.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Adds a todo task if the user provided a description.
     *
     * @param input The full user command.
     */
    private void addTodo(String input) {
        String description = Parser.getTodoDescription(input);
        if (description.isEmpty()) {
            ui.printError("Oh snow! A todo needs a description. Try: todo read book");
            return;
        }

        Task task = new Todo(description);
        tasks.add(task);
        if (!saveTasks(tasks, ui)) {
            tasks.remove(tasks.size() - 1);
            return;
        }
        ui.printTaskAdded(task, tasks.size());
    }

    /**
     * Adds a deadline task if the user command is valid.
     *
     * @param input The full user command.
     */
    private void addDeadline(String input) {
        Deadline deadline = Parser.createDeadline(input, ui);
        if (deadline == null) {
            return;
        }

        tasks.add(deadline);
        if (!saveTasks(tasks, ui)) {
            tasks.remove(tasks.size() - 1);
            return;
        }
        ui.printTaskAdded(deadline, tasks.size());
    }

    /**
     * Adds an event task if the user command is valid.
     *
     * @param input The full user command.
     */
    private void addEvent(String input) {
        Event event = Parser.createEvent(input, ui);
        if (event == null) {
            return;
        }

        tasks.add(event);
        if (!saveTasks(tasks, ui)) {
            tasks.remove(tasks.size() - 1);
            return;
        }
        ui.printTaskAdded(event, tasks.size());
    }

    /**
     * Marks a task as done after checking that the task number is valid.
     *
     * @param input The full user command.
     * @param tasks The current task list.
     */
    private static void markTask(String input, TaskList tasks, Ui ui) {
        int index = Parser.getTaskIndex(input, Command.MARK.getCommandWord(), tasks.size(), ui);
        if (index == -1) {
            return;
        }

        assert index >= 0 && index < tasks.size()
                : "Validated task index sould be within task list bounds";

        Task task = tasks.get(index);
        task.markAsDone();
        if (!saveTasks(tasks, ui)) {
            task.markAsNotDone();
            return;
        }
        ui.printResponse("Splendid! Task " + (index + 1) + " is now marked as done:", " " + task);
    }

    /**
     * Marks a task as not done after checking that the task number is valid.
     *
     * @param input The full user command.
     * @param tasks The current task list.
     */
    private static void unmarkTask(String input, TaskList tasks, Ui ui) {
        int index = Parser.getTaskIndex(input, Command.UNMARK.getCommandWord(), tasks.size(), ui);
        if (index == -1) {
            return;
        }

        assert index >= 0 && index < tasks.size()
                : "Validated task index should be within task list bounds";

        Task task = tasks.get(index);
        task.markAsNotDone();
        if (!saveTasks(tasks, ui)) {
            task.markAsDone();
            return;
        }
        ui.printResponse(
                "Certainly, Your Highness. Task " + (index + 1) + " is marked as not done again:",
                " " + task);
    }

    /**
     * Deletes a task after checking that the task number is valid.
     *
     * @param input The full user command.
     * @param tasks The current task list.
     */
    private static void deleteTask(String input, TaskList tasks, Ui ui) {
        int index = Parser.getTaskIndex(input, Command.DELETE.getCommandWord(), tasks.size(), ui);
        if (index == -1) {
            return;
        }

        assert index >= 0 && index < tasks.size()
                : "Validated task index should be within task list bounds";

        Task removedTask = tasks.remove(index);
        if (!saveTasks(tasks, ui)) {
            tasks.getTasks().add(index, removedTask);
            return;
        }
        ui.printResponse(
                "It's been removed, Your Highness.",
                "Deleted: " + removedTask,
                "You now have " + tasks.size() + " " + (tasks.size() == 1 ? "task" : "tasks")
                        + " in your list.");
    }

    /**
     * Finds tasks whose description contains the given keyword.
     *
     * @param input The full user command.
     * @param tasks The current task list.
     * @param ui The UI used to show results and errors.
     */
    private static void findTasks(String input, TaskList tasks, Ui ui) {
        String keyword = input.substring(Command.FIND.getCommandWord().length()).trim();

        if (keyword.isEmpty()) {
            ui.printError("Oh snow! Please give me a keyword to find.");
            return;
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matchingTasks = tasks.getTasks().stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));

        ui.printMatchingTasks(matchingTasks);
    }

    private static boolean saveTasks(TaskList tasks, Ui ui) {
        if (Storage.saveTasks(tasks.getTasks())) {
            return true;
        }

        ui.printError("Oh snow! I couldn't save your tasks to the data file.");
        return false;
    }
}
