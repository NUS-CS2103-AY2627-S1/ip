package clsl.ui.javafx;

import java.util.ArrayList;
import java.util.List;

import clsl.Clsl;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Controls the main JavaFX window defined in {@code Main.fxml}. */
public class MainController {
    private static final double MESSAGE_WIDTH_RATIO = 0.82;
    private static final double MIN_MESSAGE_WIDTH = 180;
    private static final double MAX_MESSAGE_WIDTH = 720;
    private static final double AVATAR_AND_GAP_WIDTH = 52;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane conversationArea;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Clsl clsl;
    private Image clslAvatar;
    private Image userAvatar;
    private final List<VBox> messageBubbles = new ArrayList<>();

    /** Initializes the application logic and displays the welcome message. */
    @FXML
    private void initialize() {
        assert scrollPane != null : "FXML must inject scrollPane";
        assert conversationArea != null : "FXML must inject conversation area";
        assert backgroundImageView != null : "FXML must inject background image";
        assert dialogContainer != null : "FXML must inject dialogContainer";
        assert userInput != null : "FXML must inject userInput";
        assert sendButton != null : "FXML must inject sendButton";
        clsl = new Clsl("data/csls.txt");
        clslAvatar = loadImage("/images/pig.jpg");
        userAvatar = loadImage("/images/dog.jpg");
        configureConversationArea();
        userInput.setOnAction(this::handleUserInput);
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) ->
                resizeMessageBubbles(newWidth.doubleValue()));
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToBottom());
        addDialog("Clsl", "Hello! I'm Clsl.\nWhat can I do for you?", false);
    }

    /** Processes the command entered by the user. */
    @FXML
    private void handleUserInput(ActionEvent event) {
        String command = userInput.getText();
        if (command.isBlank()) {
            return;
        }

        addDialog("You", command, true);
        addDialog("Clsl", clsl.getResponse(command), false);
        userInput.clear();
        scrollToBottom();
        if (command.trim().equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            scheduleApplicationExit();
        }
    }

    /** Closes the application five seconds after displaying the goodbye message. */
    private void scheduleApplicationExit() {
        PauseTransition exitDelay = new PauseTransition(Duration.seconds(5));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /** Adds a message to the conversation display. */
    private void addDialog(String speaker, String message, boolean isUser) {
        Label sender = new Label(speaker);
        sender.getStyleClass().add("sender-label");

        Label dialog = new Label(message);
        dialog.getStyleClass().add("message-text");
        dialog.setWrapText(true);

        VBox bubble = new VBox(sender, dialog);
        bubble.getStyleClass().add("message-bubble");
        bubble.setFillWidth(true);
        messageBubbles.add(bubble);
        resizeMessageBubble(bubble, dialogContainer.getWidth());

        ImageView avatar = createAvatar(isUser ? userAvatar : clslAvatar);
        HBox dialogRow = new HBox(10);
        dialogRow.setMaxWidth(Double.MAX_VALUE);
        dialogRow.getStyleClass().add("message-row");
        dialogRow.getStyleClass().add(isUser ? "user-message" : "clsl-message");
        dialogRow.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        if (isUser) {
            dialogRow.getChildren().addAll(bubble, avatar);
        } else {
            dialogRow.getChildren().addAll(avatar, bubble);
        }
        dialogContainer.getChildren().add(dialogRow);
    }

    /** Resizes all message bubbles to fit the current conversation width. */
    private void resizeMessageBubbles(double containerWidth) {
        for (VBox bubble : messageBubbles) {
            resizeMessageBubble(bubble, containerWidth);
        }
    }

    /** Sets a readable responsive width for one message bubble. */
    private void resizeMessageBubble(VBox bubble, double containerWidth) {
        double responsiveWidth = containerWidth * MESSAGE_WIDTH_RATIO - AVATAR_AND_GAP_WIDTH;
        double messageWidth = Math.min(MAX_MESSAGE_WIDTH,
                Math.max(MIN_MESSAGE_WIDTH, responsiveWidth));
        bubble.setMaxWidth(messageWidth);
    }

    /** Adds the static CLSL artwork as a responsive background without covering controls. */
    private void configureConversationArea() {
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setManaged(false);
        backgroundImageView.fitWidthProperty().bind(conversationArea.widthProperty());
        backgroundImageView.fitHeightProperty().bind(conversationArea.heightProperty());
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);

        Rectangle conversationClip = new Rectangle();
        conversationClip.widthProperty().bind(conversationArea.widthProperty());
        conversationClip.heightProperty().bind(conversationArea.heightProperty());
        conversationArea.setClip(conversationClip);
    }

    /** Loads an avatar image from the application resources. */
    private Image loadImage(String resourcePath) {
        return new Image(getClass().getResourceAsStream(resourcePath));
    }

    /** Creates a circular avatar image for a chat message. */
    private ImageView createAvatar(Image image) {
        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(42);
        avatar.setFitHeight(42);
        avatar.setPreserveRatio(true);
        avatar.setClip(new Circle(21, 21, 21));
        avatar.getStyleClass().add("message-avatar");
        return avatar;
    }

    /** Scrolls the conversation to its lowest position after JavaFX lays out the new message. */
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
    }
}
