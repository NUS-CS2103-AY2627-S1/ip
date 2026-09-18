package meowmeow.ui;

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
import meowmeow.MeowMeow;

/**
 * Controller for the main GUI.
 * <p>
 * Reused from the SE-EDU JavaFX tutorial, part 4
 * (https://se-education.org/guides/tutorials/javaFxPart4.html),
 * with modifications: renamed from Duke, shows a welcome message on startup,
 * and closes the window a short time after the bye command.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private MeowMeow meowMeow;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Meow2.JPG"));
    private Image meowMeowImage = new Image(this.getClass().getResourceAsStream("/images/Meow1.jpg"));
    private static final Duration EXIT_DELAY = Duration.seconds(2);

    /**
     * Prepares the window after FXML loading, binding the scroll position to the
     * dialog container so that new messages scroll into view.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the MeowMeow instance and shows its greeting.
     *
     * @param m Chatbot instance to route user input to.
     */
    public void setMeowMeow(MeowMeow m) {
        meowMeow = m;
        dialogContainer.getChildren().add(
                DialogBox.getMeowMeowDialog(meowMeow.getWelcome(), meowMeowImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing MeowMeow's reply and then appends
     * them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response;
        try {
            response = meowMeow.getResponse(input);
        } catch (Exception e) {
            response = "Something went wrong inside me: " + e;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMeowMeowDialog(response, meowMeowImage)
        );
        userInput.clear();

        if (meowMeow.isExit()) {
            exitAfterDelay();
        }
    }

    /**
     * Closes the window once the farewell message has been on screen long enough to read.
     */
    private void exitAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(EXIT_DELAY);
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}