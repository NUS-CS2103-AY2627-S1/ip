package wally;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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

    private Wally wally;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image wallyImage = new Image(getClass().getResourceAsStream("/images/DaWally.png"));

    /**
     * Initializes the main window after its FXML components are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the chatbot that processes user input and displays its startup message.
     *
     * @param wally Chatbot instance.
     */
    public void setWally(Wally wally) {
        this.wally = wally;
        applyBackground(wally.getBackgroundColour());
        dialogContainer.getChildren().add(DialogBox.getWallyDialog(wally.getStartupMessage(), wallyImage));
    }

    /** Applies a supported background colour or returns the validation error. */
    private String changeBackground(String colour) {
        try {
            String background = wally.changeBackgroundColour(colour);
            applyBackground(background);
            return "Background changed to " + (background.equals("lightblue") ? "light blue" : background) + ".";
        } catch (InvalidBackgroundColourException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Could not save the background colour. Check that the save folder is writable.";
        }
    }

    /** Applies the background and a readable text colour to the window. */
    private void applyBackground(String background) {
        String textColour = background.equals("black") ? "white" : "black";
        scrollPane.getScene().getRoot().setStyle("-wally-background: " + background
                + "; -wally-text: " + textColour + ";");
    }

    /**
     * Processes user input and exits the application when Wally signals termination.
     * Otherwise, displays the input and Wally's reply and clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String[] commandParts = input.trim().split("\\s+", 2);
        String response;
        if (commandParts[0].equals("background")) {
            response = changeBackground(commandParts.length == 2 ? commandParts[1] : "");
        } else {
            response = wally.getResponse(input);
        }
        if ("TERMINATE_PROGRAM".equals(response)) {
            Platform.exit();
            return;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getWallyDialog(response, wallyImage));
        userInput.clear();
    }
}
