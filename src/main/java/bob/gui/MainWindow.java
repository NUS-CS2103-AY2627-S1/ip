package bob.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import bob.ChatBot;
import bob.ChatResponse;
import bob.ResponseType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main chatbot window.
 */
public class MainWindow {
    private static final String BOB_IMAGE_PATH = "/images/yuai.png";
    private static final String BOB_GREETING = "Hi! I'm Bob, your task companion.";
    private static final String STATUS_TYPING_TEXT = "Bob is thinking";
    private static final String STATUS_TYPING_STYLE = "status-typing";
    private static final String STATUS_ENDED_STYLE = "status-ended";
    private static final String INPUT_INVALID_STYLE = "input-invalid";
    private static final String STATUS_ENDED_TEXT = "Session ended";
    private static final String STATUS_READY_TEXT = "Ready";
    private static final String EMPTY_INPUT_MESSAGE = "Enter a command, or open Help for examples.";
    private static final double HEADER_AVATAR_SIZE = 42.0;
    private static final Duration RESPONSE_DELAY = Duration.millis(650);
    private static final Duration INPUT_FEEDBACK_DURATION = Duration.seconds(2.5);
    private static final Duration TYPING_FRAME_DURATION = Duration.millis(180);
    private static final int TYPING_FRAME_COUNT = 3;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button helpButton;

    @FXML
    private ImageView headerAvatar;

    @FXML
    private Label statusIndicator;

    @FXML
    private Label appTitle;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Tooltip statusTooltip;

    @FXML
    private VBox helpPanel;

    @FXML
    private Label inputFeedback;

    private final Image bobImage = loadImage(BOB_IMAGE_PATH);
    private final PauseTransition inputFeedbackDelay = new PauseTransition(INPUT_FEEDBACK_DURATION);

    private ChatBot chatBot;
    private DialogBox welcomeDialog;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
        userInput.textProperty().addListener((observable, oldText, newText) -> {
            if (!newText.isBlank()) {
                hideInputFeedback();
            }
        });
        inputFeedbackDelay.setOnFinished(event -> hideInputFeedback());
    }

    /**
     * Supplies the chatbot used to process user input.
     *
     * @param chatBot chatbot backing this window.
     */
    public void setChatBot(ChatBot chatBot) {
        this.chatBot = Objects.requireNonNull(chatBot);
        AvatarView.configure(headerAvatar, bobImage, HEADER_AVATAR_SIZE);
        appTitle.setText("Bob");
        rootPane.getStyleClass().add("theme-ocean");
        String welcomeMessage = BOB_GREETING + "\n\n"
                + "Try \"todo read a book\" to add your first task.";
        welcomeDialog = DialogBox.getBobDialog(welcomeMessage, ResponseType.INFO);
        addDialog(welcomeDialog);
    }

    @FXML
    private void handleUserInput() {
        String inputText = userInput.getText();
        Optional<String> validationMessage = validateInput(inputText);
        if (validationMessage.isPresent()) {
            showInputFeedback(validationMessage.get());
            return;
        }

        String input = inputText.trim();
        removeWelcomeDialog();
        setHelpPanelVisible(false);

        ChatResponse response = chatBot.processCommand(input);
        DialogBox typingDialog = DialogBox.getTypingDialog(getTypingText(0));

        addDialog(DialogBox.getUserDialog(input));
        addDialog(typingDialog);

        userInput.clear();
        showPendingResponse(typingDialog, response);
    }

    @FXML
    private void toggleHelpPanel() {
        setHelpPanelVisible(!helpPanel.isVisible());
    }

    /**
     * Validates text before it is sent to the chatbot.
     *
     * @param input text entered in the command field.
     * @return feedback for invalid input, or an empty result for valid input.
     */
    static Optional<String> validateInput(String input) {
        if (input == null || input.isBlank()) {
            return Optional.of(EMPTY_INPUT_MESSAGE);
        }
        return Optional.empty();
    }

    /**
     * Returns one frame of the animated typing message.
     *
     * @param frameIndex zero-based animation frame index.
     * @return typing message containing between one and three dots.
     */
    static String getTypingText(int frameIndex) {
        int dotCount = Math.floorMod(frameIndex, TYPING_FRAME_COUNT) + 1;
        return STATUS_TYPING_TEXT + ".".repeat(dotCount);
    }

    /**
     * Displays and animates a temporary typing dialog before showing Bob's response.
     *
     * @param typingDialog temporary dialog to animate.
     * @param response response that replaces the typing dialog.
     */
    private void showPendingResponse(DialogBox typingDialog, ChatResponse response) {
        setComposerDisabled(true);
        statusIndicator.getStyleClass().add(STATUS_TYPING_STYLE);
        statusIndicator.setAccessibleText(STATUS_TYPING_TEXT);
        statusTooltip.setText(STATUS_TYPING_TEXT);

        Timeline typingAnimation = createTypingAnimation(typingDialog);
        PauseTransition responseDelay = new PauseTransition(RESPONSE_DELAY);
        responseDelay.setOnFinished(event -> {
            typingAnimation.stop();
            replaceTypingDialog(typingDialog, response);
        });

        typingAnimation.play();
        responseDelay.play();
    }

    private Timeline createTypingAnimation(DialogBox typingDialog) {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO, event -> typingDialog.setDialogText(getTypingText(0))),
                new KeyFrame(TYPING_FRAME_DURATION, event -> typingDialog.setDialogText(getTypingText(1))),
                new KeyFrame(TYPING_FRAME_DURATION.multiply(2),
                        event -> typingDialog.setDialogText(getTypingText(2))),
                new KeyFrame(TYPING_FRAME_DURATION.multiply(TYPING_FRAME_COUNT)));
        animation.setCycleCount(Animation.INDEFINITE);
        return animation;
    }

    private void replaceTypingDialog(DialogBox typingDialog, ChatResponse response) {
        int dialogIndex = dialogContainer.getChildren().indexOf(typingDialog);
        DialogBox responseDialog = DialogBox.getBobDialog(response.text(), response.responseType());

        if (dialogIndex >= 0) {
            dialogContainer.getChildren().set(dialogIndex, responseDialog);
        } else {
            dialogContainer.getChildren().add(responseDialog);
        }
        responseDialog.playEntranceAnimation();
        scrollToLatestDialog();

        statusIndicator.getStyleClass().remove(STATUS_TYPING_STYLE);

        if (response.isExit()) {
            statusIndicator.getStyleClass().add(STATUS_ENDED_STYLE);
            statusIndicator.setAccessibleText(STATUS_ENDED_TEXT);
            statusTooltip.setText(STATUS_ENDED_TEXT);
            return;
        }

        setComposerDisabled(false);
        statusIndicator.setAccessibleText(STATUS_READY_TEXT);
        statusTooltip.setText(STATUS_READY_TEXT);
        userInput.requestFocus();
    }

    private void setComposerDisabled(boolean isDisabled) {
        userInput.setDisable(isDisabled);
        sendButton.setDisable(isDisabled);
    }

    private void addDialog(DialogBox dialogBox) {
        dialogContainer.getChildren().add(dialogBox);
        dialogBox.playEntranceAnimation();
        scrollToLatestDialog();
    }

    private void removeWelcomeDialog() {
        if (welcomeDialog == null) {
            return;
        }
        dialogContainer.getChildren().remove(welcomeDialog);
        welcomeDialog = null;
    }

    private void setHelpPanelVisible(boolean isVisible) {
        helpPanel.setManaged(isVisible);
        helpPanel.setVisible(isVisible);
        helpButton.setAccessibleText(isVisible ? "Hide command help" : "Show command help");

        if (!isVisible) {
            userInput.requestFocus();
        }
    }

    private void showInputFeedback(String message) {
        inputFeedback.setText(message);
        inputFeedback.setManaged(true);
        inputFeedback.setVisible(true);
        if (!userInput.getStyleClass().contains(INPUT_INVALID_STYLE)) {
            userInput.getStyleClass().add(INPUT_INVALID_STYLE);
        }
        inputFeedbackDelay.playFromStart();
        userInput.requestFocus();
    }

    private void hideInputFeedback() {
        inputFeedbackDelay.stop();
        inputFeedback.setManaged(false);
        inputFeedback.setVisible(false);
        userInput.getStyleClass().remove(INPUT_INVALID_STYLE);
    }

    private void scrollToLatestDialog() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private static Image loadImage(String path) {
        try (InputStream imageStream = Objects.requireNonNull(MainWindow.class.getResourceAsStream(path),
                "Missing image resource: " + path)) {
            return new Image(imageStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to close image resource: " + path, exception);
        }
    }
}
