package edith;

import java.util.Objects;

import edith.command.Command;
import edith.command.Parser;
import edith.task.Task;
import edith.task.TaskList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** Provides a graphical direct-message interface for interacting with Edith. */
public class Main extends Application {
    private static final String TASK_FILE = "./data/edith.txt";
    private static final double HEADER_LOGO_SIZE = 32;
    private static final double MESSAGE_LOGO_SIZE = 28;

    private final Storage storage = new Storage(TASK_FILE);
    private TaskList tasks;
    private final Image logoImage = new Image(Objects.requireNonNull(
            Main.class.getResource("/edith/images/logo.png")).toExternalForm());
    private final ChatUi ui = new ChatUi(logoImage);
    private TextField commandField;
    private Button sendButton;
    private Label connectionIndicator;
    private Label connectionStatus;

    /** Builds the chat window and wires its input controls to Edith. */
    @Override
    public void start(Stage stage) {
        VBox messages = new VBox(12);
        messages.setFillWidth(true);
        messages.getStyleClass().add("messages");

        ScrollPane conversation = new ScrollPane(messages);
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.getStyleClass().add("conversation");
        ui.setConversation(messages, conversation);

        commandField = new TextField();
        commandField.setPromptText("Enter a command, e.g. deadline submit report /by Friday 5pm");
        commandField.setOnAction(event -> sendCommand());
        commandField.getStyleClass().add("command-field");

        sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendCommand());
        sendButton.getStyleClass().add("send-button");

        HBox input = new HBox(8, commandField, sendButton);
        input.setAlignment(Pos.CENTER);
        input.getStyleClass().add("input-bar");
        HBox.setHgrow(commandField, Priority.ALWAYS);

        Label commandGuide = new Label("Examples: todo <description>  ·  deadline <description> /by <date>  ·  bye");
        commandGuide.getStyleClass().add("command-guide");

        VBox inputArea = new VBox(input, commandGuide);
        inputArea.getStyleClass().add("input-area");

        BorderPane root = new BorderPane();
        root.setTop(createTitleBar());
        root.setCenter(conversation);
        root.setBottom(inputArea);
        root.getStyleClass().add("app");

        Scene scene = new Scene(root, 700, 620);
        scene.getStylesheets().add(Main.class.getResource("/edith/gui.css").toExternalForm());

        try {
            tasks = storage.load();
            ui.showWelcome();
        } catch (EdithException error) {
            ui.showError(error.getMessage());
            commandField.setDisable(true);
            sendButton.setDisable(true);
            showOfflineStatus();
        }
        stage.setTitle("E.D.I.T.H.");
        stage.getIcons().add(createCircularLogo(logoImage, 64));
        stage.setMinWidth(520);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
        commandField.requestFocus();
    }

    /** Creates the title bar that displays Edith's identity and connection state. */
    private HBox createTitleBar() {
        ImageView logo = createLogoView(logoImage, HEADER_LOGO_SIZE);
        logo.getStyleClass().add("header-logo");

        Label name = new Label("E.D.I.T.H.");
        name.getStyleClass().add("profile-name");
        Label subtitle = new Label("Even Dead, I'm The Helper");
        subtitle.getStyleClass().add("profile-status");
        VBox identity = new VBox(1, name, subtitle);

        HBox profile = new HBox(8, logo, identity);
        profile.setAlignment(Pos.CENTER_LEFT);

        connectionIndicator = new Label("●");
        connectionIndicator.getStyleClass().add("status-indicator");
        connectionStatus = new Label("Online");
        connectionStatus.getStyleClass().add("connection-status");
        HBox connection = new HBox(6, connectionIndicator, connectionStatus);
        connection.setAlignment(Pos.CENTER_RIGHT);
        connection.getStyleClass().add("connection-state");

        HBox header = new HBox(profile, connection);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 18, 10, 18));
        header.getStyleClass().add("header");
        HBox.setHgrow(profile, Priority.ALWAYS);
        return header;
    }

    /** Updates the title-bar indicator after Edith has ended the session. */
    private void showOfflineStatus() {
        connectionIndicator.getStyleClass().add("offline-indicator");
        connectionStatus.getStyleClass().add("offline-status");
        connectionStatus.setText("Offline");
    }

    /** Creates a square view of the supplied EDITH logo. */
    private static ImageView createLogoView(Image image, double size) {
        ImageView logo = new ImageView(image);
        logo.setFitWidth(size);
        logo.setFitHeight(size);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        logo.setClip(new Circle(size / 2, size / 2, size / 2));
        return logo;
    }

    /** Creates a transparent circular image for the operating system title bar. */
    private static Image createCircularLogo(Image image, int size) {
        Canvas canvas = new Canvas(size, size);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        double radius = size / 2.0;

        graphics.save();
        graphics.beginPath();
        graphics.arc(radius, radius, radius, radius, 0, 360);
        graphics.closePath();
        graphics.clip();
        graphics.drawImage(image, 0, 0, size, size);
        graphics.restore();
        return canvas.snapshot(null, null);
    }

    /** Sends the current command to Edith and displays its response. */
    private void sendCommand() {
        String commandText = commandField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        ui.showUserMessage(commandText);
        commandField.clear();

        try {
            Command command = Parser.parse(commandText);
            command.execute(tasks, ui, storage);
            if (command.isExit()) {
                commandField.setDisable(true);
                sendButton.setDisable(true);
                showOfflineStatus();
            }
        } catch (EdithException error) {
            ui.showError(error.getMessage());
        }
    }

    /** Adapts Edith's output methods from the console to direct-message bubbles. */
    private static class ChatUi extends Ui {
        private static final double BUBBLE_WIDTH_OFFSET = 120;
        private static final double MINIMUM_BUBBLE_WIDTH = 250;
        private static final double MAXIMUM_BUBBLE_WIDTH = 620;
        private static final String WELCOME_MESSAGE = "Good evening, Peter. I have your schedule under surveillance.\n"
                + "Type \"help\" if you require the briefing.";
        private static final String GUI_HELP_TEXT = "The briefing, since we are doing this properly:\n\n"
                + "ADD TASKS\n"
                + "  todo <description>\n"
                + "  deadline <description> /by <date>\n"
                + "  event <description> /from <start> /to <end>\n\n"
                + "MANAGE TASKS\n"
                + "  list\n"
                + "  find <keyword>\n"
                + "  mark <number>\n"
                + "  unmark <number>\n"
                + "  delete <number>\n\n"
                + "SORT TASKS\n"
                + "  sort alph [desc]\n"
                + "  sort date [desc]\n"
                + "  sort status\n"
                + "  sort added [desc]\n\n"
                + "OTHER\n"
                + "  help\n"
                + "  bye";

        private VBox messages;
        private ScrollPane conversation;
        private final Image logoImage;

        /** Creates a chat adapter that displays the supplied EDITH logo. */
        ChatUi(Image logoImage) {
            this.logoImage = logoImage;
        }

        /** Sets the controls used for the chat transcript. */
        void setConversation(VBox messages, ScrollPane conversation) {
            this.messages = messages;
            this.conversation = conversation;
        }

        /** Adds a right-aligned user message. */
        void showUserMessage(String message) {
            appendBubble(message, true);
        }

        /** Adds a message bubble and scrolls it into view. */
        private void appendBubble(String message, boolean isUser) {
            appendBubble(message, isUser, false);
        }

        /** Adds a message bubble with optional error styling and scrolls it into view. */
        private void appendBubble(String message, boolean isUser, boolean isError) {
            assert messages != null && conversation != null
                    : "Chat controls must be initialized before displaying messages";
            Label bubble = new Label(message);
            bubble.setWrapText(true);
            bubble.maxWidthProperty().bind(Bindings.createDoubleBinding(
                    this::calculateMessageMaxWidth, conversation.widthProperty()));
            bubble.getStyleClass().addAll("message-bubble", isUser ? "user-bubble" : "edith-bubble");
            if (isError) {
                bubble.getStyleClass().add("error-bubble");
            }

            HBox row;
            if (isUser) {
                row = new HBox(bubble);
            } else if (isError) {
                Label sender = new Label("COMMAND ERROR");
                sender.getStyleClass().add("error-label");
                VBox response = new VBox(4, sender, bubble);
                row = new HBox(8, createAssistantAvatar(), response);
            } else {
                row = new HBox(8, createAssistantAvatar(), bubble);
            }
            row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            row.getStyleClass().add("message-row");
            messages.getChildren().add(row);
            scrollToLatestMessage();
        }

        /** Creates the circular assistant marker used beside Edith's messages. */
        private ImageView createAssistantAvatar() {
            ImageView logo = createLogoView(logoImage, MESSAGE_LOGO_SIZE);
            logo.getStyleClass().add("message-logo");
            return logo;
        }

        /** Scrolls to the latest message after JavaFX measures the new message bubble. */
        private void scrollToLatestMessage() {
            Platform.runLater(() -> {
                messages.applyCss();
                messages.layout();
                conversation.applyCss();
                conversation.layout();
                conversation.setVvalue(conversation.getVmax());
            });
        }

        /** Returns a readable bubble width for the current conversation size. */
        private double calculateMessageMaxWidth() {
            return Math.max(MINIMUM_BUBBLE_WIDTH,
                    Math.min(conversation.getWidth() - BUBBLE_WIDTH_OFFSET, MAXIMUM_BUBBLE_WIDTH));
        }

        /** Adds Edith's opening message to the conversation. */
        private void appendWelcomeMessage() {
            appendBubble(WELCOME_MESSAGE, false);
        }

        @Override
        public void showWelcome() {
            appendWelcomeMessage();
        }

        @Override
        public void showError(String message) {
            appendBubble("A minor complication:\n" + message, false, true);
        }

        @Override
        public void showList(TaskList tasks) {
            StringBuilder response = new StringBuilder("Your task situation. Do try to keep up:");
            for (int i = 0; i < tasks.size(); i++) {
                response.append("\n").append(i + 1).append(".").append(tasks.get(i));
            }
            appendBubble(response.toString(), false);
        }

        @Override
        public void showHelp() {
            appendBubble(GUI_HELP_TEXT, false);
        }

        @Override
        public void showMatchingTasks(TaskList tasks, String keyword) {
            StringBuilder response = new StringBuilder("Search complete. These survived the filter:");
            for (int index : tasks.findMatchingIndices(keyword)) {
                response.append("\n").append(index + 1).append(".").append(tasks.get(index));
            }
            appendBubble(response.toString(), false);
        }

        @Override
        public void showAddedTask(Task task, int count) {
            appendBubble("Logged. Organisation suits you:\n  " + task
                    + "\nActive tasks: " + count + ".", false);
        }

        @Override
        public void showMarked(Task task) {
            appendBubble("Marked complete. Miracles do happen:\n  " + task, false);
        }

        @Override
        public void showUnmarked(Task task) {
            appendBubble("Marked incomplete. Back to the grind:\n  " + task, false);
        }

        @Override
        public void showDeletedTask(Task task, int count) {
            appendBubble("Removed. One less thing to avoid:\n  " + task
                    + "\nActive tasks: " + count + ".", false);
        }

        @Override
        public void showBye() {
            appendBubble("EDITH signing off. Try not to create chaos without me, Peter.", false);
        }
    }
}
