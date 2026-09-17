package penny;

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

    private Penny penny;

    private final Image imageUser = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image imagePenny = new Image(this.getClass().getResourceAsStream("/images/DaPenny.png"));
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Penny instance */
    public void setPenny(Penny penny) {
        this.penny = penny;
        if (!penny.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getPennyDialog(penny.getStartupWarning(), imagePenny));
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Penny's reply and then appends them to
     * the dialog container. Clears the user input after processing. If the input was the exit command, closes the
     * app shortly after so the user still sees Penny's goodbye message.
     */
    @FXML
    private void handleUserInput() {
        assert penny != null : "Penny instance must be injected via setPenny before handling input";

        String input = userInput.getText();
        ParseResult result = penny.respond(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, imageUser),
                DialogBox.getPennyDialog(result.message(), imagePenny)
        );
        userInput.clear();

        if (result.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

