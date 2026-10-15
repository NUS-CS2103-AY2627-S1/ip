package sine.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import sine.Sine;
import sine.ui.Messages;

/**
 * Controls Sine's main chat window.
 */
public class MainWindow {
    private static final int AVATAR_SIZE = 56;
    private static final Image USER_IMAGE = createAvatar(Color.web("#536dfe"));
    private static final Image SINE_IMAGE = createAvatar(Color.web("#7c4dff"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Sine sine;

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot that generates replies for user commands.
     *
     * @param sine Chatbot backing this window.
     */
    public void setSine(Sine sine) {
        this.sine = sine;
        dialogContainer.getChildren().add(
                DialogBox.getSineDialog(
                        "Hello! I'm Sine.\n" + Messages.COMMAND_HELP, SINE_IMAGE));
    }

    /**
     * Sends the entered command to Sine and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        String response = sine.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                DialogBox.getSineDialog(response, SINE_IMAGE));
        userInput.clear();
    }

    private static Image createAvatar(Color color) {
        WritableImage image = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter pixels = image.getPixelWriter();
        double center = (AVATAR_SIZE - 1) / 2.0;
        double radiusSquared = center * center;

        for (int x = 0; x < AVATAR_SIZE; x++) {
            for (int y = 0; y < AVATAR_SIZE; y++) {
                double horizontalDistance = x - center;
                double verticalDistance = y - center;
                if (horizontalDistance * horizontalDistance
                        + verticalDistance * verticalDistance <= radiusSquared) {
                    pixels.setColor(x, y, color);
                }
            }
        }
        return image;
    }
}
