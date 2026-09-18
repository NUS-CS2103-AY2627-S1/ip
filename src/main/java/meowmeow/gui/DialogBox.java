package meowmeow.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import meowmeow.Response;
import meowmeow.ResponseKind;

/**
 * A single line of the conversation: a speaker's picture next to what they
 * said. Built from {@code DialogBox.fxml} using the {@code fx:root}
 * pattern, so this class is both the root {@link HBox} and its own
 * controller.
 *
 * <p>The avatar is clipped to a circle, and the text bubble's width is
 * bound to a fraction of this box's own width so long replies wrap instead
 * of forcing the window wider.
 */
public class DialogBox extends HBox {
    /** How much of the row's width the text bubble may use before wrapping. */
    private static final double BUBBLE_WIDTH_FRACTION = 0.72;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(BUBBLE_WIDTH_FRACTION));
        displayPicture.setImage(img);
        clipToCircle();
    }

    /** Clips the avatar image to a circle inscribed in its display bounds. */
    private void clipToCircle() {
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /** Flips the box so the picture is on the left and the text on the right. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for the user, picture on the right.
     *
     * @param text the user's message.
     * @param img  the user's picture.
     * @return the dialog box to add to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img);
        box.dialog.getStyleClass().add("user");
        return box;
    }

    /**
     * Returns a dialog box for one of Meowmeow's replies, picture on the
     * left. The bubble is styled differently depending on
     * {@link Response#kind()}, so an error or a warning reads as visually
     * distinct from an ordinary reply.
     *
     * @param response Meowmeow's reply, with the kind of reply it is.
     * @param img      Meowmeow's picture.
     * @return the dialog box to add to the conversation.
     */
    public static DialogBox getMeowmeowDialog(Response response, Image img) {
        DialogBox box = new DialogBox(response.text(), img);
        box.flip();
        if (response.kind() == ResponseKind.ERROR) {
            box.dialog.getStyleClass().add("error");
        } else if (response.kind() == ResponseKind.WARNING) {
            box.dialog.getStyleClass().add("warning");
        }
        return box;
    }
}
