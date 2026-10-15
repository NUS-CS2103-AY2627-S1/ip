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
import sunny.Sunny;
/**
 * Controller for the main GUI.
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

    private Sunny sunny;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image sunnyImage = new Image(this.getClass().getResourceAsStream("/images/sunny.png"));

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        dialogContainer.setFillWidth(true);
    }

    /** Injects the Sunny instance */
    public void setSunny(Sunny d) {
        sunny = d;
        dialogContainer.getChildren().add(DialogBox.getSunnyDialog(sunny.getGreeting(), sunnyImage,"greeting"));
    }

    private void closeAfterDelay() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Sunny's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = sunny.getResponse(input);
        String commandType = sunny.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSunnyDialog(response, sunnyImage, commandType)
        );
        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        if (input.equalsIgnoreCase("bye")) {
            closeAfterDelay();
        }

        userInput.clear();
    }
}

