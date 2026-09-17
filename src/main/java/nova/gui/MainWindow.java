package nova.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nova.Nova;

/**
 * Controls user interaction in Nova's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(700);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nova nova;

    /**
     * Configures automatic scrolling after the FXML fields are initialized.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects the window to Nova and displays its welcome message.
     *
     * @param nova chatbot instance used to execute commands.
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        dialogContainer.getChildren().add(DialogBox.getNovaDialog(nova.getWelcomeMessage()));
    }

    /**
     * Moves keyboard focus to the command field.
     */
    public void focusInput() {
        userInput.requestFocus();
    }

    /**
     * Sends the current command to Nova and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Nova.Response response = nova.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.isError()
                        ? DialogBox.getErrorDialog(response.message())
                        : DialogBox.getNovaDialog(response.message()));
        userInput.clear();

        if (response.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }
}
