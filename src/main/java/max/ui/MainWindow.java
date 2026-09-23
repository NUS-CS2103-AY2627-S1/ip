package max.ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private Max max;


    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getMaxDialog(new Ui().getGreetings(), null));
    }

    /** Injects the command processor used by the GUI. */
    public void setMax(Max m) {
        max = m;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = max.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, null),
                DialogBox.getMaxDialog(response, null, max.wasLastResponseAnError())
        );
        userInput.clear();

        if (!max.shouldContinue()) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        }
    }
}
