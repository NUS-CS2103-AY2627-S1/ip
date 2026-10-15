package pulbot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pulbot.ui.Ui;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_IMAGE_PATH = "/images/DaUser.png";
    private static final String PULBOT_IMAGE_PATH = "/images/DaDuke.png";

    @FXML
    private Label backgroundArt;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Pulbot pulbot;

    private final Image userImage = new Image(getClass().getResourceAsStream(USER_IMAGE_PATH));
    private final Image pulbotImage = new Image(getClass().getResourceAsStream(PULBOT_IMAGE_PATH));

    /** Initializes the background art and automatic chat scrolling. */
    @FXML
    public void initialize() {
        backgroundArt.setText(Ui.getBanner());
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Pulbot instance. */
    public void setPulbot(Pulbot pulbot) {
        assert pulbot != null : "Pulbot must be provided before the window handles input";
        this.pulbot = pulbot;
        DialogBox welcomeDialog = DialogBox.getPulbotDialog(Ui.getWelcomePrompt(), pulbotImage);
        dialogContainer.getChildren().add(welcomeDialog);
        if (!pulbot.getStartupWarning().isEmpty()) {
            DialogBox warningDialog = DialogBox.getPulbotDialog(
                    pulbot.getStartupWarning(), pulbotImage);
            dialogContainer.getChildren().add(warningDialog);
        }
    }

    /**
     * Appends the user's message and Pulbot's reply to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = pulbot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPulbotDialog(response, pulbotImage)
        );
        userInput.clear();
        if (pulbot.isExitRequested()) {
            Platform.exit();
        }
    }
}
