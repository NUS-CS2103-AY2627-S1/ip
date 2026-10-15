package bos;

import javafx.fxml.FXML;
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

    private Bos bos;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image bosImage = new Image(getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Configures the dialog pane after its FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null && dialogContainer != null && userInput != null
                : "Main window controls must be injected before initialization";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Bos instance that will respond to user input.
     *
     * @param bos chatbot used by this window.
     */
    public void setBos(Bos bos) {
        this.bos = bos;
        dialogContainer.getChildren().add(DialogBox.createBosDialog(bos.getGreeting(), bosImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Bos's reply, and appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert bos != null : "Bos must be configured before handling input";

        String input = userInput.getText();
        String response = bos.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, userImage),
                DialogBox.createBosDialog(response, bosImage));
        userInput.clear();
    }
}
