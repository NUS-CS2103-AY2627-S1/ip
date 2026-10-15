package puke;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the main Puke chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Puke puke;

    /** Initializes the chat window after its FXML fields are injected. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the task manager used to process messages from the chat window.
     *
     * @param puke task manager backing this user interface
     */
    public void setPuke(Puke puke) {
        this.puke = puke;
        dialogContainer.getChildren().add(DialogBox.getPukeDialog(
                "Hey! I'm Puke, your mildly dramatic task sprite."));
    }

    /** Adds the user's command and Puke's response to the chat. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        String response = puke.getResponse(command);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(command),
                DialogBox.getPukeDialog(response));
        userInput.clear();
        if (puke.isExitRequested()) {
            userInput.setDisable(true);
        }
    }
}
