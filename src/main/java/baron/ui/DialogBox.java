package baron.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A single chat message rendered with the appropriate avatar. */
public class DialogBox extends HBox {
    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    private DialogBox(String text, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            // The FXML contract requires these controls to be injected before use.
            assert dialog != null : "DialogBox FXML must inject the dialog label";
            assert displayPicture != null : "DialogBox FXML must inject the avatar view";
            dialog.setText(text);
            String imagePath = isUser ? "/images/user.png" : "/images/baron.png";
            java.net.URL imageResource = getClass().getResource(imagePath);
            assert imageResource != null : "DialogBox avatar resource must exist";
            Image image = new Image(imageResource.toExternalForm());
            displayPicture.setImage(image);
            displayPicture.setSmooth(false);
            displayPicture.setVisible(true);
            displayPicture.setManaged(true);
            double avatarSize = isUser ? 48 : 120;
            displayPicture.setFitWidth(avatarSize);
            displayPicture.setFitHeight(avatarSize);
            if (!isUser) {
                setAlignment(javafx.geometry.Pos.TOP_LEFT);
                dialog.getStyleClass().add("reply-label");
                getChildren().setAll(displayPicture, dialog);
            } else {
                getChildren().setAll(dialog, displayPicture);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box", e);
        }
    }

    /** Creates a chat message for the given speaker. */
    public static DialogBox getDialog(String text, boolean isUser) {
        return new DialogBox(text, isUser);
    }
}
