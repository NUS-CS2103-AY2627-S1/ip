package snoopy.gui;

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
import snoopy.Snoopy;

/**
 * Controls the main Snoopy user interface defined in MainWindow.fxml.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    private final Image snoopyImage = new Image(
            getClass().getResourceAsStream("/images/snoopy-avatar.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Snoopy snoopy;

    /**
     * Initializes scrolling and keyboard focus after FXML injects the controls.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the Snoopy instance and displays its welcome message.
     *
     * @param snoopy Snoopy instance used to process commands.
     */
    public void setSnoopy(Snoopy snoopy) {
        this.snoopy = snoopy;
        dialogContainer.getChildren().add(
                DialogBox.getSnoopyDialog(snoopy.getGuiWelcomeMessage(), snoopyImage));
    }

    /**
     * Displays the user's command and Snoopy's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = snoopy.getResponse(input);
        String displayResponse = response.stripLeading();
        DialogBox responseDialog = isErrorResponse(response)
                ? DialogBox.getErrorDialog(displayResponse, snoopyImage)
                : DialogBox.getSnoopyDialog(displayResponse, snoopyImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog
        );
        userInput.clear();

        if (snoopy.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }

    /**
     * Checks whether a response represents a command-processing error.
     *
     * @param response Response returned by Snoopy.
     * @return {@code true} when the response begins with Snoopy's error marker.
     */
    static boolean isErrorResponse(String response) {
        assert response != null : "Response must be provided";
        return response.stripLeading().startsWith("OOPS!");
    }
}
