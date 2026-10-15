package pebby;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pebby.parser.CommandType;
import pebby.parser.ParsedCommand;
import pebby.parser.Parser;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    /** The first message Pebby shows in the graphical interface. */
    static final String GUI_WELCOME = "Hi, I’m Pebby. I may be sleepy, but I’m here.";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Pebby pebby;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image pebbyImage = new Image(this.getClass().getResourceAsStream("/images/DaPebby.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Pebby instance */
    public void setPebby(Pebby p) {
        pebby = p;
        dialogContainer.getChildren().add(DialogBox.getPebbyDialog(commandGuide(), pebbyImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = pebby.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPebbyDialog(addFormatGuidance(response), pebbyImage)
        );
        userInput.clear();
        if (isExitCommand(input)) {
            Platform.exit();
        }
    }

    /** Returns whether the supplied input is Pebby's valid command to close the window. */
    static boolean isExitCommand(String input) {
        ParsedCommand parsedCommand = Parser.parse(input);
        return parsedCommand.getType() == CommandType.BYE && !parsedCommand.hasError();
    }

    /**
     * Adds a command example to error messages displayed in the graphical interface.
     *
     * @param response Pebby's original response
     * @return the response with a relevant command format when it is an error
     */
    static String addFormatGuidance(String response) {
        if (response.startsWith("Invalid todo")) {
            return response + "\n\nCorrect format: todo <task description>";
        }
        if (response.startsWith("Invalid deadline")) {
            return response + "\n\nCorrect format: deadline <task description> "
                    + "/by <yyyy-MM-dd or d MMMM yyyy>";
        }
        if (response.startsWith("Invalid event")) {
            return response + "\n\nCorrect format: event <task description> "
                    + "/from <yyyy-MM-dd or d MMMM yyyy> /to <yyyy-MM-dd or d MMMM yyyy>";
        }
        if (response.startsWith("Invalid mark")) {
            return response + "\n\nCorrect format: mark <task number>";
        }
        if (response.startsWith("Invalid unmark")) {
            return response + "\n\nCorrect format: unmark <task number>";
        }
        if (response.startsWith("Invalid delete")) {
            return response + "\n\nCorrect format: delete <task number>";
        }
        if (response.startsWith("Invalid find")) {
            return response + "\n\nCorrect format: find <keyword>";
        }
        if (response.startsWith("Invalid schedule")) {
            return response + "\n\nCorrect format: schedule <yyyy-MM-dd or d MMMM yyyy>";
        }
        if (response.startsWith("Hmmm")) {
            return response + "\n\nType help to see the available commands.";
        }
        return response;
    }

    /**
     * Returns the command reference shown when the graphical interface opens.
     */
    private String commandGuide() {
        return GUI_WELCOME;
    }
}
