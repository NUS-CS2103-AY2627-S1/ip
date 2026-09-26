package dulio.gui;

import dulio.Dulio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Dulio GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_IMAGE_PATH = "/images/account-avatar-profile-user-svgrepo-com.png";
    private static final String DULIO_IMAGE_PATH = "/images/account-avatar-profile-user-3-svgrepo-com.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(getClass().getResourceAsStream(USER_IMAGE_PATH));
    private final Image dulioImage = new Image(getClass().getResourceAsStream(DULIO_IMAGE_PATH));
    private Dulio dulio;

    /**
     * Creates the controller for the main Dulio window.
     */
    public MainWindow() {
    }

    /**
     * Configures scrolling and displays the initial sample messages.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog("Hello!", userImage),
            DialogBox.getDulioDialog("Hello! I'm Dulio. What can I do for you?", dulioImage));
    }

    public void setDulio(Dulio dulio) {
        this.dulio = dulio;
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = dulio.getResponse(input);
        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog(input, userImage),
            DialogBox.getDulioDialog(response, dulioImage));
        userInput.clear();

        if ("bye".equals(input)) {
            userInput.getScene().getWindow().hide();
        }
    }
}
