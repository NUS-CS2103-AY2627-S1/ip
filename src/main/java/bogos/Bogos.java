package bogos;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts the Bogos task-list application and processes user commands.
 */
public class Bogos {
    private static final String WELCOME_MESSAGE = "Blessings! Bogos beckons. Bring Bogos business? :]";
    private static final List<String> HELP_MESSAGE = List.of(
            "Bogos' basic business:",
            "",
            "  todo DESCRIPTION [#TAG]...",
            "  deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...",
            "  event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
            "",
            "  list",
            "  find KEYWORD",
            "  mark NUMBER",
            "  unmark NUMBER",
            "  delete NUMBER",
            "  help",
            "  bye",
            "",
            "Browse beyond basics: https://oofky.github.io/ip/");

    private final Ui userInterface;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a Bogos application backed by the default data file.
     */
    public Bogos() {
        this(new Ui());
    }

    /**
     * Creates a Bogos application that reports storage problems through the given UI.
     *
     * @param userInterface UI used for storage messages.
     */
    public Bogos(Ui userInterface) {
        this.userInterface = userInterface;
        parser = new Parser();
        storage = new Storage(Paths.get("data", "bogos.txt"));
        tasks = new TaskList(storage.loadTasks(userInterface));
    }

    /**
     * Runs the application command loop.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui consoleUi = new Ui();
        Bogos bogos = new Bogos(consoleUi);
        consoleUi.showWelcome();

        while (consoleUi.hasNextCommand()) {
            String command = normalizeCommand(consoleUi.readCommand());
            consoleUi.showDivider();

            if (bogos.isExitCommand(command)) {
                consoleUi.showGoodbye();
                consoleUi.showDivider();
                break;
            }

            for (String responseLine : bogos.processCommand(command)) {
                consoleUi.showMessage(responseLine);
            }
            consoleUi.showDivider();
        }
    }

    /**
     * Returns Bogos's greeting for the graphical user interface.
     *
     * @return Bogos's greeting.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Prints the standard Bogos banner and greeting to the console.
     */
    public void showConsoleWelcome() {
        userInterface.showWelcome();
    }

    /**
     * Returns whether the command ends the application.
     *
     * @param command Command entered by the user.
     * @return True if the command is {@code bye}.
     */
    public boolean isExitCommand(String command) {
        return normalizeCommand(command).equals("bye");
    }

    /**
     * Processes a GUI command and returns Bogos's reply for display in a dialog box.
     *
     * @param command Command entered by the user.
     * @return Response text, possibly spanning multiple lines.
     */
    public String getResponse(String command) {
        String normalizedCommand = normalizeCommand(command);
        List<String> responseLines;
        userInterface.showDivider();

        if (isExitCommand(normalizedCommand)) {
            responseLines = List.of("Bye bye! :]");
            userInterface.showGoodbye();
        } else {
            responseLines = processCommand(normalizedCommand);
            for (String responseLine : responseLines) {
                userInterface.showMessage(responseLine);
            }
        }
        userInterface.showDivider();
        return String.join(System.lineSeparator(), responseLines);
    }

    /**
     * Removes surrounding whitespace and reduces each internal whitespace run to one space.
     *
     * @param command Raw command entered by the user.
     * @return Command in the format used by command parsing.
     */
    static String normalizeCommand(String command) {
        return command.trim().replaceAll("\\s+", " ");
    }

    /**
     * Processes one non-exit command and returns the lines that should be shown to the user.
     *
     * @param command Command to process.
     * @return Response lines for the command.
     */
    private List<String> processCommand(String command) {
        List<String> responseLines = new ArrayList<>();

        try {
            boolean hasTasksChanged = executeCommand(command, responseLines);
            if (hasTasksChanged) {
                storage.saveTasks(tasks.asList(), userInterface);
            }
        } catch (BogosException e) {
            responseLines.add(e.getMessage());
        }
        return responseLines;
    }

    /**
     * Executes a command and reports whether it changes the task list.
     *
     * @param command Command to execute.
     * @param responseLines Lines to be shown to the user.
     * @return Whether the command changed the task list.
     * @throws BogosException If the command or its arguments are invalid.
     */
    private boolean executeCommand(String command, List<String> responseLines) throws BogosException {
        if (command.contains("|")) {
            throw new BogosException("Bah! Bpipes ('|') banned!");
        } else if (command.equals("list")) {
            handleListCommand(responseLines);
            return false;
        } else if (command.equals("help")) {
            responseLines.addAll(HELP_MESSAGE);
            return false;
        } else if (command.equals("find") || command.startsWith("find ")) {
            handleFindCommand(command, responseLines);
            return false;
        } else if (command.equals("mark") || command.startsWith("mark ")
                || command.equals("unmark") || command.startsWith("unmark ")) {
            handleMarkCommand(command, responseLines);
            return true;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            handleDeleteCommand(command, responseLines);
            return true;
        } else if (parser.isTaskCommand(command)) {
            addTask(parser.parseTask(command), responseLines);
            return true;
        } else {
            throw new BogosException("Bwhat? Best browse: help");
        }
    }

    /**
     * Adds the numbered task list to the response.
     *
     * @param responseLines Lines to be shown to the user.
     * @throws BogosException If there are no tasks to list.
     */
    private void handleListCommand(List<String> responseLines) throws BogosException {
        if (tasks.isEmpty()) {
            throw new BogosException("But board be blank...");
        }

        responseLines.add("Behold bulleted board:");
        for (int i = 1; i <= tasks.size(); i++) {
            responseLines.add(i + "." + tasks.getTask(i));
        }
    }

    /**
     * Marks or unmarks the task specified by a mark-related command and displays the result.
     *
     * @param command Mark or unmark command to process.
     * @throws BogosException If the task number is invalid or its status is unchanged.
     */
    private void handleMarkCommand(String command, List<String> responseLines) throws BogosException {
        assert command.equals("mark") || command.startsWith("mark ")
                || command.equals("unmark") || command.startsWith("unmark ")
                : "Only mark and unmark commands are routed to the mark handler.";
        boolean isMarkCommand = command.equals("mark") || command.startsWith("mark ");
        String taskNumberText = command.substring(isMarkCommand ? "mark".length() : "unmark".length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new BogosException(isMarkCommand ? "Bwhere base-ten? Be: mark NUMBER"
                    : "Bwhere base-ten? Be: unmark NUMBER");
        }
        Task task = tasks.getTask(parser.parseTaskNumber(taskNumberText));

        if (task.isDone() == isMarkCommand) {
            throw new BogosException("Bro, box basically behaved beforehand.");
        }

        if (isMarkCommand) {
            task.markAsDone();
            assert task.isDone() : "Marking a task must set its completion state.";
            responseLines.add("Bravo! Bogos boxed bullet:");
        } else {
            task.markAsNotDone();
            assert !task.isDone() : "Unmarking a task must clear its completion state.";
            responseLines.add("Bet! Bogos blanked box:");
        }
        responseLines.add("  " + task);
    }

    /**
     * Deletes the task specified by a delete command and displays the result.
     *
     * @param command Delete command to process.
     * @throws BogosException If the task number is invalid.
     */
    private void handleDeleteCommand(String command, List<String> responseLines) throws BogosException {
        assert command.equals("delete") || command.startsWith("delete ")
                : "Only delete commands are routed to the delete handler.";
        String taskNumberText = command.substring("delete".length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new BogosException("Bwhere base-ten? Be: delete NUMBER");
        }
        Task task = tasks.removeTask(parser.parseTaskNumber(taskNumberText));
        responseLines.add("Brilliant! Bye bye bullet:");
        responseLines.add("  " + task);
        responseLines.add(Integer.toString(tasks.size()) + " bullet(s) being.");
    }

    /**
     * Finds tasks with descriptions containing the keyword in a find command.
     *
     * @param command Find command to process.
     * @throws BogosException If the keyword is empty.
     */
    private void handleFindCommand(String command, List<String> responseLines) throws BogosException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new BogosException("Bwhere buzzword? Be: find KEYWORD");
        }

        List<Task> matchingTasks = tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            responseLines.add("Bogos' beams bounced back blank. :[");
            return;
        }

        responseLines.add("Bogos brings befitting bullets:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            responseLines.add((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Adds a task to the list and displays the newly added task.
     *
     * @param newTask Task to add.
     */
    private void addTask(Task newTask, List<String> responseLines) throws BogosException {
        tasks.addTask(newTask);
        responseLines.add("Boom! Bullet born: ");
        responseLines.add("  " + newTask);
        responseLines.add(Integer.toString(tasks.size()) + " bullet(s) being.");
    }

}
