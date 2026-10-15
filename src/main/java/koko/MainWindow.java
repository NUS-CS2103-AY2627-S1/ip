package koko;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls the main window of the Koko user interface.
 */
public class MainWindow {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Koko koko;

    /**
     * Keeps the newest chat message visible after the dialog container grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToLatestMessage());
    }

    public void setKoko(Koko koko) {
        this.koko = koko;

        dialogContainer.getChildren().add(
                DialogBox.getKokoDialog(
                        "Chirp! I'm Koko the Taskbird. What shall we get flying today?"
                )
        );

        userInput.requestFocus();
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input == null || input.isBlank()) {
            return;
        }

        Koko.GuiResponse response = koko.getGuiResponse(input);

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        dialogContainer.getChildren().add(response.isError()
                ? DialogBox.getErrorDialog(response.text())
                : DialogBox.getKokoDialog(response.text()));

        userInput.clear();
        scrollToLatestMessage();
        userInput.requestFocus();
    }

    /**
     * Scrolls after JavaFX has finished laying out a newly added message.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
