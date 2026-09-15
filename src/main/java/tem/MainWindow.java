package tem;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller for the main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final int AVATAR_SIZE = 36;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Tem tem;

    private final Image userImage = createAvatar(Color.web("#64748b"));
    private final Image temImage = createAvatar(Color.web("#2563eb"));

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Tem instance that handles user input.
     *
     * @param tem chatbot backing this window.
     */
    public void setTem(Tem tem) {
        this.tem = tem;
        dialogContainer.getChildren().add(DialogBox.getTemDialog(tem.welcomeMessage(), temImage));
    }

    /**
     * Sends the current input to Tem and shows the response in the chat area.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        addConversation(input);
        userInput.clear();

        if (tem.isExit(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    private void addConversation(String input) {
        String response = tem.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTemDialog(response, temImage, tem.wasLastResponseAnError()));
    }

    private static Image createAvatar(Color color) {
        WritableImage image = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter writer = image.getPixelWriter();
        double radius = AVATAR_SIZE / 2.0;
        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                double dx = x - radius + 0.5;
                double dy = y - radius + 0.5;
                boolean isInsideCircle = dx * dx + dy * dy <= radius * radius;
                writer.setColor(x, y, isInsideCircle ? color : Color.TRANSPARENT);
            }
        }
        return image;
    }
}
