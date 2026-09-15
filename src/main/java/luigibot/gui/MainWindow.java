package luigibot.gui;

import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import luigibot.LuigiBot;

/**
 * Controls the main LuigiBot GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String DIVIDER_LINE = "____________________________________________________________";
    private static final String WELCOME_MESSAGE = "Its a-me,LuigiBot!\nWhat can I do for you?";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private LuigiBot luigiBot;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/Mario.png"));
    private final Image luigiImage = new Image(getClass().getResourceAsStream("/images/Luigi.png"));

    /**
     * Creates the controller used by the FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Sets the LuigiBot instance that processes GUI commands.
     *
     * @param luigiBot LuigiBot instance used by the GUI.
     */
    public void setLuigiBot(LuigiBot luigiBot) {
        this.luigiBot = Objects.requireNonNull(luigiBot);
    }

    /**
     * Binds the message area to scroll to the newest dialog.
     */
    @FXML
    public void initialize() {
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
        this.dialogContainer.getChildren().add(
                DialogBox.getLuigiDialog(WELCOME_MESSAGE, this.luigiImage));
    }

    /**
     * Displays the user's message and LuigiBot's response.
     */
    @FXML
    private void handleUserInput() {
        String userText = this.userInput.getText();
        if (userText.isBlank()) {
            this.userInput.clear();
            return;
        }

        String luigiText = formatResponse(this.luigiBot.getResponse(userText));
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, this.userImage),
                DialogBox.getLuigiDialog(luigiText, this.luigiImage));
        this.userInput.clear();

        if (this.luigiBot.isExitRequested()) {
            Platform.exit();
        }
    }

    /**
     * Removes console-only separator lines before displaying a response in the GUI.
     *
     * @param response Console-formatted LuigiBot response.
     * @return Response text formatted for a chat dialog.
     */
    static String formatResponse(String response) {
        return response.replace(DIVIDER_LINE, "").trim();
    }
}
