package auto.ui;

import auto.Auto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Handles user interaction with the main chat window.
 */
public class MainWindow {
    private static final double MESSAGE_CONTENT_SPACING = 6;
    private static final double MESSAGE_MAX_WIDTH = 320;
    private static final double SCROLL_BOTTOM_TOLERANCE = 0.05;
    private static final String EXIT_COMMAND = "bye";
    private static final String APPLICATION_ICON = "/images/auto-icon.png";
    private static final double WELCOME_ICON_SIZE = 96;
    private static final String WELCOME_MESSAGE = String.join(System.lineSeparator(),
            "Hello! I'm Auto, your task kaki.",
            "What you need to settle today?");

    private Auto auto;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button latestMessageButton;

    /**
     * Always follows new messages and offers a shortcut when manually scrolled up.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().addListener(observable -> updateLatestMessageButton());
        scrollPane.viewportBoundsProperty().addListener(observable -> updateLatestMessageButton());
        dialogContainer.heightProperty().addListener(observable -> {
            scrollPane.setVvalue(scrollPane.getVmax());
            updateLatestMessageButton();
        });
        updateLatestMessageButton();
    }

    /**
     * Shows the shortcut only when scrollable messages remain below the viewport.
     */
    private void updateLatestMessageButton() {
        boolean hasOverflow = dialogContainer.getHeight() > scrollPane.getViewportBounds().getHeight();
        boolean isAboveBottom = scrollPane.getVvalue() < scrollPane.getVmax() - SCROLL_BOTTOM_TOLERANCE;
        latestMessageButton.setVisible(hasOverflow && isAboveBottom);
    }

    /** Returns to the newest message and restores focus to the command input. */
    @FXML
    private void scrollToLatestMessage() {
        scrollPane.setVvalue(scrollPane.getVmax());
        updateLatestMessageButton();
        userInput.requestFocus();
    }

    /**
     * Supplies the application instance used to process chat messages.
     *
     * @param auto Application instance shared for the lifetime of this window.
     */
    public void setAuto(Auto auto) {
        this.auto = auto;
        String greeting = WELCOME_MESSAGE;
        String startupMessage = auto.getStartupMessage();
        if (!startupMessage.isEmpty()) {
            greeting += System.lineSeparator() + System.lineSeparator() + startupMessage;
        }

        ImageView welcomeIcon = new ImageView(new Image(
                MainWindow.class.getResource(APPLICATION_ICON).toExternalForm()));
        welcomeIcon.setFitWidth(WELCOME_ICON_SIZE);
        welcomeIcon.setFitHeight(WELCOME_ICON_SIZE);
        welcomeIcon.setPreserveRatio(true);
        welcomeIcon.setSmooth(true);
        welcomeIcon.setAccessibleText("Auto coffee-cup icon");
        Label greetingLabel = createContentLabel(greeting);
        dialogContainer.getChildren().add(createMessageRow(
                "Auto", false, welcomeIcon, greetingLabel));
    }

    /**
     * Executes the user's command, displays its response, and clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = auto.getResponse(input);
        dialogContainer.getChildren().addAll(
                createMessageBox("You", input, true),
                createMessageBox("Auto", response, false));
        userInput.clear();

        if (input.equals(EXIT_COMMAND)) {
            closeAfterFarewell();
        }
    }

    /** Closes the active window and terminates the application process. */
    private void closeAfterFarewell() {
        userInput.getScene().getWindow().hide();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Creates an aligned message bubble with separate sender and content rows.
     *
     * @param sender  Name displayed above the message.
     * @param message Message displayed in the bubble.
     * @param isUser  Whether the bubble represents a user message.
     * @return Row containing the configured message bubble.
     */
    private HBox createMessageBox(String sender, String message, boolean isUser) {
        return createMessageRow(sender, isUser, createContentLabel(message));
    }

    /** Creates a wrapping label for message content. */
    private Label createContentLabel(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("message-content");
        return messageLabel;
    }

    /** Creates an aligned bubble row containing the supplied content nodes. */
    private HBox createMessageRow(String sender, boolean isUser, Node... contentNodes) {
        Label senderLabel = new Label(sender);
        senderLabel.getStyleClass().add("message-sender");

        VBox bubble = new VBox(MESSAGE_CONTENT_SPACING);
        bubble.getChildren().add(senderLabel);
        bubble.getChildren().addAll(contentNodes);
        bubble.setMaxWidth(MESSAGE_MAX_WIDTH);
        bubble.getStyleClass().addAll("message-bubble", isUser ? "user-bubble" : "auto-bubble");

        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }
}
