package alfred.gui;

import alfred.Alfred;
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
 * Controller for the main chat window.
 */
public class MainWindow extends AnchorPane {
    /** Pause before closing the window after the user types {@code bye}. */
    private static final double EXIT_DELAY_SECONDS = 1.5;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Alfred alfred;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image alfredImage = new Image(this.getClass().getResourceAsStream("/images/alfred.png"));

    /**
     * Keeps the conversation scrolled to the latest message and focuses the command field.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Injects the Alfred instance and shows the opening greeting.
     *
     * @param alfred Chatbot used to produce replies.
     */
    public void setAlfred(Alfred alfred) {
        // Main.start() injects the application Alfred before showing the window.
        assert alfred != null : "MainWindow needs an Alfred instance before showing a greeting";
        this.alfred = alfred;
        String greeting = alfred.getGreeting();
        dialogContainer.getChildren().add(
                DialogBox.getAlfredDialog(greeting, alfredImage, alfred.getLastReplyStyle()));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Alfred's reply,
     * then appends them to the dialog container. Clears the user input after processing.
     * Closes the application after a short pause when the user types {@code bye}.
     */
    @FXML
    private void handleUserInput() {
        assert alfred != null : "User input is handled only after Alfred is injected";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = alfred.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAlfredDialog(response, alfredImage, alfred.getLastReplyStyle())
        );
        userInput.clear();

        if (alfred.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
