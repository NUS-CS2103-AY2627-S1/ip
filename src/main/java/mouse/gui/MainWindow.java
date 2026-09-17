package mouse.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mouse.Mouse;

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

    private Mouse mouse;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image mouseImage = new Image(this.getClass().getResourceAsStream("/images/mouse.png"));

    /**
     * Binds the scroll pane to the dialog list so new messages stay in view.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(4));
    }

    /**
     * Injects the Mouse chatbot and shows the greeting.
     *
     * @param mouse Chatbot instance to use.
     */
    public void setMouse(Mouse mouse) {
        this.mouse = mouse;
        dialogContainer.getChildren().add(
                DialogBox.getMouseDialog(mouse.getGreeting(), mouseImage));
    }

    /**
     * Echoes the user input and appends Mouse's reply.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = mouse.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMouseDialog(response, mouseImage, mouse.isErrorResponse(response)));
        userInput.clear();
        if (mouse.isExit(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
