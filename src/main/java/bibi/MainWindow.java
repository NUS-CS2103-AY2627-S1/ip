package bibi;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window: the transcript, the input box, and the send button.
 *
 * <p>It does no task handling of its own. Every command is passed to {@link Bibi},
 * which is the same object the console interface drives, and the reply that comes
 * back is put on screen as a {@link DialogBox}.
 */
public class MainWindow {
    private static final String USER_IMAGE = "/images/DaUser.png";
    private static final String BIBI_IMAGE = "/images/DaBibi.png";

    /**
     * How long the goodbye stays on screen before the window closes. Long enough
     * to read, short enough not to feel stuck.
     */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Bibi bibi;

    private final Image userImage = loadImage(USER_IMAGE);
    private final Image bibiImage = loadImage(BIBI_IMAGE);

    /**
     * Keeps the transcript scrolled to the newest message.
     *
     * <p>Called by the FXML loader once the window's parts have been created.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot this window talks to, and shows its greeting.
     *
     * <p>Separate from the constructor because the FXML loader builds the
     * controller itself and cannot pass arguments to it.
     *
     * @param bibi the chatbot that answers what the user types
     */
    public void setBibi(Bibi bibi) {
        this.bibi = bibi;
        dialogContainer.getChildren().add(DialogBox.forBibi(bibi.getGreeting(), bibiImage));
    }

    /**
     * Answers whatever the user has typed, then clears the input box.
     *
     * <p>Wired to both the send button and the Enter key by the FXML.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = bibi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.forUser(input, userImage),
                DialogBox.forBibi(response, bibiImage));
        userInput.clear();

        if (bibi.isExitRequested()) {
            closeAfterGoodbye();
        }
    }

    /**
     * Closes the window a moment after Bibi has said goodbye.
     *
     * <p>Closing immediately would wipe the parting message off the screen before
     * anyone could read it.
     */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);

        PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Reads one of the pictures packaged with the application.
     *
     * @param resourcePath the image's path inside the resources folder
     * @return the loaded image
     */
    private static Image loadImage(String resourcePath) {
        InputStream stream = MainWindow.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing bundled image: " + resourcePath);
        }
        return new Image(stream);
    }
}
