package bingbong;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a graphical dialog box class containing text and user avatars.
 */
public class DialogBox extends HBox {

    private final Label text;
    private final ImageView displayPicture;

    /**
     * Constructs a new DialogBox instance with specified text contents and profile image.
     *
     * @param l The label containing text strings.
     * @param iv The imageView containing avatar assets.
     * @param labelStyleClass The CSS style configuration name.
     */
    public DialogBox(Label l, ImageView iv, String labelStyleClass) {
        text = l;
        displayPicture = iv;

        this.getStyleClass().add("dialog-box");
        text.getStyleClass().add(labelStyleClass);

        displayPicture.setFitWidth(55);
        displayPicture.setFitHeight(55);

        Circle clipCircle = new Circle(27.5, 27.5, 27.5);
        displayPicture.setClip(clipCircle);

        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(text, displayPicture);
    }

    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    public static DialogBox getUserDialog(Label l, ImageView iv) {
        l.setWrapText(true);
        return new DialogBox(l, iv, "user-label");
    }

    public static DialogBox getBingBongDialog(Label l, ImageView iv, boolean isError) {
        l.setWrapText(true);
        String styleClass = isError ? "error-label" : "chatbot-label";
        var db = new DialogBox(l, iv, styleClass);
        db.flip();
        iv.setScaleX(-1);
        return db;
    }
}
