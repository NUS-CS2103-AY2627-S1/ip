package rocky.gui;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rocky.TaskList;
import rocky.parser.Parser;
import rocky.storage.Storage;
import rocky.task.Task;

/** Provides a JavaFX interface for Rocky while reusing its existing command logic. */
public class RockyGui extends Application {
    private static final int MESSAGE_LIST_SPACING = 12;
    private static final int CONTENT_SPACING = 12;
    private static final int COMMAND_BAR_SPACING = 8;
    private static final int MESSAGE_SPACING = 3;
    private static final int AVATAR_SIZE = 80;
    private static final int USER_MESSAGE_WIDTH = 220;
    private static final int ROCKY_MESSAGE_WIDTH = 480;
    private static final int WINDOW_WIDTH = 620;
    private static final int WINDOW_HEIGHT = 460;
    private static final double LATEST_MESSAGE_SCROLL_POSITION = 1.0;
    private static final String TASK_FILE_PATH = "./data/rocky.txt";
    private static final String USER_SENDER_NAME = "YOU";
    private static final String ROCKY_SENDER_NAME = "ROCKY · Your deep space friend :D";
    private final Storage storage = new Storage(TASK_FILE_PATH);
    private final Parser parser = new Parser();
    private final TaskList tasks = new TaskList(storage.load());
    private final VBox messageList = new VBox(MESSAGE_LIST_SPACING);
    private final ScrollPane conversation = new ScrollPane(messageList);
    private final TextField commandInput = new TextField();
    private final Image chatBackground = loadGuiImage("/rocky/gui/chat-background.jpg");
    private final Image userAvatar = loadGuiImage("/rocky/gui/user-avatar.jpeg");
    private final Image rockyAvatar = loadGuiImage("/rocky/gui/rocky-avatar.jpeg");

    /** Creates the JavaFX application. */
    public RockyGui() {
    }

    /** Builds and displays the Rocky window.
     *
     * @param stage the primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        Label title = new Label("Rocky");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        messageList.setPadding(new Insets(12));
        messageList.setFillWidth(true);
        messageList.setStyle("-fx-background-color: rgba(255, 255, 255, 0.18);");
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.getStyleClass().add("conversation-scroll-pane");
        messageList.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> conversation.setVvalue(LATEST_MESSAGE_SCROLL_POSITION)));
        addRockyMessage(formatWelcome());

        commandInput.setPromptText("Enter a command, e.g. find book");
        commandInput.setOnAction(event -> processCommand());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> processCommand());

        HBox commandBar = new HBox(COMMAND_BAR_SPACING, commandInput, sendButton);
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        StackPane conversationArea = new StackPane(conversation);
        conversationArea.setBackground(createChatBackground());

        VBox content = new VBox(CONTENT_SPACING, title, conversationArea, commandBar);
        content.setPadding(new Insets(16));
        VBox.setVgrow(conversationArea, Priority.ALWAYS);

        BorderPane root = new BorderPane(content);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(RockyGui.class.getResource("/rocky/gui/rocky-gui.css").toExternalForm());
        stage.setTitle("Rocky Task Manager");
        stage.setScene(scene);
        stage.show();
    }

    /** Processes the command entered in the input field and updates the output area. */
    private void processCommand() {
        String userInput = commandInput.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        Parser.Command command = parser.parse(userInput);
        String response = execute(command);
        addUserMessage(userInput);
        addRockyMessage(response);
        commandInput.clear();
        if (command.getType() == Parser.CommandType.BYE) {
            Platform.exit();
        }
    }

    /** Adds a command from the user to the conversation feed. */
    private void addUserMessage(String message) {
        messageList.getChildren().add(createMessage(USER_SENDER_NAME, message, userAvatar, true));
        scrollToLatestMessage();
    }

    /** Adds a response from Rocky to the conversation feed. */
    private void addRockyMessage(String message) {
        messageList.getChildren().add(createMessage(ROCKY_SENDER_NAME, message, rockyAvatar, false));
        scrollToLatestMessage();
    }

    /** Creates one chat message with its speaker label, text, and avatar. */
    private HBox createMessage(String sender, String message, Image avatar, boolean isUser) {
        assert sender != null : "Conversation messages must have a sender";
        assert message != null : "Conversation messages must have text";
        assert avatar != null : "Conversation messages must have an avatar";
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(true);

        Label senderLabel = new Label(sender);
        senderLabel.getStyleClass().addAll("sender-label",
                isUser ? "user-sender-label" : "rocky-sender-label");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.getStyleClass().addAll("message-content",
                isUser ? "user-message-content" : "rocky-message-content");

        VBox messageBubble = new VBox(MESSAGE_SPACING, senderLabel, messageLabel);
        int messageWidth = isUser ? USER_MESSAGE_WIDTH : ROCKY_MESSAGE_WIDTH;
        messageBubble.setPrefWidth(messageWidth);
        messageBubble.setMaxWidth(messageWidth);
        messageBubble.getStyleClass().addAll("message-bubble",
                isUser ? "user-message-bubble" : "rocky-message-bubble");
        HBox messageRow = new HBox(COMMAND_BAR_SPACING);
        messageRow.setAlignment(Pos.TOP_RIGHT);
        if (isUser) {
            messageRow.getChildren().addAll(messageBubble, avatarView);
            messageRow.setAlignment(Pos.TOP_RIGHT);
        } else {
            messageRow.getChildren().addAll(avatarView, messageBubble);
            messageRow.setAlignment(Pos.TOP_LEFT);
        }
        return messageRow;
    }

    /** Scrolls the conversation feed to its newest message. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            messageList.applyCss();
            messageList.layout();
            conversation.setVvalue(LATEST_MESSAGE_SCROLL_POSITION);
        });
    }

    /** Creates the image background that covers the conversation area. */
    private Background createChatBackground() {
        BackgroundSize size = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage image = new BackgroundImage(chatBackground, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        return new Background(image);
    }

    /** Loads a bundled GUI image and reports a configuration error clearly. */
    private Image loadGuiImage(String resourcePath) {
        if (RockyGui.class.getResource(resourcePath) == null) {
            throw new IllegalStateException("Missing GUI image resource: " + resourcePath);
        }
        return new Image(RockyGui.class.getResource(resourcePath).toExternalForm());
    }

    /** Executes a parsed command using the same task and storage operations as the CLI.
     *
     * @param command the parsed user command.
     * @return the response to display in the GUI.
     */
    private String execute(Parser.Command command) {
        assert command != null : "GUI must execute a parsed command";
        switch (command.getType()) {
        case BYE:
            return "Bye. We meet again soon!";
        case LIST:
            return formatTasks(tasks.asList(), "Rocky remember you have these tasks");
        case SORT:
            sortTasks();
            return "Rocky help you sort the task from earliest to latest!\n"
                    + formatTasks(tasks.asList(), "Rocky remember you have these tasks");
        case FIND:
            return formatMatchingTasks(tasks.find(command.getArgument()));
        case ADD:
            assert command.getTask() != null : "ADD commands must contain a task";
            tasks.add(command.getTask());
            storage.save(tasks.asList());
            return formatTaskAdded(command.getTask(), tasks.size());
        case MARK:
            return updateTaskStatus(command.getArgument(), "mark", true);
        case UNMARK:
            return updateTaskStatus(command.getArgument(), "unmark", false);
        case DELETE:
            return deleteTask(command.getArgument());
        case MISSING_DESCRIPTION:
        case ERROR:
            return formatError(command);
        default:
            return "Rocky cannot process that command.";
        }
    }

    /** Formats a task collection for display in the output area.
     *
     * @param taskList the tasks to format.
     * @param heading the heading shown above the tasks.
     * @return the formatted task list.
     */
    private String formatTasks(List<Task> taskList, String heading) {
        StringBuilder result = new StringBuilder(heading);
        if (taskList.isEmpty()) {
            result.append("\nRocky don't see anything!");
        } else {
            appendNumberedTasks(result, taskList);
        }
        return result.toString();
    }

    /** Formats the response for a task search using the console UI's wording. */
    private String formatMatchingTasks(List<Task> matchingTasks) {
        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            result.append("\nRocky don't see any matching tasks!");
        } else {
            appendNumberedTasks(result, matchingTasks);
        }
        return result.toString();
    }

    /** Appends numbered tasks to a response under construction. */
    private void appendNumberedTasks(StringBuilder result, List<Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            result.append("\n").append(i + 1).append(". ").append(taskList.get(i));
        }
    }

    /** Formats Rocky's task-added confirmation. */
    private String formatTaskAdded(Task task, int taskCount) {
        return "Amaze! Rocky add this to task...:\n" + task
                + "\nRocky see " + taskCount + " tasks in the list.";
    }

    /** Formats a parser error for display in Rocky's chat message. */
    private String formatError(Parser.Command command) {
        return command.getMessage();
    }

    /** Formats Rocky's initial welcome message. */
    private String formatWelcome() {
        return " ____             _\n"
                + "|  _ \\ ___   ___| | ___   _\n"
                + "| |_) / _ \\ / __| |/ / | | |\n"
                + "|  _ < (_) | (__|   <| |_| |\n"
                + "|_| \\_\\___/ \\___|_|\\_\\__, |\n"
                + "                         |___/\n\n"
                + "Hello! I Rocky.\n"
                + "Amaze, what a special human being! What rocky do for you?";
    }

    /** Updates a task's completion status and persists the change.
     *
     * @param taskNumberText the one-based task number entered by the user.
     * @param completed the desired completion status.
     * @return the operation result for display.
     */
    private String updateTaskStatus(String taskNumberText, String command, boolean completed) {
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                return "Rocky cannot find that task number.";
            }
            Task task = tasks.get(taskIndex);
            if (completed) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            storage.save(tasks.asList());
            return (completed ? "Nice! Rocky marked this task as done:"
                    : "Oh No! Rocky marked this task as not done yet:")
                    + "\n" + task;
        } catch (NumberFormatException exception) {
            return "Please provide a task number, for example: " + command + " 2";
        }
    }

    /** Deletes a task and persists the updated task list.
     *
     * @param taskNumberText the one-based task number entered by the user.
     * @return the operation result for display.
     */
    private String deleteTask(String taskNumberText) {
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                return "Rocky cannot find that task number.";
            }
            Task task = tasks.get(taskIndex);
            tasks.delete(taskIndex);
            storage.save(tasks.asList());
            return "Rocky will remove that annoying task for you!:\n" + task;
        } catch (NumberFormatException exception) {
            return "Please provide a task number, for example: delete 2";
        }
    }

    /** Sorts and persists the task list. */
    private void sortTasks() {
        tasks.sortByDate();
        storage.save(tasks.asList());
    }
}
