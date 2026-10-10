package zsiggy;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Zsiggy GUI.
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

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.jpg")
    );

    private final Image zsiggyImage = new Image(
            this.getClass().getResourceAsStream("/images/DaZsiggy.jpg")
    );

    /**
     * Initializes the GUI after the FXML components are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Duke backend into this controller.
     *
     * @param duke The Duke instance handling Zsiggy commands.
     */
    public void setDuke(Duke duke) {
        this.duke = duke;

        dialogContainer.getChildren().add(
                DialogBox.getZsiggyDialog(
                        "Zsiggy here. Make it quick.\n"
                                + "What mess do you need me to sort out today?",
                        zsiggyImage
                )
        );
        if (!duke.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getZsiggyDialog("Oi. " + duke.getStartupWarning(), zsiggyImage));
        }
    }

    /**
     * Processes the user's input and displays Zsiggy's reply.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.clear();
            return;
        }

        String response = duke.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getZsiggyDialog(response, zsiggyImage)
        );

        userInput.clear();
        if (input.strip().equals("bye")) {
            Platform.exit();
        }
    }
}
