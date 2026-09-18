package blud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import blud.command.AddTask;
import blud.command.Command;
import blud.command.CommandValidator;
import blud.command.Find;
import blud.command.ListTasks;
import blud.command.SortTasks;
import blud.command.TaskOperations;
import blud.exception.TaskTypeException;
import blud.model.Deadline;
import blud.model.Event;
import blud.model.TaskList;
import blud.model.ToDo;
import blud.storage.Storage;
import blud.ui.Ui;


/**
 * The Blud chatbot and its JavaFX user interface.
 *
 * <p>The command-processing method is shared by the graphical interface and
 * can also be used by other interfaces without depending on JavaFX controls.</p>
 */
public class Blud {
    private static final String DEFAULT_FILE_PATH = "data/blud.txt";
    private static final String BREAK_LINE = "-------------------------------";
    private static final String DEPARTURE = "Thanks for the conversation, see you soon!";

    private final Storage storage;
    private final TaskList taskList;
    private final AddTask addTask;
    private final ListTasks listTasks;
    private final SortTasks sortTasks;
    private final TaskOperations taskOperations;

    /** Creates a Blud instance using the default storage file. */
    public Blud() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a Blud instance using the specified task storage file.
     * @param filePath path to the task storage file.
     */
    public Blud(String filePath) {
        this.storage = new Storage(filePath);
        this.taskList = new TaskList(this.storage.loadTasks());
        this.addTask = new AddTask(taskList, storage);
        this.listTasks = new ListTasks(taskList);
        this.sortTasks = new SortTasks(taskList);
        this.taskOperations = new TaskOperations(taskList, storage);
    }

    /**
     * Processes one chatbot command and returns the response for display.
     * @param userInput command entered by the user.
     * @return a human-readable response without UI-specific formatting.
     */
    public String processCommand(String userInput) {
        return processCommandWithStatus(userInput).response();
    }

    /** Processes a command while retaining whether the response represents an error. */
    public CommandResult processCommandWithStatus(String userInput) {
        try {
            CommandValidator.validate(userInput);
            if ("bye".equalsIgnoreCase(userInput)) {
                return new CommandResult(DEPARTURE, false);
            }

            String[] splitInput = userInput.split(" ");
            Command inputCommand = Command.stringToCommand(splitInput[0]);
            CommandValidator.validateCommand(inputCommand, userInput);
            String[] parts = CommandValidator.splitTaskParts(userInput);
            switch (inputCommand) {
                case LIST:
                    return new CommandResult(listTasks.execute(), false);
                case FIND:
                    return new CommandResult(new Find(userInput).execute(taskList), false);
                case MARK:
                    return new CommandResult(taskOperations.mark(splitInput), false);
                case UNMARK:
                    return new CommandResult(taskOperations.unmark(splitInput), false);
                case DELETE:
                    return new CommandResult(taskOperations.delete(splitInput), false);
                case SORT:
                    return new CommandResult(sortTasks.execute(splitInput), false);
                case TODO:
                    return new CommandResult(addTask.execute(new ToDo(parts)), false);
                case DEADLINE:
                    return new CommandResult(addTask.execute(new Deadline(parts)), false);
                case EVENT:
                    return new CommandResult(addTask.execute(new Event(parts)), false);
                default:
                    throw new TaskTypeException("invalid task type " + splitInput[0]
                            + ", please use one of todo, event or deadline task types");
            }
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "Unable to process command." : e.getMessage();
            return new CommandResult(message, true);
        }
    }

    /** Represents a chatbot response and whether it should be visually highlighted as an error. */
    public record CommandResult(String response, boolean isError) {
    }


    /** Retains the original command-line interface for direct invocation. */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String banner = " ____  _            _\n"
                + "| __ )| |_   _  ___| |\n"
                + "|  _ \\| | | | |/ __| |\n"
                + "| |_) | | |_| | (__|_|\n"
                + "|____/|_|\\__,_|\\___(_)\n";
        String greeting = "Hey! This is Blud, what can I do for you today?";
        List<String> startupList = new ArrayList<>(Arrays.asList(banner.split("\n")));
        startupList.add(greeting);
        Ui ui = new Ui();
        ui.sectionString(BREAK_LINE, startupList, null, Ui.Mode.SIMPLE);
        if (!scanner.hasNextLine()) {
            ui.sectionString(null, List.of(DEPARTURE), BREAK_LINE, Ui.Mode.SIMPLE);
            return;
        }

        String userInput = scanner.nextLine();
        ui.sectionString(null, List.of(), BREAK_LINE, Ui.Mode.SIMPLE);
        while (!"bye".equalsIgnoreCase(userInput)) {
            String response = processCommand(userInput);
            ui.sectionString(null, Arrays.asList(response.split("\n")), BREAK_LINE, Ui.Mode.SIMPLE);
            if (!scanner.hasNextLine()) {
                ui.sectionString(null, List.of(DEPARTURE), BREAK_LINE, Ui.Mode.SIMPLE);
                return;
            }
            userInput = scanner.nextLine();
        }
        ui.sectionString(null, List.of(DEPARTURE), BREAK_LINE, Ui.Mode.SIMPLE);
    }

    /** Console-compatible entry point retained for direct invocation. */
    public static void main(String[] args) {
        String filePath = args.length == 0 ? DEFAULT_FILE_PATH : args[0];
        try {
            new Blud(filePath).run();
        } catch (RuntimeException exception) {
            String message = exception.getMessage() == null
                    ? "Unable to start Blud."
                    : exception.getMessage();
            System.out.println("Blud could not start: " + message);
        }
    }
}
