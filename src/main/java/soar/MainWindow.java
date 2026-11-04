package soar;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    /** Greeting shown when the chat window opens. */
    private static final String WELCOME_MESSAGE = "Hey there! I'm Soar, your upbeat little sidekick!\n"
            + "What exciting thing can I help you tackle today?";

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user.png"));
    private final Image soarImage = new Image(getClass().getResourceAsStream("/images/soar.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Soar soar;

    /** Initializes behavior that depends on controls injected from FXML. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getSoarDialog(WELCOME_MESSAGE, soarImage));
    }

    /**
     * Injects the Soar session used to process commands.
     *
     * @param soar Soar session shared by this window.
     */
    public void setSoar(Soar soar) {
        this.soar = Objects.requireNonNull(soar, "Soar session must not be null");
    }

    /** Creates and displays dialog boxes for the user's input and Soar's response. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String soarText = soar.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSoarDialog(soarText, soarImage));
        userInput.clear();
    }
}
