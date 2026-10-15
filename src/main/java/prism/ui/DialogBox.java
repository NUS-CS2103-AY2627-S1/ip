package prism.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one user or Prism message row in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label messageLabel;

    @FXML
    private ImageView avatarView;

    private DialogBox(String message, Image avatar, boolean isUserMessage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Unable to load a Prism message row.", exception);
        }

        messageLabel.setText(message);
        avatarView.setImage(avatar);
        messageLabel.setStyle(getMessageStyle(message, isUserMessage));
        if (avatar == null) {
            getChildren().remove(avatarView);
        } else {
            avatarView.setClip(new Circle(15, 15, 15));
        }
        if (!isUserMessage) {
            setAlignment(Pos.CENTER_LEFT);
            if (avatar == null) {
                getChildren().setAll(messageLabel);
            } else {
                getChildren().setAll(avatarView, messageLabel);
            }
        }
    }

    /** Creates a message row with the appropriate participant alignment and styling. */
    public static DialogBox create(String message, Image avatar, boolean isUserMessage) {
        return new DialogBox(message, avatar, isUserMessage);
    }

    /** Returns the visual style that distinguishes user, application, and error messages. */
    private String getMessageStyle(String message, boolean isUserMessage) {
        if (isUserMessage) {
            return "-fx-background-color: #3568d4; -fx-text-fill: white; -fx-background-radius: 12; "
                    + "-fx-font-size: 14px; -fx-padding: 8 12 8 12;";
        }
        if (message.contains(": !!!")) {
            return "-fx-background-color: #fff0f0; -fx-text-fill: #a51d2d; -fx-border-color: #e25563; "
                    + "-fx-border-width: 0 0 0 4; -fx-font-size: 14px; -fx-padding: 10 14 10 14;";
        }
        return "-fx-background-color: white; -fx-text-fill: #253047; -fx-background-radius: 10; "
                + "-fx-border-color: #e0e6f0; -fx-border-radius: 10; -fx-font-size: 14px; "
                + "-fx-padding: 10 14 10 14;";
    }
}
