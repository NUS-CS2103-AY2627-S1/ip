package baemax;

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

/**
 * One chat bubble: a wrapped text label beside the speaker's avatar. The
 * layout comes from {@code DialogBox.fxml}.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        getStyleClass().add("dialog-box");

        // Clip the square avatar to a circle.
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /** Flips the bubble so the avatar is on the left and the text on the right. */
    private void flip() {
        // DialogBox.fxml always builds exactly the label and the image view;
        // reversing anything else would silently scramble the layout.
        assert getChildren().size() == 2 : "expected exactly the label and the image view";
        ObservableList<Node> nodes = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("reply");
    }

    /**
     * Returns a bubble for something the user typed (avatar on the right).
     *
     * @param text the user's message
     * @param image the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a bubble for a Baemax reply (avatar on the left).
     *
     * @param text Baemax's message
     * @param image Baemax's avatar
     * @param isError whether to style this bubble as an error reply
     * @return the flipped dialog box
     */
    public static DialogBox getBaemaxDialog(String text, Image image, boolean isError) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        if (isError) {
            box.getStyleClass().add("error");
        }
        return box;
    }
}
