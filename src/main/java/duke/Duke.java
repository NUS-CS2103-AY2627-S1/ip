package duke;

import java.io.IOException;

/**
 * Entry point and main driver of the Ducke chatbot.
 * Wires together the Ui, Storage, and TaskList, and produces responses for
 * both the command-line and graphical interfaces.
 */
public class Duke {
    private static final String DEFAULT_SAVE_PATH = "data/ducke.txt";

    private Ui ui;
    private Storage storage;
    private TaskList tasks;
    private boolean isExit = false;
    private int invalidCount = 0;

    /**
     * Creates a Duke that loads its tasks from the given save file.
     *
     * @param filePath location of the save file
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | DuckeException e) {
            ui.showError("No previously saved tasks.");
            tasks = new TaskList();
        }
    }

    /** Creates a Duke using the default save file, for the graphical interface. */
    public Duke() {
        this(DEFAULT_SAVE_PATH);
    }

    /**
     * Runs the command-line loop, reading and executing commands until the user exits.
     */
    public void run() {
        ui.showWelcome(getWelcome());
        while (!isExit) {
            String input = ui.readCommand();
            ui.show(getResponse(input));
        }
    }

    /**
     * Processes one command and returns Ducke's response, for use by either interface.
     *
     * @param input the full command line entered by the user
     * @return the response text to show the user
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parseCommand(input);
            String argument = Parser.parseArgument(input);
            assert argument != null : "parseArgument never returns null";
            String response = switch (command) {
            case BYE -> {
                isExit = true;
                yield "Quack quack! (bye bye)\nYou made " + invalidCount
                        + (invalidCount == 1 ? " mistake" : " mistakes") + " this session.";
            }
            case LIST -> listResponse();
            case MARK -> markTask(argument);
            case UNMARK -> unmarkTask(argument);
            case TODO -> addTodo(argument);
            case DEADLINE -> addDeadline(argument);
            case EVENT -> addEvent(argument);
            case DELETE -> deleteTask(argument);
            case FIND -> findTasks(argument);
            case HELP -> helpResponse();
            default -> "Quack?";
            };
            storage.save(tasks);
            return response;
        } catch (DuckeException e) {
            invalidCount++;
            return escalate(e.getMessage());
        } catch (IOException e) {
            return "Couldn't save your tasks: " + e.getMessage();
        }
    }

    /**
     * Wraps an error message with a tone that grows angrier the more invalid
     * commands the user has entered this session.
     *
     * @param message the plain error message to show
     * @return the error message adjusted for the current anger level
     */
    private String escalate(String message) {
        if (invalidCount <= 2) {
            return message;
        } else if (invalidCount <= 5) {
            return message + "\n(Mistake #" + invalidCount + ".)";
        } else if (invalidCount <= 7) {
            return message.toUpperCase() + "\n(Mistake #" + invalidCount + "!!)";
        } else {
            return message.toUpperCase()
                    + "\nI am going to beat the quack out of you. (#"
                    + invalidCount + ".)";
        }
    }

    /**
     * Returns whether the user has issued the exit command.
     *
     * @return true if Ducke should stop
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the welcome greeting shown when the chatbot starts.
     *
     * @return the greeting text
     */
    public String getWelcome() {
        return "Hi, I am Ducke! Your quackbot!\nWaddle you want me to do?";
    }

    public static void main(String[] args) {
        new Duke(DEFAULT_SAVE_PATH).run();
    }

    private String addedMessage(Task task) {
        return "Added: \n" + task + "\n" + taskCountMessage();
    }

    private String taskCountMessage() {
        return "Now you have " + tasks.size()
                + (tasks.size() == 1 ? " task" : " tasks") + " in the list.";
    }

    private String helpResponse() {
        return "Here's what I understand:\n"
                + "  todo <description>\n"
                + "  deadline <description> /by <yyyy-mm-dd>\n"
                + "  event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\n"
                + "  list\n"
                + "  mark <number>   |   unmark <number>\n"
                + "  delete <number>\n"
                + "  find <keyword>\n"
                + "  help\n"
                + "  bye";
    }

    private String listResponse() {
        if (tasks.isEmpty()) {
            return "No tasks. Life is ponderful";
        }
        return numberedList(tasks);
    }

    private String findTasks(String keyword) throws DuckeException {
        if (keyword.isBlank()) {
            throw new DuckeException("Which quackword should I search for? (e.g. find book)");
        }
        TaskList matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "No quacking tasks found.";
        }
        return "Here are the quacking tasks in your list:\n" + numberedList(matches);
    }

    /** Returns the given tasks as a newline-separated, 1-based numbered list. */
    private String numberedList(TaskList list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(list.get(i));
        }
        return sb.toString();
    }

    private String markTask(String indexStr) throws DuckeException {
        String usageHint = "Quack! Which task should I mark? (e.g. mark 2)";
        int index = parseTaskIndex(indexStr, usageHint);
        Task task = tasks.get(index);
        task.markDone();
        return "Quack! I've marked this task as done:\n" + task;
    }

    private String unmarkTask(String indexStr) throws DuckeException {
        String usageHint = "Quack! Which task should I unmark? (e.g. unmark 2)";
        int index = parseTaskIndex(indexStr, usageHint);
        Task task = tasks.get(index);
        task.unmarkDone();
        return "Quack! I've marked this task as not done yet\n" + task;
    }

    private String addTodo(String info) throws DuckeException {
        Task task = Todo.of(info);
        tasks.add(task);
        return addedMessage(task);
    }

    private String addDeadline(String info) throws DuckeException {
        Task task = Deadline.of(info);
        tasks.add(task);
        return addedMessage(task);
    }

    private String addEvent(String info) throws DuckeException {
        Task task = Event.of(info);
        tasks.add(task);
        return addedMessage(task);
    }

    private String deleteTask(String indexStr) throws DuckeException {
        String usageHint = "Which task should I delete? (e.g. delete 2)";
        int index = parseTaskIndex(indexStr, usageHint);
        Task removed = tasks.delete(index);
        return "Removed: \n" + removed + "\n" + taskCountMessage();
    }

    /** Parses a 1-based task number from user input into a valid list index. */
    private int parseTaskIndex(String indexStr, String usageHint) throws DuckeException {
        if (indexStr.isBlank()) {
            throw new DuckeException(usageHint);
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new DuckeException("Quack! There's no task number " + indexStr + ".");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new DuckeException("Quack! '" + indexStr + "' isn't a number.");
        }
    }
}
