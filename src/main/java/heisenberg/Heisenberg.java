package heisenberg;

import java.time.LocalDateTime;
import java.util.Scanner;

/** Executes Heisenberg commands for both the text and graphical interfaces. */
public class Heisenberg {
    private final Ui ui;
    private final TaskList taskList;
    private final Storage storage;
    private String startupError;
    private boolean isRunning;
    private boolean lastResponseWasError;

    /** Creates a chatbot and loads previously saved tasks. */
    public Heisenberg() {
        ui = new Ui();
        taskList = new TaskList();
        storage = new Storage();
        isRunning = true;

        try {
            storage.loadTasks(taskList);
        } catch (StorageException e) {
            startupError = e.getMessage();
        }
    }

    /**
     * Runs the retained text interface until the user enters {@code bye} or closes the input stream.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Heisenberg heisenberg = new Heisenberg();
        heisenberg.ui.showWelcome();
        if (heisenberg.startupError != null) {
            heisenberg.ui.showMessage(heisenberg.startupError);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (heisenberg.isRunning() && scanner.hasNextLine()) {
                heisenberg.ui.showMessage(heisenberg.getResponse(scanner.nextLine()));
            }
        }
    }

    /** Returns the welcome message displayed when the GUI opens. */
    public String getWelcomeMessage() {
        if (startupError == null) {
            return ui.getWelcomeMessage();
        }
        return ui.getWelcomeMessage() + System.lineSeparator() + startupError;
    }

    /**
     * Executes one user command and returns the message that should be displayed.
     *
     * @param input Raw command entered by the user.
     * @return Response for the command, including validation and storage errors.
     */
    public String getResponse(String input) {
        try {
            lastResponseWasError = false;
            Parser parser = new Parser(input);

            return switch (parser.getCommand()) {
                case MARK -> markTask(parser);
                case LIST -> listTasks(parser);
                case BYE -> exit(parser);
                case DEADLINE -> addDeadline(parser);
                case TODO -> addTodo(parser);
                case EVENT -> addEvent(parser);
                case DELETE -> deleteTask(parser);
                case FIND -> findTasks(parser);
                case SORT -> sortTasks(parser);
            };
        } catch (InvalidCommandException
                | InvalidFormatException
                | InvalidTaskNumberException
                | StorageException e) {
            lastResponseWasError = true;
            return e.getMessage();
        }
    }

    /** Returns whether the most recent response reports an invalid command or storage error. */
    public boolean wasLastResponseAnError() {
        return lastResponseWasError;
    }

    /** Returns whether the chatbot should continue accepting commands. */
    public boolean isRunning() {
        return isRunning;
    }

    private String markTask(Parser parser) {
        Task task = taskList.markTask(parser.getTaskNumber());
        storage.saveTasks(taskList);
        return ui.getTaskMarkedMessage(task);
    }

    private String listTasks(Parser parser) {
        parser.requireNoArguments();
        return ui.getTaskListMessage(taskList);
    }

    private String exit(Parser parser) {
        parser.requireNoArguments();
        isRunning = false;
        return ui.getGoodbyeMessage();
    }

    private String addDeadline(Parser parser) {
        String description = parser.getDescription();
        LocalDateTime deadlineDateTime = parser.getDeadlineDateTime();
        Deadline deadline = new Deadline(description, deadlineDateTime);
        taskList.addTask(deadline);
        storage.saveTasks(taskList);
        return ui.getTaskAddedMessage(deadline, taskList);
    }

    private String addTodo(Parser parser) {
        ToDo todo = new ToDo(parser.getDescription());
        taskList.addTask(todo);
        storage.saveTasks(taskList);
        return ui.getTaskAddedMessage(todo, taskList);
    }

    private String addEvent(Parser parser) {
        String description = parser.getDescription();
        LocalDateTime startDateTime = parser.getEventFromDateTime();
        LocalDateTime endDateTime = parser.getEventToDateTime();
        if (!startDateTime.isBefore(endDateTime)) {
            throw new InvalidFormatException("Event must start before it ends.");
        }
        Event event = new Event(description, startDateTime, endDateTime);
        taskList.addTask(event);
        storage.saveTasks(taskList);
        return ui.getTaskAddedMessage(event, taskList);
    }

    private String deleteTask(Parser parser) {
        Task removedTask = taskList.deleteTask(parser.getTaskNumber());
        storage.saveTasks(taskList);
        return ui.getTaskDeletedMessage(removedTask, taskList);
    }

    private String findTasks(Parser parser) {
        TaskList matches = taskList.findTasks(parser.getKeyword());
        return ui.getMatchingTasksMessage(matches);
    }

    private String sortTasks(Parser parser) {
        parser.requireNoArguments();
        taskList.sortByDeadline();
        storage.saveTasks(taskList);
        return ui.getTasksSortedMessage(taskList);
    }
}
