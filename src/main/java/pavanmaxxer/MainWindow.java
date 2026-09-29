package pavanmaxxer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main Pavanmaxxer window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image pavanmaxxerImage = new Image(
            getClass().getResourceAsStream("/images/DaDuke.png"));
    private Pavanmaxxer pavanmaxxer;

    /**
     * Creates the main window controller.
     */
    public MainWindow() {
    }

    /**
     * Connects automatic scrolling after FXML fields have been loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the application core used to handle commands.
     *
     * @param pavanmaxxer Application core.
     */
    public void setPavanmaxxer(Pavanmaxxer pavanmaxxer) {
        this.pavanmaxxer = pavanmaxxer;
        dialogContainer.getChildren().add(DialogBox.getPavanmaxxerDialog(
                "Hello! I'm Pavanmaxxer.\nWhat can I do for you?",
                pavanmaxxerImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = pavanmaxxer.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPavanmaxxerDialog(response, pavanmaxxerImage));
        userInput.clear();
        if (Parser.parseCommand(input) == Command.BYE) {
            Platform.runLater(Platform::exit);
        }
    }
}
