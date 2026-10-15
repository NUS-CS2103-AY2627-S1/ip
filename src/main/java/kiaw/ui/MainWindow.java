package kiaw.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kiaw.Kiaw;

/**
 * Controller for Kiaw's main GUI.
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

    private Kiaw kiaw;

    /**
     * Initializes the main window after its FXML components are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(
                dialogContainer.heightProperty());

        dialogContainer.getChildren().add(
                DialogBox.getKiawDialog(
                        "Hi! I'm Kiaw. 👋\n"
                                + "I can help you manage your tasks.\n"
                                + "Try adding a todo, deadline, or event."));
    }

    /**
     * Injects the Kiaw instance used to process commands.
     *
     * @param kiaw Kiaw instance
     */
    public void setKiaw(Kiaw kiaw) {
        this.kiaw = kiaw;
    }

    /**
     * Processes the user's input and displays Kiaw's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        String response = kiaw.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getKiawDialog(response));

        userInput.clear();

        if (input.equals("bye")) {
            PauseTransition delay =
                    new PauseTransition(Duration.seconds(1.5));

            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
