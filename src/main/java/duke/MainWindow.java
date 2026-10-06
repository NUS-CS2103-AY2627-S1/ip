package duke;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button themeButton;

    private Duke duke;
    private boolean isDarkMode = false;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image tbcImage = new Image(this.getClass().getResourceAsStream("/images/DaTbc.png"));

    /**
     * Initializes the controller, binds the scroll pane to the dialog container's height,
     * and greets the user with TBC's welcome message.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML injection failed: scrollPane is null";
        assert dialogContainer != null : "FXML injection failed: dialogContainer is null";
        assert userInput != null : "FXML injection failed: userInput is null";
        assert sendButton != null : "FXML injection failed: sendButton is null";
        assert themeButton != null : "FXML injection failed: themeButton is null";
        assert mainPane != null : "FXML injection failed: mainPane is null";
        assert userImage != null : "Resource loading failed: userImage is null";
        assert tbcImage != null : "Resource loading failed: tbcImage is null";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getTbcDialog("Hello! I'm TBC.\nWhat can I do for you?", tbcImage)
        );
    }

    /**
     * Injects the Duke instance into this controller.
     *
     * @param d The Duke instance.
     */
    public void setDuke(Duke d) {
        assert d != null : "Duke instance to inject must not be null";
        duke = d;
    }

    /**
     * Toggles between light theme and dark theme.
     */
    @FXML
    private void handleToggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            mainPane.getStyleClass().add("dark-theme");
            themeButton.setText("Light Mode");
        } else {
            mainPane.getStyleClass().remove("dark-theme");
            themeButton.setText("Dark Mode");
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing TBC's reply,
     * and appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert duke != null : "Duke instance must be set before handling user input";
        String input = userInput.getText();
        String response = duke.getResponse(input);
        assert response != null : "Duke response must not be null";
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTbcDialog(response, tbcImage)
        );
        userInput.clear();
    }
}
