package dave.gui;

import dave.Dave;
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

/**
 * Controller for the main graphical user interface window.
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

    private Dave dave;

    private Image userImage;
    private Image daveImage;

    /**
     * Initializes the main window, setting up scroll listener and loading avatar images.
     */
    @FXML
    public void initialize() {
        this.dialogContainer.heightProperty().addListener(observable -> {
            Platform.runLater(() -> this.scrollPane.setVvalue(1.0));
        });
        this.dialogContainer.prefWidthProperty().bind(this.scrollPane.widthProperty());
        this.dialogContainer.minHeightProperty().bind(this.scrollPane.heightProperty().subtract(2));

        this.userInput.setOnScroll(event -> {
            double delta = event.getDeltaY();
            if (delta != 0) {
                double step = (delta / 40.0) * 0.05;
                this.scrollPane.setVvalue(this.scrollPane.getVvalue() - step);
            }
        });

        this.userImage = loadImageSafely("/images/DaUser.png");
        this.daveImage = loadImageSafely("/images/DaDave.png");
    }

    /**
     * Safely loads an image resource from the classpath without throwing runtime exceptions.
     *
     * @param resourcePath Relative classpath location of the image.
     * @return Loaded Image instance, or null if the image cannot be found.
     */
    private Image loadImageSafely(String resourcePath) {
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            // Falls back to null; DialogBox gracefully handles null images
        }
        return null;
    }

    /**
     * Sets the Dave application instance and displays the initial welcome greeting.
     *
     * @param dave Instance of the Dave chatbot.
     */
    public void setDave(Dave dave) {
        this.dave = dave;
        this.dialogContainer.getChildren().add(
                DialogBox.getDaveDialog(this.dave.getWelcome(), this.daveImage)
        );
    }

    /**
     * Handles user input triggered by either pressing Enter in the text field or clicking the Send button.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = this.dave.getResponse(input);
        boolean isError = this.dave.isErrorResponse(response);
        DialogBox daveBox = isError
                ? DialogBox.getDaveErrorDialog(response, this.daveImage)
                : DialogBox.getDaveDialog(response, this.daveImage);

        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, this.userImage),
                daveBox
        );
        this.userInput.clear();

        if (this.dave.isExit()) {
            this.userInput.setDisable(true);
            this.sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
