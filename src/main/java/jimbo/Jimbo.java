package jimbo;

import java.util.Scanner;

import jimbo.exception.JimboException;
import jimbo.parser.Parser;
import jimbo.storage.Storage;
import jimbo.task.Deadline;
import jimbo.task.DoneStatus;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.TaskList;
import jimbo.ui.Ui;

/**
 * Entry point for the Jimbo task-list chatbot. Wires together the four
 * collaborators (see {@link Ui}, {@link Storage}, {@link Parser},
 * {@link TaskList}) and drives the command loop that reads user input,
 * interprets it, and applies it to the task list.
 */
public class Jimbo {
    public static final String FILE_PATH = "./data/jimbo.txt";

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    /**
     * Creates a Jimbo instance whose task list is loaded from, and saved
     * to, the save file at {@code filePath}.
     */
    public Jimbo(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList(storage.load());
    }

    /**
     * Shows the welcome message, then reads and handles commands from
     * standard input until the user types "bye".
     */
    public void run() {
        System.out.println(ui.showLine());
        System.out.println(ui.showWelcome());
        System.out.println(ui.showLine());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            Response response = getResponse(command);
            System.out.println(ui.showLine());
            System.out.println(response.text());
            System.out.println(ui.showLine());
            if (command.equals("bye")) {
                break;
            }
        }
        scanner.close();
    }

    /**
     * Returns the startup banner, greeting, and command list shown when the
     * GUI first opens.
     */
    public String getWelcomeMessage() {
        return ui.showGuiWelcome();
    }

    /**
     * Jimbo's reply to a single command: the message text, and whether it
     * represents an error (so callers like the GUI can style it
     * differently) rather than a normal confirmation.
     */
    public record Response(String text, boolean isError) {
    }

    /**
     * Interprets a single line of user input and returns Jimbo's reply,
     * applying whatever task-list/storage change the command implies. This
     * is the sole place command dispatch happens, so it can be driven by a
     * console loop (see {@link #run()}), a future GUI, or a unit test, all
     * without duplicating the parsing/response logic.
     */
    public Response getResponse(String input) {
        try {
            String[] tokens = input.split(" ", 2);
            String commandWord = tokens[0];
            String args = tokens.length > 1 ? tokens[1].trim() : "";

            switch (commandWord) {
                case "bye" -> {
                    return new Response(ui.showGoodbye(), false);
                }
                case "help" -> {
                    return new Response(ui.showHelp(), false);
                }
                case "list" -> {
                    return new Response(ui.showTaskList(tasks), false);
                }
                case "mark" -> {
                    int index = parser.parseTaskIndex(tasks, args, "mark");
                    return new Response(setTaskDone(index, DoneStatus.DONE), false);
                }
                case "unmark" -> {
                    int index = parser.parseTaskIndex(tasks, args, "unmark");
                    return new Response(setTaskDone(index, DoneStatus.NOT_DONE), false);
                }
                case "todo" -> {
                    return new Response(addTask(parser.parseTodo(args)), false);
                }
                case "deadline" -> {
                    return new Response(addTask(parser.parseDeadline(args)), false);
                }
                case "event" -> {
                    return new Response(addTask(parser.parseEvent(args)), false);
                }
                case "delete" -> {
                    int index = parser.parseTaskIndex(tasks, args, "delete");
                    return new Response(deleteTask(index), false);
                }
                case "update" -> {
                    return new Response(updateTask(args), false);
                }
                case "find" -> {
                    return new Response(ui.showMatchingTasks(tasks.find(parser.parseFind(args))), false);
                }
                default -> throw new JimboException("I don't know what that means!");
            }
        } catch (JimboException e) {
            return new Response(ui.showError(e.getMessage()), true);
        }
    }

    /**
     * Marks or unmarks the task at {@code index}, persists the change, and
     * returns the standard confirmation message.
     */
    private String setTaskDone(int index, DoneStatus status) {
        Task task = tasks.get(index);
        if (status == DoneStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.getTasks());
        return ui.showTaskMarked(task, status == DoneStatus.DONE);
    }

    /**
     * Removes the task at {@code index} from the list, persists the change,
     * and returns the standard "Noted. I've removed this task" confirmation.
     */
    private String deleteTask(int index) {
        Task task = tasks.remove(index);
        storage.save(tasks.getTasks());
        return ui.showTaskDeleted(task, tasks.size());
    }

    /**
     * Updates a single field of the task named in {@code args}, which must
     * be of the form "{@code <index> /<field> <new value>}", e.g.
     * "{@code 2 /by 3/12/2026 1800}". Only one field can be updated per
     * command; {@code /desc} applies to any task type, while {@code /by}
     * (Deadline) and {@code /from}/{@code /to} (Event) are rejected if the
     * task at {@code index} is not of the matching type.
     *
     * @throws JimboException if the index is missing/invalid, the field
     *                        flag or new value is missing, the flag is not
     *                        recognised, the flag does not apply to the
     *                        task's type, or the new value is invalid for
     *                        that field (e.g. an unparsable date/time).
     */
    private String updateTask(String args) throws JimboException {
        String[] indexAndRest = args.split(" ", 2);
        int index = parser.parseTaskIndex(tasks, indexAndRest[0], "update");
        if (indexAndRest.length < 2 || indexAndRest[1].trim().isEmpty()) {
            throw new JimboException("Tell me what to update — try \"update 2 /by 3/12/2026 1800\".");
        }

        String[] flagAndValue = indexAndRest[1].trim().split(" ", 2);
        String flag = flagAndValue[0];
        String value = flagAndValue.length > 1 ? flagAndValue[1].trim() : "";
        if (value.isEmpty()) {
            throw new JimboException("Gimme a new value after \"" + flag + "\"!");
        }

        Task task = tasks.get(index);
        switch (flag) {
            case "/desc" -> task.setDescription(value);
            case "/by" -> {
                if (!(task instanceof Deadline deadline)) {
                    throw new JimboException("Only deadlines have a \"/by\" time.");
                }
                deadline.setBy(value);
            }
            case "/from" -> {
                if (!(task instanceof Event event)) {
                    throw new JimboException("Only events have a \"/from\" time.");
                }
                event.setFrom(value);
            }
            case "/to" -> {
                if (!(task instanceof Event event)) {
                    throw new JimboException("Only events have a \"/to\" time.");
                }
                event.setTo(value);
            }
            default -> throw new JimboException("Can't update \"" + flag + "\" — "
                    + "try \"/desc\", \"/by\", \"/from\", or \"/to\".");
        }

        storage.save(tasks.getTasks());
        return ui.showTaskUpdated(task);
    }

    /**
     * Adds the given task to the list, persists the change, and returns the
     * standard "Got it. I've added this task" confirmation.
     */
    private String addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Starts Jimbo, loading and saving tasks at {@link #FILE_PATH}.
     */
    public static void main(String[] args) {
        new Jimbo(FILE_PATH).run();
    }
}
