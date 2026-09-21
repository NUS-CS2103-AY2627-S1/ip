package twizzy.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import twizzy.Twizzy;

/** Controls the main Twizzy chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Label taskSummary;

    private Twizzy twizzy;

    /** Configures scrolling after FXML fields have been injected. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the task application used to process GUI commands.
     *
     * @param twizzy application instance to use
     */
    public void setTwizzy(Twizzy twizzy) {
        this.twizzy = twizzy;
        twizzy.initializeForGui();
        dialogContainer.getChildren().add(DialogBox.bot("Wsg, I'm Twizzy — your task-list twin.\n"
                + "Type help to see what I can do."));
        updateTaskSummary();
        if (twizzy.getGuiStartupError() != null) {
            dialogContainer.getChildren().add(DialogBox.error(twizzy.getGuiStartupError()));
        }
    }

    /** Processes text submitted with Enter or the Send button. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText();
        if (command.isBlank()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.user(command));
        String response = twizzy.getResponse(command);
        dialogContainer.getChildren().add(isErrorResponse(response)
                ? DialogBox.error(response) : DialogBox.bot(response));
        updateTaskSummary();
        userInput.clear();

        if (command.equals("bye")) {
            Platform.runLater(Platform::exit);
        }
    }

    /** Returns whether a response represents a rejected command. */
    private boolean isErrorResponse(String response) {
        return response.startsWith("Nah, you gotta lock in, gang.");
    }

    /** Refreshes the persistent task-status summary after each submitted command. */
    private void updateTaskSummary() {
        taskSummary.setText(twizzy.getTaskSummary());
    }
}
