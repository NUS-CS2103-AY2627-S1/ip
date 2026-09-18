package joebiden;

import joebiden.exception.JoeBidenException;
import joebiden.parser.Parser;
import joebiden.storage.Storage;
import joebiden.task.Task;
import joebiden.task.TaskList;
import joebiden.task.Todo;
import joebiden.ui.Ui;

/**
 * Runs the Joe Biden chatbot.
 */
public class JoeBiden {

    public static final String NAME = "Joe Biden";

    private final Ui ui;
    private TaskList tasks;
    private String startupError;

    /**
     * Creates a Joe Biden chatbot and loads saved tasks.
     */
    public JoeBiden() {
        ui = new Ui();

        try {
            tasks = new TaskList(Storage.loadList());
        } catch (JoeBidenException e) {
            tasks = new TaskList();
            startupError = "ERROR! " + e.getMessage();
        }
    }

    /**
     * Starts the text-based version of the chatbot.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        JoeBiden joeBiden = new JoeBiden();
        joeBiden.runCli();
    }

    /**
     * Runs the command-line interface until the user exits.
     */
    public void runCli() {
        if (startupError != null) {
            ui.showMessage(startupError);
        }

        System.out.println(getWelcomeBanner());

        while (true) {
            String input = ui.readInput();
            String response = getResponse(input);

            ui.showMessage(response);

            try {
                String command = Parser.getCommand(input);
                String arguments = Parser.getArguments(input);

                if (command.equals("bye")) {
                    Parser.validateNoArguments(command, arguments);
                    ui.close();
                    return;
                }
            } catch (JoeBidenException e) {
                // Error has already been handled by getResponse().
            }
        }
    }

    /**
     * Generates a response to the user's input.
     *
     * @param input User input.
     * @return Response to the user.
     */
    public String getResponse(String input) {
        try {
            String command = Parser.getCommand(input);
            String arguments = Parser.getArguments(input);

            switch (command) {
                case "bye":
                    Parser.validateNoArguments(command, arguments);
                    return getGoodbyeBanner();

                case "list":
                    Parser.validateNoArguments(command, arguments);
                    return tasks.listTasks();

                case "mark": {
                    int number = Parser.getTaskNumber(arguments);
                    String response = tasks.markTask(number);

                    saveTasks();
                    return response;
                }

                case "unmark": {
                    int number = Parser.getTaskNumber(arguments);
                    String response = tasks.unmarkTask(number);

                    saveTasks();
                    return response;
                }

                case "delete": {
                    int number = Parser.getTaskNumber(arguments);
                    Task removedTask = tasks.deleteTask(number);

                    saveTasks();

                    return "Noted. I've removed this task:\n"
                            + removedTask
                            + "\nNow you have "
                            + tasks.size()
                            + " tasks in the list.";
                }

                case "todo": {
                    String description = Parser.parseDescription(arguments);
                    return addTask(new Todo(description));
                }
                case "deadline":
                    return addTask(Parser.parseDeadline(arguments));

                case "event":
                    return addTask(Parser.parseEvent(arguments));
                case "find": {
                    String keyword =
                            Parser.parseFindKeyword(arguments);

                    return tasks.findTasks(keyword);
                }

                default:
                    throw new JoeBidenException(
                            "Invalid input try again"
                    );
            }

        } catch (JoeBidenException e) {
            return "ERROR! " + e.getMessage();
        }
    }
    /**
     * Returns the welcome message displayed when the chatbot starts.
     *
     * @return Welcome message.
     */
    public static String getWelcomeBanner() {
        return "Hello! I'm " + NAME + ".\n"
                + "What can I do for you?";
    }

    /**
     * Returns the goodbye message displayed when the chatbot exits.
     *
     * @return Goodbye message.
     */
    public static String getGoodbyeBanner() {
        return "Bye. Hope to see you again soon!\n";
    }
    /**
     * Saves the current task list to storage.
     *
     * @throws JoeBidenException If the task list cannot be saved.
     */
    private void saveTasks() throws JoeBidenException {
        Storage.saveList(tasks.getList());
    }

    /**
     * Adds a task and returns the confirmation message.
     *
     * @param task Task to add.
     * @return Confirmation message.
     * @throws JoeBidenException If the task list cannot be saved.
     */
    private String addTask(Task task) throws JoeBidenException {
        tasks.addTask(task);
        saveTasks();

        return "Got it. I've added this task:\n"
                + task
                + "\nNow you have "
                + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Returns reminders for tasks and events happening tomorrow.
     *
     * @return Tomorrow's reminders.
     */
    public String getTomorrowReminders() {
        return tasks.getTomorrowReminders();
    }
}

