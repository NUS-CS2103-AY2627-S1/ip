package della;
import della.command.Command;
import della.ui.UI;
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

    private Della della;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private Image dellaImage = new Image(this.getClass().getResourceAsStream("/images/della.png"));

    /** Injects the Della instance. */
    public void setDella(Della d) {
        this.della = d;
    }

    /**
     * Creates dialog box that welcomes the user.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getDellaDialog(UI.showWelcome(), dellaImage, Command.BYE));
    }


    /**
     * Creates two dialog boxes, one echoing user input and the other containing Della's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert della != null : "Della must be injected before handling user input";
        String input = userInput.getText();
        String response = della.getResponse(input);
        Command command = della.getCommand();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDellaDialog(response, dellaImage, command)
        );
        userInput.clear();
    }
}
