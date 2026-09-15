package gepit.ui;

import gepit.Gepit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main Gepit GUI.
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

    private Gepit gepit;

    private final Image userImage =
            new Image(getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image gepitImage =
            new Image(getClass().getResourceAsStream("/images/DaGepit.png"));

    /**
     * Initializes the main window after its FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the Gepit instance used to process commands.
     *
     * @param gepit Gepit instance to use.
     */
    public void setGepit(Gepit gepit) {
        this.gepit = gepit;
        dialogContainer.getChildren().add(
                DialogBox.getGepitDialog(
                        "Hey, friendo! I'm Gepit. Type 'help' to see what I can do.",
                        gepitImage));
    }

    /**
     * Processes user input and displays Gepit's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        Gepit.Response response = gepit.process(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getGepitDialog(
                        response.message(), gepitImage, response.isError()));

        userInput.clear();
    }
}
