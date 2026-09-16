package rambo.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import rambo.Rambo;

/**
 * Controller for the main Rambo chat window.
 */
public class MainWindow {
    private static final String RAMBO_IMAGE_PATH = "/images/DaRambo.png";
    private static final String USER_IMAGE_PATH = "/images/DaUser.png";

    private Rambo rambo;

    private final Image ramboImage = new Image(getClass().getResourceAsStream(RAMBO_IMAGE_PATH));
    private final Image userImage = new Image(getClass().getResourceAsStream(USER_IMAGE_PATH));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Sets up the scroll behavior after FXML loading is complete.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML loader should inject the scroll pane";
        assert dialogContainer != null : "FXML loader should inject the dialog container";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot used to produce responses.
     *
     * @param rambo chatbot instance
     */
    public void setRambo(Rambo rambo) {
        assert rambo != null : "The application should inject a Rambo instance";
        this.rambo = rambo;
        dialogContainer.getChildren().add(DialogBox.getRamboDialog(rambo.getWelcomeMessage(), ramboImage));
    }

    /**
     * Handles user input from the text field and send button.
     */
    @FXML
    public void handleUserInput() {
        assert rambo != null : "Rambo should be injected before input is handled";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = rambo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRamboDialog(response, ramboImage));
        userInput.clear();

        if (rambo.isExitRequested()) {
            Platform.exit();
        }
    }
}
