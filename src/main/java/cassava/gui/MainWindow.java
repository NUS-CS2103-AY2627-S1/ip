package cassava.gui;

import static cassava.Handlers.handleInput;
import static cassava.ui.Parser.VALID_CMDS;
import static cassava.ui.Parser.parseUserInput;

import java.util.HashMap;
import java.util.List;

import cassava.Handlers;
import cassava.task.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Represents the main window in the GUI.
 */
public class MainWindow {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private ToggleButton darkModeToggle;

    private List<Task> tasks;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Handles the user input.
     * Gets the result from Cassava.Handlers,
     * then creates and adds the corresponding DialogBox.
     */
    @FXML
    private void handleUserInput() {
        // Add User Dialog
        String input = userInput.getText();
        addUserDialog(input);
        userInput.clear();

        // Handle User Input
        HashMap<String, String> inputArgs = parseUserInput(input);
        Handlers.HandlerResult result = handleInput(tasks, inputArgs, VALID_CMDS);

        if (result.isError()) {
            addErrorDialog(result.message());
        } else {
            addCassavaDialog(result.message());
        }

        if (result.isExit()) {
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Toggles the application between light and dark mode.
     */
    @FXML
    private void handleToggleDarkMode() {
        boolean isDark = darkModeToggle.isSelected();
        if (isDark) {
            rootPane.getStyleClass().add("dark-mode");
            darkModeToggle.setText("☀");
        } else {
            rootPane.getStyleClass().remove("dark-mode");
            darkModeToggle.setText("🌙");
        }
    }

    /**
     * Add a user message DialogBox.
     * @param input Message to display.
     */
    private void addUserDialog(String input) {
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
    }

    /**
     * Add a Cassava message DialogBox.
     * @param input Message to display.
     */
    public void addCassavaDialog(String input) {
        dialogContainer.getChildren().add(DialogBox.getCassavaDialog(input));
    }

    /**
     * Add an error message DialogBox.
     * @param input Message to display.
     */
    public void addErrorDialog(String input) {
        dialogContainer.getChildren().add(DialogBox.getErrorDialog(input));
    }
}
