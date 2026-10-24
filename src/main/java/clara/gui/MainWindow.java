package clara.gui;

import clara.Clara;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

// AI-assisted JavaFX GUI implementation. See CITATIONS.md [C-013].
// NOTE: AI-assisted GUI polish. See CITATIONS.md [C-019].

/**
 * Controller for Clara's main GUI.
 */
public class MainWindow {
    private final Image claraImage = new Image(
            getClass().getResourceAsStream("/images/Clara.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Clara clara;

    /**
     * Initializes controls after FXML has injected them.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects Clara and displays her opening message.
     *
     * @param clara the application that processes user commands
     */
    public void setClara(Clara clara) {
        this.clara = clara;
        dialogContainer.getChildren().add(
                DialogBox.getClaraDialog(clara.getStartupMessage(), claraImage, false));
    }

    /**
     * Creates dialog boxes for the user's command and Clara's response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = clara.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getClaraDialog(response, claraImage, clara.isInputError(response)));
        userInput.clear();
        if (clara.shouldExit()) {
            Platform.exit();
        }
    }
}
