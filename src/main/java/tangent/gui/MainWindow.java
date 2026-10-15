package tangent.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tangent.Tangent;
import tangent.ui.Ui;

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

    private Tangent tangent;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/ena.jpg"));
    private final Image tangentImage = new Image(this.getClass().getResourceAsStream("/images/mizuki.jpg"));
    private final StringBuilder responseOutput = new StringBuilder();
    private final Ui ui = new Ui(this::appendResponse, this::appendError);

    /** Scrolls the ScrollPane to the bottom whenever the dialog box is resized. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        ui.showGuiWelcome();
        dialogContainer.getChildren().add(DialogBox.getTangentDialog(responseOutput.toString(), tangentImage));
        responseOutput.setLength(0);
    }

    /** Injects the Tangent instance. */
    public void setTangent(Tangent t) {
        tangent = t;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Tangent's reply and then appends
     * them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        responseOutput.setLength(0);
        tangent.executeCommand(input, ui);
        String response = responseOutput.toString();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTangentDialog(response, tangentImage, response.startsWith("⚠"))
        );
        userInput.clear();
        if (tangent.isExitRequested()) {
            Platform.exit();
        }
    }

    /** Appends one formatted command message to the GUI response buffer. */
    private void appendResponse(String message) {
        if (!responseOutput.isEmpty()) {
            responseOutput.append(System.lineSeparator());
        }
        responseOutput.append(message);
    }

    /** Appends an error response with a visual warning marker. */
    private void appendError(String message) {
        appendResponse("⚠ " + message);
    }
}
