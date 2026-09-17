package baemax;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI window: takes the user's typed command, asks
 * {@link Baemax} for a reply, and shows both as dialog boxes.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Baemax baemax;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image baemaxImage = new Image(this.getClass().getResourceAsStream("/images/DaBaemax.png"));

    /** Keeps the view scrolled to the newest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Baemax} instance and greets the user.
     *
     * @param b the chatbot to send commands to
     */
    public void setBaemax(Baemax b) {
        baemax = b;
        dialogContainer.getChildren().add(
                DialogBox.getBaemaxDialog(baemax.getWelcomeMessage(), baemaxImage, false));
    }

    /**
     * Shows the user's input and Baemax's reply as dialog boxes - the reply
     * styled as an error when the command failed - then clears the input
     * field. Closes the window shortly after a {@code bye} command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }

        Baemax.Reply reply = baemax.getReply(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBaemaxDialog(reply.text(), baemaxImage, reply.isError()));
        userInput.clear();

        if (input.trim().equals("bye")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
