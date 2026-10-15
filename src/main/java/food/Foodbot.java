package food;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import food.exception.FoodInputException;
import food.exception.FoodStorageException;
import food.task.Deadline;
import food.task.Event;
import food.task.Task;
import food.task.TaskList;
import food.task.Todo;

/**
 * The chatbot itself. It takes one line of user input at a time and drives the four parts that do
 * the actual work: the {@link Parser} that works out what the line means, the {@link TaskList}
 * that holds the tasks, the {@link Ui} that talks to the user, and the {@link Storage} that
 * remembers the tasks between runs.
 */
public class Foodbot {
    /** The command word stripped off before searching; see {@link #findTasks}. */
    private static final String FIND_COMMAND = "find";

    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;
    /**
     * The list as it was before the most recent change, or null when there is nothing to undo.
     * Only one level is kept, so "undo" reverses the last change and no more.
     */
    private List<String> lastSnapshot = null;

    /**
     * Greets the user and loads any previously saved tasks.
     *
     * <p>The Ui is passed in rather than created here so that the whole program shares one reader
     * of System.in; see {@link Ui}.
     *
     * @param ui the shared user interface to greet through and report to.
     * @throws FoodStorageException if the save file could not be opened or read.
     */
    public Foodbot(Ui ui) throws FoodStorageException {
        this.ui = ui;
        this.ui.showWelcome();
        this.storage = new Storage();
        this.tasks = new TaskList(this.storage.retrieveSaved());
    }

    /**
     * Handles one line of user input.
     *
     * @param input the raw line the user typed.
     * @return false if the user asked to exit, true to keep the chatbot running.
     * @throws FoodInputException   if the command is unknown or badly formed.
     * @throws FoodStorageException if the resulting task list could not be saved.
     */
    public boolean addInput(String input) throws FoodInputException, FoodStorageException {
        Parser.Command command = Parser.parse(input);

        boolean isMutating = switch (command.type()) {
            case MARK, UNMARK, DELETE, ADD -> true;
            case LIST, FIND, UNDO, EXIT -> false;
        };
        if (isMutating) {
            this.lastSnapshot = this.tasks.snapshot();
        }

        switch (command.type()) {
            case EXIT -> {
                this.ui.showGoodbye();
                return false;
            }
            case LIST -> this.listTasks();
            case FIND -> this.findTasks(command.rawInput());
            case MARK -> this.markComplete(command.index());
            case UNMARK -> this.markIncomplete(command.index());
            case DELETE -> this.deleteTask(command.index());
            case ADD -> this.addTask(command.rawInput());
            case UNDO -> this.undoLastChange();
            default -> {
                assert false : "Unhandled CommandType: " + command.type();
            }
        }

        // "list" and "find" only read, so there is nothing new to write for them.
        boolean isReadOnly = command.type() == Parser.CommandType.LIST
                || command.type() == Parser.CommandType.FIND;
        if (!isReadOnly) {
            this.storage.save(this.tasks.asList());
        }
        return true;
    }

    /**
     * Puts the list back the way it was before the most recent change and shows the result.
     *
     * <p>The backup is cleared once used, so a second "undo" reports that there is nothing to
     * undo rather than acting as a redo.
     *
     * @throws FoodInputException   if no change has been made since the program started, or the
     *                              last change was already undone.
     * @throws FoodStorageException if the backup could not be rebuilt, which is not expected.
     */
    public void undoLastChange() throws FoodInputException, FoodStorageException {
        if (this.lastSnapshot == null) {
            throw new FoodInputException("Nothing to send back, chef. The board hasn't changed.");
        }
        this.tasks.restore(this.lastSnapshot);
        this.lastSnapshot = null;
        this.ui.showUndone(this.tasks.asList());
    }

    /**
     * Creates the right kind of task from the user's line, adds it, and tells the user.
     *
     * <p>Only the first word is read here; each task class parses the rest of the line itself,
     * which keeps the knowledge of a task's format in the class that owns that format.
     *
     * @param input the raw line, starting with "todo", "deadline" or "event".
     * @throws FoodInputException if the first word is not a kind of task, or the rest of the line
     *                            is not in the shape that kind of task expects.
     */
    public void addTask(String input) throws FoodInputException {
        String[] parts = input.trim().split(" ");
        // A switch expression rather than three ifs, so addedTask can never be left null:
        // an unrecognised command fails here instead of surfacing as a NullPointerException below.
        final Task addedTask = switch (parts[0]) {
            case "todo" -> new Todo(input);
            case "deadline" -> new Deadline(input);
            case "event" -> new Event(input);
            default -> throw new FoodInputException(
                    String.format("%s is not a dish I know how to make, chef", parts[0]));
        };
        this.tasks.add(addedTask);
        this.ui.showTaskAdded(addedTask, this.tasks.size());
    }

    /**
     * Removes a task from the list and tells the user what is left.
     *
     * @param index 0-based position of the task, as worked out by the {@link Parser}.
     * @throws FoodInputException if no task sits at that index.
     */
    public void deleteTask(int index) throws FoodInputException {
        Task task = this.tasks.delete(index, "delete");
        this.ui.showTaskDeleted(task, this.tasks.size());
    }

    /**
     * Marks a task done and shows it in its new state.
     *
     * @param index 0-based position of the task, as worked out by the {@link Parser}.
     * @throws FoodInputException if no task sits at that index.
     */
    public void markComplete(int index) throws FoodInputException {
        Task task = this.tasks.get(index, "mark");
        task.markComplete();
        this.ui.showMarked(task);
    }

    /**
     * Marks a task not done and shows it in its new state.
     *
     * @param index 0-based position of the task, as worked out by the {@link Parser}.
     * @throws FoodInputException if no task sits at that index.
     */
    public void markIncomplete(int index) throws FoodInputException {
        Task task = this.tasks.get(index, "unmark");
        task.markIncomplete();
        this.ui.showUnmarked(task);
    }

    /**
     * Shows every task whose description contains the keyword the user typed.
     *
     * @param input the raw line, which starts with "find".
     */
    public void findTasks(String input) {
        // Everything after the command word is the keyword, so "find read book" searches for the
        // whole phrase rather than just the first word.
        String keyword = input.trim().substring(FIND_COMMAND.length()).trim();
        this.ui.showFoundTasks(this.tasks.find(keyword));
    }

    /** Shows every task, numbered from 1 as the user refers to them. */
    public void listTasks() {
        this.ui.showTaskList(this.tasks.asList());
    }

    /**
     * Handles one line of input from the GUI and returns Foodbot's reply as text.
     *
     * <p>{@link Ui} prints straight to System.out, which suits the console version but not a GUI,
     * so this temporarily redirects System.out to capture that text and returns it instead.
     *
     * @param input the raw line the user typed into the chat window.
     * @return the reply to show in the chat window.
     */
    public String getResponse(String input) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            this.addInput(input);
        } catch (FoodInputException e) {
            this.ui.showError(e.getMessage());
        } catch (FoodStorageException e) {
            this.ui.showLoadingError(e);
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString().trim();
    }
}
