package sal;

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

    private Sal sal;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image salImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Binds the scroll pane so it stays at the latest dialog after each message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Sal instance and shows the welcome message.
     *
     * @param s Chatbot instance used to generate replies.
     */
    public void setSal(Sal s) {
        sal = s;
        dialogContainer.getChildren().add(DialogBox.getSalDialog(sal.getWelcomeMessage(), salImage, false));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Sal's reply,
     * then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        ChatResponse response = sal.getChatResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSalDialog(response.getMessage(), salImage, response.isError())
        );
        userInput.clear();

        if (sal.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
