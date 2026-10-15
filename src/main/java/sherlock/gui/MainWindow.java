package sherlock.gui;

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
import sherlock.Sherlock;

/**
 * Controls the main chat window defined in {@code MainWindow.fxml}.
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

    // Public domain: https://commons.wikimedia.org/wiki/File:Portrait_of_Sherlock_Holmes_by_Sidney_Paget-Cropped.jpg
    private final Image sherlockImage = loadImage("/images/Sherlock.jpg");
    // Public domain: https://commons.wikimedia.org/wiki/File:Paget_holmes.png
    private final Image userImage = loadImage("/images/User.png");
    private Sherlock sherlock;

    /** Keeps the newest dialog visible when the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the application logic used to answer commands.
     *
     * @param sherlock application instance shared by all dialog interactions
     */
    public void setSherlock(Sherlock sherlock) {
        this.sherlock = sherlock;
        dialogContainer.getChildren().add(
                DialogBox.getSherlockDialog(sherlock.getWelcomeMessage(), sherlockImage));
    }

    /** Echoes a non-blank command, obtains Sherlock's response, and clears the input. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = sherlock.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSherlockDialog(response, sherlockImage, sherlock.isErrorResponse(response)));
        userInput.clear();
        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Loads a required image bundled with the application.
     *
     * @param resourcePath absolute classpath location of the image
     * @return loaded JavaFX image
     */
    private Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}
