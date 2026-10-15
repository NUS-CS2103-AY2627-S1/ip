package jiji.gui;

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
import jiji.Jiji;

/**
 * Controller for the main GUI window.
 * <p>
 * Adapted and enhanced from the SE-EDU JavaFX Tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
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

    private Jiji jiji;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image jijiImage = new Image(this.getClass().getResourceAsStream("/images/DaJiji.png"));

    /**
     * Initializes auto-scrolling to the latest message whenever new content is added.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
    }

    /**
     * Injects the Jiji chatbot instance and displays the startup greeting.
     *
     * @param j The Jiji chatbot instance.
     */
    public void setJiji(Jiji j) {
        this.jiji = j;
        dialogContainer.getChildren().add(DialogBox.getJijiDialog(jiji.getGreeting(), jijiImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Jiji's reply,
     * and appends them to the dialog container. Clears the user input after processing.
     * If the user sends the exit command, the window closes after a brief delay.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        JijiResponse response = jiji.getResponseDetails(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getJijiDialog(response.getMessage(), jijiImage, response.isError())
        );
        userInput.clear();
        userInput.requestFocus();

        if (input.trim().equalsIgnoreCase("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.0));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
