package puyo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
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

    private Puyo puyo;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private Image puyoImage = new Image(this.getClass().getResourceAsStream("/images/puyo.png"));

    /**
     * Sets the Puyo instance for the controller.
     *
     * @param puyo The Puyo instance to be set.
     */
    public void setPuyo(Puyo puyo) {
        this.puyo = puyo;
    }

    /**
     * Initializes the scroll pane and displays the welcome message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        String welcomeMessage = "Hello! I'm Puyo!\nWhat can I do for you today?";
        dialogContainer.getChildren().add(
                DialogBox.getPuyoDialog(welcomeMessage, puyoImage)
        );
    }

    /**
     * Adds multiple dialog boxes to the dialog container using varargs.
     *
     * @param dialogs Array or sequence of DialogBox nodes to add.
     */
    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Puyo's reply.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = puyo.getResponse(input);

        // Using varargs helper method to display user and puyo dialogs simultaneously
        addDialogs(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPuyoDialog(response, puyoImage)
        );

        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                javafx.application.Platform.exit();
            });
        }
    }
}
