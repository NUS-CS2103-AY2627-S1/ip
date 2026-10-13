package baymax.ui;

import java.io.IOException;

import baymax.Baymax;
import baymax.Baymax.CommandResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Baymax's JavaFX chat window.
 */
public class MainWindow extends AnchorPane {
    private static final String BAYMAX_IMAGE_PATH = "/image/bot.jpg";
    private static final String USER_IMAGE_PATH = "/image/user.jpg";
    private static final String WARNING_IMAGE_PATH = "/image/warning.jpg";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Baymax bot;
    private Image userImage;
    private Image baymaxImage;
    private Image warningImage;

    /**
     * Initializes scrolling and the packaged user and Baymax avatars.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml should inject scrollPane.";
        assert dialogContainer != null : "MainWindow.fxml should inject dialogContainer.";
        assert userInput != null : "MainWindow.fxml should inject userInput.";
        assert sendButton != null : "MainWindow.fxml should inject sendButton.";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userImage = UiAssets.loadAvatar(USER_IMAGE_PATH);
        baymaxImage = UiAssets.loadAvatar(BAYMAX_IMAGE_PATH);
        warningImage = UiAssets.loadAvatar(WARNING_IMAGE_PATH);
    }

    /**
     * Sets the Baymax instance used to process commands.
     *
     * @param bot the shared Baymax command processor
     */
    public void setBot(Baymax bot) {
        assert bot != null : "MainWindow should receive Baymax before processing input.";
        this.bot = bot;
        if (!bot.getLoadWarning().isEmpty()) {
            showBotMessage(bot.getLoadWarning());
        }
    }

    /**
     * Displays an initial message from Baymax.
     *
     * @param message the message to display
     */
    public void showBotMessage(String message) {
        assert dialogContainer != null : "Dialog container should be initialized before display.";
        assert baymaxImage != null : "Baymax avatar should be initialized before display.";

        dialogContainer.getChildren().add(
                DialogBox.getBaymaxDialog(message, baymaxImage, false));
    }

    /**
     * Processes text submitted through the input field or Send button.
     */
    @FXML
    private void handleUserInput() {
        assert bot != null : "Baymax should be set before handling user input.";
        assert userImage != null : "User avatar should be initialized before display.";
        assert baymaxImage != null : "Baymax avatar should be initialized before display.";
        assert warningImage != null : "Warning image should be initialized before display.";

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(input, userImage));

        CommandResponse response = bot.processCommand(input);
        Image responseImage = response.isError() ? warningImage : baymaxImage;
        dialogContainer.getChildren().add(
                DialogBox.getBaymaxDialog(
                        response.message(), responseImage, response.isError()));
        userInput.clear();

        if (response.shouldExit()) {
            saveAndDisableInput();
        }
    }

    private void saveAndDisableInput() {
        try {
            bot.saveTasks();
        } catch (IOException exception) {
            dialogContainer.getChildren().add(DialogBox.getBaymaxDialog(
                    "I have some concerns. I cannot save your care plan right now.\n" + exception.getMessage(),
                    warningImage,
                    true));
            return;
        }
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }

}
