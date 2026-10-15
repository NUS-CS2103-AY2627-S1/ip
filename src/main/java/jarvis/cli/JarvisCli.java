package jarvis.cli;

import java.util.List;
import java.util.Scanner;

import jarvis.backend.JarvisController;
import jarvis.backend.Parser;
import jarvis.classes.Task;
import jarvis.exceptions.InvalidTaskNumberException;
import jarvis.exceptions.JarvisException;

/**
 * Provides the command-line interface for the Jarvis chatbot.
 *
 * This class is responsible for reading commands from the user,
 * processing the commands, delegating task operations to
 * {@link JarvisController}, and displaying the appropriate responses
 * through {@link Ui}.
 */
public class JarvisCli {
    /** Handles input and output for the command-line interface. */
    private static final Ui ui = new Ui();

    /** Separates individual responses displayed in the CLI. */
    private static final String BORDER_LINE =
            "---------------------------------------------------------------------\n";

    /** Controller responsible for managing Jarvis tasks. */
    private final JarvisController jarvisController;

    /**
     * Creates a new CLI instance with a {@link JarvisController}
     * for managing tasks.
     */
    public JarvisCli() {
        jarvisController = new JarvisController();
    }

    /**
     * Entry point for the Jarvis command-line application.
     *
     * @param args command-line arguments, which are not used by Jarvis
     */
    public static void main(String[] args) {
        JarvisCli cli = new JarvisCli();
        cli.runCli();
    }

    /**
     * Starts the Jarvis CLI and continuously reads user commands
     * until the user exits the application.
     *
     * The method displays the welcome message, reads commands from
     * standard input, and delegates each command to
     * {@link #processCommand(String)}. The scanner is closed when the
     * CLI terminates.
     */
    public void runCli() {
        Scanner scanner = new Scanner(System.in);

        ui.showWelcome();

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Goodbye! Your tasks have been saved.");
                break;
            }

            processCommand(input);
        }

        scanner.close();
    }

    /**
     * Processes a user command and performs the corresponding action.
     *
     * Supported commands include creating, listing, finding, marking,
     * unmarking, and deleting tasks. Commands are delegated to the
     * {@link JarvisController} where appropriate.
     *
     * @param userInput command entered by the user
     */
    public void processCommand(String userInput) {

        String byeCommand = "bye";
        String deadlineCommand = "deadline";
        String deleteCommand = "delete";
        String eventCommand = "event";
        String findCommand = "find";
        String funnyCommand = "Jarvis, clip that";
        String listCommand = "list";
        String markCommand = "mark";
        String todoCommand = "todo";
        String unmarkCommand = "unmark";

        try {
            if (userInput.equals(funnyCommand)) {
                ui.showClipThat();
            } else if (userInput.equals(listCommand)) {
                printAllTasks();
            } else if (userInput.startsWith(findCommand)) {
                printFindResults(userInput);
            } else if (userInput.startsWith(markCommand)) {
                jarvisController.markTaskAs(
                        userInput,
                        Task.CompletionStatus.DONE);
                printTaskStatus(Parser.parseTaskNumber(userInput));
            } else if (userInput.startsWith(unmarkCommand)) {
                jarvisController.markTaskAs(
                        userInput,
                        Task.CompletionStatus.UNDONE);
                printTaskStatus(Parser.parseTaskNumber(userInput));
            } else if (userInput.startsWith(todoCommand)) {
                jarvisController.createToDoTask(userInput);
                printLastAddedTask();
            } else if (userInput.startsWith(deadlineCommand)) {
                jarvisController.createDeadlineTask(userInput);
                printLastAddedTask();
            } else if (userInput.startsWith(eventCommand)) {
                jarvisController.createEventTask(userInput);
                printLastAddedTask();
            } else if (userInput.startsWith(deleteCommand)) {
                deleteTask(userInput);
            } else if (userInput.equals(byeCommand)) {
                ui.sayGoodbye();
            } else {
                ui.showUnknownCommand();
            }
        } catch (JarvisException error) {
            ui.showError(error.getMessage());
            ui.showError(BORDER_LINE);
        }
    }

    /**
     * Displays a confirmation message for the most recently added task.
     *
     * The message includes the newly added task and the current number
     * of tasks in the task list.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    private void printLastAddedTask() throws InvalidTaskNumberException {
        Task task = jarvisController.getTask(
                jarvisController.size() - 1);

        System.out.printf(
                "Very well Sir/Ma' am, I have added the following task below:%n"
                        + "%s%n"
                        + "Please do note Sir/Ma' am, that you now currently "
                        + "have %d task(s) awaiting you%n"
                        + "%s",
                task,
                jarvisController.size(),
                BORDER_LINE);
    }

    /**
     * Displays all tasks currently stored by Jarvis.
     *
     * If the task list is empty, an appropriate message is displayed.
     * Otherwise, each task is displayed with its corresponding
     * one-based task number.
     */
    private void printAllTasks() {
        List<Task> tasks = jarvisController.getTasks();

        if (tasks.isEmpty()) {
            System.out.println("You have no tasks in your list.");
            System.out.println(BORDER_LINE);
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, tasks.get(i));
        }

        System.out.println(BORDER_LINE);
    }

    /**
     * Deletes the task specified by the user's command and displays
     * a confirmation message.
     *
     * The task is retrieved before deletion so that its details can
     * be displayed after it has been removed from the task list.
     *
     * @param input command containing the one-based task number to delete
     * @throws JarvisException if the task number is invalid or cannot
     *         be parsed
     */
    private void deleteTask(String input) throws JarvisException {
        int taskNumber = Parser.parseTaskNumber(input);
        Task task = jarvisController.getTask(taskNumber - 1);

        jarvisController.deleteTask(input);

        System.out.printf(
                "Very good Sir/Ma' am, I have removed the following task "
                        + "from your list of tasks-to-do:%n"
                        + "%s%n"
                        + "Please do note Sir/Ma' am, now you have %d "
                        + "task(s) awaiting you%n"
                        + "%s",
                task,
                jarvisController.size(),
                BORDER_LINE);
    }

    /**
     * Finds and displays tasks matching the keyword in the user's command.
     *
     * If no tasks match the keyword, an appropriate message is displayed.
     * Otherwise, each matching task is displayed with its corresponding
     * one-based task number.
     *
     * @param input command containing the keyword to search for
     * @throws JarvisException if the command cannot be parsed
     */
    private void printFindResults(String input) throws JarvisException {
        List<Task> tasks = jarvisController.filterTasks(input);

        if (tasks.isEmpty()) {
            System.out.println("No matching tasks found.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, tasks.get(i));
            }
        }

        System.out.println(BORDER_LINE);
    }

    /**
     * Prints the updated status of the requested task to the console.
     *
     * @param taskNumber the one-based number of the task whose status is to be printed
     * @throws InvalidTaskNumberException if the specified task number is invalid
     */
    private void printTaskStatus(int taskNumber) throws InvalidTaskNumberException {
        int taskIndex = taskNumber - 1;
        System.out.println("\nVery well Sir/Ma' am, I have marked the following task as: \n"
                + jarvisController.getTask(taskIndex) + "\n"
                + BORDER_LINE);
    }
}
