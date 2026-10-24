package clara;

import java.util.List;

import clara.exception.ClaraException;
import clara.parser.Parser;
import clara.storage.TodoFileHandler;
import clara.task.Priority;
import clara.task.Task;
import clara.task.TaskList;

// NOTE: AI-assisted OOP refactoring and extraction of responsibilites.
// See CITATIONS.md [C-009].
// AI-assisted GUI command-response integration. See CITATIONS.md [C-013].
// NOTE: AI-assisted GUI response-state support. See CITATIONS.md [C-019].
// AI-assisted persistence-failure rollback. See CITATIONS.md [C-017].

/**
 * Represents the main application for Clara, a simple task management chatbot.
 */
public class Clara {
    private static final String CLARA_HEADER = "[Clara] ";
    private static final String INPUT_ERROR_PREFIX = "I couldn't make sense of that:";

    private final TaskList tasks;
    private final String startupMessage;
    private boolean shouldExit;

    /**
     * Creates Clara and restores any previously saved tasks.
     */
    public Clara() {
        tasks = new TaskList();
        startupMessage = loadTasks();
    }

    /**
     * Starts the Clara application, loads saved tasks, and processes user commands.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        Clara clara = new Clara();
        System.out.println("\n" + CLARA_HEADER + clara.getStartupMessage());

        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            while (true) {
                System.out.print(">> ");
                String input = scanner.nextLine();
                String response = clara.getResponse(input);
                if (!response.isEmpty()) {
                    System.out.println("\n" + CLARA_HEADER + response);
                }
                if (clara.shouldExit()) {
                    return;
                }
            }
        }
    }

    /**
     * Returns Clara's greeting and task-loading result.
     *
     * @return the message to display when Clara starts
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Indicates whether a validated {@code bye} command has requested application shutdown.
     *
     * @return {@code true} if Clara should exit
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Processes one user command and returns Clara's reply.
     *
     * @param input the user command
     * @return Clara's response to the command
     */
    public String getResponse(String input) {
        final String[] userInput = input.trim().split("\\s+", 2);
        if (userInput[0].isEmpty()) {
            return "";
        }

        try {
            String command = userInput[0];
            String arguments = userInput.length == 2 ? userInput[1] : "";
            return switch (command) {
                case "bye" -> handleBye(command, arguments);
                case "list" -> handleList(command, arguments);
                case "mark" -> markTask(command, arguments);
                case "unmark" -> unmarkTask(command, arguments);
                case "delete" -> deleteTask(command, arguments);
                // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
                case "priority" -> changeTaskPriority(command, arguments);
                case "todo" -> addTask(Parser.parseTodo(arguments));
                case "deadline" -> addTask(Parser.parseDeadline(arguments));
                case "event" -> addTask(Parser.parseEvent(arguments));
                case "find" -> formatFindResults(Parser.parseFindQuery(arguments));
                default -> throw new ClaraException(command);
            };
        } catch (ClaraException ex) {
            return INPUT_ERROR_PREFIX + "\n" + ex.getMessage() + "\nPlease try again.";
        }
    }

    /**
     * Handles the {@code bye} command.
     *
     * @param command the command name
     * @param arguments the supplied command arguments
     * @return the farewell response
     * @throws ClaraException if arguments were supplied
     */
    private String handleBye(String command, String arguments) throws ClaraException {
        Parser.requireNoArguments(command, arguments);
        shouldExit = true;
        return "All noted. See you next time.";
    }

    /**
     * Handles the {@code list} command.
     *
     * @param command the command name
     * @param arguments the supplied command arguments
     * @return the formatted task list
     * @throws ClaraException if arguments were supplied
     */
    private String handleList(String command, String arguments) throws ClaraException {
        Parser.requireNoArguments(command, arguments);
        return formatTaskList();
    }

    /**
     * Marks a task as done and restores its original state if saving fails.
     *
     * @param command the command name
     * @param arguments the supplied task index
     * @return the mark confirmation
     * @throws ClaraException if the task index is invalid or saving fails
     */
    private String markTask(String command, String arguments) throws ClaraException {
        int taskIndex = Parser.parseTaskIndex(command, arguments);
        Task task = tasks.markTask(taskIndex);
        try {
            TodoFileHandler.flushTasksToDisk(tasks.getTasks());
        } catch (ClaraException ex) {
            task.setDone(false);
            throw ex;
        }
        return "I marked this task:\n" + task;
    }

    /**
     * Marks a task as not done and restores its original state if saving fails.
     *
     * @param command the command name
     * @param arguments the supplied task index
     * @return the unmark confirmation
     * @throws ClaraException if the task index is invalid or saving fails
     */
    private String unmarkTask(String command, String arguments) throws ClaraException {
        int taskIndex = Parser.parseTaskIndex(command, arguments);
        Task task = tasks.unmarkTask(taskIndex);
        try {
            TodoFileHandler.flushTasksToDisk(tasks.getTasks());
        } catch (ClaraException ex) {
            task.setDone(true);
            throw ex;
        }
        return "Back to being unmarked:\n" + task;
    }

    /**
     * Deletes a task and restores it at its original position if saving fails.
     *
     * @param command the command name
     * @param arguments the supplied task index
     * @return the deletion confirmation
     * @throws ClaraException if the task index is invalid or saving fails
     */
    private String deleteTask(String command, String arguments) throws ClaraException {
        int taskIndex = Parser.parseTaskIndex(command, arguments);
        Task task = tasks.deleteTask(taskIndex);
        try {
            TodoFileHandler.flushTasksToDisk(tasks.getTasks());
        } catch (ClaraException ex) {
            tasks.getTasks().add(taskIndex - 1, task);
            throw ex;
        }
        return formatTaskDeleted(task, taskIndex);
    }

    /**
     * Changes a task's priority and saves the updated task list.
     *
     * @param command the command name
     * @param arguments the supplied task index and priority
     * @return the priority-change confirmation
     * @throws ClaraException if the arguments are invalid or saving fails
     */
    private String changeTaskPriority(String command, String arguments) throws ClaraException {
        String[] priorityArguments = arguments.split("\\s+", 2);
        if (priorityArguments.length != 2) {
            throw new ClaraException("Use: priority <task number> <low|medium|high>.");
        }
        int taskIndex = Parser.parseTaskIndex(command, priorityArguments[0]);
        Priority priority = Parser.parsePriority(priorityArguments[1]);
        Task task = tasks.getTask(taskIndex);
        task.setPriority(priority);
        TodoFileHandler.flushTasksToDisk(tasks.getTasks());
        return "Changed priority of task " + taskIndex + ":\n" + task;
    }

    /**
     * Indicates whether a response reports an invalid user command.
     *
     * @param response the response to inspect
     * @return whether the response is an input error
     */
    public boolean isInputError(String response) {
        return response.startsWith(INPUT_ERROR_PREFIX);
    }

    /**
     * Loads saved tasks and produces the corresponding startup message.
     *
     * @return the startup message
     */
    private String loadTasks() {
        try {
            TodoFileHandler.loadTasksFromDisk(tasks.getTasks());
            return "Hello, I'm Clara. What can I help you keep track of?\n\n"
                    + "Restoring your tasks...\nAll set.";
        } catch (ClaraException ex) {
            return "Hello, I'm Clara. What can I help you keep track of?\n\n"
                    + "I couldn't restore your saved tasks:\n"
                    + ex.getMessage()
                    + "\nWe'll start with a clean slate.";
        }
    }

    /**
     * Formats the current task list.
     *
     * @return the formatted task list response
     */
    private String formatTaskList() {
        if (tasks.isEmpty()) {
            return "There are no tasks... yay?";
        }

        StringBuilder response = new StringBuilder("Here is what you have on your list:");
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(tasks.getTasks().get(i));
        }
        return response.toString();
    }

    /**
     * Saves an added task and formats its confirmation.
     *
     * @param task the task to add
     * @return the formatted confirmation response
     * @throws ClaraException if the task cannot be saved
     */
    private String addTask(Task task) throws ClaraException {
        tasks.addTask(task);
        try {
            TodoFileHandler.flushTasksToDisk(tasks.getTasks());
        } catch (ClaraException ex) {
            tasks.getTasks().remove(task);
            throw ex;
        }
        return "I've added this to your list:\n" + task + " (task #" + tasks.size() + ")";
    }

    /**
     * Formats the confirmation after a task is deleted.
     *
     * @param deletedTask the task that was deleted
     * @param taskIndex   the 1-based index of the deleted task
     * @return the formatted deletion response
     */
    private String formatTaskDeleted(Task deletedTask, int taskIndex) {
        return "I've removed this from your list:\n"
                + deletedTask
                + " ("
                + tasks.size()
                + " task"
                + (tasks.size() == 1 ? " " : "s ")
                + "remain)";
    }

    /**
     * Finds matching tasks and formats the results.
     *
     * @param query the text to search for
     * @return the formatted search response
     */
    private String formatFindResults(String query) {
        List<Integer> matchingIndices = tasks.findMatchingIndices(query);
        StringBuilder response = new StringBuilder("I found these tasks matching: ").append(query);
        for (int taskIndex : matchingIndices) {
            response.append("\n")
                    .append(taskIndex + 1)
                    .append(". ")
                    .append(tasks.getTasks().get(taskIndex));
        }
        return response.append("\n")
                .append(matchingIndices.size())
                .append(" task")
                .append(matchingIndices.size() == 1 ? " " : "s ")
                .append("found.")
                .toString();
    }
}
