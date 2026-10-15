package chudgpt;

import javafx.application.Platform;
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

    private ChudGpt chudGpt;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/ChudGPT.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the application instance and adds its welcome message to the dialog area.
     *
     * @param chud application instance used to process GUI commands.
     */
    public void setChud(ChudGpt chud) {
        assert chud != null : "Main window should receive an application instance";
        chudGpt = chud;
        dialogContainer.getChildren().add(DialogBox.getDukeDialog(chudGpt.getGuiWelcomeMessage(), dukeImage));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert chudGpt != null : "Application should be set before handling input";
        String input = userInput.getText();
        ChudGpt.Response response = chudGpt.getResponseDetails(input);
        DialogBox responseDialog = switch (response.responseType()) {
            case ERROR -> DialogBox.getErrorDialog(response.text(), dukeImage);
            case TASK_ADDED -> DialogBox.getSuccessDialog(response.text(), dukeImage);
            case NORMAL -> DialogBox.getDukeDialog(response.text(), dukeImage);
        };

        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), responseDialog);
        userInput.clear();
        userInput.requestFocus();

        if (chudGpt.hasExited()) {
            disableInput();
        }
    }

    /** Disables the controls after the user exits ChudGPT. */
    private void disableInput() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }
}
