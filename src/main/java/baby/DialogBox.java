package baby;

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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Represents one row in the chat conversation.
 */
public class DialogBox extends HBox {
    private static final Image QUEEN_IMAGE =
            new Image(DialogBox.class.getResourceAsStream("/images/queen.png"));
    private static final Image SNOWMAN_IMAGE =
            new Image(DialogBox.class.getResourceAsStream("/images/snowman.png"));
    private static final Image USER_IMAGE =
            new Image(DialogBox.class.getResourceAsStream("/images/princess.png"));

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    @FXML
    private Label avatarText;

    @FXML
    private StackPane avatarFrame;

    private DialogBox(String text, Image image, String avatarTextValue,
            String labelStyleClass, String avatarStyleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.getStyleClass().add("dialog-label");
        dialog.getStyleClass().add(labelStyleClass);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.68));
        avatarFrame.getStyleClass().add(avatarStyleClass);

        if (image == null) {
            displayPicture.setVisible(false);
            displayPicture.setManaged(false);
            avatarText.setText(avatarTextValue);
        } else {
            displayPicture.setImage(image);
            displayPicture.setClip(new Circle(29.0, 29.0, 29.0));
            avatarText.setVisible(false);
            avatarText.setManaged(false);
        }
    }

    /**
     * Flips the dialog box so the avatar is on the left.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog row for the user's input.
     *
     * @param text The user's message.
     * @return The user's dialog row.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, USER_IMAGE, "", "user-label", "user-avatar");
        db.getStyleClass().add("user-row");
        return db;
    }

    /**
     * Creates a left-aligned dialog row for Baby's reply.
     *
     * @param text Baby's response.
     * @param isError True if the response should use the error style.
     * @return Baby's dialog row.
     */
    public static DialogBox getBabyDialog(String text, boolean isError) {
        Image image = isError ? SNOWMAN_IMAGE : QUEEN_IMAGE;
        String styleClass = isError ? "error-label" : "queen-label";
        String avatarStyleClass = isError ? "error-avatar" : "baby-avatar";
        DialogBox db = new DialogBox(text, image, "", styleClass, avatarStyleClass);
        db.getStyleClass().add("baby-row");
        db.flip();
        return db;
    }
}
