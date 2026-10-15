package food;

import java.io.IOException;
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

/** A chat bubble pairing a speaker's message with their avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert dialog != null && displayPicture != null : "DialogBox.fxml must define fx:id "
                + "\"dialog\" and \"displayPicture\" matching the fields in this class";
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Mirrors the bubble so the avatar and text swap sides. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /** Creates a right-aligned, tomato-red bubble for something the user typed. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox db = new DialogBox(text, image);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /** Creates a left-aligned bubble styled as a kitchen order ticket for one of Chef Food's replies. */
    public static DialogBox getFoodbotDialog(String text, Image image) {
        DialogBox db = new DialogBox(text, image);
        db.dialog.getStyleClass().add("chef-label");
        db.flip();
        return db;
    }
}
