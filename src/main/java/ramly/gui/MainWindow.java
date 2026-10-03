package ramly.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ramly.Ramly;
import ramly.ui.Ui;

/** Controller for the FXML-based Ramly chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage("/images/human-face.png");
    private final Image ramlyImage = loadImage("/images/pip-firefly.png");
    private Ramly ramly;

    /** Binds the scroll position to the growing conversation, as in the tutorial. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        addRamlyDialog("Hey, I’m Pip—your pocket pathfinder.\n"
                + "Try `todo`, `deadline`, `event`, `list`, `find`, `mark`, `unmark`, `delete`, `undo`, or `bye`.");
    }

    /** Injects the command engine used by both the GUI and text interfaces. */
    public void setRamly(Ramly ramly) {
        assert ramly != null : "Command engine must not be null";
        this.ramly = ramly;
        ramly.getStartupMessages().forEach(this::addRamlyDialog);
        if (!ramly.isStorageAvailable()) {
            disableInput("Task storage is unavailable");
        }
    }

    /** Processes text entered by the user and appends the conversation to the window. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || ramly == null) {
            return;
        }

        List<String> output = new ArrayList<>();
        Ui responseUi = new Ui(output::add);
        boolean isExit = ramly.executeCommand(input, responseUi);
        responseUi.close();

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        if (!output.isEmpty()) {
            addRamlyDialog(String.join("\n", output));
        }
        userInput.clear();

        if (isExit) {
            disableInput("Pip has left the trail for now");
        }
    }

    /** Disables command entry and explains why it is unavailable. */
    private void disableInput(String promptText) {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        userInput.setPromptText(promptText);
    }

    /** Loads a required packaged image or fails with a descriptive startup error. */
    private static Image loadImage(String resourcePath) {
        try (InputStream stream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Missing GUI image resource: " + resourcePath);
            }
            return new Image(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to close GUI image resource: " + resourcePath, e);
        }
    }

    /** Appends a response bubble from Ramly to the conversation. */
    private void addRamlyDialog(String text) {
        dialogContainer.getChildren().add(DialogBox.getRamlyDialog(text, ramlyImage));
    }

}
