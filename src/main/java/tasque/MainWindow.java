package tasque;

import java.io.InputStream;
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

/**
 * Controls the main Tasque window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1.0);
    // Load at twice the 40px display size for high-density screens.
    private static final double AVATAR_IMAGE_SIZE = 80.0;

    private final Image tasqueImage = loadImage("/images/tasque.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Tasque tasque;

    /**
     * Creates the controller for the main Tasque window.
     */
    public MainWindow() {
    }

    /**
     * Supplies the Tasque backend used to process user commands.
     *
     * @param tasque Tasque backend for this window.
     */
    public void setTasque(Tasque tasque) {
        this.tasque = tasque;
        addResponseCard(ResponseCard.createStandalone(tasque.getWelcomeResponse(), this.tasqueImage));
        Platform.runLater(this.userInput::requestFocus);
    }

    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText();
        TasqueResponse response = this.tasque.getCommandResponse(input);
        addResponseCard(ResponseCard.createExchange(input, response, this.tasqueImage));
        this.userInput.clear();

        if (this.tasque.isExitCommand(input)) {
            exitAfterDelay();
        } else {
            this.userInput.requestFocus();
        }
    }

    private void addResponseCard(ResponseCard card) {
        this.dialogContainer.getChildren().add(card);
        Platform.runLater(() -> {
            // Measure wrapped content before following the newly added result.
            this.scrollPane.applyCss();
            this.scrollPane.layout();
            this.scrollPane.setVvalue(this.scrollPane.getVmax());
        });
    }

    private void exitAfterDelay() {
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    private static Image loadImage(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath);
        return new Image(imageStream, AVATAR_IMAGE_SIZE, AVATAR_IMAGE_SIZE, true, true);
    }
}
