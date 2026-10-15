package potato.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import potato.Potato;

/**
 * Controller for the main GUI layout.
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

    private Potato potato;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image potatoImage = new Image(this.getClass().getResourceAsStream("/images/DaPotato.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        this.getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
    }

    public void setPotato(Potato p) {
        potato = p;
        String welcome = potato.getWelcomeMessage();
        dialogContainer.getChildren().add(DialogBox.getPotatoDialog(welcome, potatoImage, false));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        boolean isError = false;
        boolean shouldExit = false;
        String response;
        try {
            response = potato.getResponse(input);
            isError = potato.wasLastResponseError();
            shouldExit = potato.wasLastResponseExit();
        } catch (Exception e) {
            response = e.getMessage();
            isError = true;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPotatoDialog(response, potatoImage, isError)
        );
        userInput.clear();
        if (shouldExit) {
            Platform.exit();
        }
    }
}
