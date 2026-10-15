package alexa;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls Alexa's main JavaFX chat window. */
public class MainWindow extends AnchorPane {
    /** Prefix used by Alexa for user-facing error responses. */
    private static final String ERROR_PREFIX = "OOPS!!!";
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    /** The task application that processes messages from the user. */
    private Alexa alexa;
    /** The avatar displayed beside user messages. */
    private final Image userImage = new Image(Objects.requireNonNull(
            MainWindow.class.getResourceAsStream("/images/DaUser.png")));
    /** The avatar displayed beside Alexa messages. */
    private final Image alexaImage = new Image(Objects.requireNonNull(
            MainWindow.class.getResourceAsStream("/images/DaDuke.png")));

    /** Binds the scroll position so that new messages remain visible. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the task application used by the user interface.
     *
     * @param alexa The task application instance.
     */
    public void setAlexa(Alexa alexa) {
        this.alexa = alexa;
        dialogContainer.getChildren().add(DialogBox.getAlexaDialog(alexa.getGreeting(), alexaImage));
    }

    /** Creates user and Alexa dialog boxes for the text currently entered. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        if (userText.trim().isEmpty()) {
            return;
        }

        assert alexa != null : "Alexa must be injected before user input is handled.";

        String alexaText = alexa.getResponse(userText);
        DialogBox alexaDialog = alexaText.startsWith(ERROR_PREFIX)
                ? DialogBox.getErrorDialog(alexaText, alexaImage)
                : DialogBox.getAlexaDialog(alexaText, alexaImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                alexaDialog);
        userInput.clear();
    }
}