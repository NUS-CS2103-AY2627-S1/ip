package hse.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the main JavaFX chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private HseApplication hseApplication;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image hseImage = new Image(getClass().getResourceAsStream("/images/Hse.png"));

    /** Binds the transcript scroll position to its content height. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the HSE application instance. */
    public void setHseApplication(HseApplication hseApplication) {
        this.hseApplication = hseApplication;
        dialogContainer.getChildren().addAll(DialogBox.getHseDialog(
                "Hello! I'm HSE. What can I do for you?", hseImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing HSE's reply.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = hseApplication.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                response.startsWith("OOPS!!!")
                        ? DialogBox.getErrorDialog(response, hseImage)
                        : DialogBox.getHseDialog(response, hseImage)
        );
        userInput.clear();
    }
}
