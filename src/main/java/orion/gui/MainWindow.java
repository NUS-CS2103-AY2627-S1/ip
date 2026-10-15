package orion.gui;

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
import orion.Orion;
import orion.exception.OrionException;
import orion.parser.Parser;

/**
 * Controller for the main GUI. Provides the layout for the other controls.
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

    private Orion orion;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private Image orionImage = new Image(this.getClass().getResourceAsStream("/images/orion.jpg"));

    /**
     * Initializes the scroll pane to automatically scroll to the bottom.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setOrion(Orion orion) {
        assert orion != null : "Orion instance should be successfully initialised before setting";
        this.orion = orion;
        dialogContainer.getChildren().add(DialogBox.getOrionDialog(this.orion.getWelcomeMessage(), orionImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Orion's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox orionDialog;
        try {
            orionDialog = DialogBox.getOrionDialog(orion.getResponse(input), orionImage);
        } catch (OrionException e) {
            orionDialog = DialogBox.getOrionErrorDialog(e.getMessage(), orionImage);
        }
        dialogContainer.getChildren().addAll(userDialog, orionDialog);
        userInput.clear();

        if (Parser.isExit(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
