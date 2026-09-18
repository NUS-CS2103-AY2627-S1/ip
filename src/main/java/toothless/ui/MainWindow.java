package toothless.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import toothless.Toothless;

/**
 * Controls the main Toothless chat window.
 */
public class MainWindow {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
    private static final String GREETING = "Hi there! I'm Toothless. It's wonderful to meet you!\n"
            + "Ready to tame some quests? Tap Help above whenever you want a command example.\n"
            + "Tiny roar! ★";
    private static final Image TOOTHLESS_IMAGE = loadImage("/images/toothless-avatar.png");
    private static final Image USER_IMAGE = loadImage("/images/user-avatar.png");
    private static final Duration GOODBYE_DELAY = Duration.millis(1800);

    private Toothless toothless;
    private boolean isShuttingDown;
    private boolean isHandlingInput;

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
    private FlowPane commandButtons;
    @FXML
    private FlowPane editCommandButtons;
    @FXML
    private ScrollPane commandHelp;

    /**
     * Configures scrolling and keyboard focus after the FXML fields are loaded.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollToLatestMessage());
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Connects the window to the chatbot and displays its opening messages.
     *
     * @param toothless chatbot that handles commands entered in this window.
     */
    public void setToothless(Toothless toothless) {
        this.toothless = Objects.requireNonNull(toothless);
        dialogContainer.getChildren().add(DialogBox.getToothlessDialog(GREETING, TOOTHLESS_IMAGE));

        Toothless.Response startupResponse = toothless.getChatStartupResponse();
        if (!startupResponse.text().isBlank()) {
            dialogContainer.getChildren().add(createToothlessDialog(startupResponse));
        }
    }

    /**
     * Sends a non-blank command and adds both sides of the exchange to the conversation.
     * This method is shared by the text field's Enter action and the Send button.
     */
    @FXML
    private void handleUserInput() {
        if (isShuttingDown || isHandlingInput) {
            return;
        }
        if (toothless == null) {
            throw new IllegalStateException("A Toothless instance must be supplied before accepting input");
        }

        String input = userInput.getText();
        userInput.clear();
        if (input == null || input.isBlank()) {
            return;
        }

        isHandlingInput = true;
        try {
            Toothless.Response response = toothless.getCommandResult(input);
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, USER_IMAGE),
                    createToothlessDialog(response));
            if (toothless.hasExited()) {
                scheduleShutdown();
            }
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unable to process a chat command", exception);
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(
                    "Toothless hit a snag with that command. Please try again.", TOOTHLESS_IMAGE));
        } finally {
            isHandlingInput = false;
        }
        scrollToLatestMessage();
    }

    /**
     * Disables further input and closes the window after the farewell is shown.
     */
    private void scheduleShutdown() {
        isShuttingDown = true;
        userInput.setPromptText("The adventure continues another day!");
        userInput.setDisable(true);
        sendButton.setDisable(true);
        helpButton.setDisable(true);
        commandButtons.setDisable(true);
        editCommandButtons.setDisable(true);

        PauseTransition delay = new PauseTransition(GOODBYE_DELAY);
        delay.setOnFinished(event -> {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
            Platform.exit();
        });
        delay.play();
    }

    /**
     * Places a friendly example command into the composer so the user can edit or send it.
     *
     * @param event click from one of the Help panel's suggestion buttons.
     */
    @FXML
    private void useCommandSuggestion(ActionEvent event) {
        Button suggestionButton = (Button) event.getSource();
        String suggestedCommand = Objects.toString(suggestionButton.getUserData(), "");
        userInput.setText(suggestedCommand);
        userInput.positionCaret(suggestedCommand.length());
        userInput.requestFocus();
    }

    /**
     * Shows or hides the command examples beneath the header.
     */
    @FXML
    private void toggleHelp() {
        boolean shouldShowHelp = !commandHelp.isVisible();
        commandHelp.setVisible(shouldShowHelp);
        commandHelp.setManaged(shouldShowHelp);
        if (!shouldShowHelp) {
            editCommandButtons.setVisible(false);
            editCommandButtons.setManaged(false);
        }
        if (shouldShowHelp) {
            Platform.runLater(userInput::requestFocus);
        }
    }

    /**
     * Shows or hides the type-specific edit command examples.
     */
    @FXML
    private void toggleEditHelp() {
        boolean shouldShowEditHelp = !editCommandButtons.isVisible();
        editCommandButtons.setVisible(shouldShowEditHelp);
        editCommandButtons.setManaged(shouldShowEditHelp);
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Moves the conversation viewport to its newest message after layout finishes.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    /**
     * Applies error styling only when the command workflow reports an error.
     */
    private DialogBox createToothlessDialog(Toothless.Response response) {
        if (response.isError()) {
            return DialogBox.getErrorDialog(response.text(), TOOTHLESS_IMAGE);
        }
        return DialogBox.getToothlessDialog(response.text(), TOOTHLESS_IMAGE);
    }

    /**
     * Loads an avatar packaged with the application.
     *
     * @param resourcePath absolute classpath location of the avatar.
     * @return loaded avatar image, or {@code null} for the image view's empty fallback.
     */
    private static Image loadImage(String resourcePath) {
        try (InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (imageStream == null) {
                return null;
            }
            Image image = new Image(imageStream);
            return image.isError() ? null : image;
        } catch (IOException | IllegalArgumentException exception) {
            return null;
        }
    }
}
