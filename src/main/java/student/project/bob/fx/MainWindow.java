package student.project.bob.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import student.project.bob.Bob;
import student.project.bob.personality.PersonalityProfile;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String ERROR_PREFIX = "Oops!";
    private static final PersonalityProfile PERSONALITY = PersonalityProfile.DEFAULT;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Bob bob;

    private Image userImage = new Image(this.getClass().getResourceAsStream(PERSONALITY.getUserImagePath()));
    private Image bobImage = new Image(this.getClass().getResourceAsStream(PERSONALITY.getBotImagePath()));

    /**
     * Binds the dialog container's scroll position to its content height.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bob instance used to process commands.
     *
     * @param bob Bob instance used by this window
     */
    public void setBob(Bob bob) {
        this.bob = bob;
        if (dialogContainer.getChildren().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getBobDialog(PERSONALITY.getWelcomeMessage(), bobImage));
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Bob's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        String response = bob.getResponse(input);
        DialogBox responseDialog = response.startsWith(ERROR_PREFIX)
                ? DialogBox.getErrorDialog(response, bobImage)
                : DialogBox.getBobDialog(response, bobImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), responseDialog);
        userInput.clear();
    }
}
