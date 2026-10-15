package eva;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Eva's main application window.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_AVATAR = "You";
    private static final String EVA_AVATAR = "Eva";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Eva eva;

    /**
     * Connects the scrolling behavior after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Eva instance that processes user commands.
     *
     * @param eva Eva instance used by this window.
     */
    public void setEva(Eva eva) {
        this.eva = eva;
        dialogContainer.getChildren().add(
                DialogBox.getEvaDialog(
                        "Hello, I'm Eva.\nLet's take tasks one step at a time.",
                        EVA_AVATAR));
    }

    /**
     * Displays the user's command and Eva's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = eva.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_AVATAR),
                DialogBox.getEvaDialog(response, EVA_AVATAR));
        userInput.clear();

        if (input.equals("bye")) {
            Platform.exit();
        }
    }
}
