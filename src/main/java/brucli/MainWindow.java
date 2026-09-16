package brucli;

import java.util.Objects;

import brucli.ui.CommandResponse;
import brucli.ui.Messages;
import brucli.ui.ResponseType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main BruCLI window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = loadImage("/images/DaUser.jpg");
    private final Image bruCliImage = loadImage("/images/DaDuke.jpg");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private BruCliApp bruCliApp;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getBruCliDialog(Messages.welcome(), bruCliImage, ResponseType.STANDARD)
        );
    }

    /**
     * Supplies the application that handles commands entered in this window.
     *
     * @param bruCliApp BruCLI application used to generate responses.
     */
    public void setBruCliApp(BruCliApp bruCliApp) {
        this.bruCliApp = bruCliApp;
    }

    /**
     * Adds the user's command and BruCLI's response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        CommandResponse response = bruCliApp.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBruCliDialog(response.text(), bruCliImage, response.type())
        );
        userInput.clear();
    }

    /**
     * Loads a required image resource.
     */
    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}
