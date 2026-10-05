package gatsby.ui;

import gatsby.Gatsby;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Controls Gatsby's main JavaFX chat window. */
public class MainWindow {
    /** Shows the conversation history. */
    @FXML
    private ScrollPane scrollPane;

    /** Holds each user and Gatsby dialog. */
    @FXML
    private VBox dialogContainer;

    /** Accepts a command from the user. */
    @FXML
    private TextField userInput;

    /** Sends the command currently in {@link #userInput}. */
    @FXML
    private Button sendButton;

    /** The command-processing chatbot shared with the console UI. */
    private Gatsby gatsby;

    /** Binds scrolling to the conversation height after FXML injection. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot that supplies responses to this view.
     *
     * @param gatsby the command-processing chatbot
     */
    public void setGatsby(Gatsby gatsby) {
        this.gatsby = gatsby;
        dialogContainer.getChildren().add(DialogBox.getGatsbyDialog(
                "Wassup! I'm Gatsby.\n"
                        + Ui.getStartingJoke() + "\n"
                        + "Try \"todo read book\" or \"list\"."));
    }

    /** Handles both pressing Enter and clicking Send. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        Gatsby.Response response = gatsby.getResponseDetails(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getGatsbyDialog(response.getText(), response.isError()));
        userInput.clear();

        if (gatsby.isExitCommand(input)) {
            closeWindow();
        }
    }

    /** Closes the main window and ends the JavaFX application. */
    private void closeWindow() {
        Stage stage = (Stage) userInput.getScene().getWindow();
        stage.close();
        Platform.exit();
    }
}
