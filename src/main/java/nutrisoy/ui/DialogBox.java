package nutrisoy.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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

/**
 * Represents a chat message with an avatar identifying the speaker.
 */
public class DialogBox extends HBox {
    /** Shared image data; each dialog uses its own ImageView node. */
    private static final Image SOYA_AVATAR = new Image(
            DialogBox.class.getResource("/images/soya-carton.png").toExternalForm(), 72, 72, true, true);

    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text, String speaker) {
        assert text != null : "Dialog text must not be null";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the NutriSoy conversation layout", e);
        }

        dialog.setText(text);
        avatar.setText(speaker);
        avatar.setAccessibleText(speaker.equals("S") ? "Soya" : "You");
        if (speaker.equals("S")) {
            ImageView picture = new ImageView(SOYA_AVATAR);
            picture.setFitWidth(36);
            picture.setFitHeight(36);
            picture.setPreserveRatio(true);
            avatar.setText(null);
            avatar.setGraphic(picture);
        }
        dialog.maxWidthProperty().bind(Bindings.min(widthProperty().multiply(0.76), 560));
    }

    /**
     * Places the avatar on the left and the message on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a user message with a You avatar.
     *
     * @param text message to display
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "You");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a Soya response with the generated soya-milk carton avatar.
     *
     * @param text response to display
     * @return app dialog box
     */
    public static DialogBox getNutriSoyDialog(String text) {
        var db = new DialogBox(text, "S");
        db.getStyleClass().add("nutrisoy-dialog");
        db.flip();
        return db;
    }

    /**
     * Creates an app response styled to make command errors easy to notice.
     *
     * @param text error message to display
     * @return dialog box with error styling
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "S");
        dialogBox.getStyleClass().addAll("nutrisoy-dialog", "error-dialog");
        dialogBox.flip();
        return dialogBox;
    }
}
