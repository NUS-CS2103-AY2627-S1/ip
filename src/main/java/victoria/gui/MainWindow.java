package victoria.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import victoria.Victoria;
import victoria.ui.Ui;

/** Controls the chat window used to send commands to Victoria. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/Olivia.jpg"));
    private final Image victoriaImage = new Image(getClass().getResourceAsStream("/images/Victoria.png"));
    private Victoria victoria;

    /** Scrolls to the newest dialog whenever the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Supplies the task application that executes the user's commands. */
    public void setVictoria(Victoria victoria) {
        this.victoria = victoria;
        addVictoriaDialog(Ui.getGreetingMessage());
        addVictoriaDialog(victoria.getLoadResultMessage().strip());
    }

    /** Sends the text field's command and shows Victoria's reply. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank() || victoria == null) {
            return;
        }
        addUserDialog(input);
        Victoria.CommandResult result = victoria.executeCommand(input);
        addVictoriaDialog(result.response().strip(), result.response().startsWith(" Oops!"));
        userInput.clear();
        if (result.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Adds a dialog authored by the user. */
    private void addUserDialog(String text) {
        dialogContainer.getChildren().add(DialogBox.getUserDialog(text, userImage));
    }

    /** Adds a dialog authored by Victoria. */
    private void addVictoriaDialog(String text) {
        addVictoriaDialog(text, false);
    }

    /** Adds a dialog authored by Victoria, highlighting an error reply when needed. */
    private void addVictoriaDialog(String text, boolean isError) {
        DialogBox dialogBox = isError
                ? DialogBox.getErrorDialog(text, victoriaImage)
                : DialogBox.getVictoriaDialog(text, victoriaImage);
        dialogContainer.getChildren().add(dialogBox);
    }
}
