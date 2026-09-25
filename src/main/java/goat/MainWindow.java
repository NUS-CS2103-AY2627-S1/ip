package goat;

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
 * Controls the main chat window defined in {@code MainWindow.fxml}.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(650);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image goatImage = new Image(
            getClass().getResourceAsStream("/images/DaGoat.png"));

    private Goat goat;

    /**
     * Configures the conversation to scroll to its newest dialog.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the application and displays its startup message.
     *
     * @param goat application that processes commands
     */
    public void setGoat(Goat goat) {
        this.goat = goat;
        dialogContainer.getChildren().add(
                DialogBox.getGoatDialog(goat.getWelcomeMessage(), goatImage));
    }

    /**
     * Displays the user's command and Goat's response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = goat.getResponse(input);
        DialogBox responseDialog = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response, goatImage)
                : DialogBox.getGoatDialog(response, goatImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog);
        userInput.clear();

        if (goat.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
