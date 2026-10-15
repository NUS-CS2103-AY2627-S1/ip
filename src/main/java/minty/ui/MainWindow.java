package minty.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import minty.Minty;

/**
 * Controls Minty's main chat window.
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

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user-avatar.png"));
    private final Image mintyImage = new Image(getClass().getResourceAsStream("/images/minty-avatar.png"));

    private final Image celebratingImage = new Image(
            getClass().getResourceAsStream("/images/minty-celebrating.png"));
    private final Image curiousImage = new Image(getClass().getResourceAsStream("/images/minty-curious.png"));
    private final Image wavingImage = new Image(getClass().getResourceAsStream("/images/minty-waving.png"));

    private Minty minty;

    /**
     * Configures behavior that depends only on the loaded interface controls.
     */
    @FXML
    public void initialize() {
        for (String weight : new String[] {"Regular", "Bold", "ExtraBold"}) {
            String resource = "/fonts/Nunito-" + weight + ".ttf";
            Font.loadFont(getClass().getResource(resource).toExternalForm(), 15);
        }
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the window to the chatbot and displays its welcome message.
     *
     * @param minty chatbot that processes user commands.
     */
    public void setMinty(Minty minty) {
        this.minty = minty;
        dialogContainer.getChildren().add(DialogBox.getMintyDialog(
                "Heyyy! I'm Minty, your fresh little task buddy!\nLet's get things moving. What's"
                        + " on your list today?", mintyImage));
        if (!minty.getStartupError().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getMintyDialog(
                    minty.getStartupError(), curiousImage, Minty.ResponseType.ERROR));
        }
        userInput.requestFocus();
        String reminders = minty.getStartupReminders();
        if (!reminders.isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getMintyDialog(reminders, mintyImage, Minty.ResponseType.REMINDER));
        }
    }

    /**
     * Sends the current command to Minty and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        Minty.ChatResponse response = minty.getChatResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMintyDialog(response.text(), getExpressionImage(response.expression()), response.type()));
        userInput.clear();

        if (response.expression() == Minty.Expression.WAVING) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            Platform.exit();
        }
    }
    /**
     * Selects the cached avatar for one response without changing earlier bubbles.
     *
     * @param expression reaction reported by the chatbot.
     * @return matching mascot image.
     */
    private Image getExpressionImage(Minty.Expression expression) {
        return switch (expression) {
            case DEFAULT -> mintyImage;
            case CELEBRATING -> celebratingImage;
            case CURIOUS -> curiousImage;
            case WAVING -> wavingImage;
        };
    }

}
