package prism;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import prism.parser.Parser;
import prism.storage.Storage;
import prism.task.Deadline;
import prism.task.Event;
import prism.task.Task;
import prism.task.Todo;
import prism.tasklist.TaskList;
import prism.ui.Ui;

/** Coordinates the Prism command loop and delegates work to the application components. */
public class Prism {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates a Prism application backed by the specified data file. */
    public Prism(String filePath) {
        this(filePath, new Ui());
    }

    /** Creates a Prism application using the supplied UI adapter. */
    public Prism(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(this.storage.load());
    }

    /** Runs the interactive command loop until the user enters {@code bye}. */
    public void run() {
        this.ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String fullCommand = this.ui.readCommand();
            this.ui.showLine();

            this.executeCommand(fullCommand);
            isExit = fullCommand.trim().equals("bye");
            this.ui.showLine();
        }

        this.ui.showBye();
        this.ui.close();
    }

    /** Executes one command and returns all messages produced by it. */
    public String executeCommand(String fullCommand) {
        try {
            Parser.CommandType commandType = Parser.parseCommandType(fullCommand);
            switch (commandType) {
                case BYE:
                    break;

                case LIST:
                    handleList();
                    break;

                case MARK:
                    handleMark(fullCommand);
                    break;

                case UNMARK:
                    handleUnmark(fullCommand);
                    break;

                case DELETE:
                    handleDelete(fullCommand);
                    break;

                case TODO:
                    handleTodo(fullCommand);
                    break;

                case DEADLINE:
                    handleDeadline(fullCommand);
                    break;

                case EVENT:
                    handleEvent(fullCommand);
                    break;

                case DATE:
                    handleDate(fullCommand);
                    break;

                case FIND:
                    handleFind(fullCommand);
                    break;

                case SNOOZE:
                    handleSnooze(fullCommand);
                    break;

                default:
                    throw new PrismException("!!! Unknown command.");
            }
        } catch (PrismException e) {
            this.ui.showError(e.getMessage());
        }
        return this.ui.collectMessages();
    }

    /** Displays all tasks currently stored in the task list. */
    private void handleList() {
        if (this.tasks.getSize() == 0) {
            this.ui.showMessage("Your task prism is clear—no tasks yet. Ready to add a new facet?");
            return;
        }
        this.ui.showMessage("Here is the current shape of your task prism:\n");
        for (int i = 0; i < this.tasks.getSize(); i++) {
            try {
                this.ui.showMessage((i + 1) + "." + this.tasks.getTask(i));
            } catch (PrismException ignored) {
                // Task index guaranteed valid by loop bounds
            }
        }
    }

    /** Marks the task selected by the command as done and persists the change. */
    private void handleMark(String command) throws PrismException {
        int index = Parser.parseIndex(command, "mark");
        Task task = this.tasks.markTask(index);
        this.storage.save(this.tasks.getTasks());
        this.ui.showMessage("That facet is complete! I've marked this task as done:\n  " + task);
    }

    /** Marks the task selected by the command as not done and persists the change. */
    private void handleUnmark(String command) throws PrismException {
        int index = Parser.parseIndex(command, "unmark");
        Task task = this.tasks.unmarkTask(index);
        this.storage.save(this.tasks.getTasks());
        this.ui.showMessage("No problem—I've returned this facet to your active list:\n  " + task);
    }

    /** Deletes the task selected by the command and persists the change. */
    private void handleDelete(String command) throws PrismException {
        int index = Parser.parseIndex(command, "delete");
        Task removed = this.tasks.deleteTask(index);
        this.storage.save(this.tasks.getTasks());
        this.ui.showMessage("Consider it refracted away. I've removed this task:\n"
                + "  " + removed + "\n"
                + "Now you have " + this.tasks.getSize() + " tasks in the list.");
    }

    /** Creates and stores a todo task from the command. */
    private void handleTodo(String command) throws PrismException {
        String description = Parser.parseTodoDescription(command);
        Task newTask = this.tasks.addTask(new Todo(description));
        this.storage.save(this.tasks.getTasks());
        showAddedTaskMessage(newTask);
    }

    /** Creates and stores a deadline task from the command. */
    private void handleDeadline(String command) throws PrismException {
        String[] args = Parser.parseDeadlineArgs(command);
        assert args.length == 2 : "Deadline parser must return description and due time";
        Task newTask = this.tasks.addTask(new Deadline(args[0], args[1]));
        this.storage.save(this.tasks.getTasks());
        showAddedTaskMessage(newTask);
    }

    /** Creates and stores an event task from the command. */
    private void handleEvent(String command) throws PrismException {
        String[] args = Parser.parseEventArgs(command);
        assert args.length == 3 : "Event parser must return description and two endpoints";
        Task newTask = this.tasks.addTask(new Event(args[0], args[1], args[2]));
        this.storage.save(this.tasks.getTasks());
        showAddedTaskMessage(newTask);
    }

    /** Displays tasks that occur on the date supplied in the command. */
    private void handleDate(String command) throws PrismException {
        LocalDate queryDate = Parser.parseQueryDate(command);
        List<Task> matchingTasks = this.tasks.getTasksOnDate(queryDate);

        this.ui.showMessage("Here are the task facets appearing on "
                + queryDate.format(DISPLAY_DATE_FORMATTER) + ":\n");
        if (matchingTasks.isEmpty()) {
            this.ui.showMessage("  No matching facets found—your schedule is clear.");
            return;
        }
        for (Task task : matchingTasks) {
            this.ui.showMessage("  " + task);
        }
    }

    /** Displays tasks whose descriptions contain the keyword supplied in the command. */
    private void handleFind(String command) throws PrismException {
        String keyword = Parser.parseFindKeyword(command);
        List<Task> matchingTasks = this.tasks.findTasks(keyword);

        this.ui.showMessage("I found these matching facets in your prism:\n");
        if (matchingTasks.isEmpty()) {
            this.ui.showMessage("  No matching facets found. Try a different keyword?");
            return;
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            this.ui.showMessage((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /** Reschedules the selected deadline or event and persists the change. */
    private void handleSnooze(String command) throws PrismException {
        String[] args = Parser.parseSnoozeArgs(command);
        int index = Parser.parseIndex("snooze " + args[0], "snooze");
        Task task = this.tasks.snoozeTask(index, args[1]);
        this.storage.save(this.tasks.getTasks());
        this.ui.showMessage("The schedule has shifted beautifully. I've snoozed this task to "
                + args[1] + ":\n  " + task);
    }

    /** Displays the standard confirmation message for a newly added task. */
    private void showAddedTaskMessage(Task task) {
        this.ui.showMessage("A new facet has joined your prism:\n"
                + "  " + task + "\n"
                + "Now you have " + this.tasks.getSize() + " tasks in the list.");
    }

    /** Starts Prism using the default data file. */
    public static void main(String[] args) {
        new Prism("./data/prism.txt").run();
    }
}
