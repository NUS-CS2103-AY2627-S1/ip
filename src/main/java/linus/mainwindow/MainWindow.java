package linus.mainwindow;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import linus.Linus;
import linus.dialogbox.DialogBox;
import linus.ui.Ui;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String BYE_COMMAND = "bye";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Linus linus;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image linusImage = new Image(this.getClass().getResourceAsStream("/images/DaLinus.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Linus instance */
    public void setLinus(Linus l) {
        linus = l;
        dialogContainer.getChildren().add(
                DialogBox.getLinusDialog(Ui.getHelloMessage(), linusImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Linus's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.equals(BYE_COMMAND)) {
            Platform.exit();
            return;
        }
        String response = linus.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLinusDialog(response, linusImage)
        );
        userInput.clear();
    }
}
