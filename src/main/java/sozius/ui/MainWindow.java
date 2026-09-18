package sozius.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sozius.Sozius;
import sozius.exception.SoziusException;

/** Controller for the main GUI. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sozius sozius;
    private Stage stage;

    /** Initializes the conversation and input controls. */
    @FXML
    public void initialize() {
        // Follow new messages and wrapped content after JavaFX lays them out.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty());
        dialogContainer.getChildren().add(DialogBox.getSoziusDialog(
                "Hello! I'm Sozius. What do you need?\nEnter \"help\" for more info.\n", null));
        Platform.runLater(() -> userInput.requestFocus());
    }

    /** Injects the Sozius instance. */
    public void setSozius(Sozius sozius) {
        assert sozius != null;
        this.sozius = sozius;
        if (sozius.getLoadWarning() != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(sozius.getLoadWarning()));
        }
    }

    /** Injects the Stage instance. */
    public void setStage(Stage stage) {
        assert stage != null;
        this.stage = stage;
    }

    /** Sends a command and keeps unsuccessful input available for correction. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            userInput.clear();
            userInput.requestFocus();
            return;
        }
        if (input.equals("bye")) {
            try {
                sozius.saveTasks();
            } catch (SoziusException e) {
                dialogContainer.getChildren().add(DialogBox.getErrorDialog(e.getMessage()));
                userInput.requestFocus();
                return;
            }
            stage.close();
            return;
        }

        String response;
        boolean isError;
        try {
            response = sozius.getResponse(input);
            isError = false;
        } catch (SoziusException e) {
            response = e.getMessage();
            isError = true;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, null),
                isError ? DialogBox.getErrorDialog(response) : DialogBox.getSoziusDialog(response, null));
        if (isError) {
            userInput.selectAll();
        } else {
            userInput.clear();
        }
        userInput.requestFocus();
    }
}
