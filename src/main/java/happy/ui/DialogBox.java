package happy.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Custom JavaFX control representing a speech dialog box.
 * Consists of an ImageView representing the speaker's face and a Label containing text.
 */
public class DialogBox extends HBox {
    private Label text;
    private ImageView displayPicture;

    /**
     * Constructs a DialogBox with the specified text label and avatar image.
     *
     * @param label Message text label.
     * @param imageView Speaker avatar image.
     */
    public DialogBox(Label label, ImageView imageView) {
        text = label;
        displayPicture = imageView;

        text.setWrapText(true);
        displayPicture.setFitWidth(50.0);
        displayPicture.setFitHeight(50.0);

        Circle clip = new Circle(25, 25, 25);
        displayPicture.setClip(clip);

        setSpacing(10);
        setAlignment(Pos.TOP_RIGHT);
        getChildren().addAll(text, displayPicture);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text is on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(tmp);
        getChildren().setAll(tmp);
    }

    /**
     * Creates a user dialog box (right-aligned).
     *
     * @param label Message text label.
     * @param imageView User avatar image.
     * @return User DialogBox instance.
     */
    public static DialogBox getUserDialog(Label label, ImageView imageView) {
        label.getStyleClass().add("user-label");
        return new DialogBox(label, imageView);
    }

    /**
     * Creates a Happy chatbot dialog box (left-aligned / flipped).
     *
     * @param label Message text label.
     * @param imageView Happy avatar image.
     * @return Happy DialogBox instance.
     */
    public static DialogBox getHappyDialog(Label label, ImageView imageView) {
        label.getStyleClass().add("happy-label");
        DialogBox dialogBox = new DialogBox(label, imageView);
        dialogBox.flip();
        return dialogBox;
    }
}
