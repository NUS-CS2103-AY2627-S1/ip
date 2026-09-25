package shan.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import shan.CommandResult;
import shan.Shan;

/**
 * Controls the main Shan window defined in {@code MainWindow.fxml}.
 */
public class MainWindow {
    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image shanImage = new Image(
            this.getClass().getResourceAsStream("/images/DaShan.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Shan shan;

    /**
     * Constructs the main-window controller for the FXML loader.
     */
    public MainWindow() {
    }

    @FXML
    private void initialize() {
        assert this.scrollPane != null : "Scroll pane must be injected before initialization";
        assert this.dialogContainer != null : "Dialog container must be injected before initialization";
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
    }

    /**
     * Injects Shan and displays its startup messages.
     *
     * @param shan Shan instance that processes user commands.
     */
    public void setShan(Shan shan) {
        assert shan != null : "Shan must not be null";
        assert this.dialogContainer != null : "Dialog container must be injected before Shan is set";
        this.shan = shan;
        this.dialogContainer.getChildren().add(
                DialogBox.createShanDialog("Hey! I'm Shan.\nHow can I help?", this.shanImage));

        String startupWarning = this.shan.initialize();
        if (startupWarning != null) {
            this.dialogContainer.getChildren().add(
                    DialogBox.createShanDialog(startupWarning, this.shanImage));
        }
    }

    /**
     * Displays the user's command and Shan's response.
     */
    @FXML
    private void handleUserInput() {
        assert this.shan != null : "Shan must be set before handling user input";
        String input = this.userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        CommandResult result = this.shan.executeCommand(input);
        this.dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, this.userImage),
                DialogBox.createShanDialog(result.message(), this.shanImage));
        this.userInput.clear();

        if (result.shouldExit()) {
            Platform.exit();
        }
    }
}
