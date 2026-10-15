package trybot.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import trybot.ui.ChatReply;

/**
 * Displays a compact user bubble or a wide, labelled TryBot reply.
 */
public class DialogBox extends HBox {
    private static final Image USER_IMAGE = new Image(DialogBox.class
            .getResource("/images/human.png").toExternalForm());
    private static final Image BOT_IMAGE = new Image(DialogBox.class
            .getResource("/images/trybot.png").toExternalForm());
    private static final double PORTRAIT_SPACE = 56;

    @FXML
    private VBox message;
    @FXML
    private Label speaker;
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, String heading, String styleClass, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a conversation message.", exception);
        }

        dialog.setText(text);
        speaker.setText(heading);
        message.getStyleClass().add(styleClass);
        displayPicture.setImage(isUser ? USER_IMAGE : BOT_IMAGE);
        displayPicture.setAccessibleText(isUser ? "Your profile picture" : "TryBot's profile picture");
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        // Reserve space for the portrait and gap so wrapped replies still fit narrow windows.
        message.maxWidthProperty().bind(widthProperty().subtract(PORTRAIT_SPACE).multiply(isUser ? 0.85 : 1.0));
        if (!isUser) {
            getChildren().remove(displayPicture);
            getChildren().addFirst(displayPicture);
            message.prefWidthProperty().bind(widthProperty().subtract(PORTRAIT_SPACE));
        }
    }

    /**
     * Creates a compact, right-aligned command bubble.
     *
     * @param text command entered by the user.
     * @return user message.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "YOU", "user-message", true);
    }

    /**
     * Creates a reply whose label and colour distinguish errors from ordinary messages.
     *
     * @param reply command result to display.
     * @return TryBot message.
     */
    public static DialogBox getTryBotDialog(ChatReply reply) {
        String heading = "TRYBOT";
        String styleClass = "bot-message";
        if (reply.hasError()) {
            heading = "TRYBOT · LET'S FIX THAT";
            styleClass = "error-message";
        } else if (reply.hasWarning()) {
            heading = "TRYBOT · NOT SAVED";
            styleClass = "error-message";
        }
        return new DialogBox(reply.text(), heading, styleClass, false);
    }
}
