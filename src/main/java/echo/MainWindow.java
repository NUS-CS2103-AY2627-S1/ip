package echo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.scene.text.Font;

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

    private Echo echo;

    //private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    //private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.setFont(Font.font("Monospaced"));
        dialogContainer.setBackground(DialogBox.blackBackground);
    }

    /** Injects the Duke instance */
    public void setEcho(Echo echo) {
        this.echo = echo;
        dialogContainer.getChildren().add(
                DialogBox.getEchoDialog(Ui.getWelcome()));
        echo.store.readData();
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = echo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog("User: " + input),
                DialogBox.getEchoDialog("Echo:\n" + response)
        );
        userInput.clear();
        if (response.equals(Ui.getFarewell())) {
            Platform.exit();
        }
    }
}

