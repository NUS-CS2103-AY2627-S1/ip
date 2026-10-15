package littler.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import littler.ui.UI;

/**
* Represents a dialog box consisting of an ImageView to represent the speaker's face
* and a TextFlow containing styled text from the speaker, styled as a rounded speech bubble.
*/
public class DialogBox extends HBox {
    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayPicture.setImage(img);
        clipToCircle(displayPicture);

        // Cap bubble at 75% of the HBox's actual width so it uses the available
        // space rather than being pinned to a fixed pixel count.
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.75));
    }

    /**
     * Crops the given ImageView into a circle, sized to match its current fit dimensions.
     * Avoids needing a separate circular image asset for the avatar.
     *
     * @param imageView the ImageView to clip
     */
    private static void clipToCircle(ImageView imageView) {
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;
        imageView.setClip(new Circle(radius, radius, radius));
    }

    /**
    * Flips the dialog box such that the ImageView is on the left and text on the right.
    */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a message typed by the user, with the command word and any
     * "/delimiter" tokens highlighted.
     *
     * @param text the user's raw input
     * @param img the user's avatar
     * @return a DialogBox styled as the user's speech bubble
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(img);
        db.dialog.getChildren().setAll(MessageFormatter.formatUserInput(text));
        db.dialog.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Creates a dialog box for LittleR's reply, with list indices, bracketed tags, and hashtags
     * highlighted. If the reply is an error message, it is additionally styled to stand out from
     * normal replies.
     *
     * @param text LittleR's response text
     * @param img LittleR's avatar
     * @return a DialogBox styled as LittleR's speech bubble
     */
    public static DialogBox getLittleRDialog(String text, Image img) {
        var db = new DialogBox(img);
        db.dialog.getChildren().setAll(MessageFormatter.formatBotReply(text));
        db.dialog.getStyleClass().add("bot-bubble");
        if (text.startsWith(UI.ERROR_PREFIX)) {
            db.dialog.getStyleClass().add("error-bubble");
        }
        db.flip();
        return db;
    }
}
