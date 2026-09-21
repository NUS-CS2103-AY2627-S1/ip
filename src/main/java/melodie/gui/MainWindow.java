package melodie.gui;

import java.util.Objects;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import melodie.Melodie;

/**
 * Controls the main Melodie chat window.
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

    @FXML
    private Label commandHint;

    private final Image userImage = this.loadImage("/images/DaUser.jpeg");
    private final Image melodieImage = this.loadImage("/images/DaMelodie.jpeg");
    private final CommandHistory commandHistory = new CommandHistory();

    private Melodie melodie;
    private boolean isRestoringHistory;

    /**
     * Configures behaviour that depends on controls injected from FXML.
     */
    @FXML
    public void initialize() {
        this.dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                this.scrollPane.setVvalue(1.0));
        this.userInput.textProperty().addListener((observable, oldText, newText) -> {
            this.commandHint.setText(CommandHint.getMissingFields(newText));
            if (!this.isRestoringHistory && this.commandHistory.isBrowsing()) {
                this.commandHistory.stopBrowsing();
            }
        });
        this.userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryNavigation);
    }

    /**
     * Supplies the chatbot used to process input and displays its greeting.
     *
     * @param melodie Melodie chatbot instance.
     */
    public void setMelodie(Melodie melodie) {
        this.melodie = melodie;
        this.dialogContainer.getChildren().add(
                DialogBox.getMelodieDialog(melodie.getGreeting(), this.melodieImage));
    }

    /**
     * Sends the entered command to Melodie and adds both sides of the exchange to the chat.
     */
    @FXML
    private void handleUserInput() {
        assert this.melodie != null : "Melodie must be set before handling user input";
        String input = this.userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        this.commandHistory.record(input);
        String response = this.melodie.getResponse(input);
        DialogBox responseDialog = this.melodie.wasLastResponseError()
                ? DialogBox.getErrorDialog(response, this.melodieImage)
                : DialogBox.getMelodieDialog(response, this.melodieImage);
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, this.userImage),
                responseDialog);
        this.userInput.clear();
        this.userInput.requestFocus();

        if (this.melodie.isExitRequested()) {
            this.userInput.setDisable(true);
            this.sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Replaces the current input with an older or newer submitted command.
     *
     * @param event Key press received by the command field.
     */
    private void handleHistoryNavigation(KeyEvent event) {
        Optional<String> replacement;
        if (event.getCode() == KeyCode.UP) {
            replacement = this.commandHistory.getPreviousCommand(this.userInput.getText());
        } else if (event.getCode() == KeyCode.DOWN) {
            replacement = this.commandHistory.getNextCommand();
        } else {
            return;
        }

        if (replacement.isEmpty()) {
            return;
        }

        this.isRestoringHistory = true;
        this.userInput.setText(replacement.get());
        this.userInput.positionCaret(this.userInput.getLength());
        this.isRestoringHistory = false;
        event.consume();
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(path), "Missing image resource: " + path));
    }
}
