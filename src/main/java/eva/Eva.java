package eva;

import eva.task.Deadline;
import eva.task.Event;
import eva.task.Task;
import eva.task.Todo;

/**
 * Runs the Eva task management application.
 */
public class Eva {
    private static final String FEATURE_HELP_PROMPT =
            "You help users of Eva, a desktop task manager. "
                    + "Answer only about Eva's features using the commands "
                    + "below. Be concise and use no more than two sentences.\n\n"
                    + "todo DESCRIPTION - adds a todo.\n"
                    + "deadline DESCRIPTION /by YYYY-MM-DD - adds a deadline.\n"
                    + "event DESCRIPTION /from START /to END - adds an event.\n"
                    + "list - lists all tasks.\n"
                    + "mark NUMBER - marks a task as done.\n"
                    + "unmark NUMBER - marks a task as not done.\n"
                    + "delete NUMBER - deletes a task.\n"
                    + "find KEYWORD - searches task descriptions.\n"
                    + "sort - sorts deadlines chronologically.\n"
                    + "bye - saves tasks and exits.\n"
                    + "@ai QUESTION - asks about Eva's features.\n"
                    + "Eva saves tasks automatically in data/eva.txt. "
                    + "It has no priority or reminder feature.";

    private final Storage storage;
    private final Ui ui;
    private final AiHelper aiHelper;
    private TaskList tasks;
    private boolean dataLoadFailed;

    /**
     * Creates an Eva application that stores tasks at the specified path.
     *
     * @param filePath Path of the task data file.
     */
    public Eva(String filePath) {
        this(filePath, new AiHelper());
    }

    Eva(String filePath, AiHelper aiHelper) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.aiHelper = aiHelper;

        try {
            this.tasks = new TaskList(storage.load());
        } catch (EvaException e) {
            ui.showResponse("OOPS!!! " + e.getMessage() + " :(");
            this.tasks = new TaskList();
            this.dataLoadFailed = true;
        }
    }

    /**
     * Starts the command-reading loop and processes commands until exit.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String input = ui.readCommand();
            ui.showResponse(getResponse(input));
            isExit = input.strip().equals("bye");
        }

        ui.close();
    }

    /**
     * Processes a command and returns Eva's response.
     *
     * @param input User command.
     * @return Eva's response to the command.
     */
    public String getResponse(String input) {
        try {
            Parser.ParsedCommand command = Parser.parse(input);
            if (dataLoadFailed) {
                if (command.getType() == Parser.CommandType.BYE) {
                    return ui.getByeMessage();
                }
                throw new EvaException(
                        "Saved tasks could not be loaded. "
                                + "Fix the data file before continuing.");
            }
            return execute(command);
        } catch (EvaException | IllegalArgumentException e) {
            return "OOPS!!! " + e.getMessage() + " :(";
        }
    }

    private String execute(Parser.ParsedCommand command) throws EvaException {
        switch (command.getType()) {
            case BYE:
                storage.save(tasks);
                return ui.getByeMessage();

            case LIST:
                return ui.getTaskListMessage(tasks);

            case MARK:
                Task markedTask = tasks.mark(command.getTaskNumber());
                storage.save(tasks);
                return ui.getMarkedTaskMessage(markedTask);

            case UNMARK:
                Task unmarkedTask = tasks.unmark(command.getTaskNumber());
                storage.save(tasks);
                return ui.getUnmarkedTaskMessage(unmarkedTask);

            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskNumber());
                storage.save(tasks);
                return ui.getDeletedTaskMessage(deletedTask, tasks.size());

            case TODO:
                Task todo = new Todo(command.getValue(0));
                tasks.add(todo);
                storage.save(tasks);
                return ui.getAddedTaskMessage(todo, tasks.size());

            case DEADLINE:
                Task deadline = new Deadline(
                        command.getValue(0), command.getValue(1));
                tasks.add(deadline);
                storage.save(tasks);
                return ui.getAddedTaskMessage(deadline, tasks.size());

            case EVENT:
                Task event = new Event(
                        command.getValue(0),
                        command.getValue(1),
                        command.getValue(2));
                tasks.add(event);
                storage.save(tasks);
                return ui.getAddedTaskMessage(event, tasks.size());

            case FIND:
                TaskList matchingTasks =
                        tasks.find(command.getValue(0));
                return ui.getMatchingTasksMessage(matchingTasks);

            case SORT:
                tasks.sort();
                storage.save(tasks);
                return ui.getTaskListMessage(tasks);

            case AI:
                return aiHelper.getAiResponse(
                        FEATURE_HELP_PROMPT, command.getValue(0));

            default:
                throw new EvaException("Unknown command.");
        }
    }

    /**
     * Starts the Eva application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        new Eva("data/eva.txt").run();
    }
}
