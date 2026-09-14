package baron.ui;

import baron.command.Commands;
import baron.data.BaronState;
import baron.exception.BaronException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controller for the main Baron chat window. */
public class MainWindow {
    @FXML private AnchorPane root;
    @FXML private ImageView backgroundImage;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private ScrollPane scrollPane;

    /** Loads the supplied Minecraft image as a cover background for the window. */
    public void applyBackground() {
        assert root != null && backgroundImage != null : "MainWindow FXML must inject the background controls";
        java.net.URL backgroundResource = getClass().getResource("/images/background.png");
        assert backgroundResource != null : "MainWindow background resource must exist";
        Image image = new Image(backgroundResource.toExternalForm());
        backgroundImage.setImage(image);
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
    }

    /** Displays the initial greeting in the conversation. */
    public void showWelcome() {
        assert dialogContainer != null : "MainWindow FXML must inject the dialog container";
        dialogContainer.getChildren().add(DialogBox.getDialog(
                "Welcome, traveler. I am Baron, keeper of this ledger from the sands of Barathos.\n"
                + "Mine is a land of proud warriors, ancient oaths, and strength proven by what we protect.\n"
                + "Name your next challenge, and I shall guard it until victory—or remind you when it draws near.",
                false));
    }

    /** Executes the command entered by the user and appends both messages. */
    @FXML
    private void handleUserInput() {
        assert userInput != null && dialogContainer != null && scrollPane != null
                : "MainWindow FXML must inject all input controls";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        displayUserMessage(input);
        displayResponse(executeCommand(input));
        if (BaronState.getExitStatus()) {
            Platform.exit();
            return;
        }
        clearInputAndScroll();
    }

    /** Adds the user's input to the conversation. */
    private void displayUserMessage(String input) {
        dialogContainer.getChildren().add(DialogBox.getDialog(input, true));
    }

    /** Executes a command and returns either its result or an error message. */
    private String executeCommand(String input) {
        String command = input.split(" ", 2)[0];
        String arguments = input.length() == command.length() ? "" : input.substring(command.length()).trim();
        try {
            String response = Commands.parse(command).execute(arguments);
            assert response != null : "Command execution must always produce a response";
            return response;
        } catch (BaronException e) {
            return e.getMessage();
        }
    }

    /** Adds a non-empty command response to the conversation. */
    private void displayResponse(String response) {
        if (!response.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getDialog(response, false));
        }
    }

    /** Clears the input field and scrolls to the newest message. */
    private void clearInputAndScroll() {
        userInput.clear();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
}
