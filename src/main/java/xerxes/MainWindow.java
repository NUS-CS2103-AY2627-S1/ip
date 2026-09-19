package xerxes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import xerxes.parser.CommandResult;
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

    private Xerxes xerxes;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/oldMan1.jpg"));
    private final Image xerxesImage = new Image(this.getClass().getResourceAsStream("/images/oldMan2.jpg"));

    /**
     * Initialises the dialog container and displays the introductory message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        String introduction = "Yo wassup! I'm Xerxes.\nWhat do ya need?";
        dialogContainer.getChildren().add(
                DialogBox.getXerxesDialog(introduction, xerxesImage, false)
        );
    }

    /** Injects the Xerxes instance. */
    public void setXerxes(Xerxes xerxes) {
        this.xerxes = xerxes;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Xerxes's reply and then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        CommandResult result = xerxes.executeCommand(input.trim());
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getXerxesDialog(result.getMessage(), xerxesImage, result.isError())
        );
        userInput.clear();

        if (result.shouldExit()) {
            Platform.exit();
        }
    }
}
