package lumine.ui;

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
import lumine.Lumine;

/**
 * Controls Lumine's main conversation window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Lumine lumine;

    private final Image userImage = new Image(Objects.requireNonNull(
            MainWindow.class.getResource("/images/user.gif")).toExternalForm());
    private final Image lumineImage = new Image(Objects.requireNonNull(
            MainWindow.class.getResource("/images/lumine_profile.png")).toExternalForm());

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
    }

    /**
     * Connects the application logic to this conversation window.
     *
     * @param lumine the application instance that handles user commands.
     */
    public void setLumine(Lumine lumine) {
        this.lumine = Objects.requireNonNull(lumine);
        addLumineDialog(lumine.getGreeting());
    }

    /**
     * Adds the user's message and Lumine's command response to the conversation.
     * Disables input and briefly displays the farewell before closing when an exit is requested.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        if (lumine == null) {
            throw new IllegalStateException("Lumine has not been connected to the main window.");
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage));
        addLumineDialog(lumine.getResponse(input));
        userInput.clear();

        if (lumine.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            // Keep the JavaFX thread free to render the farewell during the delay.
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Adds Lumine's response as a left-aligned dialog box.
     */
    private void addLumineDialog(String response) {
        dialogContainer.getChildren().add(DialogBox.getLumineDialog(response, lumineImage));
    }
}
