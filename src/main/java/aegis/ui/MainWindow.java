package aegis.ui;

import aegis.Aegis;
import javafx.application.Platform;
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

    private Aegis aegis;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/userIcon.png"));
    private Image aegisImage = new Image(this.getClass().getResourceAsStream("/images/aegisIcon.png"));

    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollToLatestDialog());
    }

    /**
     * Scrolls to the latest dialog after JavaFX has recalculated the layout.
     */
    private void scrollToLatestDialog() {
        Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /** Injects the Aegis instance. */
    public void setAegis(Aegis aegis) {
        this.aegis = aegis;
        dialogContainer.getChildren().add(DialogBox.getAegisDialog(aegis.getStartMessage(), aegisImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Aegis's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = aegis.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAegisDialog(response, aegisImage)
        );
        userInput.clear();
    }
}
