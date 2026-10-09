package sumo.gui;

import java.net.URL;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import sumo.Sumo;

/** Controls Sumo's main chat window. */
public class MainWindow extends AnchorPane {
    private static final String USER_IMAGE_PATH = "/images/UserPfp.png";
    private static final String SUMO_IMAGE_PATH = "/images/SumoPfp.png";
    private static final double SCROLL_SPEED_MULTIPLIER = 1.3;
    private static final Duration EXIT_DELAY = Duration.seconds(5);

    private final Image userImage = loadImage(USER_IMAGE_PATH);
    private final Image sumoImage = loadImage(SUMO_IMAGE_PATH);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label sessionStatus;
    @FXML
    private Circle statusRing;

    private Sumo sumo;

    /** Creates the chat window controller used by the FXML loader. */
    public MainWindow() {
    }

    /** Keeps the newest dialog visible when the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
    }

    /** Moves wheel and trackpad scrolling slightly farther, while keeping it within the conversation. */
    private void handleScroll(ScrollEvent event) {
        double scrollableHeight = dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight();
        if (scrollableHeight <= 0 || event.getDeltaY() == 0 || event.isControlDown()) {
            return;
        }
        double position = scrollPane.getVvalue() - event.getDeltaY() * SCROLL_SPEED_MULTIPLIER
                / scrollableHeight * (scrollPane.getVmax() - scrollPane.getVmin());
        scrollPane.setVvalue(Math.clamp(position, scrollPane.getVmin(), scrollPane.getVmax()));
        event.consume();
    }

    /**
     * Supplies the Sumo instance that handles commands and displays its greeting.
     *
     * @param sumo Sumo application to use for this window.
     */
    public void setSumo(Sumo sumo) {
        this.sumo = sumo;
        dialogContainer.getChildren().add(
                DialogBox.getSumoDialog("My name is Sumo.\nI'm here to help you stay on task.", sumoImage));
        if (!sumo.getStartupMessage().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getSumoDialog(sumo.getStartupMessage(), sumoImage));
        }
    }

    /** Displays the user's command followed by Sumo's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank() || sumo == null || sumo.isExit()) {
            return;
        }

        String response = sumo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSumoDialog(response, sumoImage));
        userInput.clear();
        userInput.requestFocus();

        if (sumo.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            sessionStatus.setText("Closing in 5 seconds");
            statusRing.getStyleClass().add("ended");
            // Keep the farewell visible without blocking JavaFX rendering or scrolling.
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /** Loads an optional avatar, returning {@code null} while the image is absent. */
    private static Image loadImage(String resourcePath) {
        URL imageUrl = MainWindow.class.getResource(resourcePath);
        return imageUrl == null ? null : new Image(imageUrl.toExternalForm());
    }
}
