package remy.gui;

import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import remy.Remy;

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

    private Remy remy;

    /** Profile image displayed beside the user's messages. */
    private final Image userImage = loadImage("/images/User.png");

    /** Profile image displayed beside Remy's messages. */
    private final Image remyImage = loadImage("/images/Remy.png");

    /** Configures automatic scrolling after the FXML controls have been injected. */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML loader must inject the scroll pane";
        assert dialogContainer != null : "FXML loader must inject the dialog container";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects the chatbot to this window and displays its greeting.
     *
     * @param remy chatbot that handles commands entered in this window
     */
    public void setRemy(Remy remy) {
        assert remy != null : "Main window requires a chatbot instance";

        this.remy = remy;
        dialogContainer.getChildren()
                .add(DialogBox.getRemyDialog(remy.getGreeting(), remyImage));
        Platform.runLater(userInput::requestFocus);
    }

    /** Processes user input, closing the application when the chat session ends. */
    @FXML
    private void handleUserInput() {
        assert remy != null : "Chatbot must be set before user input is handled";

        String input = userInput.getText();
        String response = remy.getResponse(input);
        if (remy.hasExited()) {
            Platform.exit();
            return;
        }

        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input.strip(), userImage));
        }
        dialogContainer.getChildren().add(DialogBox.getRemyDialog(response, remyImage));
        userInput.clear();
        userInput.requestFocus();
    }

    /** Returns an image resource, failing early when the packaged resource is missing. */
    private Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
    }
}
