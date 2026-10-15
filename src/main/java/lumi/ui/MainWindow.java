package lumi.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lumi.Lumi;

/**
 * Controls Lumi's main graphical interface.
 */
public class MainWindow extends BorderPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Lumi lumi;

    /** Binds the scroll position to the height of the dialog container. */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml must inject scrollPane";
        assert dialogContainer != null : "MainWindow.fxml must inject dialogContainer";
        assert userInput != null : "MainWindow.fxml must inject userInput";
        assert sendButton != null : "MainWindow.fxml must inject sendButton";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Lumi instance used to generate responses.
     *
     * @param lumi Lumi chatbot instance.
     */
    public void setLumi(Lumi lumi) {
        assert lumi != null : "MainWindow requires a Lumi command engine";
        this.lumi = lumi;
        if (!lumi.getStartupMessage().isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getLumiDialog(lumi.getStartupMessage()));
        }
    }

    /**
     * Shows the user's input and Lumi's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        assert lumi != null : "Lumi must be set before the window accepts input";
        String input = userInput.getText();
        String response = lumi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getLumiDialog(response)
        );
        userInput.clear();
        if (!lumi.isRunning()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        } else {
            userInput.requestFocus();
        }
    }
}
