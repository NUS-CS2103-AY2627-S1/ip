package clover.ui;

import clover.Clover;
import clover.command.CommandResponseStyle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(700);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Clover clover;
    private Image userImage = new Image(getClass().getResourceAsStream("/images/curiousKitty.jpeg"));
    private Image cloverImage = new Image(getClass().getResourceAsStream("/images/clover-forest-sprite.png"));

    /**
     * Injects the Clover instance.
     *
     * @param cloverInstance the Clover instance that generates responses
     */
    public void setClover(Clover cloverInstance) {
        clover = cloverInstance;
        dialogContainer.getChildren().add(DialogBox.getCloverDialog(
                "Welcome to Clover's study grove.\n"
                        + "What learning quest shall we tend today?", cloverImage));
        scrollToLatestMessage();
    }

    /**
     * Adds dialogs for the user's input and Clover's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.clear();
            userInput.requestFocus();
            return;
        }
        String response = clover.getResponse(input);
        CommandResponseStyle responseStyle = clover.getResponseStyle();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getCloverDialog(addForestSpriteReaction(response, responseStyle), cloverImage, responseStyle)
        );
        scrollToLatestMessage();
        userInput.clear();
        if (clover.isExitRequested()) {
            closeAfterFarewell();
        } else {
            userInput.requestFocus();
        }
    }

    /** Disables further input and closes Clover after its farewell is visible briefly. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /**
     * Adds a context-appropriate forest-sprite introduction to GUI responses.
     * The command result follows the introduction so it remains easy to read.
     *
     * @param response the command result to display
     * @param responseStyle the visual style selected for the result
     * @return the response with a BunBun reaction when one is appropriate
     */
    static String addForestSpriteReaction(String response, CommandResponseStyle responseStyle) {
        return switch (responseStyle) {
            case TASK_ADDED, TUTOREE_ADDED, TASK_MARKED, TASK_DELETED, ERROR -> response;
            case STANDARD -> addStandardForestSpriteIntroduction(response);
        };
    }

    /**
     * Adds a forest-sprite introduction to standard command responses where the content reveals the action.
     *
     * @param response the standard command result to display
     * @return the response with a context-appropriate introduction
     */
    private static String addStandardForestSpriteIntroduction(String response) {
        return response;
    }

    /** Scrolls to the newest message after JavaFX calculates its final size. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }
}
