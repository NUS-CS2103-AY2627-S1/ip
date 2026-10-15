package nob.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Displays a user command bubble or a branded Nob response panel.
 */
public class DialogBox extends HBox {
    private static final double USER_MESSAGE_MAX_WIDTH = 360;
    private static final double NOB_AVATAR_SIZE = 40;
    private static final double NOB_AVATAR_RADIUS = NOB_AVATAR_SIZE / 2;

    @FXML
    private Label dialog;

    /** Loads and configures a dialog box for the specified speaker. */
    private DialogBox(String text, Image avatarImage, String fallbackAvatar, boolean isUser) {
        try {
            URL dialogBoxResource = DialogBox.class.getResource("/view/DialogBox.fxml");
            assert dialogBoxResource != null : "The application package must contain DialogBox.fxml";
            FXMLLoader loader = new FXMLLoader(dialogBoxResource);
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        assert dialog != null : "DialogBox.fxml must inject dialog";
        dialog.setText(text);
        if (isUser) {
            configureUserMessage();
        } else {
            configureNobResponse(avatarImage, fallbackAvatar);
        }
    }

    /** Configures a compact message bubble for a command entered by the user. */
    private void configureUserMessage() {
        getStyleClass().add("user-dialog");
        dialog.getStyleClass().add("user-bubble");
        dialog.setMaxWidth(USER_MESSAGE_MAX_WIDTH);
        setAlignment(Pos.TOP_RIGHT);
        getChildren().setAll(dialog);
    }

    /** Configures a wider, branded response panel for a reply produced by Nob. */
    private void configureNobResponse(Image avatarImage, String fallbackAvatar) {
        getStyleClass().add("nob-dialog");
        dialog.getStyleClass().add("nob-response");
        dialog.setMaxWidth(Double.MAX_VALUE);

        Label speakerLabel = new Label("NOB");
        speakerLabel.getStyleClass().add("nob-name");
        VBox responseContent = new VBox(4, speakerLabel, dialog);
        responseContent.getStyleClass().add("nob-response-content");
        responseContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(responseContent, Priority.ALWAYS);

        Node avatar = createAvatar(avatarImage, fallbackAvatar);
        setAlignment(Pos.TOP_LEFT);
        getChildren().setAll(avatar, responseContent);
    }

    /** Returns a circular image avatar, or an emoji avatar when the image is unavailable. */
    private Node createAvatar(Image avatarImage, String fallbackAvatar) {
        if (avatarImage == null) {
            Label avatarLabel = new Label(fallbackAvatar);
            avatarLabel.getStyleClass().add("avatar-fallback");
            return avatarLabel;
        }

        ImageView avatarView = new ImageView(avatarImage);
        avatarView.setFitWidth(NOB_AVATAR_SIZE);
        avatarView.setFitHeight(NOB_AVATAR_SIZE);
        avatarView.setPreserveRatio(false);
        avatarView.setClip(new Circle(NOB_AVATAR_RADIUS, NOB_AVATAR_RADIUS, NOB_AVATAR_RADIUS));
        return avatarView;
    }

    /**
     * Returns a compact, right-aligned command bubble for the user.
     *
     * @param text The user's message.
     * @return The formatted user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null, "", true);
    }

    /**
     * Returns a wide, left-aligned response panel for Nob.
     *
     * @param text Nob's response.
     * @param avatarImage Nob's avatar, or {@code null} to use the fallback emoji.
     * @return The formatted Nob dialog.
     */
    public static DialogBox getNobDialog(String text, Image avatarImage) {
        return new DialogBox(text, avatarImage, "🤖", false);
    }
}
