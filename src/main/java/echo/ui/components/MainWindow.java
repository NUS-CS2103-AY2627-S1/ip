package echo.ui.components;

import java.net.URL;

import echo.Echo;
import echo.ui.EchoResponse;
import echo.ui.ResponseType;
import javafx.application.Platform;
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
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Echo echo;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image echoImage = loadImage("/images/DaEcho.png");

    /** Initializes automatic scrolling for the conversation. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Echo instance and shows the greeting once it is set. */
    public void setEcho(Echo e) {
        echo = e;
        dialogContainer.getChildren().add(
                DialogBox.getEchoDialog(echo.start(), echoImage, scrollPane.viewportBoundsProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Echo's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.requestFocus();
            return;
        }

        EchoResponse response = echo.getTypedResponse(input);
        DialogBox userDialog = DialogBox.getUserDialog(
                input, userImage, scrollPane.viewportBoundsProperty());
        DialogBox echoDialog = response.type() == ResponseType.ERROR
                ? DialogBox.getErrorDialog(response.text(), echoImage, scrollPane.viewportBoundsProperty())
                : DialogBox.getEchoDialog(response.text(), echoImage, scrollPane.viewportBoundsProperty());
        dialogContainer.getChildren().addAll(userDialog, echoDialog);
        userInput.clear();
        userInput.requestFocus();
        if (echo.isExit()) {
            Platform.exit();
        }
    }

    private Image loadImage(String resourcePath) {
        URL imageResource = getClass().getResource(resourcePath);
        if (imageResource == null) {
            throw new IllegalStateException("A required avatar image is missing.");
        }
        return new Image(imageResource.toExternalForm());
    }
}
