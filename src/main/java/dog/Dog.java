package dog;

import java.util.Map.Entry;

import dog.exceptions.DogException;
import dog.model.Deadline;
import dog.model.Event;
import dog.model.Task;
import dog.model.TaskList;
import dog.model.Todo;
import dog.parser.Command;
import dog.storage.Storage;

/**
 * Main logic controller for the Dog task management application.
 * Handles input parsing, storage operations, and task management.
 * Returns response strings that Main passes to Ui for display.
 */
public class Dog {
    private static final Storage STORAGE = new Storage("./data/dog.txt");

    private static final String GREETING = "WOOF WOOF! How can I help? WOOF";
    private static final String WOOF_REPLY = "WOOF WOOF i'm a dog WOOF WOOF";
    private static final String FAREWELL = "WOOF! Goodbye! WOOF WOOF";

    private TaskList taskList;

    /**
     * Constructs a new Dog instance and loads existing tasks.
     */
    public Dog() {
        this.taskList = new TaskList(STORAGE.load());
    }

    public String getWelcomeString() {
        return GREETING;
    }

    /**
     * Represents the response after handling a user input.
     */
    public static class DogResponse {
        private final String reply;
        private final boolean shouldExit;

        /**
         * Constructs a new DogResponse instance.
         */
        public DogResponse(String reply, boolean shouldExit) {
            this.reply = reply;
            this.shouldExit = shouldExit;
        }

        public String getReply() {
            return reply;
        }

        public boolean shouldExit() {
            return shouldExit;
        }
    }

    /**
     * Handles user input and returns the response message.
     *
     * @param input The raw user input string.
     * @return The response containing reply message and exit status.
     * @throws DogException if there's an error processing the input.
     */
    public DogResponse handleUserInput(String input) throws DogException {
        assert input.equals(input.trim()) : "input should have been trimmed";
        Entry<Command, String> parsedResult = parseInput(input);
        Command command = parsedResult.getKey();
        String rest = parsedResult.getValue();
        return executeCommand(command, rest);
    }

    private DogResponse executeCommand(Command command, String rest) throws DogException {
        String reply;
        switch (command) {
            case BYE:
                handleSaveTasksToStorage();
                return new DogResponse(FAREWELL, true);
            case WOOF:
                return new DogResponse(WOOF_REPLY, false);
            case LIST:
                reply = handlePrintList();
                return new DogResponse(reply, false);
            case MARK:
                reply = handleMarkTask(rest);
                return new DogResponse(reply, false);
            case DELETE:
                reply = handleDeleteTask(rest);
                return new DogResponse(reply, false);
            case FIND:
                reply = handleFindTasks(rest);
                return new DogResponse(reply, false);
            case TODO:
                reply = handleAddTodo(rest);
                return new DogResponse(reply, false);
            case DEADLINE:
                reply = handleAddDeadline(rest);
                return new DogResponse(reply, false);
            case EVENT:
                reply = handleAddEvent(rest);
                return new DogResponse(reply, false);
            default:
                assert false : "Unexpected command state";
                return new DogResponse("", false);
        }
    }

    private Entry<Command, String> parseInput(String input) throws DogException {
        Entry<Command, String> parseResult = Command.fromInput(input);
        if (parseResult == null) {
            throw new DogException("woof...? I never learnt that command...");
        }
        return parseResult;
    }

    private void handleSaveTasksToStorage() {
        STORAGE.save(taskList.getTasks());
    }

    private String handlePrintList() {
        return "WOOF! I've fetched the tasks in your list:\n" + taskList;
    }

    private String handleMarkTask(String rest) throws DogException {
        try {
            int index = Integer.parseInt(rest.trim()) - 1;
            taskList.markTask(index);
            handleSaveTasksToStorage();
            return "WOOF! I've marked this task as done:\n" + taskList.getTask(index);
        } catch (NumberFormatException e) {
            throw new DogException("woof...? Please give me a valid task number... (e.g., 'mark 2').");
        }
    }

    private String handleDeleteTask(String rest) throws DogException {
        try {
            int index = Integer.parseInt(rest.trim()) - 1;
            Task deletedTask = taskList.deleteTask(index);
            handleSaveTasksToStorage();
            return "WOOF! I've deleted this task:\n" + deletedTask
                    + "\nYou have " + taskList.size() + " tasks left in your list! WOOF!";
        } catch (NumberFormatException e) {
            throw new DogException("woof...? Please give me a valid task number... (e.g., 'delete 2').");
        }
    }

    private String handleFindTasks(String rest) throws DogException {
        TaskList foundTasks = taskList.findTasks(rest);
        return "WOOF! I've fetched these matching tasks from your list:\n" + foundTasks;
    }

    private String handleAddTodo(String rest) throws DogException {
        Task newTodo = Todo.parse(rest);
        assert newTodo != null : "parsed Todo should not be null";
        return handleAddTask(newTodo);
    }

    private String handleAddDeadline(String rest) throws DogException {
        Task newDeadline = Deadline.parse(rest);
        assert newDeadline != null : "parsed Deadline should not be null";
        return handleAddTask(newDeadline);
    }

    private String handleAddEvent(String rest) throws DogException {
        Task newEvent = Event.parse(rest);
        assert newEvent != null : "parsed Event should not be null";
        return handleAddTask(newEvent);
    }

    private String handleAddTask(Task task) {
        taskList.addTask(task);
        handleSaveTasksToStorage();
        return "WOOF! I've added a new task: \n" + task;
    }
}
