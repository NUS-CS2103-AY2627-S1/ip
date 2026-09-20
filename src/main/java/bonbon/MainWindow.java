package bonbon;

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

    private BonBon bonbon;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Lyra.png"));
    private Image bonBonImage = new Image(this.getClass().getResourceAsStream("/images/BonBon.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Add greeting message on startup
        String welcomeMessage = "Hello! I'm BonBon!\nWhat can I do for you today?";
        dialogContainer.getChildren().add(
                DialogBox.getBonDialog(welcomeMessage, bonBonImage)
        );
    }

    /**
     * Injects the BonBon application instance into the controller.
     *
     * @param bon The BonBon application instance to link with the GUI.
     */    public void setBonbon(BonBon bon) {
        bonbon = bon;
    }

    /**
     * Creates user and chatbot dialog boxes in response to user input, clears
     * the input text field, and schedules application termination if "bye" is entered.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = BonBon.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBonDialog(response, bonBonImage)
        );
        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> {
                Platform.exit();
                System.exit(0);
            });
            delay.play();
        }
    }
}