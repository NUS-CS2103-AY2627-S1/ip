package duke.gui;

import java.io.InputStream;

import duke.CommandResponse;
import duke.Duke;
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

    private Duke duke;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image dukeImage = loadImage("/images/DaDuke.png");

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            return null;
        }

        return new Image(imageStream);
    }

    /**
     * Injects the Duke instance.
     *
     * @param duke Duke instance to use for generating responses.
     */
    public void setDuke(Duke duke) {
        this.duke = duke;
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(duke.getWelcomeMessage(), dukeImage));
        duke.getStartupError().ifPresent(error -> dialogContainer.getChildren().add(
                DialogBox.getErrorDialog(error, dukeImage)));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert duke != null : "Duke instance must be injected before handling input";

        String input = userInput.getText();
        CommandResponse response = duke.getCommandResponse(input);
        DialogBox responseDialog = response.isError()
                ? DialogBox.getErrorDialog(response.text(), dukeImage)
                : DialogBox.getDukeDialog(response.text(), dukeImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog
        );
        userInput.clear();

        if (response.shouldExit()) {
            Platform.exit();
        }
    }
}
