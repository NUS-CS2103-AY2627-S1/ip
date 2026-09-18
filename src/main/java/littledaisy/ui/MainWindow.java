package littledaisy.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import littledaisy.LittleDaisy;

/** Controls the main chat window. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private LittleDaisy littleDaisy;

    /** Keeps the newest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the application logic and displays its startup greeting.
     *
     * @param littleDaisy application logic shared with the text interface
     */
    public void setLittleDaisy(LittleDaisy littleDaisy) {
        this.littleDaisy = littleDaisy;
        dialogContainer.getChildren().add(
                DialogBox.getDaisyDialog(littleDaisy.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /** Sends the entered command and appends both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || littleDaisy == null) {
            return;
        }

        String response = littleDaisy.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.startsWith("OOPS!!!")
                        ? DialogBox.getErrorDialog(response)
                        : DialogBox.getDaisyDialog(response));
        userInput.clear();
    }
}
