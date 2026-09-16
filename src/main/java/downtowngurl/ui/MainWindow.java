package downtowngurl.ui;

import downtowngurl.DowntownGurl;
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
    private static final String INTRODUCTION_MESSAGE = "Hi bestie! I'm Downtown Gurl.\n"
            + "You can try: \n"
            + " * todo read notes\n"
            + " * deadline submit project /by 20/9/2026 2359\n"
            + " * event party /from 20/9/2026 1900 /to 20/9/2026 2300";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private DowntownGurl downtownGurl;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image downtownGurlImage =
            new Image(this.getClass().getResourceAsStream("/images/DaDowntownGurl.png"));

    /**
     * Initializes the controller after its FXML fields are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getDowntownGurlDialog(INTRODUCTION_MESSAGE, downtownGurlImage));
    }

    /**
     * Injects the Downtown Gurl instance used to generate chatbot responses.
     *
     * @param downtownGurl chatbot instance used by this window.
     */
    public void setDowntownGurl(DowntownGurl downtownGurl) {
        this.downtownGurl = downtownGurl;
    }

    /**
     * Shows the user's message and Downtown Gurl's response, then clears the input box.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = downtownGurl.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDowntownGurlDialog(response, downtownGurlImage)
        );
        userInput.clear();

        if (downtownGurl.isExit()) {
            Platform.exit();
        }
    }
}
