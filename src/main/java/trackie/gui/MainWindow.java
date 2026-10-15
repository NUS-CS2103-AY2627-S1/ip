package trackie.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls Trackie's main chat window. */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user.png"));
    private final Image trackieImage = new Image(getClass().getResourceAsStream("/images/trackie.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private TrackieGui trackie;

    /** Keeps the newest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Trackie instance and displays its welcome message.
     *
     * @param trackie Trackie's GUI-facing command adapter
     */
    public void setTrackie(TrackieGui trackie) {
        this.trackie = trackie;
        dialogContainer.getChildren().add(
                DialogBox.getTrackieDialog(trackie.getWelcomeMessage(), trackieImage));
    }

    /** Adds the user's command and Trackie's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = trackie.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTrackieDialog(response, trackieImage));
        userInput.clear();

        if (trackie.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
