package computa.ui;

import computa.Computa;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** Provides a JavaFX interface for interacting with Computa. */
public class ComputaGui extends Application {
    /** Resource path of Computa's chat avatar. */
    private static final String BOT_AVATAR_PATH = "/images/computa-avatar.png";
    /** Resource path of the user's chat avatar. */
    private static final String USER_AVATAR_PATH = "/images/user-avatar.png";
    /** Diameter used for each circular chat avatar. */
    private static final double AVATAR_SIZE = 38;
    /** Widely available font used throughout the chat interface. */
    private static final String CHAT_FONT_FAMILY = "Arial";
    /** Readable font size shared by message bubbles. */
    private static final String BUBBLE_FONT_SIZE = "15px";

    /** Chat feed containing the user and chatbot message bubbles. */
    private VBox conversation;
    /** Scroll container that keeps the newest chat bubble visible. */
    private ScrollPane conversationScrollPane;
    /** Text field where the user enters a chatbot command. */
    private TextField commandInput;
    /** Button that submits the current command. */
    private Button sendButton;
    /** Avatar shown beside chatbot responses. */
    private Image botAvatar;
    /** Avatar shown beside user messages. */
    private Image userAvatar;
    /** Chatbot instance shared by all commands in this window. */
    private Computa computa;

    /**
     * Builds and displays the Computa window.
     *
     * @param stage primary JavaFX window supplied by the platform.
     */
    @Override
    public void start(Stage stage) {
        botAvatar = loadAvatar(BOT_AVATAR_PATH);
        userAvatar = loadAvatar(USER_AVATAR_PATH);
        conversation = createConversationView();
        conversationScrollPane = new ScrollPane(conversation);
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToLatestMessage());
        conversationScrollPane.setFitToWidth(true);
        conversationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScrollPane.setStyle("-fx-background: #f5f7fb; -fx-background-color: #f5f7fb;");
        commandInput = new TextField();
        commandInput.setPromptText("Enter a command, e.g. todo read a book");
        commandInput.setStyle("-fx-background-color: #fffdfd; -fx-background-radius: 18;"
                + " -fx-border-color: #e7b8d1; -fx-border-radius: 18; -fx-padding: 10 14 10 14;"
                + " -fx-font-family: " + CHAT_FONT_FAMILY + "; -fx-font-size: " + BUBBLE_FONT_SIZE + ";");
        commandInput.setOnAction(event -> submitCommand());

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-background-color: #e68db8; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-font-family: " + CHAT_FONT_FAMILY + "; -fx-background-radius: 18;"
                + " -fx-padding: 10 18 10 18; -fx-effect: dropshadow(gaussian, #d89bb7, 5, 0.2, 0, 2);");
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(8, commandInput, sendButton);
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        Label title = new Label("COMPUTA");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 25px; -fx-font-weight: bold;"
                + " -fx-text-fill: #8b3f64;");
        Label subtitle = new Label("Your personal task chatbot");
        subtitle.setStyle("-fx-font-family: " + CHAT_FONT_FAMILY + "; -fx-text-fill: #805469;");
        VBox header = new VBox(2, title, subtitle);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #fff9fc;");
        root.setTop(header);
        root.setCenter(conversationScrollPane);
        root.setBottom(commandBar);
        BorderPane.setMargin(conversationScrollPane, new Insets(16, 0, 16, 0));

        computa = new Computa(new Ui(this::appendBotOutput));
        computa.startSession();

        stage.setTitle("Computa");
        stage.setScene(new Scene(root, 720, 600));
        stage.show();
        commandInput.requestFocus();
    }

    /** Creates the scrollable feed used to show the conversation bubbles. */
    private VBox createConversationView() {
        VBox chatFeed = new VBox(10);
        chatFeed.setPadding(new Insets(14));
        chatFeed.setFillWidth(true);
        chatFeed.setStyle("-fx-background-color: #fff3f8;");
        return chatFeed;
    }

    /** Appends one chatbot output line to the conversation display. */
    private void appendBotOutput(String line) {
        if (line.startsWith("____")) {
            return;
        }
        appendBubble(line, false, isErrorMessage(line));
    }

    /** Sends the current input to Computa and disables controls after exit. */
    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        appendBubble(command, true, false);
        commandInput.clear();
        boolean shouldContinue = computa.processCommand(command);
        if (!shouldContinue) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Adds a left-aligned chatbot bubble or right-aligned user bubble. */
    private void appendBubble(String text, boolean isUserMessage, boolean isErrorMessage) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.maxWidthProperty().bind(conversationScrollPane.widthProperty().multiply(0.72));
        bubble.setStyle(getBubbleStyle(isUserMessage, isErrorMessage));

        ImageView avatar = createAvatar(isUserMessage);
        HBox bubbleRow = isUserMessage
                ? new HBox(8, bubble, avatar)
                : new HBox(8, avatar, bubble);
        bubbleRow.setMaxWidth(Double.MAX_VALUE);
        bubbleRow.setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        conversation.getChildren().add(bubbleRow);
        scrollToLatestMessage();
    }

    /** Scrolls after JavaFX lays out a newly added message so the latest response remains visible. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            conversationScrollPane.applyCss();
            conversationScrollPane.layout();
            conversationScrollPane.setVvalue(1.0);
        });
    }

    /** Returns the style for a user, chatbot, or invalid-command message bubble. */
    private String getBubbleStyle(boolean isUserMessage, boolean isErrorMessage) {
        if (isUserMessage) {
            return "-fx-background-color: #ffd8eb; -fx-background-radius: 22;"
                    + " -fx-border-color: #ed9fc8; -fx-border-radius: 22;"
                    + " -fx-padding: 11 16 11 16; -fx-font-family: " + CHAT_FONT_FAMILY
                    + "; -fx-font-size: " + BUBBLE_FONT_SIZE + "; -fx-text-fill: #5b2441;"
                    + " -fx-effect: dropshadow(gaussian, #e9b8cf, 4, 0.18, 0, 2);";
        }
        if (isErrorMessage) {
            return "-fx-background-color: #fff0f2; -fx-background-radius: 22;"
                    + " -fx-border-color: #e78091; -fx-border-radius: 22;"
                    + " -fx-padding: 11 16 11 16; -fx-font-family: " + CHAT_FONT_FAMILY
                    + "; -fx-font-size: " + BUBBLE_FONT_SIZE + "; -fx-text-fill: #8a1c3d;"
                    + " -fx-effect: dropshadow(gaussian, #f0bdc7, 4, 0.18, 0, 2);";
        }
        return "-fx-background-color: #fffaff; -fx-background-radius: 22;"
                + " -fx-border-color: #e7c6e8; -fx-border-radius: 22;"
                + " -fx-padding: 11 16 11 16; -fx-font-family: " + CHAT_FONT_FAMILY
                + "; -fx-font-size: " + BUBBLE_FONT_SIZE + "; -fx-text-fill: #50384f;"
                + " -fx-effect: dropshadow(gaussian, #e9d4e9, 4, 0.18, 0, 2);";
    }

    /** Loads a bundled avatar image for use in chat rows. */
    private Image loadAvatar(String resourcePath) {
        return new Image(ComputaGui.class.getResource(resourcePath).toExternalForm());
    }

    /** Creates a clipped, circular avatar that corresponds to the message sender. */
    private ImageView createAvatar(boolean isUserMessage) {
        ImageView avatar = new ImageView(isUserMessage ? userAvatar : botAvatar);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        return avatar;
    }

    /** Returns whether a response is an invalid-command message that should stand out visually. */
    private boolean isErrorMessage(String message) {
        return message.startsWith("Hmph!") || message.startsWith("TOMARE!");
    }
}
