package amigobot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Controller for the main GUI window.
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
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Rectangle bgOverlay;

    private AmigoBot amigoBot;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaBot.png"));

    /** Binds the scroll pane to auto-scroll and the background to resize with the window. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Bind background image and overlay to the actual root pane's size
        AnchorPane root = (AnchorPane) scrollPane.getParent();
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
        bgOverlay.widthProperty().bind(root.widthProperty());
        bgOverlay.heightProperty().bind(root.heightProperty());

        dialogContainer.getChildren().add(
                DialogBox.getBotDialog("Hola amigo! I'm AmigoBot, your friendly capybara assistant!\n"
                        + "Did you know capybaras are the world's largest rodents?\n"
                        + "Anyway, what can I do for you, compadre?", botImage));
    }

    /** Injects the AmigoBot instance. */
    public void setAmigoBot(AmigoBot bot) {
        amigoBot = bot;
    }

    /**
     * Creates two dialog boxes — one for the user input and one for AmigoBot's reply —
     * then appends them to the dialog container and clears the input field.
     * Error responses are displayed with a distinct red style.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = amigoBot.getResponse(input);

        DialogBox botDialog = response.startsWith("Ay caramba!")
                ? DialogBox.getErrorDialog(response, botImage)
                : DialogBox.getBotDialog(response, botImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                botDialog
        );
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // ignored
                }
                Platform.exit();
            }).start();
        }
    }
}
