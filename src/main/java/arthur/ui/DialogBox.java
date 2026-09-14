package arthur.ui;

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
import javafx.scene.text.Font;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    /*
        AI-assisted:
        * Named the dialog layout resource and reply CSS class.
        * Loads the bundled JetBrains Mono font before FXML creates dialog labels.
     */
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";
    private static final String DIALOG_FONT_RESOURCE = "/fonts/JetBrainsMono.ttf";
    private static final String REPLY_LABEL_CSS_CLASS = "reply-label";

    static {
        Font.loadFont(DialogBox.class.getResource(DIALOG_FONT_RESOURCE).toExternalForm(), -1);
    }

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, String className) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(DIALOG_BOX_FXML));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getStyleClass().add(className);
        dialog.setText(text);
        displayPicture.setImage(img);

        double circleWidth = displayPicture.getFitWidth() / 2;
        double circleHeight = displayPicture.getFitHeight() / 2;
        double circleRadius = Math.min(circleWidth, circleHeight);
        Circle clip = new Circle(circleWidth, circleHeight, circleRadius);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(REPLY_LABEL_CSS_CLASS);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, "");
    }

    public static DialogBox getArthurDialog(String text, Image img, String className) {
        var db = new DialogBox(text, img, className);
        db.flip();
        return db;
    }
}
