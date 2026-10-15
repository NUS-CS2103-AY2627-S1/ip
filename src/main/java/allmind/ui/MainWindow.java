package allmind.ui;
import allmind.AllMind;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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

    private AllMind allMind;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Sample_User_Icon.png"));
    private Image allMindImage = new Image(this.getClass().getResourceAsStream("/images/Emblem_ACVI_ALLMIND.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setAllMind(AllMind a) {
        allMind = a;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = allMind.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAllMindDialog(response, allMindImage)
        );
        userInput.clear();

        if (input.equals("bye")) {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.close();
        }
    }
}
