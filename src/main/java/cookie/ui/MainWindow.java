package cookie.ui;

import cookie.Cookie;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls Cookie's main chat window. */
public class MainWindow extends AnchorPane {
    private final Image cookieImage = new Image(
            getClass().getResourceAsStream("/images/DaCookie.jpg"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Cookie cookie;

    /** Configures scrolling after the FXML controls have been injected. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot that handles commands from this window.
     *
     * @param cookie The Cookie instance to use.
     */
    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
        Cookie.CommandResult startupResult = cookie.getStartupResult();
        if (!startupResult.message().isBlank()) {
            DialogBox startupDialog = startupResult.isError()
                    ? DialogBox.getErrorDialog(startupResult.message(), cookieImage)
                    : DialogBox.getCookieDialog(startupResult.message(), cookieImage);
            dialogContainer.getChildren().add(startupDialog);
        }
    }

    /** Adds the user's message and Cookie's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Cookie.CommandResult result = cookie.getCommandResult(input);
        DialogBox responseDialog = result.isError()
                ? DialogBox.getErrorDialog(result.message(), cookieImage)
                : DialogBox.getCookieDialog(result.message(), cookieImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();
    }
}
