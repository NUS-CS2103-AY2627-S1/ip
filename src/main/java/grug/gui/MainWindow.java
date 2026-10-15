package grug.gui;

import grug.ui.Ui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
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

    private Grug grug;

    /**
     * Initializes the Grug backend API, loads the saved tasks and greets the user.
     */
    public void initializeGrugBackend() {
        Ui ui = new GraphicalUi(this.userInput, this.dialogContainer);
        this.grug = new Grug(Grug.DEFAULT_STORAGE_FILE, ui);

        // show startup dialog and load the tasks
        this.grug.loadTasks();
        this.grug.greet();
    }

    /**
     * Sets the behavior of the {@link #dialogContainer} to scroll to the bottom
     * when its height changes (i.e. when a new dialog is added).
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow fxml does not inject `scrollPane`";
        assert dialogContainer != null : "MainWindow fxml does not inject `dialogContainer`";
        assert userInput != null : "MainWindow fxml does not inject `userInput`";
        assert sendButton != null : "MainWindow fxml does not inject `sendButton`";

        // scroll to the bottom when vbox height changes
        this.dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            this.scrollPane.setVvalue(this.scrollPane.getVmax());
        });
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Grug's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert this.grug != null : "initializeGrugBackend() must be called before handleUserInput()";

        String input = this.grug.readUserInput("").get();

        // only display the user's dialog box if its not blank
        // but don't return since blank could be a valid command
        if (!input.isBlank()) {
            DialogBox userDialogBox = DialogBox.createFromUserDialog(input);
            this.dialogContainer.getChildren().add(userDialogBox);
        }
        this.userInput.clear();

        boolean shouldExit = this.grug.handleUserInput(input);
        if (shouldExit) {
            Platform.exit();
        }
    }
}
