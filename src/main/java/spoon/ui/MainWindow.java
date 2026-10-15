package spoon.ui;

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
import spoon.Spoon;

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

    private Spoon spoon;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private Image spoonImage = new Image(this.getClass().getResourceAsStream("/images/spoon.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Spoon instance and sends the introductory message.
     */
    public void setSpoon(Spoon spoon) {
        this.spoon = spoon;

        dialogContainer.getChildren().add(
                DialogBox.getSpoonDialog(spoon.getIntroduction(), spoonImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = spoon.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSpoonDialog(response, spoonImage)
        );
        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

