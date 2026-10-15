package noms.gui;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import noms.Noms;
import noms.NomsResponse;

/**
 * Controller for the main GUI window.
 *
 * It connects the text field and Send button to {@link Noms#getResponse}, and
 * renders each turn of the conversation as a pair of {@link DialogBox} bubbles
 * (the user's message and Noms' reply).
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage("/images/User.png");
    private final Image nomsImage = loadImage("/images/Noms.png");

    private Noms noms;

    /**
     * Keeps the scroll pane pinned to the newest message as the dialog grows.
     * Called automatically by the FXML loader after the fields are injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax())));
    }

    /**
     * Injects the Noms back end the GUI should talk to and shows its greeting.
     *
     * @param noms the Noms instance that answers each command
     */
    public void setNoms(Noms noms) {
        this.noms = noms;
        dialogContainer.getChildren().add(
                DialogBox.getNomsDialog(noms.getGreeting(), nomsImage));
    }

    /**
     * Handles one line of user input: echoes it as a user bubble, shows Noms'
     * reply as a Noms bubble, and clears the input field. Blank input is
     * ignored so an accidental empty Enter does nothing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        NomsResponse response = noms.getResponse(input);
        DialogBox responseDialog = response.isError()
                ? DialogBox.getErrorDialog(response.text(), nomsImage)
                : DialogBox.getNomsDialog(response.text(), nomsImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog);
        userInput.clear();

        if (noms.isExitRequested()) {
            // Pause briefly so the user can read the farewell, then close.
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Loads an avatar image from the classpath, returning {@code null} when the
     * file is absent so the GUI still runs without the optional pictures.
     *
     * @param resourcePath classpath path of the image, e.g. {@code /images/User.png}
     * @return the loaded image, or {@code null} if it could not be found
     */
    private static Image loadImage(String resourcePath) {
        InputStream stream = MainWindow.class.getResourceAsStream(resourcePath);
        return stream == null ? null : new Image(stream);
    }
}
