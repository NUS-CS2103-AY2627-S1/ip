package cooper.ui;

import cooper.CommandResult;
import cooper.Cooper;
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
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    private Cooper cooper;
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/tom.jpg"));
    private final Image cooperImage = new Image(this.getClass().getResourceAsStream("/images/cooper.png"));

    /**
     * Keeps the newest dialog visible whenever the dialog container grows.
     */
    @FXML
    public void initialize() {
        // Scroll values range from 0 to 1, so a listener is clearer than binding them to a pixel height.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Adds any number of dialogs to the conversation.
     * @param dialogs dialogs to display in order
     */
    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }

    /**
     * Displays the user's command followed by Cooper's reply.
     *
     * @param input Command entered by the user.
     * @param reply Cooper's response and command status.
     */
    private void displayExchange(String input, CommandResult reply) {
        addDialogs(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getCooperDialog(reply.message(), cooperImage, reply.isError())
        );
    }

    /**
     * Injects Cooper and displays the startup message after FXML fields have been initialized.
     *
     * @param cooper Cooper instance used to process commands.
     */
    public void setCooper(Cooper cooper) {
        this.cooper = cooper;
        addDialogs(DialogBox.getCooperDialog(
                cooper.getStartupMessage(), cooperImage
        ));
    }

    /** Disables further input and closes the application after goodbye message is shown briefly. */
    private void scheduleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished((event) -> Platform.exit());
        delay.play();
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Cooper's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        CommandResult response = cooper.getResponse(input);
        displayExchange(input, response);
        userInput.clear();

        if (response.shouldExit()) {
            scheduleExit();
        }
    }
}
