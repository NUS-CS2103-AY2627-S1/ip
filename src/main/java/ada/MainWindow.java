package ada;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    private Ada ada;

    private final Image userImage =
            new Image(getClass().getResourceAsStream("/images/0601.png"));

    private final Image adaImage =
            new Image(getClass().getResourceAsStream("/images/0319.png"));

    /**
     * Initializes the main window after its FXML elements are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Ada instance used to process user commands.
     *
     * @param ada Ada application instance
     */
    public void setAda(Ada ada) {
        this.ada = ada;

        dialogContainer.getChildren().add(
                DialogBox.getAdaDialog(
                        ada.getWelcomeText(),
                        adaImage,
                        "welcome"));
    }

    /**
     * Processes user input and displays the user and Ada dialog boxes.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isBlank()) {
            return;
        }

        String response = ada.getResponse(input);
        String commandType = ada.wasLastResponseError()
                ? "error"
                : getCommandType(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAdaDialog(response, adaImage, commandType));

        userInput.clear();

        if (commandType.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private String getCommandType(String input) {
        return input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
    }
}
