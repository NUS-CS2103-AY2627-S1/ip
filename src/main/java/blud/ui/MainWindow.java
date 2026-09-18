package blud.ui;

import java.io.InputStream;

import blud.Blud;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/** Controls the FXML-defined main window and coordinates input with Blud. */
public class MainWindow extends BorderPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage("/images/User.png");
    private final Image bludImage = loadImage("/images/Blud.png");
    private Blud blud;

    /** Binds the conversation scroll position to the growing message list. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the chatbot model used to process submitted commands. */
    public void setBlud(Blud blud) {
        this.blud = blud;
        addBludMessage("Hey! This is Blud, what can I do for you today?\n\n"
                + "Try 'list', 'sort deadline', or 'bye'.", false);
        userInput.requestFocus();
    }

    /** Handles commands submitted by pressing Enter or clicking Send. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty() || blud == null) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(command, userImage));
        Blud.CommandResult result = blud.processCommandWithStatus(command);
        addBludMessage(result.response(), result.isError());
        userInput.clear();

        if ("bye".equalsIgnoreCase(command)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Adds a Blud response and applies error styling when processing failed. */
    private void addBludMessage(String text, boolean isError) {
        dialogContainer.getChildren().add(DialogBox.getBludDialog(text, bludImage, isError));
    }

    /** Loads a required avatar from the application resources. */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            throw new IllegalStateException("Unable to load Blud image resource: " + resourcePath);
        }
        return new Image(imageStream);
    }
}
