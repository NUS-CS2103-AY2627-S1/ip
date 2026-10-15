package elora;

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
 * A single chat bubble. Elora's bubbles are left-aligned with a small
 * avatar next to them; the user's are right-aligned, plain speech
 * bubbles with no avatar - the conversation is between a person and an
 * app, not two people, so the two sides deliberately don't look alike.
 */
public class DialogBox extends HBox {
    private static final double BUBBLE_WIDTH_FRACTION = 0.72;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads DialogBox.fxml as this HBox's own content, then fills in its
     * message text and (if given) avatar image. Private: callers must go
     * through {@link #getUserDialog(String)} or
     * {@link #getEloraDialog(String, Image, boolean)}, which also apply
     * the alignment and styling appropriate to who's speaking.
     *
     * @param text The message to display.
     * @param img The avatar to show, or null to show no avatar.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml should always be on the classpath", e);
        }
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(BUBBLE_WIDTH_FRACTION));
        if (img != null) {
            displayPicture.setImage(img);
            displayPicture.setClip(new Circle(20, 20, 20));
        }
    }

    /**
     * Creates a dialog box for a message the user typed: a plain bubble,
     * right-aligned, with no avatar (there's only one user in this
     * conversation, so a repeated picture of them would add nothing).
     *
     * @param text The user's message.
     * @return The dialog box to add to the conversation.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, null);
        db.displayPicture.setVisible(false);
        db.displayPicture.setManaged(false);
        db.dialog.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Creates a dialog box for one of Elora's replies: left-aligned,
     * with her avatar, styled as an error bubble if the reply reports a
     * problem so mistakes catch the user's attention.
     *
     * @param text Elora's reply.
     * @param img Elora's avatar.
     * @param isError Whether this reply reports an error.
     * @return The dialog box to add to the conversation.
     */
    public static DialogBox getEloraDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add(isError ? "elora-bubble-error" : "elora-bubble");
        return db;
    }

    /**
     * Reverses this box's children (avatar and label) and re-aligns them
     * to the left, turning a user-style bubble into an Elora-style one.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
