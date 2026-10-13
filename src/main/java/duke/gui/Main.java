package duke.gui;

import java.util.ArrayList;
import java.util.List;

import duke.AiHelper;
import duke.Command;
import duke.Parser;
import duke.SaveLocation;
import duke.Storage;
import duke.StorageException;
import duke.Task;
import duke.TaskRepository;
import duke.TaskService;
import duke.WangsaException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Coordinates Wangsa's JavaFX lifecycle and conversation commands.
 *
 * <p>Task commands use the same parser and service as the terminal interface.
 * {@link MainView} owns the window controls and layout.
 * AI requests run in the background so the window stays responsive.</p>
 */
public class Main extends Application {
    private static final String STYLESHEET_PATH = "main.css";

    private final Parser parser = new Parser();

    private final AiHelper aiHelper = new AiHelper();

    /**
     * At most one AI request runs at a time; normal task commands remain available.
     */
    private javafx.concurrent.Task<String> aiRequest;

    private MainView view;

    private final List<String> commandHistory = new ArrayList<>();

    private int historyIndex;

    private TaskService taskService;

    /**
     * Creates the JavaFX application instance used by the launcher.
     */
    public Main() {
    }

    /**
     * Starts the JavaFX window and loads saved tasks.
     */
    @Override
    public void start(Stage stage) {
        view = new MainView(this::handleCommand, this::navigateHistory, this::selectSuggestion);
        Typography.apply(view);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(view, Math.min(1120, screen.getWidth() - 48),
                Math.min(700, screen.getHeight() - 76));
        String stylesheet = Main.class.getResource(STYLESHEET_PATH).toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("Wangsa");
        stage.setMinWidth(780);
        stage.setMinHeight(580);
        stage.setScene(scene);
        stage.show();

        loadTasks();
        view.requestCommandFocus();
    }

    /**
     * Cancels an outstanding AI request when the window closes.
     */
    @Override
    public void stop() {
        if (aiRequest != null) {
            aiRequest.cancel();
        }
    }

    /**
     * Loads persisted tasks and renders the initial conversation.
     */
    private void loadTasks() {
        try {
            taskService = new TaskService(createRepository(), parser);
            appendAssistant("Hi! I'm Wangsa. Let's make room for what matters.\n\n"
                    + "• Add a task: todo read a book\n"
                    + "• See command examples: help\n"
                    + "• Your changes save automatically.");
            appendTasks(TaskView.tasks(taskService.getTasks()));
        } catch (StorageException | WangsaException exception) {
            appendError("I couldn't load your saved tasks. Nothing has been changed.\n"
                    + exception.getMessage()
                    + "\nType `help` for commands or `bye` to close.");
        }
        refreshHeader();
    }

    /**
     * Creates the default repository shared by the JavaFX workflows.
     */
    private TaskRepository createRepository() {
        return new Storage(SaveLocation.getDefaultFile());
    }

    /**
     * Parses and executes the command currently entered in the command field.
     */
    private void handleCommand() {
        String command = view.getCommand().trim();
        if (command.isEmpty()) {
            return;
        }
        view.clearCommand();
        appendUser(command);
        try {
            execute(command);
            rememberCommand(command);
        } catch (WangsaException | StorageException exception) {
            appendError(exception.getMessage());
        }
        refreshHeader();
    }

    /**
     * Places a command in the composer without executing it.
     */
    private void prepareCommand(String command) {
        view.setCommand(command);
        view.requestCommandFocus();
    }

    /**
     * Prepares a shortcut and immediately submits it when it requests the task list.
     */
    private void selectSuggestion(String command) {
        prepareCommand(command);
        if (command.equals("list")) {
            handleCommand();
        }
    }

    /**
     * Stores a successful command once so history remains useful and compact.
     */
    private void rememberCommand(String command) {
        if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(command)) {
            commandHistory.add(command);
        }
        historyIndex = commandHistory.size();
    }

    /**
     * Navigates the command history without leaving the current conversation.
     */
    private void navigateHistory(KeyCode direction) {
        if (commandHistory.isEmpty()) {
            return;
        }
        historyIndex += direction == KeyCode.UP ? -1 : 1;
        historyIndex = Math.max(0, Math.min(historyIndex, commandHistory.size()));
        view.setCommand(historyIndex == commandHistory.size() ? "" : commandHistory.get(historyIndex));
    }

    /**
     * Executes a parsed command and persists mutations.
     */
    private void execute(String command) throws WangsaException, StorageException {
        Command parsedCommand = parser.parse(command);
        requireAvailableTasks(parsedCommand.type());
        switch (parsedCommand.type()) {
            case BYE -> {
                appendAssistant("See you next time!");
                Platform.exit();
            }
            case LIST -> appendTasks(TaskView.tasks(taskService.getTasks()));
            case HELP -> view.appendMessage(ChatMessage.help());
            case AI -> askAi(((Command.AiQuestion) parsedCommand).question());
            case FIND -> appendTasks(TaskView.matches(
                    taskService.findMatches(((Command.Search) parsedCommand).keyword())));
            case SORT -> sortTasks();
            case MARK, UNMARK -> updateStatus((Command.TaskNumber) parsedCommand);
            case DELETE -> deleteTask((Command.TaskNumber) parsedCommand);
            case ADD_TASK -> addTask((Command.AddTask) parsedCommand);
            default -> throw new IllegalStateException("Unsupported command type: " + parsedCommand.type());
        }
    }

    /**
     * Blocks task commands after a startup error while keeping help and exit available.
     */
    private void requireAvailableTasks(Parser.CommandType commandType) throws StorageException {
        if (taskService == null && commandType != Parser.CommandType.BYE && commandType != Parser.CommandType.HELP) {
            throw new StorageException("Your saved task list is unavailable. Fix the startup error and restart "
                    + "Wangsa before using task commands.");
        }
    }

    /**
     * Saves the new order before showing the user any changed task numbers.
     */
    private void sortTasks() throws StorageException {
        taskService.sortByDeadline();
        appendTasks(TaskView.sorted(taskService.getTasks()));
    }

    /**
     * Removes a task and shows the remaining progress after the save succeeds.
     */
    private void deleteTask(Command.TaskNumber command) throws WangsaException, StorageException {
        Task removedTask = taskService.delete(command.taskNumber());
        appendTasks(TaskView.confirmation("Deleted:", removedTask,
                TaskFormatter.formatProgressSummary(taskService.getTasks())));
    }

    /**
     * Adds a task and confirms it only after the service saves it.
     */
    private void addTask(Command.AddTask command) throws WangsaException, StorageException {
        Task addedTask = taskService.add(command.task());
        appendTasks(TaskView.confirmation("Added:", addedTask,
                TaskFormatter.formatProgressSummary(taskService.getTasks())));
    }

    /**
     * Starts one background request while allowing the user to keep managing tasks.
     */
    private void askAi(String question) throws WangsaException {
        if (aiRequest != null) {
            throw new WangsaException("I'm still checking your last question. You can keep using task commands.");
        }
        Node pendingMessage = createMessage("Let me check that for you...", false);
        view.appendMessage(pendingMessage);

        javafx.concurrent.Task<String> request = new javafx.concurrent.Task<>() {
            @Override
            protected String call() {
                return aiHelper.ask(question);
            }
        };
        request.setOnSucceeded(event -> finishAiRequest(pendingMessage, request.getValue()));
        request.setOnFailed(event -> finishAiRequest(pendingMessage,
                "I couldn't get an AI answer just now. Type `help` for the command guide."));
        aiRequest = request;
        Thread worker = new Thread(request, "wangsa-ai-help");
        worker.setDaemon(true); // A slow network request must not keep the app open after the window closes.
        worker.start();
    }

    /**
     * Replaces the loading bubble on the JavaFX thread, keeping the answer beside its question.
     */
    private void finishAiRequest(Node pendingMessage, String answer) {
        view.replaceMessage(pendingMessage, createMessage(answer, false));
        aiRequest = null;
    }

    /**
     * Updates a task's completion status and persists the change.
     */
    private void updateStatus(Command.TaskNumber command)
            throws WangsaException, StorageException {
        boolean isMarked = command.type() == Parser.CommandType.MARK;
        Task updatedTask = isMarked ? taskService.mark(command.taskNumber()) : taskService.unmark(command.taskNumber());
        appendTasks(TaskView.confirmation(isMarked ? "Nice work! Completed:" : "Ready to work on again:",
                updatedTask, TaskFormatter.formatProgressSummary(taskService.getTasks())));
    }

    /**
     * Refreshes the live status copy after loading or mutating tasks.
     */
    private void refreshHeader() {
        if (taskService == null) {
            view.setStatus("STORAGE UNAVAILABLE", "TASK COMMANDS BLOCKED");
            return;
        }
        view.setStatus("SYSTEM READY", TaskFormatter.formatHeaderStats(taskService.getTasks()));
    }

    /**
     * Adds an assistant message to the chat and scrolls to the latest entry.
     */
    private void appendAssistant(String message) {
        view.appendMessage(createMessage(message, false));
    }

    /** Adds a structured task snapshot using the same scrolling behavior as other replies. */
    private void appendTasks(TaskView tasks) {
        view.appendMessage(ChatMessage.tasks(tasks));
    }

    /**
     * Adds a user message to the chat and scrolls to the latest entry.
     */
    private void appendUser(String message) {
        view.appendMessage(createMessage(message, true));
    }

    /**
     * Makes failures distinguishable by both text and color, not color alone.
     */
    private void appendError(String message) {
        view.appendMessage(new ChatMessage(message, ChatMessage.Role.ERROR));
    }

    /**
     * Creates a left-aligned assistant or right-aligned user message bubble.
     */
    private Node createMessage(String message, boolean isUser) {
        return new ChatMessage(message, isUser ? ChatMessage.Role.USER : ChatMessage.Role.ASSISTANT);
    }

}
