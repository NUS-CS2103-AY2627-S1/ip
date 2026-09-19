package eli;

import java.util.Objects;

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

/** Controls the main GUI window and passes commands to Eli. */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 1.5;

    private final Image userImage = loadImage("/images/user-avatar.png");
    private final Image eliImage = loadImage("/images/eli-avatar.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Eli eli;

    /** Keeps the most recent dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Eli instance that handles commands. */
    public void setEli(Eli eli) {
        assert eli != null : "Eli instance must be provided before accepting input";
        this.eli = eli;
    }

    /** Sends the entered command to Eli and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        assert eli != null : "Eli instance must be set before accepting input";
        String input = userInput.getText();
        String response = eli.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getEliDialog(response, eliImage));
        userInput.clear();

        if (eli.isExitCommand(input)) {
            closeAfterFarewell();
        }
    }

    /** Disables further input and closes the GUI after the farewell is visible. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }

    /** Loads a required classpath image or fails with a clear resource path. */
    private Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}
