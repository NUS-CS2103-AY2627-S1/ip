// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus.ui;

import bingus.Bingus;
import bingus.command.CommandType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Bingus GUI.
 */
public class MainWindow {
    private static final String DEFAULT_INPUT_PROMPT = "Tell Bingus what to remember...";
    private static final String EMPTY_INPUT_PROMPT = "Enter a command first.";
    private static final Duration REPLY_DELAY = Duration.millis(250);
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    private static final double DIALOG_START_OFFSET = 16;
    private static final Duration SLIDE_DURATION = Duration.millis(180);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Bingus bingus;

    private final Image bingusImage = new Image(this.getClass().getResourceAsStream("/images/DaBingus.png"));

    /** Animates the view to the newest message. */
    private Timeline scrollAnimation;

    /**
     * Initializes the chat controls.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "Scroll pane must be injected from FXML";
        assert dialogContainer != null : "Dialog container must be injected from FXML";
        assert userInput != null : "User input field must be injected from FXML";
        scrollPane.setVvalue(1.0);
    }

    /**
     * Injects the Bingus instance used to process user commands.
     *
     * @param bingus Bingus instance to use.
     */
    public void setBingus(Bingus bingus) {
        this.bingus = bingus;

        String loadErrorMessage = bingus.getLoadErrorMessage();
        if (loadErrorMessage != null) {
            DialogBox errorDialog = DialogBox.getBingusDialog(
                    loadErrorMessage,
                    bingusImage,
                    CommandType.INVALID);
            dialogContainer.getChildren().add(errorDialog);
        }

        DialogBox welcomeDialog = DialogBox.getBingusDialog(
                bingus.getWelcomeMessage(),
                bingusImage,
                CommandType.NONE);
        addDialogWithAnimation(welcomeDialog);
    }

    /**
     * Creates and displays a user dialog followed by the corresponding Bingus reply.
     */
    @FXML
    private void handleUserInput() {
        assert bingus != null : "Bingus must be injected before users can submit commands";

        String input = userInput.getText();
        if (input == null || input.trim().isEmpty()) {
            userInput.setPromptText(EMPTY_INPUT_PROMPT);
            userInput.requestFocus();
            return;
        }

        userInput.setPromptText(DEFAULT_INPUT_PROMPT);
        String response = bingus.getResponse(input);
        CommandType commandType = bingus.getCommandType();
        DialogBox userDialog = DialogBox.getUserDialog(input);
        DialogBox botDialog = DialogBox.getBingusDialog(response, bingusImage, commandType);

        addDialogWithAnimation(userDialog);

        PauseTransition replyDelay = new PauseTransition(REPLY_DELAY);
        replyDelay.setOnFinished(event -> {
            addDialogWithAnimation(botDialog);
            if (commandType == CommandType.EXIT) {
                PauseTransition closeDelay = new PauseTransition(EXIT_DELAY);
                closeDelay.setOnFinished(closeEvent -> Platform.exit());
                closeDelay.play();
            }
        });
        replyDelay.play();

        userInput.clear();
        userInput.requestFocus();
    }

    private void addDialogWithAnimation(DialogBox dialogBox) {
        dialogBox.setTranslateY(DIALOG_START_OFFSET);

        dialogContainer.getChildren().add(dialogBox);

        TranslateTransition slideUp = new TranslateTransition(SLIDE_DURATION, dialogBox);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);
        slideUp.setOnFinished(event -> animateScrollToBottom());
        slideUp.play();
    }

    /** Animates the chat view to the newest message. */
    private void animateScrollToBottom() {
        if (scrollAnimation != null) {
            scrollAnimation.stop();
        }

        KeyValue scrollToBottom = new KeyValue(scrollPane.vvalueProperty(), 1.0, Interpolator.EASE_OUT);
        KeyFrame scrollFrame = new KeyFrame(SLIDE_DURATION, scrollToBottom);
        scrollAnimation = new Timeline(scrollFrame);
        scrollAnimation.play();
    }
}
