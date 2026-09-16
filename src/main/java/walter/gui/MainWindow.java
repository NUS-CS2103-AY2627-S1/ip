package walter.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import walter.Walter;
import walter.command.CommandCategory;

/**
 * Controls Walter's main conversation window.
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

    private final Image userImage = loadAvatar("/images/user-avatar.png");
    private final Image walterImage = loadAvatar("/images/walter-avatar.png");

    private Walter walter;

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the Walter instance used to process commands and shows its greeting.
     *
     * @param walter Walter application facade shared throughout this window.
     */
    public void setWalter(Walter walter) {
        this.walter = walter;
        dialogContainer.getChildren().add(
                DialogBox.getWalterDialog(walter.getWelcomeMessage(), walterImage));
        userInput.requestFocus();
    }

    /**
     * Sends the text-field content to Walter and appends both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (isBlankInput(input)) {
            userInput.clear();
            return;
        }
        String response = walter.getResponse(input);
        CommandCategory commandCategory = walter.getLastCommandCategory();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getWalterDialog(response, walterImage, commandCategory));
        userInput.clear();
        if (walter.wasLastCommandExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(2.5));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Reports whether GUI input should be ignored without creating a conversation bubble.
     *
     * @param input Current text-field content.
     * @return {@code true} for null, empty, or whitespace-only input.
     */
    static boolean isBlankInput(String input) {
        return input == null || input.isBlank();
    }

    /**
     * Loads an avatar from the application's classpath and fails clearly if it is unavailable.
     *
     * @param resourcePath Absolute classpath path of the avatar resource.
     * @return Loaded avatar image.
     */
    private static Image loadAvatar(String resourcePath) {
        try (InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (imageStream == null) {
                throw new IllegalStateException("Missing Walter avatar resource: " + resourcePath);
            }
            return new Image(imageStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Walter could not load avatar: " + resourcePath,
                    exception);
        }
    }
}
