package orbit;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main chat window defined in FXML.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Orbit orbit;

    /** Keeps the latest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** @param orbit Orbit instance that processes commands for this window. */
    public void setOrbit(Orbit orbit) {
        this.orbit = orbit;
        dialogContainer.getChildren().add(DialogBox.getOrbitDialog(
                "Hello! I'm Orbit. Keep your tasks in view.\nTry todo read book, list, or find book."));
        if (!orbit.getStorageWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getOrbitDialog(orbit.getStorageWarning()));
        }
    }

    /** Sends the current input to Orbit and appends both messages to the chat. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        addDialogs(
                DialogBox.getUserDialog(input),
                DialogBox.getOrbitDialog(orbit.getResponse(input)));
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Appends any number of dialogs to the conversation in the supplied order.
     *
     * @param dialogs Dialogs to append.
     */
    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }
}
