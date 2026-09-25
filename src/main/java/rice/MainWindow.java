package rice;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

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

    private Rice rice;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Student.png"));
    private Image riceImage = new Image(this.getClass().getResourceAsStream("/images/RiceCooker.jpg"));

    /**
     * Initialises the chat window and keeps the scroll view pinned to the latest message.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> Platform.runLater(() -> scrollPane.setVvalue(1.0)));
    }

    /**
     * Injects the Rice application instance and displays the initial greeting.
     *
     * @param r the Rice application logic to use
     */
    public void setRice(Rice r) {
        rice = r;
        dialogContainer.getChildren().add(DialogBox.getRiceDialog(rice.displayList(), riceImage));
    }

    /**
     * Reads a user command, processes it, and shows the appropriate response or correction dialog.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String response = rice.getResponse(input);
        String suggestion = rice.getSuggestion(response);
        if (suggestion == null) {
            showNormalResponse(input, response);
        } else {
            showErrorResponse(input, response, suggestion);
        }
        userInput.clear();
    }

    private void showNormalResponse(String input, String response) {
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRiceDialog(response, riceImage));
    }

    private void showErrorResponse(String input, String response, String suggestion) {
        dialogContainer.getChildren().addAll(
                DialogBox.getErrorDialog(response, input, riceImage),
                DialogBox.getSuggestionDialog(suggestion, riceImage));
    }
}
