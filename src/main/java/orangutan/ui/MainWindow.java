package orangutan.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import orangutan.Orangutan;

/**
 * controller for main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String INIT_COMMAND_NAME = "init";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Orangutan orangutan;
    // taken from www.pexels.com/photo/portrait-of-man-in-suit-10041264/
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    // taken from www.orangutans-sos.org/content/uploads/2025/04/Three-Faces-of-the-Forest.jpg
    private final Image orangutanImage = new Image(this.getClass().getResourceAsStream("/images/orangutan.png"));
    // image location given relative to main/resources

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Loads saved data from file (if any) and returns a welcome message.
     * If load has failed, will instead return an appropriate error message and not accept user input.
     */
    public void welcome() {
        assert orangutan != null : "MainWindow says: orangutan should not be null!!!";
        assert dialogContainer != null : "MainWindow says: dialogContainer should not be null!!!";

        String welcome = orangutan.getResponse(INIT_COMMAND_NAME);
        dialogContainer.getChildren().addAll(
                DialogBox.getOrangutanDialog(welcome, orangutanImage)
        );

        checkRun();
    }

    /**
     * Injects Orangutan instance.
     */
    public void setOrangutan(Orangutan o) {
        orangutan = o;
    }

    /**
     * creates two dialog boxes, one for user input and one for Orangutan's reply
     * and appends them to dialog container.
     * Clears user input after processing.
     * Ceases to receive input when Orangutan is no longer taking input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (!input.isBlank()) {
            String response = orangutan.getResponse(input);
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getOrangutanDialog(response, orangutanImage)
            );
            userInput.clear();
        }

        checkRun();
    }

    /**
     * Disables all input if orangutan is no longer accepting queries.
     */
    private void checkRun() {
        if (!orangutan.isRun()) {
            // solution provided by DeepSeek.
            userInput.setDisable(true);
            sendButton.setDisable(true);

            userInput.setText("The window may be closed, good bye.");
        }
    }
}
