package kaya.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kaya.Kaya;

/**
 * Controls Kaya's main chat window.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Kaya kaya;

    /**
     * Creates a controller that JavaFX will populate from FXML.
     */
    public MainWindow() {
        // FXML fields are injected after construction.
    }

    /**
     * Configures automatic scrolling and disables Send when there is no command.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        sendButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> userInput.getText().isBlank(), userInput.textProperty()));
    }

    /**
     * Supplies the chatbot that generates replies.
     *
     * @param kaya the chatbot backing this window
     */
    public void setKaya(Kaya kaya) {
        this.kaya = kaya;
        dialogContainer.getChildren().add(DialogBox.getKayaDialog(Messages.GREETING));
        if (!kaya.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getKayaDialog(kaya.getStartupWarning()));
        }
    }

    /**
     * Sends the entered command to Kaya and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = kaya.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getKayaDialog(response));
        userInput.clear();
        userInput.requestFocus();

        if (input.equals("bye")) {
            PauseTransition farewellDelay = new PauseTransition(Duration.seconds(1));
            farewellDelay.setOnFinished(event -> Platform.exit());
            farewellDelay.play();
        }
    }
}
