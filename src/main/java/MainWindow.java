import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ralph.Ralph;

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

    private Ralph ralph;

    private final Image userImage = new Image(this
        .getClass()
        .getResourceAsStream("/images/DaUser.png"));
    private final Image ralphImage = new Image(this
        .getClass()
        .getResourceAsStream("/images/DaRalph.png"));

    /**
     * Initializes the scroll pane to follow the dialog container as content grows.
     */
    @FXML
    public void initialize() {
        scrollPane
            .vvalueProperty()
            .bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Ralph instance
     */
    public void setRalph(Ralph r) {
        ralph = r;
        // show initial banner message from Ralph (mirrors CLI startup)
        String banner = ralph.getBanner();
        dialogContainer
            .getChildren()
            .add(DialogBox.getRalphDialog(banner, ralphImage, ""));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Ralph's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = ralph.getResponse(input);
        dialogContainer
            .getChildren()
            .addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRalphDialog(response, ralphImage, ralph.getCommandType())
            );
        userInput.clear();
    }
}
