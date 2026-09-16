package turtley.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import turtley.Turtley;

/**
 * Controls the main Turtley conversation window.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Turtley turtley;
    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image turtleyImage = loadImage("/images/DaTurtley.png");
    private final Image backgroundImage = loadImage("/images/Background.png");

    /**
     * Creates the main conversation window.
     */
    public MainWindow() {
    }

    /**
     * Binds the conversation scroll position for the responsive conversation area.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        BackgroundSize backgroundSize = new BackgroundSize(1.0, 1.0, true, true, false, true);
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        rootPane.setBackground(new Background(background));
    }

    /**
     * Supplies the Turtley instance that processes user commands and displays the initial greeting.
     *
     * @param turtley the application logic used by this window.
     */
    public void setTurtley(Turtley turtley) {
        this.turtley = turtley;
        dialogContainer.getChildren().add(DialogBox.getTurtleyWelcomeDialog(
                turtley.getWelcomeMessage(),
                turtleyImage));
    }

    /**
     * Sends the current input to Turtley and appends both dialog messages.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Turtley.Response response = turtley.getResponseDetails(input);
        DialogBox responseDialog = response.isError()
                ? DialogBox.getTurtleyErrorDialog(response.text(), turtleyImage)
                : DialogBox.getTurtleyDialog(response.text(), turtleyImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog
        );
        userInput.clear();
    }

    /**
     * Loads an image from the application's bundled resources.
     *
     * @param resourcePath the classpath path of the image.
     * @return the loaded image.
     */
    private static Image loadImage(String resourcePath) {
        try (InputStream inputStream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing GUI image resource: " + resourcePath);
            }
            return new Image(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load GUI image resource: " + resourcePath, exception);
        }
    }
}
