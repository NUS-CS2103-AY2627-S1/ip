package peter.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import peter.Peter;
import peter.command.Command;

/**
 * Controller for Peter's main GUI.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Peter peter;

    private final Image peterImage = new Image(this.getClass().getResourceAsStream("/images/DaPeter.png"));

    /**
     * Configures automatic scrolling and puts keyboard focus in the command field.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            scrollPane.setVvalue(1.0);
        });
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Injects the Peter instance and displays its welcome message.
     *
     * @param p Peter instance that handles commands.
     */
    public void setPeter(Peter p) {
        peter = p;
        if (peter.getLoadingWarning() != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(peter.getLoadingWarning(), peterImage));
        }
        dialogContainer.getChildren().add(DialogBox.getPeterDialog(peter.getWelcomeMessage(), peterImage));
    }

    /**
     * Creates dialog boxes for the user's command and Peter's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            userInput.clear();
            return;
        }
        Peter.Response response = peter.getResponseResult(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.isError()
                        ? DialogBox.getErrorDialog(response.message(), peterImage)
                        : DialogBox.getPeterDialog(response.message(), peterImage)
        );
        userInput.clear();
        if (Command.fromString(input) == Command.BYE && !response.isError()) {
            Platform.exit();
        }
    }
}
