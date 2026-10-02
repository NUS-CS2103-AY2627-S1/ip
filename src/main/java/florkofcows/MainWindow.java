package florkofcows;

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
    private static final String[] ITALIC_UPPER = {"𝐀", "𝐁", "𝐂", "𝐃", "𝐄", "𝐅", "𝐆", "𝐇", "𝐈", "𝐉", "𝐊", "𝐋", "𝐌", "𝐍", "𝐎", "𝐏", "𝐐", "𝐑", "𝐒", "𝐓", "𝐔", "𝐕", "𝐖", "𝐗", "𝐘", "𝐙"};
    private static final String[] ITALIC_LOWER = {"𝐚", "𝐛", "𝐜", "𝐝", "𝐞", "𝐟", "𝐠", "𝐡", "𝐢", "𝐣", "𝐤", "𝐥", "𝐦", "𝐧", "𝐨", "𝐩", "𝐪", "𝐫", "𝐬", "𝐭", "𝐮", "𝐯", "𝐰", "𝐱", "𝐲", "𝐳"};
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private FlorkOfCows florkOfCows;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/florktheuser.jpg"));
    private Image florkOfCowsImage = new Image(this.getClass().getResourceAsStream("/images/florkofcowsChat.png"));

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     * It sets up the scroll pane to always scroll to the bottom when new messages are added.
     * It also displays a welcome message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        String welcomeMessage = String.join(System.lineSeparator(),
                "Wassup! Got something on your mind today?",
                "",
                "Here are some useful commands!",
                formatCommandLine("list", "View tasks"),
                formatCommandLine("todo", "TASK"),
                formatCommandLine("deadline", "TASK /by DATE"),
                formatCommandLine("event", "TASK /from DATE /to DATE"),
                formatCommandLine("mark", "TASK"),
                formatCommandLine("unmark", "TASK"),
                formatCommandLine("delete", "INDEX"),
                formatCommandLine("find", "KEYWORD"),
                formatCommandLine("on", "DATE"),
                formatCommandLine("tag", "INDEX TAGNAME"),
                formatCommandLine("bye", "Exit program"),
                formatCommandLine("date format", "yyyy-MM-dd"));
        DialogBox welcome = DialogBox.getFlorkOfCowsDialog(welcomeMessage, florkOfCowsImage);
        welcome.setWelcomeStyle();
        dialogContainer.getChildren().add(welcome);
    }

    private String formatCommandLine(String command, String details) {
        if (details == null || details.isEmpty()) {
            return italicize(command);
        }
        return italicize(command) + " - " + details;
    }

    private String italicize(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                builder.append(ITALIC_UPPER[ch - 'A']);
            } else if (ch >= 'a' && ch <= 'z') {
                builder.append(ITALIC_LOWER[ch - 'a']);
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /** Injects the FlorkOfCows instance */
    public void setFlorkOfCows(FlorkOfCows foc) {
        florkOfCows = foc;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing FlorkOfCows's reply and then appends
     * them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = florkOfCows.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));

        DialogBox reply = DialogBox.getFlorkOfCowsDialog(response, florkOfCowsImage);
        if (florkOfCows.isLastCommandError()) {
            reply.setErrorBubble();
        }
        dialogContainer.getChildren().add(reply);
        userInput.clear();

        if (florkOfCows.isExitRequested()) {
            javafx.application.Platform.exit();
        }
    }
}
