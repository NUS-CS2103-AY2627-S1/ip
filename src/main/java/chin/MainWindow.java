package chin;

import java.nio.file.Path;

import chin.util.Command;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML controller for the chat window. Wires input to {@link Chin#getResponse}
 * and closes the window on the {@code bye} command.
 */
public class MainWindow {
    @FXML private ScrollPane scroll;
    @FXML private VBox messages;
    @FXML private TextField inputField;

    private final Chin chin = new Chin(Path.of("data", "chin.txt"));

    @FXML
    public void initialize() {
        messages.getChildren().add(DialogBox.forChin("Hello! I'm Chin. What can I do for you?"));
    }

    @FXML
    private void handleUserInput() {
        String text = inputField.getText();
        if (text.isBlank()) {
            return;
        }
        messages.getChildren().add(DialogBox.forUser(text));
        String response = chin.getResponse(text);
        messages.getChildren().add(DialogBox.forChin(response));
        inputField.clear();
        scroll.setVvalue(1.0);

        if (Command.fromInput(text) == Command.BYE) {
            inputField.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> Platform.exit());
            pause.play();
        }
    }
}
