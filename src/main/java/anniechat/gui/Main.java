package anniechat.gui;

import java.net.URL;

import anniechat.logic.CommandHandler;
import anniechat.logic.CommandResult;
import anniechat.storage.Storage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

/** Provides a graphical interface for the WhiskerList task manager. */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/anniechat.txt";
    private static final String BOT_NAME = "WhiskerList";
    private static final String BOT_AVATAR_RESOURCE = "/images/whiskerlist.png";
    private static final String BOT_BUBBLE_COLOR = "#FFE1EC";
    private static final String USER_BUBBLE_COLOR = "#E7D8FF";
    private static final String BOT_TEXT_COLOR = "#5B2945";
    private static final String USER_TEXT_COLOR = "#3F2A5C";

    private final VBox conversation = new VBox(6);
    private final ScrollPane chatScrollPane = new ScrollPane(conversation);
    private final TextField commandInput = new TextField();
    private final Button sendButton = new Button("Send");
    private Image botAvatar;
    private CommandHandler commandHandler;

    /**
     * Creates the chatbot window and connects its controls to the chatbot logic.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        commandHandler = new CommandHandler(new Storage(DATA_FILE_PATH));
        botAvatar = loadBotAvatar();
        configureConversation();
        configureCommandInput();

        BorderPane root = createLayout();
        stage.setTitle(BOT_NAME);
        stage.setScene(new Scene(root, 640, 480));

        showStartupMessages();
        stage.show();
    }

    /** Configures the scrollable area containing chat bubbles. */
    private void configureConversation() {
        conversation.setPadding(new Insets(10));
        conversation.setFillWidth(true);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setStyle("-fx-background: #FFF9FC; -fx-border-color: transparent;");
        conversation.setStyle("-fx-background-color: #FFF9FC;");
    }

    /** Configures the text field and button used to send commands. */
    private void configureCommandInput() {
        commandInput.setPromptText("Type a command, e.g. todo read book");
        commandInput.setStyle("-fx-background-radius: 18; -fx-border-radius: 18;"
                + "-fx-border-color: #E7D8FF; -fx-padding: 8 12;");
        commandInput.setOnAction(event -> sendCommand());
        sendButton.setStyle("-fx-background-color: #C9A7EB; -fx-text-fill: #2F1B43;"
                + "-fx-background-radius: 18; -fx-padding: 8 16;");
        sendButton.setOnAction(event -> sendCommand());
    }

    /** Creates the main layout of the chatbot window. */
    private BorderPane createLayout() {
        HBox commandBar = new HBox(10, commandInput, sendButton);
        commandBar.setPadding(new Insets(10));
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(chatScrollPane);
        root.setBottom(commandBar);
        root.setStyle("-fx-background-color: #FFF9FC;");
        return root;
    }

    /** Creates the header containing the chatbot avatar and product name. */
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #FFD6E7;");

        if (botAvatar != null) {
            header.getChildren().add(createAvatarView(botAvatar, 42));
        }

        Label title = new Label(BOT_NAME);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;"
                + "-fx-text-fill: #5B2945;");
        header.getChildren().add(title);
        return header;
    }

    /** Displays loading errors and the initial welcome message. */
    private void showStartupMessages() {
        if (!commandHandler.getStartupMessage().isEmpty()) {
            appendBotMessage(commandHandler.getStartupMessage());
        }
        appendBotMessage(CommandHandler.WELCOME_MESSAGE);
    }

    /** Sends the command currently typed in the input field. */
    private void sendCommand() {
        String input = commandInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        appendUserMessage(input);
        commandInput.clear();

        CommandResult result = commandHandler.handle(input);
        appendBotMessage(result.getMessage());
        if (result.isExitRequested()) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Adds a user message to the conversation display. */
    private void appendUserMessage(String message) {
        appendMessage("You", message, Pos.CENTER_RIGHT, USER_BUBBLE_COLOR,
                USER_TEXT_COLOR, null);
    }

    /** Adds a WhiskerList response to the conversation display. */
    private void appendBotMessage(String message) {
        appendMessage(BOT_NAME, message, Pos.CENTER_LEFT, BOT_BUBBLE_COLOR,
                BOT_TEXT_COLOR, botAvatar);
    }

    /** Adds a coloured, aligned chat bubble to the conversation display. */
    private void appendMessage(String sender, String message, Pos alignment,
                               String backgroundColor, String textColor, Image avatar) {
        Label senderLabel = new Label(sender);
        senderLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6E5A68;");

        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(440);
        bubble.setStyle("-fx-background-color: " + backgroundColor + ";"
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-padding: 8 12;"
                + "-fx-background-radius: 14;");

        VBox messageBlock = new VBox(2, senderLabel, bubble);
        messageBlock.setMaxWidth(460);
        HBox messageRow = new HBox(8);
        messageRow.setAlignment(alignment);
        messageRow.setMaxWidth(Double.MAX_VALUE);
        if (avatar != null) {
            messageRow.getChildren().add(createAvatarView(avatar, 32));
        }
        messageRow.getChildren().add(messageBlock);
        conversation.getChildren().add(messageRow);
        chatScrollPane.setVvalue(1.0);
    }

    /** Loads the chatbot profile picture from the application resources. */
    private Image loadBotAvatar() {
        URL avatarUrl = Main.class.getResource(BOT_AVATAR_RESOURCE);
        return avatarUrl == null ? null : new Image(avatarUrl.toExternalForm());
    }

    /** Creates a circular avatar view for a chat message or the header. */
    private ImageView createAvatarView(Image avatar, double size) {
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(size);
        avatarView.setFitHeight(size);
        avatarView.setPreserveRatio(true);
        avatarView.setClip(new Circle(size / 2, size / 2, size / 2));
        return avatarView;
    }
}
