package nimbus.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nimbus.Nimbus;
import nimbus.parser.Command;

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

    private Nimbus nimbus;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/nimbus.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Nimbus instance */
    public void setNimbus(Nimbus d) {
        nimbus = d;
        dialogContainer.getChildren().add(
                DialogBox.getNimbusDialog(nimbus.getWelcomeMessage(), dukeImage, null, false));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = nimbus.getResponse(input);
        Command commandType = nimbus.getCommandType();
        if (commandType == Command.BYE) {
            // Close the stage instead of System.exit(0) so this goes through
            // the same Stage#setOnCloseRequest path as clicking the window's
            // close button, once that's wired up (e.g. for save-on-close).
            // Delayed so the user has time to read the exit message before
            // the window disappears.
            Stage stage = (Stage) userInput.getScene().getWindow();
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(stage::close);
            }).start();
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getNimbusDialog(response, dukeImage, commandType, nimbus.hasError()));
        userInput.clear();
    }
}
