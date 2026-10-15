package nutrisoy.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import nutrisoy.Duke;

/**
 * Controls the main chat window and connects it to the NutriSoy backend.
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
    @FXML
    private Text statusText;

    private Duke duke;

    /**
     * Initializes the chat window after its FXML controls have been loaded.
     */
    @FXML
    public void initialize() {
        // Scroll after layout adds a reply, while leaving the scrollbar free for manual reading.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
    }

    /**
     * Supplies the backend instance used to process user commands.
     *
     * @param duke application backend
     */
    public void setDuke(Duke duke) {
        assert duke != null : "Application backend must not be null";
        this.duke = duke;
        dialogContainer.getChildren().add(DialogBox.getNutriSoyDialog(
                "Hey, I'm " + Ui.BOT_NAME + ". Drop me a task and I'll keep your chaos organised."));
        if (!duke.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(duke.getStartupWarning()));
            statusText.setText("Saved tasks need attention");
            statusText.getStyleClass().add("error-status");
        }
        userInput.requestFocus();
    }

    /**
     * Sends the entered command to the backend and adds both chat bubbles.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        GuiResponse response = duke.getGuiResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.isError()
                        ? DialogBox.getErrorDialog(response.getMessage())
                        : DialogBox.getNutriSoyDialog(response.getMessage())
        );
        userInput.clear();
        statusText.setText(response.isError() ? "That command needs a makeover" : "Ready to serve");
        statusText.getStyleClass().setAll("status-text", response.isError() ? "error-status" : "ready-status");

        if (response.isExit()) {
            javafx.application.Platform.exit();
        }
    }

}
