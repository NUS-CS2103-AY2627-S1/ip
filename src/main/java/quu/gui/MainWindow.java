package quu.gui;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import quu.CommandType;
import quu.Quu;

/**
 * Controls the main JavaFX window and connects it to a {@link Quu} instance.
 */
public class MainWindow {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    /**
     * Width and height, in pixels, at which the avatars are decoded. The source images are
     * 150x150 and are shown much smaller, so decoding once at this size gives a smoother
     * result than scaling the full-size image on every frame.
     */
    private static final int AVATAR_RESOLUTION = 144;

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

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image quuImage = loadImage("/images/DaQuu.png");
    private Quu quu;

    /**
     * Sets up scrolling and input focus once the FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null && dialogContainer != null
                : "MainWindow.fxml declares an fx:id for the scroll pane and the dialog container";
        assert userInput != null && sendButton != null
                : "MainWindow.fxml declares an fx:id for the text field and the send button";

        // A listener, not a binding: binding vvalue makes it read-only, which stops the
        // user from scrolling back up through the conversation.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));

        // The input field can only take focus once the window is showing.
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Connects the chatbot to this window and displays its opening messages.
     *
     * @param quu the chatbot this window communicates with
     */
    public void setQuu(Quu quu) {
        this.quu = quu;
        CommandType commandType = quu.getCommandType();
        dialogContainer.getChildren().add(DialogBox.getQuuDialog(quu.getGreeting(), quuImage, commandType));
        if (!quu.getLoadMessage().isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getQuuDialog(quu.getLoadMessage(), quuImage, commandType));
        }
    }

    /**
     * Adds the user's input and Quu's response to the dialog container.
     *
     * <p>Blank input is ignored rather than sent on, so that an accidental press of Enter
     * does not leave an empty message and an error in the conversation.
     */
    @FXML
    private void handleUserInput() {
        assert quu != null : "Main.start() calls setQuu before the window is shown";
        String input = userInput.getText();
        if (input.isBlank()) {
            LOGGER.fine("Ignoring blank input");
            userInput.clear();
            return;
        }

        String response = quu.getResponse(input);
        CommandType commandType = quu.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getQuuDialog(response, quuImage, commandType));
        userInput.clear();

        if (quu.isExitCommand(input)) {
            closeAfterFarewell();
        }
    }

    /**
     * Closes the window once the farewell has been on screen long enough to read.
     *
     * <p>Input is disabled first, so that no further command can be entered during the pause.
     */
    private void closeAfterFarewell() {
        LOGGER.info("Exit command received; the window will close shortly");
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Loads an avatar from the classpath, decoded at {@link #AVATAR_RESOLUTION}.
     *
     * @param resourcePath the absolute classpath location of the image
     * @return the loaded image
     * @throws IllegalStateException If the image is not on the classpath.
     */
    private Image loadImage(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            LOGGER.log(Level.SEVERE, "Missing image resource: {0}", resourcePath);
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }
        return new Image(stream, AVATAR_RESOLUTION, AVATAR_RESOLUTION, true, true);
    }
}
