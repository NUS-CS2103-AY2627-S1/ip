package amy;
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
    private static final String LIGHT_MAIN_STYLESHEET =
            MainWindow.class.getResource("/ css/main.css").toExternalForm();
    private static final String DARK_MAIN_STYLESHEET =
            MainWindow.class.getResource("/ css/dark-main.css").toExternalForm();
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Amy amy;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/img.png"));
    private Image amyImage = new Image(this.getClass().getResourceAsStream("/images/img_1.png"));
    private boolean isDarkMode;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        getStyleClass().add("amy-window");
    }

    /** Injects the Amy instance. */
    public void setAmy(Amy a) {
        amy = a;
        String savedTasks = amy.getResponse("list");
        if (!savedTasks.equals("Your list is empty for now!")) {
            DialogBox dialogBox = DialogBox.getAmyDialog(savedTasks, amyImage, false);
            dialogBox.setDarkMode(isDarkMode);
            dialogContainer.getChildren().add(dialogBox);
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Amy's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        // The controller is injected by Main before the user can interact with the window.
        assert amy != null : "Amy must be injected before handling user input";
        String input = userInput.getText();
        String response = amy.getResponse(input);
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox amyDialog = DialogBox.getAmyDialog(response, amyImage, isErrorResponse(response));
        userDialog.setDarkMode(isDarkMode);
        amyDialog.setDarkMode(isDarkMode);
        dialogContainer.getChildren().addAll(userDialog, amyDialog);
        if (input.trim().equalsIgnoreCase("dark mode")) {
            setDarkMode(true);
        } else if (input.trim().equalsIgnoreCase("light mode")) {
            setDarkMode(false);
        }
        userInput.clear();
    }

    /** Applies the selected theme to the window and all existing messages. */
    private void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        String replacementStylesheet = darkMode ? DARK_MAIN_STYLESHEET : LIGHT_MAIN_STYLESHEET;
        if (scrollPane.getScene() != null) {
            scrollPane.getScene().getStylesheets().setAll(replacementStylesheet);
        }
        dialogContainer.getChildren().stream()
                .filter(DialogBox.class::isInstance)
                .map(DialogBox.class::cast)
                .forEach(dialog -> dialog.setDarkMode(darkMode));
    }

    /**
     * Identifies responses that should receive Amy's error styling.
     *
     * @param response the response returned by Amy
     * @return true when the response reports invalid input or unavailable data
     */
    private boolean isErrorResponse(String response) {
        return response.startsWith("I'm sorry")
                || response.startsWith("Please ")
                || response.startsWith("A todo description cannot")
                || response.startsWith("That task does not exist")
                || response.startsWith("There is no command to undo")
                || response.startsWith("Undo cancelled")
                || response.startsWith("Unable to save");
    }
}
