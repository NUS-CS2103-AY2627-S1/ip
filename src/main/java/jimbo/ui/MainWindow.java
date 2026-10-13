package jimbo.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import jimbo.Jimbo;
import jimbo.JimboException;

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

    private Jimbo jimbo;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image jimboImage = new Image(this.getClass().getResourceAsStream("/images/DaJimbo.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Jimbo instance.
     *
     * @param j The Jimbo instance.
     */
    public void setJimbo(Jimbo j) {
        jimbo = j;
        dialogContainer.getChildren().addAll(
                DialogBox.getJimboDialog("hi i'm jimbo\nnice to meet you", jimboImage)
        );
        if (jimbo.getInitialisationError() != null) {
            dialogContainer.getChildren().addAll(
                DialogBox.getErrorDialog(jimbo.getInitialisationError(), jimboImage)
            );
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Jimbo's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        try {
            String response = jimbo.getResponse(input);
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getJimboDialog(response, jimboImage));
        } catch (JimboException e) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getErrorDialog(e.getMessage(), jimboImage));
        }
        userInput.clear();
    }
}