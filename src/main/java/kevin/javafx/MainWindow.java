package kevin.javafx;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kevin.Kevin;
import kevin.Parser;

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

    private Kevin kevin;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User Icon.png"));
    private Image kevinImage = new Image(this.getClass().getResourceAsStream("/images/Kevin Icon.png"));

    /** Injects the Kevin instance */
    public void setKevin(Kevin kevin) {
        this.kevin = kevin;
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().addAll(
                DialogBox.getKevinDialog(kevin.getBanner(), kevinImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Kevin's reply
     * Clears the user input after processing.
     *
     * If input is bye, disables userInput and sendButton, and closes GUI after 2 seconds.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Parser parser = new Parser(input);
        String response = kevin.respond(parser);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getKevinDialog(response, kevinImage)
        );
        userInput.clear();

        if (parser.isBye()) {
            sendButton.setDisable(true);
            userInput.setDisable(true);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

