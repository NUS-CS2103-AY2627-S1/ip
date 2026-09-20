package bonbon;

import bonbon.command.Command;
import bonbon.exception.BonBonException;
import bonbon.exception.BonBonUnknownCommandException;
import bonbon.parser.Parser;
import bonbon.tasklist.Task;
import bonbon.ui.Ui;
import bonbon.storage.Storage;
import bonbon.tasklist.TaskList;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**x
 * BonBon implements a chatbot that manages user's task list.
 */
public class BonBon {

    static TaskList tasks;

    /**
     * Serves as the main entry point to start the BonBon application.
     *
     * @param args Command line arguments passed during application startup.
     */
    public void main(String[] args) {
        run();
    }

    /**
     * Initializes the task list and storage, and executes the main command loop
     * for the command-line interface until the user exits.
     */
    private void run() {

        tasks = new TaskList(100);
        assert tasks != null : "TaskList should be successfully instantiated";

        Path filePath = Path.of("./src/main/java/data/bonbon.txt");
        assert filePath != null : "Storage file path must be successfully resolved";

        Storage.loadFile(filePath, tasks);

        Ui.greet();
        String input = Ui.getInput();
        boolean isSaveable = false;


        while (!input.equals("bye")) {
            try {
                String[] readInput = Parser.readInput(input);
                handleInput(readInput);

                if (!readInput[0].equals("list") && !readInput[0].equals("error") && !readInput[0].equals("find")) {
                    isSaveable = true;
                }
            } catch (BonBonUnknownCommandException e) {
                System.out.println(e.getMessage());
                System.out.println(Command.toStringCommands());
            } catch (BonBonException e) {
                System.out.println(e.getMessage());
            }

            if (isSaveable) {
                Storage.writeFile(filePath, tasks);
            }

            System.out.println();
            input = Ui.getInput();
        }
        Ui.exit();
    }

    /**
     * Executes the corresponding task list operation or UI output based on the
     * parsed command keyword and parameters.
     *
     * @param readInput The array containing the parsed command keyword and its parameters.
     */
    public void handleInput(String[] readInput) {
        String keyword = readInput[0];

        try {
            switch (keyword) {
                case "list":
                    System.out.println(tasks);
                    break;
                case "mark":
                    tasks.mark(Integer.parseInt(readInput[1]) - 1);
                    System.out.println("Task marked!");
                    break;
                case "unmark":
                    tasks.unmark(Integer.parseInt(readInput[1]) - 1);
                    System.out.println("Task unmarked!");
                    break;
                case "todo":
                    tasks.addToDo(readInput[1]);
                    Ui.printTaskAdd(tasks.getLastTask());
                    break;
                case "deadline":
                    tasks.addDeadline(readInput[1], LocalDateTime.parse(readInput[2], Parser.INPUT_FORMATTER));
                    Ui.printTaskAdd(tasks.getLastTask());
                    break;
                case "event":
                    tasks.addEvent(readInput[1], LocalDateTime.parse(readInput[2], Parser.INPUT_FORMATTER),
                            LocalDateTime.parse(readInput[3], Parser.INPUT_FORMATTER), readInput[4]);
                    Ui.printTaskAdd(tasks.getLastTask());
                    break;
                case "delete":
                    tasks.removeTask(Integer.parseInt(readInput[1]) - 1);
                    System.out.println("Task removed!");
                    break;
                case "find":
                    System.out.println(tasks.find(readInput[1]));
                    break;
                default:
                    throw new BonBonUnknownCommandException(keyword);
            }
        } catch (BonBonUnknownCommandException e) {
            System.out.println(e.getMessage());
            Ui.printCommands();
        } catch (BonBonException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Instantiates a new task list and populates it with saved task data from
     * the local storage file.
     */
    public void loadTasks() {
        tasks = new TaskList(100);
        Path filePath = Path.of("./src/main/java/data/bonbon.txt");
        Storage.loadFile(filePath, tasks);
    }

    /**
     * Processes a raw user command from the JavaFX interface, updates storage if
     * necessary, and returns a response string for display in the GUI.
     *
     * @param input The raw command string entered by the user in the GUI.
     * @return The status message or task list representation to display to the user.
     */
    public static String getResponse(String input) {

        if (input.equals("bye")) {
            return "See ya later!";
        }

        try {
            Path filePath = Path.of("./src/main/java/data/bonbon.txt");
            String[] readInput = Parser.readInput(input);
            String command = readInput[0];

            String response;

            switch (command) {
                case "list":
                    return tasks.toString();

                case "find":
                    return tasks.find(readInput[1]);

                case "mark": {
                    int index = Integer.parseInt(readInput[1]) - 1;
                    tasks.mark(index);
                    response = "Marked task:\n" + tasks.getTask(index);
                    break;
                }

                case "unmark": {
                    int index = Integer.parseInt(readInput[1]) - 1;
                    tasks.unmark(index);
                    response = "Unmarked task:\n" + tasks.getTask(index);
                    break;
                }

                case "todo":
                    tasks.addToDo(readInput[1]);
                    response = "Added todo:\n" + tasks.getTask(tasks.getCurrSize() - 1);
                    break;

                case "deadline":
                    tasks.addDeadline(readInput[1], LocalDateTime.parse(readInput[2], Parser.INPUT_FORMATTER));
                    response = "Added deadline:\n" + tasks.getTask(tasks.getCurrSize() - 1);
                    break;

                case "event":
                    tasks.addEvent(readInput[1], LocalDateTime.parse(readInput[2], Parser.INPUT_FORMATTER),
                            LocalDateTime.parse(readInput[3], Parser.INPUT_FORMATTER), readInput[4]);
                    response = "Added event:\n" + tasks.getTask(tasks.getCurrSize() - 1);
                    break;

                case "delete": {
                    int index = Integer.parseInt(readInput[1]) - 1;
                    Task task = tasks.getTask(index);
                    tasks.removeTask(index);
                    response = "Deleted task:\n" + task;
                    break;
                }

                default:
                    return "Don't know what that means :(";
            }

            // Save updated task list after successful task mutation
            Storage.writeFile(filePath, tasks);
            return response;

        } catch (BonBonUnknownCommandException e) {
            return e.getMessage() + "\n\n" + Command.toStringCommands();
        } catch (BonBonException e) {
            return e.getMessage();
        }
    }
}