package meowmeow.gui;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import meowmeow.Meowmeow;
import meowmeow.Response;
import meowmeow.ResponseKind;

/**
 * Controller for the main window. Wires the input field and Send button to
 * {@link Meowmeow#getResponse(String)} and appends a pair of
 * {@link DialogBox}es (the user's line, then Meowmeow's reply) to the
 * scrolling conversation for each command entered. {@link #setMeowmeow}
 * also shows Meowmeow's {@link Meowmeow#startupMessages() startup messages}
 * once, since that is the first point this window has a backend to ask.
 */
public class MainWindow extends BorderPane {
    /** How long the farewell stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Meowmeow meowmeow;
    private Image userImage;
    private Image meowmeowImage;

    /**
     * Keeps the scroll pane pinned to the newest message as the conversation
     * grows, and loads the avatar images. The images are loaded here rather
     * than in a field initialiser so a missing resource is caught with a
     * clear message instead of an opaque {@link NullPointerException} deep
     * inside {@link javafx.scene.image.Image Image}'s constructor.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userImage = loadImage("/images/DaUser.png");
        meowmeowImage = loadImage("/images/DaMeowmeow.png");
    }

    /**
     * Loads an image from the classpath, failing with a clear message rather
     * than a null pointer deep inside {@link Image}'s constructor if the
     * resource is missing.
     *
     * @param resourcePath the classpath location of the image, e.g. {@code "/images/DaUser.png"}.
     * @return the loaded image.
     */
    private Image loadImage(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing bundled image resource: " + resourcePath);
        }
        return new Image(stream);
    }

    /**
     * Injects the {@link Meowmeow} instance this window talks to, and shows
     * its startup messages (any load warning, then the welcome greeting) as
     * the first bubbles in the conversation.
     *
     * @param meowmeow the backend to route user input through.
     */
    public void setMeowmeow(Meowmeow meowmeow) {
        this.meowmeow = meowmeow;
        for (Response response : meowmeow.startupMessages()) {
            dialogContainer.getChildren().add(DialogBox.getMeowmeowDialog(response, meowmeowImage));
        }
    }

    /**
     * Handles one line of user input: shows it, shows each of Meowmeow's
     * reply messages, and clears the input field. Bound to both the Send
     * button and the Enter key in the FXML. If any reply is
     * {@link ResponseKind#EXIT} (the "bye" reply), input is disabled and the
     * window closes shortly after, so the farewell stays readable.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        boolean isExit = false;
        for (Response response : meowmeow.getResponse(input)) {
            dialogContainer.getChildren().add(DialogBox.getMeowmeowDialog(response, meowmeowImage));
            isExit = isExit || response.kind() == ResponseKind.EXIT;
        }
        userInput.clear();
        if (isExit) {
            closeAfterDelay();
        }
    }

    /** Disables input and closes the window after {@link #EXIT_DELAY}, so the farewell stays readable. */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
